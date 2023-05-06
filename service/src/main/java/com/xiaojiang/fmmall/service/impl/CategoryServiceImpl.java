package com.xiaojiang.fmmall.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xiaojiang.fmmall.entity.CategoryVO;
import com.xiaojiang.fmmall.entity.IndexImg;
import com.xiaojiang.fmmall.mapper.CategoryMapper;
import com.xiaojiang.fmmall.service.CategoryService;
import com.xiaojiang.fmmall.vo.ResStatus;
import com.xiaojiang.fmmall.vo.ResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Author xiaojiang
 * @Date 2023-04-03 22:05
 * @Description
 **/
@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public ResultVo listCategories() {
        List<CategoryVO> categoryVOS = null;
        String cateGoriesJson = stringRedisTemplate.boundValueOps("cateGories").get();
        try {
            if(cateGoriesJson!=null){ //redis查询不为空
                JavaType javaType = objectMapper.getTypeFactory().constructParametricType(ArrayList.class, CategoryVO.class);
                categoryVOS = objectMapper.readValue(cateGoriesJson, javaType);
                return new ResultVo(ResStatus.OK,"success",categoryVOS);
            }else { //为空数据库查询
                categoryVOS = categoryMapper.selectAllCategories2(0);
                stringRedisTemplate.boundValueOps("cateGories").set(objectMapper.writeValueAsString(categoryVOS));
                //设置过期时间为一天
                stringRedisTemplate.boundValueOps("indexImgs").expire(1, TimeUnit.DAYS);
                return new ResultVo(ResStatus.OK,"success",categoryVOS);
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ResultVo listFirstLevelCategories() {
        return new ResultVo(ResStatus.OK,"success",categoryMapper.selectFirstLevelCategories());
    }

}
