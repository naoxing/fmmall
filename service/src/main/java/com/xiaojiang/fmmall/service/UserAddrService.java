package com.xiaojiang.fmmall.service;

import com.xiaojiang.fmmall.vo.ResultVo;

/**
 * @Author xiaojiang
 * @Date 2023-04-10 21:18
 * @Description
 **/
public interface UserAddrService {
    public ResultVo listAddrsByUid(int userId);
}
