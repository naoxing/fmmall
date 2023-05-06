package com.xiaojiang.fmmall.service;

import com.xiaojiang.fmmall.entity.Orders;
import com.xiaojiang.fmmall.vo.ResultVo;

import java.sql.SQLException;
import java.util.HashMap;

/**
 * @Author xiaojiang
 * @Date 2023-04-12 13:44
 * @Description
 **/
public interface OrderService {
    /**
     * 根据选择购物车的商品支付并添加订单
     * @param cids 购物车id字符串
     * @param orders 前端传过来部分订单参数
     * @return 传递给微信支付所需的参数map
     * @throws SQLException
     */
    public HashMap<String,String> addOrder(String cids, Orders orders) throws SQLException;

    /**
     * 根据订单id,状态码修改订单状态
     * @param orderId 订单id
     * @param status 状态码
     * @return
     */

    public int updateOrderStatus(String orderId,String status);

    /**
     * 通过订单id获取订单信息
     * @param orderId 订单id
     * @return
     */
    public ResultVo getOrderById(String orderId);

    /**
     * 通过订单id修改未支付订单的状态,并恢复库存
     * @param orderId 未支付的订单id
     */
    public void closeOrder(String orderId);

    public ResultVo listOrders(String userId,String status,int pageNum,int limit);

}
