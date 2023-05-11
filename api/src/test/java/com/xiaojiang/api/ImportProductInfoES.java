package com.xiaojiang.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xiaojiang.fmmall.ApiApplication;
import com.xiaojiang.fmmall.entity.ProductES;
import com.xiaojiang.fmmall.entity.ProductSku;
import com.xiaojiang.fmmall.entity.ProductVO;
import com.xiaojiang.fmmall.mapper.ProductMapper;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.awt.print.Book;
import java.io.IOException;
import java.util.List;

/**
 * @Author xiaojiang
 * @Date 2023-05-11 11:02
 * @Description
 **/
@SpringBootTest(classes = ApiApplication.class,webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ImportProductInfoES {
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private RestHighLevelClient restHighLevelClient;
    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 添加索引
     * @throws IOException
     */
    @Test
    public void testCreateIndex() throws IOException {
        CreateIndexRequest createIndexRequest = new CreateIndexRequest("fmmallproductsindex");
        CreateIndexResponse createIndexResponse = restHighLevelClient.indices().create(createIndexRequest, RequestOptions.DEFAULT);
        System.out.println(createIndexResponse.isAcknowledged());
    }
    /**
     * 删除索引
     */
    @Test
    public void testDeleteIndex() throws IOException {
        DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest("fmmallproductsindex");
        AcknowledgedResponse deleteIndexRes = restHighLevelClient.indices().delete(deleteIndexRequest, RequestOptions.DEFAULT);
        System.out.println(deleteIndexRes);
    }
    /**
     * 将数据存⼊es
     */
    @Test
    public void testCreateDocument() throws IOException {
        //查询所有商品信息
        List<ProductVO> productVOS = productMapper.selectProducts();
        for (ProductVO productVO : productVOS) {
            String productId = productVO.getProductId();
            String productName = productVO.getProductName();
            Integer soldNum = productVO.getSoldNum();
            List<ProductSku> skus = productVO.getSkus();
            String skuName = skus.size()==0? "":skus.get(0).getSkuName();
            String skuImg = skus.size()==0? "":skus.get(0).getSkuImg();
            Integer skuPrice = skus.size()==0? 0:skus.get(0).getSellPrice();
            ProductES productES = new ProductES(productId, productName, soldNum, skuImg, skuName, skuPrice);
            String jsonStr = objectMapper.writeValueAsString(productES);
            IndexRequest request = new IndexRequest("fmmallproductsindex");
            request.id(productId);
            request.source(jsonStr, XContentType.JSON);
            IndexResponse indexResponse = restHighLevelClient.index(request, RequestOptions.DEFAULT);
        }
    }
}
