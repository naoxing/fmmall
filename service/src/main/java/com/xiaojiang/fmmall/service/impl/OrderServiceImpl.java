package com.xiaojiang.fmmall.service.impl;

import com.xiaojiang.fmmall.entity.*;
import com.xiaojiang.fmmall.mapper.*;
import com.xiaojiang.fmmall.service.OrderService;
import com.xiaojiang.fmmall.utils.PageHelper;
import com.xiaojiang.fmmall.vo.ResStatus;
import com.xiaojiang.fmmall.vo.ResultVo;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @Author xiaojiang
 * @Date 2023-04-12 13:45
 * @Description
 **/
@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private OrdersMapper ordersMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private ProductSkuMapper productSkuMapper;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private RedissonClient redissonClient;

    //日志管理
    private Logger logger = LoggerFactory.getLogger( OrderServiceImpl.class);

    /**
     * 保存订单业务
     * @param cids (12,14)
     * @param order 前端传过来的订单数据
     * @return
     */
    @Override
    @Transactional
    public HashMap<String,String> addOrder(String cids, Orders order){
        logger.info("add oder begin...");
        //1.查询购物车商品的详细信息
        String[] cidsStr = cids.split(",");
        List<Integer> cidsList = new ArrayList<>();
        for (String id : cidsStr) {
            try {
                cidsList.add(Integer.parseInt(id));
            } catch (NumberFormatException e) {
                throw new RuntimeException(e);
            }
        }
        //通过选择的购物车id查询购物车记录
        List<ShoppingCartVO> list = shoppingCartMapper.selectShopcartByCids(cidsList);

        //2.获取所有商品名称,字符串拼接
        boolean isLock = true;
        StringBuilder untitled = new StringBuilder();
        String[] skuIds = new String[list.size()];  //{101,102,null,...}
        Map<String,RLock> lockMap = new HashMap<>();
        for (int i = 0; i < list.size(); i++) {
            //将商品id放入redis锁住,防止超卖
            String skuId = list.get(i).getSkuId();
            //构建当前商品的锁
            RLock lock = redissonClient.getLock(skuId);
            boolean b = false;
            try {
                b = lock.tryLock(10,3, TimeUnit.SECONDS);
                if(b){
                    lockMap.put(skuId,lock);
                    skuIds[i] = skuId;
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            isLock = isLock & b;
            if(i<list.size()-1){
                untitled.append(list.get(i).getProductName()).append(",");
            }else {
                untitled.append(list.get(i).getProductName());
            }
        }
        HashMap<String, String> map = null;
        try {
            if (isLock) {  //如果将需要操作的商品id成功锁住,就进行订单操作
                boolean isFull = true;
                //再次验证库存是否可行,防止其他并发线程已经修改过
                list = shoppingCartMapper.selectShopcartByCids(cidsList);
                for (int i = 0; i < list.size(); i++) {
                    if(list.get(i).getSkuStock() < Integer.parseInt(list.get(i).getCartNum())){
                        isFull=false;
                    }
                }
                if(isFull){
                    //内存充足,进行订单等操作
                    //3.保存订单
                    //a.userId
                    //b.untitled √
                    //c.收货人信息:姓名,电话,地址
                    //d.总价格
                    //e.支付方式
                    //f.订单创建时间 √
                    //g.订单初始状态 √ (待支付:1)
                    order.setUntitled(untitled.toString());
                    order.setCreateTime(new Date());
                    order.setStatus("1");
                    //生成订单编号
                    String orderId = UUID.randomUUID().toString().replace("-", "");
                    order.setOrderId(orderId);
                    int result1 = ordersMapper.insert(order);
                    logger.info("订单添加完成...");
                    //4.生成订单快照
                    if (result1 > 0) {
                        for (ShoppingCartVO sc : list) {
                            int cnum = 0;
                            cnum = Integer.parseInt(sc.getCartNum());
                            String itemId = System.currentTimeMillis() + "" + (new Random().nextInt(89999) + 10000);
                            OrderItem orderItem = new OrderItem(itemId, orderId, sc.getProductId(), sc.getProductName(), sc.getProductImg(), sc.getSkuId(), sc.getSkuName(),
                                    sc.getProductPrice(), cnum, new BigDecimal(sc.getSellPrice() * cnum), new Date(), new Date(), 0);
                            orderItemMapper.insert(orderItem);
                            logger.info("订单快照已经生成...");
                        }
                        //5.扣减库存
                        for (ShoppingCartVO sc : list) {
                            String skuId = sc.getSkuId();
                            int newStock = 0;
                            try {
                                newStock = sc.getSkuStock() - Integer.parseInt(sc.getCartNum());
                            } catch (NumberFormatException e) {
                                throw new RuntimeException(e);
                            }
                            Example example = new Example(ProductSku.class);
                            Example.Criteria criteria = example.createCriteria();
                            criteria.andEqualTo("skuId", skuId);
                            ProductSku productSku = new ProductSku();
                            productSku.setSkuId(skuId);
                            productSku.setStock(newStock);
                            productSkuMapper.updateByPrimaryKeySelective(productSku);
                            logger.info("扣减{}库存{}完成",skuId,Integer.parseInt(sc.getCartNum()));
                        }
                        //6.删除购物车:当购物车中记录购买成功之后,购物车对应的数据作删除操作
                        for (Integer cid : cidsList) {
                            shoppingCartMapper.deleteByPrimaryKey(cid);
                        }
                        logger.info("删除购物车完成...");
                        //返回信息需要请求微信平台
                        map = new HashMap<>();
                        map.put("orderId",orderId);
                        map.put("untitled",untitled.toString());
                    }
                }
             }
        }catch (RuntimeException e) {
            throw new RuntimeException(e);
        } finally {
            //如果锁没成功或者库存不足或者订单成功or失败-->锁全部释放
            for (int j = 0; j < skuIds.length; j++) {
                String skuId = skuIds[j];
                if(skuId!=null && !"".equals(skuId)){
                    lockMap.get(skuId).unlock();
                }
            }
        }
        return map;
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)  //保持一致性
    public void closeOrder(String orderId) {
        //b.修改当前订单:status=6已关闭 close_type=1 超时未支付
        Orders ord = new Orders();
        ord.setOrderId(orderId);
        ord.setStatus("6");
        ord.setCloseType(1);
        ordersMapper.updateByPrimaryKeySelective(ord);
        //c.还原库存:先根据当前订单编号查询商品快照(skuid buy_count)-->修改product_sku
        Example example = new Example(OrderItem.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("orderId",orderId);
        List<OrderItem> orderItems = orderItemMapper.selectByExample(example);
        for (OrderItem orderItem : orderItems) {
            ProductSku productSku = productSkuMapper.selectByPrimaryKey(orderItem.getSkuId());
            productSku.setStock(productSku.getStock()+orderItem.getBuyCounts());
            productSkuMapper.updateByPrimaryKeySelective(productSku);
        }
    }

    @Override
    public int updateOrderStatus(String orderId, String status) {
        Orders orders = new Orders();
        orders.setOrderId(orderId);
        orders.setStatus(status);
        return ordersMapper.updateByPrimaryKeySelective(orders);
    }

    @Override
    public ResultVo getOrderById(String orderId) {
        Orders order = ordersMapper.selectByPrimaryKey(orderId);
        return new ResultVo(ResStatus.OK,"success",order.getStatus());
    }

    @Override
    public ResultVo listOrders(String userId, String status, int pageNum, int limit) {
        //1.分页查询
        int start = (pageNum-1)*limit;
        List<OrdersVO> ordersVOS = ordersMapper.selectOrders(userId, status, start, limit);

        //2.查询总条数
        Example example = new Example(Orders.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId",userId);
        if(status!=null && !"".equals(status)){
            criteria.andEqualTo("status",status);
        }
        int count = ordersMapper.selectCountByExample(example);

        //3.计算总页数
        int pageCount = count%limit==0? count/limit : count/limit+1;

        //封装数据
        PageHelper<OrdersVO> pageHelper = new PageHelper<>(count,pageCount,ordersVOS);
        return new ResultVo(ResStatus.OK,"success",pageHelper);
    }
}
