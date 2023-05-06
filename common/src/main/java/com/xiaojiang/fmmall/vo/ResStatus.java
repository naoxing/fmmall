package com.xiaojiang.fmmall.vo;

/**
 * @Author xiaojiang
 * @Date 2023-04-02 17:02
 * @Description
 **/
public class ResStatus {
    public final static int OK = 10000;
    public final static int NO = 10001;

    public final static int LOGIN_SUCCESS=20000; //认证成功
    public final static int LOGIN_FAIL_NOT=20001; //用户未登录
    public final static int LOGIN_FAIL_OVERDUE=20002; //用户登录过期
}
