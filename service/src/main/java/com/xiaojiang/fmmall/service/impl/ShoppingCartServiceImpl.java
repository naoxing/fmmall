package com.xiaojiang.fmmall.service.impl;

import com.xiaojiang.fmmall.entity.ShoppingCart;
import com.xiaojiang.fmmall.mapper.ShoppingCartMapper;
import com.xiaojiang.fmmall.service.ShoppingCartService;
import com.xiaojiang.fmmall.vo.ResStatus;
import com.xiaojiang.fmmall.vo.ResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @Author xiaojiang
 * @Date 2023-04-08 12:41
 * @Description
 **/
@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    @Override
    public ResultVo addShoppingCart(ShoppingCart cart) {
        cart.setCartTime(sdf.format(new Date()));
        int res = shoppingCartMapper.insert(cart);
        if(res>0){
            return new ResultVo(ResStatus.OK,"success",null);
        }else {
           return new ResultVo(ResStatus.NO,"fail",null);
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public ResultVo listShoppingCartByUserId(int userId) {
        return new ResultVo(ResStatus.OK,"success",shoppingCartMapper.selectShopcartByUserId(userId));
    }

    @Override
    public ResultVo updateCartnumByCartid(int cartId, int cartNum) {
        int result = shoppingCartMapper.updateCartnumByCartid(cartId, cartNum);
        if(result>0){
            return new ResultVo(ResStatus.OK,"success",null);
        }
        return new ResultVo(ResStatus.NO,"success",null);
    }

    @Override
    public ResultVo deleteCartByCartid(int cartId) {
        Example example = new Example(ShoppingCart.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("cartId",cartId);
        int result = shoppingCartMapper.deleteByExample(example);
        if(result>0){
            return new ResultVo(ResStatus.OK,"success",null);
        }
        return new ResultVo(ResStatus.NO,"success",null);
    }

    @Override
    public ResultVo selectShopcartByCids(String cids) {
        String[] arr = cids.split(",");
        ArrayList<Integer> carIds = new ArrayList<>();
        try {
            for (int i = 0; i < arr.length; i++) {
                carIds.add(Integer.parseInt(arr[i]));
            }
        } catch (NumberFormatException e) {
            System.out.println("没问题,继续跑");
        }
        return new ResultVo(ResStatus.OK,"success",shoppingCartMapper.selectShopcartByCids(carIds));
    }
}
