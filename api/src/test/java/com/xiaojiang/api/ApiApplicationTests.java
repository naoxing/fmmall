package com.xiaojiang.api;

import com.xiaojiang.fmmall.ApiApplication;
import com.xiaojiang.fmmall.entity.*;
import com.xiaojiang.fmmall.mapper.*;
import com.xiaojiang.fmmall.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SpringBootTest(classes = ApiApplication.class,webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ApiApplicationTests {

    @Resource
    private UsersMapper usersMapper;
    @Resource
    private CategoryMapper categoryMapper;
    @Resource
    private ProductMapper productMapper;
    @Resource
    private ProductService productService;
    @Resource
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private OrdersMapper ordersMapper;

    @Test
    public void t2(){
        List<ShoppingCartVO> shoppingCartVOS = shoppingCartMapper.selectShopcartByUserId(12);
        for (ShoppingCartVO shoppingCartVO : shoppingCartVOS) {
            System.out.println(shoppingCartVO);
        }
    }
    @Test
    public void t3(){
        String cids = "12,14";
        //1.查询购物车商品的详细信息
        String[] cidsStr = cids.split(",");
        List<Integer> cidsList = new ArrayList<>();
        for (String id : cidsStr) {
            cidsList.add(Integer.parseInt(id));
        }
        List<ShoppingCartVO> list = shoppingCartMapper.selectShopcartByCids(cidsList);
        //2.获取所有商品名称,字符串拼接
        StringBuilder untitled = new StringBuilder();
        for (ShoppingCartVO shoppingCartVO : list) {
            System.out.println(shoppingCartVO);
            untitled.append(shoppingCartVO.getProductName()).append(",");
        }
        System.out.println("untitled"+untitled.toString());

    }

    @Test
    public void test4(){
        Example example = new Example(Orders.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("status","1");
        Date date = new Date(); //当前时间
        Date halfHourAgoDate = new Date(date.getTime() - (30 * 60 * 1000)); // 创建前半小时的Date对象
        criteria.andLessThan("createTime",halfHourAgoDate); //查询小于前半小时的订单,说明此订单超时未付款
        List<Orders> orders = ordersMapper.selectByExample(example);
        for (Orders order : orders) {
            System.out.println(order.getOrderId()+"\t"+order.getCreateTime());
        }
    }

    @Test
    public void test5(){
        List<ProductVO> productVOS = productMapper.selectTop6ByCategory(2);
        for (ProductVO productVO : productVOS) {
            System.out.println(productVO);
        }
    }
    @Test
    public void test6(){
        List<ProductVO> productVOS = productMapper.selectProductByCategoryId(49, 0, 5);
        for (ProductVO productVO : productVOS) {
            System.out.println(productVO);
            if (productVO.getSkus()!=null) {
                for (ProductSku skus : productVO.getSkus()) {
                    System.out.println("\t"+skus);
                }
            }
        }
    }

    @Test
    public void testFirstLevelCategories(){
        List<CategoryVO> categoryVOS = categoryMapper.selectFirstLevelCategories();
        for (CategoryVO categoryVO : categoryVOS) {
            System.out.println(categoryVO);
            for (ProductVO product : categoryVO.getProducts()) {
                System.out.println("\t"+product);
                for (ProductImg img : product.getImgs()) {
                    System.out.println("\t\t"+img);
                }
            }
        }
    }

    @Test
    public void testProduct(){
        List<ProductVO> productVOS = productMapper.selectRecommendProducts();
        for (ProductVO productVO : productVOS) {
            System.out.println(productVO);
            for (ProductImg img : productVO.getImgs()) {
                System.out.println("\t"+img);
            }
        }
    }

    @Test
    public void testSelect(){
//        List<CategoryVO> categoryVOS = categoryMapper.selectAllCategories();
        List<CategoryVO> categoryVOS = categoryMapper.selectAllCategories2(0);
        for (CategoryVO c1 : categoryVOS) {
            System.out.println(c1);
            for (CategoryVO c2 : c1.getCategories()) {
                System.out.println("\t"+c2);
                for (CategoryVO c3 : c2.getCategories()) {
                    System.out.println("\t\t"+c3);
                }

            }
        }
    }

}
