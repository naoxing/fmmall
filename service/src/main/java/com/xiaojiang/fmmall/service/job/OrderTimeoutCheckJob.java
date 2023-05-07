package com.xiaojiang.fmmall.service.job;

import com.github.wxpay.sdk.WXPay;
import com.xiaojiang.fmmall.entity.Orders;
import com.xiaojiang.fmmall.mapper.OrdersMapper;
import com.xiaojiang.fmmall.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author xiaojiang
 * @Date 2023-04-15 09:49
 * @Description
 **/
@Component
public class OrderTimeoutCheckJob {
    @Autowired
    private OrdersMapper ordersMapper;
    @Autowired
    private OrderService orderService;

    private WXPay wxPay = new WXPay(new MyPayConfig());


    @Scheduled(cron = "* 0/5 * * * ?") //从第0秒开始,每隔5秒调用一次
    public void checkAndCloseOrder(){
        //1.查询超过30min未支付的订单
        Example example = new Example(Orders.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("status","1");
        Date date = new Date(); //当前时间
        Date halfHourAgoDate = new Date(date.getTime() - (30 * 60 * 1000)); // 创建前半小时的Date对象
        criteria.andLessThan("createTime",halfHourAgoDate); //查询小于前半小时的订单,说明此订单超时未付款
        List<Orders> orders = ordersMapper.selectByExample(example);
        //2.再次访问微信平台接口,确认当前订单最终的支付状态
        try {
            for (Orders order : orders) {
                HashMap<String, String> params = new HashMap<>();
                params.put("out_trade_no",order.getOrderId());
                Map<String, String> resp = wxPay.orderQuery(params);
                //{transaction_id=4200001821202304148159764097, nonce_str=NcyPPIBDFRDCtwjG, trade_state=SUCCESS, bank_type=OTHERS, openid=oUuptwlv5aA8t8PguQqr0sCdqM8I, sign=F628C5C84FE9751F0181742C885CDC91, return_msg=OK, fee_type=CNY, mch_id=1497984412, cash_fee=1, out_trade_no=81c50217fe9647d1b7de8fe451fcdba4, cash_fee_type=CNY, appid=wx632c8f211f8122c6, total_fee=1, trade_state_desc=支付成功, trade_type=NATIVE, result_code=SUCCESS, attach=, time_end=20230414223939, is_subscribe=N, return_code=SUCCESS}
                if(resp.get("trade_state").equalsIgnoreCase("SUCCESS")){
                    //2.1 如果订单已支付,修改订单为 待发货/已支付 status="2"
                    Orders ord = new Orders();
                    ord.setOrderId(order.getOrderId());
                    ord.setStatus("2");
                    ordersMapper.updateByPrimaryKeySelective(ord);
                }else if(resp.get("trade_state").equalsIgnoreCase("NOTPAY")){
                    //2.2 如果订单确实未支付,取消订单
                    //a.向微信平台发送请求,关闭当前订单的支付连接
                    Map<String, String> closeMap = wxPay.closeOrder(params);
                    //通过订单id修改未支付订单的状态(关闭),并恢复库存
                    orderService.closeOrder(order.getOrderId());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
