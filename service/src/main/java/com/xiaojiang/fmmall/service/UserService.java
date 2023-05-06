package com.xiaojiang.fmmall.service;

import com.xiaojiang.fmmall.vo.ResultVo;

/**
 * @Author xiaojiang
 * @Date 2023-03-23 20:50
 * @Description
 **/
public interface UserService {
    /**
     * 用户注册
     * @param name
     * @param pwd
     * @return
     */
    ResultVo userResgit(String name,String pwd);

    /**
     * 用户登录
     * @param name
     * @param pwd
     * @return
     */
    ResultVo checkLogin(String name,String pwd);
}
