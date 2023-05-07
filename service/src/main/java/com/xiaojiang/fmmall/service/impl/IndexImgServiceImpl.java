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
        //如果高并发访问轮播图
        String imgsStr = stringRedisTemplate.boundValueOps("indexImgs").get();
        try {
            if(imgsStr!=null){
                JavaType javaType = objectMapper.getTypeFactory().constructParametricType(ArrayList.class, IndexImg.class);
                indexImgs = objectMapper.readValue(imgsStr, javaType);
            }else {
                synchronized (this) { //双重检测锁, 如果第一个线程没查到,就查数据库,然后放入redis,第二个线程过来就会再次判断有没有数据
                    String str = stringRedisTemplate.boundValueOps("indexImgs").get();
                    if(str!=null){
                        JavaType javaType = objectMapper.getTypeFactory().constructParametricType(ArrayList.class, IndexImg.class);
                        indexImgs = objectMapper.readValue(str, javaType);
                    }else { //只有第一个请求为空,查数据库
                        System.out.println("查询数据库--------");
                        indexImgs = indexImgMapper.listIndexImgs();
                        if(indexImgs!=null){ //如果数据库有数据
                            stringRedisTemplate.boundValueOps("indexImgs").set(objectMapper.writeValueAsString(indexImgs));
                            //设置过期时间为一天
                            stringRedisTemplate.boundValueOps("indexImgs").expire(1, TimeUnit.DAYS);
                        }else { //如果数据库没有数据,就写一个空数据存入redis,防止缓存穿透
                            stringRedisTemplate.boundValueOps("indexImgs").set("[]");
                            //设置过期时间为十秒钟
                            stringRedisTemplate.boundValueOps("indexImgs").expire(10, TimeUnit.SECONDS);
                        }
                    }
                }
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return new ResultVo(ResStatus.OK,"success",indexImgs);
    }
}
