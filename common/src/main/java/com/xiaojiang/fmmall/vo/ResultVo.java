package com.xiaojiang.fmmall.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author xiaojiang
 * @Date 2023-03-21 21:32
 * @Description
 **/
@AllArgsConstructor
@NoArgsConstructor
@Data
@ApiModel(value = "ResultVO对象", description = "封装接口返回给前端的数据")
public class ResultVo {
    @ApiModelProperty(value = "响应状态码",dataType = "int")
    private int code;
    @ApiModelProperty(value = "响应提示信息",dataType = "String")
    private String msg;
    @ApiModelProperty(value = "响应数据",dataType = "Object")
    private Object data;
}
