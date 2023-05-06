package com.xiaojiang.fmmall.service.impl;

import com.xiaojiang.fmmall.mapper.CategoryMapper;
import com.xiaojiang.fmmall.service.CategoryService;
import com.xiaojiang.fmmall.vo.ResStatus;
import com.xiaojiang.fmmall.vo.ResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author xiaojiang
 * @Date 2023-04-03 22:05
 * @Description
 **/
@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public ResultVo listCategories() {
        return new ResultVo(ResStatus.OK,"success",categoryMapper.selectAllCategories2(0));
    }

    @Override
    public ResultVo listFirstLevelCategories() {
        return new ResultVo(ResStatus.OK,"success",categoryMapper.selectFirstLevelCategories());
    }

}
