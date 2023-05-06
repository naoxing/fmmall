package com.xiaojiang.fmmall.service;

import com.xiaojiang.fmmall.vo.ResultVo;

/**
 * @Author xiaojiang
 * @Date 2023-04-05 21:17
 * @Description
 **/
public interface ProductCommontsService {
    public ResultVo listCommontsByProductId(String productId,int pageNum,int limit);

    public ResultVo getCommentsCountByProductId(String productId);
}
