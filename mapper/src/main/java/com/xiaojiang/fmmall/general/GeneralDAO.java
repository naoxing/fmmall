package com.xiaojiang.fmmall.general;

import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

/**
 * @Author xiaojiang
 * @Date 2023-03-25 16:37
 * @Description
 **/
public interface GeneralDAO <T> extends Mapper<T>, MySqlMapper<T> {
}
