package com.xiaojiang.fmmall.mapper;

import com.xiaojiang.fmmall.entity.Orders;
import com.xiaojiang.fmmall.entity.OrdersVO;
import com.xiaojiang.fmmall.general.GeneralDAO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrdersMapper extends GeneralDAO<Orders> {

    public List<OrdersVO> selectOrders(@Param("userId") String userId,
                                       @Param("status") String status,
                                       @Param("start") int start,
                                       @Param("limit") int limit);

}
