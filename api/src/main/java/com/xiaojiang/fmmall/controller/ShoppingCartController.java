package com.xiaojiang.fmmall.controller;

import com.xiaojiang.fmmall.entity.ShoppingCart;
import com.xiaojiang.fmmall.service.ShoppingCartService;
import com.xiaojiang.fmmall.vo.ResStatus;
import com.xiaojiang.fmmall.vo.ResultVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * @Author xiaojiang
 * @Date 2023-04-03 08:29
 * @Description
 **/
@RestController
@RequestMapping("/shopcart")
@CrossOrigin
@Api(value = "提供购物车业务相关接口",tags = "购物车管理")
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;

    @PostMapping("/add")
    public ResultVo addShoppingCart(@RequestBody ShoppingCart cart,@RequestHeader("token")String token){
        return shoppingCartService.addShoppingCart(cart);
    }

    @GetMapping("/list")
    @ApiImplicitParam(dataType = "int",name = "userId",value = "用户Id",required = true)
    public ResultVo listShoppingCart(Integer userId,@RequestHeader("token")String token){
        if(userId!=null){
            return shoppingCartService.listShoppingCartByUserId(userId);
        }
        return new ResultVo(ResStatus.NO,"参数为空",null);
    }

    @PutMapping("/update/{cid}/{cnum}")
    public ResultVo updateCartnumByCartid(@PathVariable("cid")Integer cid,
                                          @PathVariable("cnum")Integer cnum,
                                          @RequestHeader("token")String token){
        if (cid!=null && cnum!=null) {
            return shoppingCartService.updateCartnumByCartid(cid,cnum);
        }
        return new ResultVo(ResStatus.NO,"参数为空",null);
    }
    @DeleteMapping("/delete/{cid}")
    public ResultVo deleteCartByCartid(@PathVariable("cid")Integer cid,@RequestHeader("token")String token){
        if (cid!=null) {
            return shoppingCartService.deleteCartByCartid(cid);
        }
        return new ResultVo(ResStatus.NO,"参数为空",null);

    }
    @GetMapping("/listbycids")
    @ApiImplicitParam(dataType = "java.lang.List",name = "cids",value = "用户Id集合",required = true)
    public ResultVo listShopcartByCids(String cids,@RequestHeader("token")String token){
        return shoppingCartService.selectShopcartByCids(cids);
    }



}
