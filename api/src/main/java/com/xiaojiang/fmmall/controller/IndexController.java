package com.xiaojiang.fmmall.controller;

import com.google.common.annotations.VisibleForTesting;
import com.xiaojiang.fmmall.mapper.UsersMapper;
import com.xiaojiang.fmmall.service.CategoryService;
import com.xiaojiang.fmmall.service.IndexImgService;
import com.xiaojiang.fmmall.service.ProductService;
import com.xiaojiang.fmmall.vo.ResultVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author xiaojiang
 * @Date 2023-04-03 15:24
 * @Description
 **/
@RestController
@RequestMapping("/index")
@CrossOrigin
@Api(value = "提供首页数据显示所需的接口",tags = "首页管理")
public class IndexController {
    @Autowired
    private IndexImgService indexImgService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ProductService productService;

    @GetMapping("indexImg")
    @ApiOperation("首页轮播图接口")
    public ResultVo listIndexImgs() {
        return indexImgService.listIndexImgs();
    }

    @GetMapping("/category-list")
    @ApiOperation("商品分类查询接口")
    public ResultVo listCategories() {
        return categoryService.listCategories();
    }

    @GetMapping("/list-recommends")
    @ApiOperation("新品推荐商品接口")
    public ResultVo listRecommendProducts() {
        return productService.listRecommendProducts();
    }

    @GetMapping("/category-recommends")
    @ApiOperation("分类推荐商品接口")
    public ResultVo listRecommendProductsByCategory() {
        return categoryService.listFirstLevelCategories();
    }



}