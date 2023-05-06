package com.xiaojiang.fmmall.service;

import com.xiaojiang.fmmall.entity.CategoryVO;
import com.xiaojiang.fmmall.vo.ResultVo;

import java.util.List;

/**
 * @Author xiaojiang
 * @Date 2023-04-03 22:04
 * @Description
 **/
public interface CategoryService {
    public ResultVo listCategories();

    public ResultVo listFirstLevelCategories();
}
