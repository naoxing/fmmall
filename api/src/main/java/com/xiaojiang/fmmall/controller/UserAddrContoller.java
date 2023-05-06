package com.xiaojiang.fmmall.controller;

import com.xiaojiang.fmmall.service.UserAddrService;
import com.xiaojiang.fmmall.vo.ResStatus;
import com.xiaojiang.fmmall.vo.ResultVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author xiaojiang
 * @Date 2023-04-10 21:25
 * @Description
 **/
@RestController
@CrossOrigin
@RequestMapping("/useraddr")
@Api(value = "提供收货地址相关接口",tags = "收货地址管理")
public class UserAddrContoller {
    @Autowired
    private UserAddrService userAddrService;

    @GetMapping("/list")
    @ApiImplicitParam(dataType = "int",name = "userId",value = "用户Id",required = true)
    public ResultVo listAddrs(Integer userId){
        if(userId!=null){
            return userAddrService.listAddrsByUid(userId);
        }
        return new ResultVo(ResStatus.NO,"参数有误",null);
    }
}
