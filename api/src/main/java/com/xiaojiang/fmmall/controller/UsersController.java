package com.xiaojiang.fmmall.controller;

import com.xiaojiang.fmmall.entity.Users;
import com.xiaojiang.fmmall.service.UserService;
import com.xiaojiang.fmmall.vo.ResStatus;
import com.xiaojiang.fmmall.vo.ResultVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Author xiaojiang
 * @Date 2023-03-23 12:00
 * @Description
 **/
@RestController()
@Api(value = "提供用户的登录和注册接口",tags = "用户管理")
@CrossOrigin //设置跨域问题
@RequestMapping("/user")
public class UsersController {
    @Autowired
    private UserService userService;

    @ApiOperation("用户登录接口")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "string",name = "username",value = "用户登录账号",required = true),
            @ApiImplicitParam(dataType = "string",name = "password",value = "用户登录密码",required = true)
    })
    @GetMapping("/login")
    public ResultVo login(@RequestParam("username") String name,
                          @RequestParam(value = "password",defaultValue = "111111") String pwd){
        return userService.checkLogin(name, pwd);
    }

    @ApiOperation("用户注册接口")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "string",name = "username",value = "用户登录账号",required = true),
            @ApiImplicitParam(dataType = "string",name = "password",value = "用户登录密码",required = true)
    })
    @PostMapping("/regist")
    public ResultVo regist(@RequestBody Users users){
        return userService.userResgit(users.getUsername(), users.getPassword());
    }


    @ApiOperation("校验token是否过期接口")
    @GetMapping("/check")
    public ResultVo userTokencheck(@RequestHeader("token") String token){
        return new ResultVo(ResStatus.OK,"success",null);
    }

}
