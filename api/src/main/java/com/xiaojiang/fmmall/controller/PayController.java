package com.xiaojiang.fmmall.controller;

import com.github.wxpay.sdk.WXPayUtil;
import com.xiaojiang.fmmall.entity.OrderItem;
import com.xiaojiang.fmmall.entity.Product;
import com.xiaojiang.fmmall.entity.ShoppingCartVO;
import com.xiaojiang.fmmall.mapper.OrderItemMapper;
import com.xiaojiang.fmmall.mapper.ProductMapper;
import com.xiaojiang.fmmall.service.OrderService;
import com.xiaojiang.fmmall.websocket.WebSocketServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author xiaojiang
 * @Date 2023-04-13 20:45
 * @Description
 **/
@RestController
@RequestMapping("/pay")
@CrossOrigin
public class PayController {
    @Autowired
    private OrderService orderService;
    /**x
     * 回调接口:当用户支付成功之后,微信支付平台就会请求这个接口,将支付状态的数据传递过来*
     */
    @RequestMapping("/callback")
    //@ResponseBody
    public String paySuccess(HttpServletRequest request) throws Exception {
        System.out.println("--------------callback");
        //1.接收微信支付平台传递的参数
        ServletInputStream is = request.getInputStream();
        byte[] bs = new byte[1024];
        int len = -1;
        StringBuilder builder = new StringBuilder();
        while ((len = is.read(bs))!=-1){
            builder.append(new String(bs,0,len));
        }
        String msg = builder.toString();
        System.out.println(msg);
        //使用帮助类将xml接口字符串转换为map
        Map<String, String> map = WXPayUtil.xmlToMap(msg);
        if("success".equalsIgnoreCase(map.get("result_code"))){
            //支付成功
            //2.修改订单状况为待发货/已付款
            String orderId = map.get("out_trade_no");
            int res = orderService.updateOrderStatus(orderId, "2");
            //3.通过websocket连接,向前端推送信息 (WebSocketServer自定义配置类)
            WebSocketServer.sendMsg(orderId,"1");
/*
            //4.添加已售数量
            Example example = new Example(OrderItem.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("orderId",orderId);
            OrderItem orderItem = orderItemMapper.selectByExample(example).get(0);
            Product product = productMapper.selectByPrimaryKey(orderItem.getProductId());
            int newStock = product.getSoldNum() + orderItem.getBuyCounts();
            product = new Product();
            product.setProductId(orderItem.getProductId());
            product.setSoldNum(newStock);
            productMapper.updateByPrimaryKeySelective(product);

*/
            if(res>0){
                //5.响应微信支付平台
                HashMap<String, String> resp = new HashMap<>();
                resp.put("return_code","success");
                resp.put("return_msg","OK");
                resp.put("appid",map.get("appid"));
                resp.put("result_code","success");
                return WXPayUtil.mapToXml(resp);
            }else {
                return null;
            }
        }
        return null;
    }
}
