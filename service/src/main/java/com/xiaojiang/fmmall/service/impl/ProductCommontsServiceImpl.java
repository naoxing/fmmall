package com.xiaojiang.fmmall.service.impl;

import com.xiaojiang.fmmall.entity.ProductComments;
import com.xiaojiang.fmmall.entity.ProductCommentsVO;
import com.xiaojiang.fmmall.mapper.ProductCommentsMapper;
import com.xiaojiang.fmmall.service.ProductCommontsService;
import com.xiaojiang.fmmall.utils.PageHelper;
import com.xiaojiang.fmmall.vo.ResStatus;
import com.xiaojiang.fmmall.vo.ResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.HashMap;
import java.util.List;

/**
 * @Author xiaojiang
 * @Date 2023-04-05 21:19
 * @Description
 **/
@Service
public class ProductCommontsServiceImpl implements ProductCommontsService {
    @Autowired
    private ProductCommentsMapper productCommentsMapper;
    @Override
    public ResultVo listCommontsByProductId(String productId,int pageNum,int limit) {
        //1.通过商品id查询总记录数
        Example example = new Example(ProductComments.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("productId",productId);
        int count = productCommentsMapper.selectCountByExample(example);
        //2.计算总页数(必须确定每页显示多少条 pageSize)
        int pageCount = count%limit==0? count/limit : count/limit+1;
        //3.查询当前页的数据
        int start = (pageNum-1)*limit;
        List<ProductCommentsVO> productCommentsVOS = productCommentsMapper.selectCommontsByProductId(productId, start, limit);
        return new ResultVo(ResStatus.OK,"success",new PageHelper<ProductCommentsVO>(count,pageCount,productCommentsVOS));
    }

    @Override
    public ResultVo getCommentsCountByProductId(String productId) {
        //1.通过商品id查询总记录数
        Example example = new Example(ProductComments.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("productId",productId);
        int total = productCommentsMapper.selectCountByExample(example);
        //2.查询好评评价数
        criteria.andEqualTo("commType",1);
        int goodTotal = productCommentsMapper.selectCountByExample(example);
        //3.查询中评评价数
        example = new Example(ProductComments.class);
        Example.Criteria criteria1 = example.createCriteria();
        criteria1.andEqualTo("productId",productId);
        criteria1.andEqualTo("commType",0);
        int midTotal = productCommentsMapper.selectCountByExample(example);
        //4.查询差评评价数
        example = new Example(ProductComments.class);
        Example.Criteria criteria2 = example.createCriteria();
        criteria2.andEqualTo("productId",productId);
        criteria2.andEqualTo("commType",-1);
        int badTotal = productCommentsMapper.selectCountByExample(example);

        //5.计算好评率
        double percent = 0;
        try {
            String s = (Double.parseDouble(goodTotal + "") / Double.parseDouble(total + "")) * 100 + "";
            try {
                percent = Double.parseDouble(s.substring(0,s.indexOf(".")+3));
            } catch (NumberFormatException e) {
                System.out.println("没问题,继续跑");
            }
        } catch (NumberFormatException e) {
            throw new RuntimeException(e);
        }

        HashMap<String, Object> map = new HashMap<>();
        map.put("total",total);
        map.put("goodTotal",goodTotal);
        map.put("midTotal",midTotal);
        map.put("badTotal",badTotal);
        map.put("percent",percent);

        return new ResultVo(ResStatus.OK, "success", map);
    }
}
