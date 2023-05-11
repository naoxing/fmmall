package com.xiaojiang.fmmall.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xiaojiang.fmmall.entity.*;
import com.xiaojiang.fmmall.mapper.ProductImgMapper;
import com.xiaojiang.fmmall.mapper.ProductMapper;
import com.xiaojiang.fmmall.mapper.ProductParamsMapper;
import com.xiaojiang.fmmall.mapper.ProductSkuMapper;
import com.xiaojiang.fmmall.service.ProductService;
import com.xiaojiang.fmmall.utils.PageHelper;
import com.xiaojiang.fmmall.vo.ResStatus;
import com.xiaojiang.fmmall.vo.ResultVo;
import org.apache.lucene.search.TotalHits;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.io.IOException;
import java.util.*;

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
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Override
    public ResultVo listRecommendProducts() {
        return new ResultVo(ResStatus.OK, "success", productMapper.selectRecommendProducts());
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public ResultVo getProductBasicInfo(String productId) {
        //查询redis商品信息
        String productsInfo = (String) stringRedisTemplate.boundHashOps("products").get(productId);
        //(1)如果redis中存在数据
        try {
            if (productsInfo != null) {
                Product product = objectMapper.readValue(productsInfo, Product.class);
                String imgStr = (String) stringRedisTemplate.boundHashOps("productImgs").get(productId);
                String skuStr = (String) stringRedisTemplate.boundHashOps("productSkus").get(productId);
                //创建集合转换的工厂类
                JavaType imgType = objectMapper.getTypeFactory().constructParametricType(ArrayList.class, ProductImg.class);
                JavaType skuType = objectMapper.getTypeFactory().constructParametricType(ArrayList.class, ProductSku.class);

                List<ProductImg> productImgs = objectMapper.readValue(imgStr, imgType);
                List<ProductSku> productSkus = objectMapper.readValue(skuStr, skuType);
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("product", product);
                hashMap.put("productImgs", productImgs);
                hashMap.put("productSkus", productSkus);
                return new ResultVo(ResStatus.OK, "success", hashMap);
            } else {
                //(2)如果redis不存在就查找数据库并放入redis缓存
                //1.商品基本信息
                Example example = new Example(Product.class);
                Example.Criteria criteria = example.createCriteria();
                criteria.andEqualTo("productId", productId);
                criteria.andEqualTo("productStatus", 1); //状态为1表示上架商品
                List<Product> products = productMapper.selectByExample(example);
                if (products.size() < 1) {
                    return new ResultVo(ResStatus.NO, "没有此商品信息", null);
                }
                stringRedisTemplate.boundHashOps("products").put(productId, objectMapper.writeValueAsString(products.get(0)));
                //2.商品图片
                example = new Example(ProductImg.class);
                criteria = example.createCriteria();
                criteria.andEqualTo("itemId", productId);
                List<ProductImg> productImgs = productImgMapper.selectByExample(example);
                stringRedisTemplate.boundHashOps("productImgs").put(productId, objectMapper.writeValueAsString(productImgs));
                //3.商品套餐
                example = new Example(ProductSku.class);
                criteria = example.createCriteria();
                criteria.andEqualTo("productId", productId);
                List<ProductSku> productSkus = productSkuMapper.selectByExample(example);
                stringRedisTemplate.boundHashOps("productSkus").put(productId, objectMapper.writeValueAsString(productSkus));

                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("product", products.get(0));
                hashMap.put("productImgs", productImgs);
                hashMap.put("productSkus", productSkus);
                return new ResultVo(ResStatus.OK, "success", hashMap);
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
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

    /**
     * 模糊查询商品信息
     *
     * @param keyword
     * @param pageNum
     * @param limit
     * @return
     */
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
/*
        //通过es进行查询
        int start = (pageNum - 1) * limit;
        SearchRequest searchRequest = new SearchRequest("fmmallproductsindex");
        //查询条件
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.multiMatchQuery("productName","productSkuName"));
        //分页条件
        searchSourceBuilder.from(start);
        searchSourceBuilder.size(limit);
        //高亮显示
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        HighlightBuilder.Field highlightTitle1 = new HighlightBuilder.Field("productName");
        HighlightBuilder.Field highlightTitle2 = new HighlightBuilder.Field("productSkuName");
        highlightBuilder.field(highlightTitle1);
        highlightBuilder.field(highlightTitle2);
        highlightBuilder.preTags("<label style='color:red'>");
        highlightBuilder.postTags("</label>");
        searchSourceBuilder.highlighter(highlightBuilder);
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResp = null;
        try {
            //执行检索
            searchResp = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //封装查询结果
        SearchHits hits = searchResp.getHits();
        //计算总记录数
        int count = (int) hits.getTotalHits().value;
        //计算总页数
        int pageCount = count % limit == 0 ? count / limit : count / limit + 1;
        Iterator<SearchHit> iterator = hits.iterator();
        List<ProductES> productESList = new ArrayList<>();
         while (iterator.hasNext()){
             SearchHit searchHit = iterator.next();
             try {
                 ProductES productES = objectMapper.readValue(searchHit.getSourceAsString(), ProductES.class);
                 //获取高亮字段
                 Map<String, HighlightField> highlightFields = searchHit.getHighlightFields();
                 HighlightField hignName = highlightFields.get("productName");
                 if(hignName!=null){
                     productES.setProductName(Arrays.toString(hignName.fragments()));
                 }
                 productESList.add(productES);
             } catch (JsonProcessingException e) {
                 throw new RuntimeException(e);
             }
        }
        PageHelper<ProductES> pageHelper = new PageHelper<>(count, pageCount, productESList);
        return new ResultVo(ResStatus.OK, "success", pageHelper);
*/
    }

    @Override
    public ResultVo listBrandByKeyword(String kw) {
        kw = "%" + kw + "%";
        List<String> list = productMapper.selectBrandByKeyword(kw);
        return new ResultVo(ResStatus.OK, "success", list);
    }
}
