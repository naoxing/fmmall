package com.xiaojiang.fmmall.service;

import com.xiaojiang.fmmall.vo.ResultVo;
import org.apache.ibatis.annotations.Param;

/**
 * @Author xiaojiang
 * @Date 2023-04-04 09:43
 * @Description
 **/
public interface ProductService {
    public ResultVo listRecommendProducts();

    public ResultVo getProductBasicInfo(String productId);

    public ResultVo getProductParamsById(String productId);
    public ResultVo getProductsByCategoryId(int categoryId,int pageNum,int limit);
    public ResultVo listBrands(int categoryId);
    public ResultVo listProductByKeyword(String keyword, int pageNum, int limit);
    public ResultVo listBrandByKeyword(String kw);
}
