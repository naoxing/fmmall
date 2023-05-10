package com.xiaojiang.fmmall.controller;

import com.github.wxpay.sdk.WXPay;
import com.xiaojiang.fmmall.config.MyPayConfig;
import com.xiaojiang.fmmall.entity.Orders;
import com.xiaojiang.fmmall.service.OrderService;
import com.xiaojiang.fmmall.vo.ResStatus;
import com.xiaojiang.fmmall.vo.ResultVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author xiaojiang
 * @Date 2023-04-12 18:15
 * @Description
 **/
@RestController
@CrossOrigin
@RequestMapping("/order")
@Api(value = "提供订单相关接口",tags = "订单管理")
public class OrderController {
    @Autowired
    private OrderService orderService;

    /**
     * 商户编号：1497984412
     * 商户账号AppID：wx632c8f211f8122c6
     * 商户Key：sbNCm1JnevqI36LrEaxFwcaT0hkGxFnC
     */
    @PostMapping("/add")
    @ApiImplicitParam(dataType = "string",name = "cids",value = "购物车id字符串",required = true)
    public ResultVo listShoppingCart(String cids, @RequestBody Orders order){
        ResultVo resultVo = null;
        try {
            HashMap<String,String> resMap = orderService.addOrder(cids, order);
            if(resMap!=null){
                //设置当前订单信息
                HashMap<String, String> data = new HashMap<>();
                data.put("body",resMap.get("untitled"));    //商品描述
                data.put("out_trade_no",resMap.get("orderId"));  //使用当前用户订单的编号作为当前支付交易的交易号
                data.put("fee_type","CNY");  //支付币种
                data.put("total_fee",/*order.getActualAmount()*100+""*/"1");  //支付金额
                data.put("trade_type","NATIVE");  //支付类种
                data.put("notify_url","http://xing.free.idcfengye.com/pay/callback");  //设置支付完成时回调接口(内网穿透)
 //               data.put("notify_url","http://47.113.202.84:8080/pay/callback");  //设置支付完成时回调接口(已在公网)
                //微信支付:申请支付链接
                WXPay wxPay = new WXPay(new MyPayConfig());
                //发送请求获取相应
                Map<String, String> resp = wxPay.unifiedOrder(data);
                //System.out.println(resp);
                resMap.put("payUrl",resp.get("code_url")); //将支付链接保存进resMap
                resultVo = new ResultVo(ResStatus.OK,"提交订单成功",resMap);
            }else {
                resultVo = new ResultVo(ResStatus.NO,"提交订单失败",null);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            resultVo = new ResultVo(ResStatus.NO,"提交订单失败",null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return resultVo;
    }

    @GetMapping("/status/{oid}")
    public ResultVo getOrderStatus(@PathVariable("oid") String orderId,@RequestHeader("token") String token){
        return orderService.getOrderById(orderId);
    }


    @GetMapping("/list")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "string",name = "userId",value = "用户id",required = true),
            @ApiImplicitParam(dataType = "string",name = "status",value = "订单状态",required = false),
            @ApiImplicitParam(dataType = "int",name = "pageNum",value = "当前页面",required = true),
            @ApiImplicitParam(dataType = "int",name = "limit",value = "每页条数",required = false),
    })
    public ResultVo list(@RequestHeader("token") String token,
                         String userId,String status,int pageNum,int limit){
        return orderService.listOrders(userId, status, pageNum, limit);
    }
}
