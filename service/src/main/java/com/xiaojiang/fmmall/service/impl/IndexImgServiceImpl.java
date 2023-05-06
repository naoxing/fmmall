package com.xiaojiang.fmmall.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xiaojiang.fmmall.entity.IndexImg;
import com.xiaojiang.fmmall.mapper.IndexImgMapper;
import com.xiaojiang.fmmall.service.IndexImgService;
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
 * @Date 2023-04-03 15:17
 * @Description
 **/
@Service
public class IndexImgServiceImpl implements IndexImgService {
    @Autowired
    private IndexImgMapper indexImgMapper;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public ResultVo listIndexImgs() {
        List<IndexImg> indexImgs = null;
        String imgsStr = stringRedisTemplate.boundValueOps("indexImgs").get();
        try {
            if(imgsStr!=null){
                JavaType javaType = objectMapper.getTypeFactory().constructParametricType(ArrayList.class, IndexImg.class);
                indexImgs = objectMapper.readValue(imgsStr, javaType);
                return new ResultVo(ResStatus.OK,"success",indexImgs);
            }else {
                indexImgs = indexImgMapper.listIndexImgs();
                if(indexImgs.size()==0){
                    return new ResultVo(ResStatus.NO,"fail",null);
                }
                stringRedisTemplate.boundValueOps("indexImgs").set(objectMapper.writeValueAsString(indexImgs));
                //设置过期时间为一天
                stringRedisTemplate.boundValueOps("indexImgs").expire(1, TimeUnit.DAYS);
                return new ResultVo(ResStatus.OK,"success",indexImgs);
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
