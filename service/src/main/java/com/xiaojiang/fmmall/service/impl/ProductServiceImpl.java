package com.xiaojiang.fmmall.service.impl;

import com.xiaojiang.fmmall.entity.*;
import com.xiaojiang.fmmall.mapper.ProductImgMapper;
import com.xiaojiang.fmmall.mapper.ProductMapper;
import com.xiaojiang.fmmall.mapper.ProductParamsMapper;
import com.xiaojiang.fmmall.mapper.ProductSkuMapper;
import com.xiaojiang.fmmall.service.ProductService;
import com.xiaojiang.fmmall.utils.PageHelper;
import com.xiaojiang.fmmall.vo.ResStatus;
import com.xiaojiang.fmmall.vo.ResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.HashMap;
import java.util.List;

/**
 * @Author xiaojiang
 * @Date 2023-04-04 09:52
 * @Description
 **/
@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private ProductImgMapper productImgMapper;
    @Autowired
    private ProductSkuMapper productSkuMapper;
    @Autowired
    private ProductParamsMapper productParamsMapper;

    @Override
    public ResultVo listRecommendProducts() {
        return new ResultVo(ResStatus.OK, "success", productMapper.selectRecommendProducts());
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public ResultVo getProductBasicInfo(String productId) {
        //1.商品基本信息
        Example example = new Example(Product.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("productId", productId);
        criteria.andEqualTo("productStatus", 1); //状态为1表示上架商品
        List<Product> products = productMapper.selectByExample(example);
        if (products.size() < 1) {
            return new ResultVo(ResStatus.NO, "没有此商品信息", null);
        }
        //2.商品图片
        example = new Example(ProductImg.class);
        criteria = example.createCriteria();
        criteria.andEqualTo("itemId", productId);
        List<ProductImg> productImgs = productImgMapper.selectByExample(example);
        //3.商品套餐
        example = new Example(ProductSku.class);
        criteria = example.createCriteria();
        criteria.andEqualTo("productId", productId);
        List<ProductSku> productSkus = productSkuMapper.selectByExample(example);

//        ProductVO productVO = toProductVo(products.get(0), productImgs, productSkus);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("product", products.get(0));
        hashMap.put("productImgs", productImgs);
        hashMap.put("productSkus", productSkus);
        return new ResultVo(ResStatus.OK, "success", hashMap);
    }

    @Override
    public ResultVo getProductParamsById(String productId) {
        Example example = new Example(ProductParams.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("productId", productId);
        List<ProductParams> productParams = productParamsMapper.selectByExample(example);
        if (productParams.size() < 1) {
            return new ResultVo(ResStatus.NO, "没有此商品参数", null);
        }
        return new ResultVo(ResStatus.OK, "success", productParams.get(0));
    }

    @Override
    public ResultVo getProductsByCategoryId(int categoryId, int pageNum, int limit) {
        //1.查询分页数据
        int start = (pageNum - 1) * limit;
        List<ProductVO> productVOS = productMapper.selectProductByCategoryId(categoryId, start, limit);
        //2.查询当前类别下的商品的总记录数
        Example example = new Example(Product.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("categoryId", categoryId);
        int count = productMapper.selectCountByExample(example);
        //3.计算总页数
        int pageCount = count % limit == 0 ? count / limit : count / limit + 1;
        PageHelper<ProductVO> pageHelper = new PageHelper<>(count, pageCount, productVOS);
        return new ResultVo(ResStatus.OK, "success", pageHelper);
    }

    @Override
    public ResultVo listBrands(int categoryId) {
        return new ResultVo(ResStatus.OK, "success", productMapper.selectBrandByCategoryId(categoryId));
    }

    @Override
    public ResultVo listProductByKeyword(String keyword, int pageNum, int limit) {
        //1.查询分页数据
        keyword = "%" + keyword + "%";
        int start = (pageNum - 1) * limit;
        List<ProductVO> productVOS = productMapper.selectProductByKeyword(keyword, start, limit);
        //2.查询当前模糊查询下的商品的总记录数
        Example example = new Example(Product.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andLike("productName", keyword);
        int count = productMapper.selectCountByExample(example);
        //3.计算总页数
        int pageCount = count % limit == 0 ? count / limit : count / limit + 1;
        PageHelper<ProductVO> pageHelper = new PageHelper<>(count, pageCount, productVOS);
        return new ResultVo(ResStatus.OK, "success", pageHelper);
    }

    @Override
    public ResultVo listBrandByKeyword(String kw) {
        kw = "%" + kw + "%";
        List<String> list = productMapper.selectBrandByKeyword(kw);
        return new ResultVo(ResStatus.OK,"success",list);
    }
}
