package com.xiaojiang.fmmall.mapper;

import com.xiaojiang.fmmall.entity.OrderItem;
import com.xiaojiang.fmmall.general.GeneralDAO;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemMapper extends GeneralDAO<OrderItem> {

    public List<OrderItem> listOrderItemsByOrderId(String orderId);
}