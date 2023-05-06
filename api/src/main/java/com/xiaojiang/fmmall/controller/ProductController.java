package com.xiaojiang.fmmall.controller;

import com.xiaojiang.fmmall.service.ProductCommontsService;
import com.xiaojiang.fmmall.service.ProductService;
import com.xiaojiang.fmmall.vo.ResultVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Author xiaojiang
 * @Date 2023-04-05 08:21
 * @Description
 **/
@RestController
@RequestMapping("product")
@Api(value = "提供商品信息相关的接口",tags = "商品管理")
@CrossOrigin
public class ProductController {
    @Autowired
    private ProductService productService;
    @Autowired
    private ProductCommontsService productCommontsService;

    @GetMapping("/detail-info/{pid}")
    @ApiOperation("商品基本信息查询接口")
    public ResultVo getProductBasicInfo(@PathVariable("pid") String pid){
        return productService.getProductBasicInfo(pid);
    }

    @GetMapping("/detail-params/{pid}")
    @ApiOperation("商品参数信息查询接口")
    public ResultVo getProductParamsById(@PathVariable("pid") String pid){
        return productService.getProductParamsById(pid);
    }

    @GetMapping("/detail-commonts/{pid}")
    @ApiOperation("商品评论信息查询接口")
    @ApiImplicitParams({
                    @ApiImplicitParam(dataType = "int",name="pageNum",value = "当前页码",required = true),
                    @ApiImplicitParam(dataType = "int",name="limit",value = "每页显示条数",required = true)
    })
    public ResultVo getProductCommonts(@PathVariable("pid") String pid,int pageNum,int limit){
        return productCommontsService.listCommontsByProductId(pid,pageNum,limit);
    }

    @GetMapping("/detail-commontscount/{pid}")
    @ApiOperation("商品评价统计查询接口")
    public ResultVo getProductCommonts(@PathVariable("pid") String pid){
        return productCommontsService.getCommentsCountByProductId(pid);
    }

    @GetMapping("/listbycid/{cid}")
    @ApiOperation("根据类别查询商品接口")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "int",name="pageNum",value = "当前页码",required = true),
            @ApiImplicitParam(dataType = "int",name="limit",value = "每页显示条数",required = true)
    })
    public ResultVo getProductsByCategoryId(@PathVariable("cid") int cid, int pageNum, int limit){
        return productService.getProductsByCategoryId(cid, pageNum, limit);
    }

    @GetMapping("/listbrands/{cid}")
    @ApiOperation("根据类别查询商品品牌接口")
    public ResultVo getBrandsByCategoryId(@PathVariable("cid") int cid){
        return productService.listBrands(cid);
    }

    @GetMapping("/listbykeyword")
    @ApiOperation("根据关键字查询商品接口")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "string",name="keyword",value = "搜索关键字",required = true),
            @ApiImplicitParam(dataType = "int",name="pageNum",value = "当前页码",required = true),
            @ApiImplicitParam(dataType = "int",name="limit",value = "每页显示条数",required = true)
    })
    public ResultVo searchProductByKeyword(String keyword, int pageNum, int limit){
        return productService.listProductByKeyword(keyword, pageNum, limit);
    }

    @GetMapping("/listbrands-keyword")
    @ApiOperation("根据关键字查询商品品牌接口")
    @ApiImplicitParam(dataType = "string",name="keyword",value = "搜索关键字",required = true)
    public ResultVo getBrandByKeyword(String keyword){
        return productService.listBrandByKeyword(keyword);
    }



}
