package com.xiaojiang.fmmall.service;

import com.xiaojiang.fmmall.entity.ShoppingCart;
import com.xiaojiang.fmmall.vo.ResultVo;

import java.util.List;

/**
 * @Author xiaojiang
 * @Date 2023-04-08 12:40
 * @Description
 **/
public interface ShoppingCartService {
    /**
     * 添加购物车信息
     * @param cart 购物车对象ShoppingCart
     * @return
     */
    public ResultVo addShoppingCart(ShoppingCart cart);

    /**
     * 通过用户id查询所有的购物车信息
     * @param userId 用户id
     * @return
     */

    public ResultVo listShoppingCartByUserId(int userId);

    /**
     * 通过购物车id和修改的数量来修改表数据
     * @param cartId 购物车id
     * @param cartNum 修改的商品数量
     * @return
     */
    public ResultVo updateCartnumByCartid(int cartId,int cartNum);

    /**
     * 通过购物车id删除数据
     * @param cartId 购物车id
     * @return
     */
    public ResultVo deleteCartByCartid(int cartId);

    /**
     * 通过购物车id集合来查询对应购物车信息
     * @param cids 购物车id集合
     * @return
     */

    public ResultVo selectShopcartByCids(String cids);
}
