package com.xiaojiang.fmmall.service.impl;

import com.xiaojiang.fmmall.entity.UserAddr;
import com.xiaojiang.fmmall.mapper.UserAddrMapper;
import com.xiaojiang.fmmall.service.UserAddrService;
import com.xiaojiang.fmmall.vo.ResStatus;
import com.xiaojiang.fmmall.vo.ResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * @Author xiaojiang
 * @Date 2023-04-10 21:20
 * @Description
 **/
@Service
public class UserAddrServiceImpl implements UserAddrService {
    @Autowired
    private UserAddrMapper userAddrMapper;

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public ResultVo listAddrsByUid(int userId) {
        Example example = new Example(UserAddr.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId",userId);
        criteria.andEqualTo("status",1);
        List<UserAddr> userAddrs = userAddrMapper.selectByExample(example);
        return new ResultVo(ResStatus.OK,"success",userAddrs);
    }
}
