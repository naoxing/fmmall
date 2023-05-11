package com.xiaojiang.fmmall.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * @Author xiaojiang
 * @Date 2023-05-11 13:02
 * @Description
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductES {
    private String productId;
    private String productName;
    private Integer soldNum;
    private String productImg;
    private String productSkuName;
    private Integer productSkuPrice;
}
