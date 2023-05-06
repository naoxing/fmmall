package com.xiaojiang.fmmall.mapper;

import com.xiaojiang.fmmall.entity.ProductImg;
import com.xiaojiang.fmmall.general.GeneralDAO;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductImgMapper extends GeneralDAO<ProductImg> {

    public List<ProductImg> selectProductImgByProductId(int productId);

}