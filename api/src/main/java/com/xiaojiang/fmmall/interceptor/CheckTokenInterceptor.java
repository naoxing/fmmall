package com.xiaojiang.fmmall.interceptor;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.xiaojiang.fmmall.vo.ResStatus;
import com.xiaojiang.fmmall.vo.ResultVo;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;

/**
 * @Author xiaojiang
 * @Date 2023-04-03 10:53
 * @Description 拦截器拦截加密的资源前的验证token操作
 **/
@Component
public class CheckTokenInterceptor implements HandlerInterceptor {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String method = request.getMethod();
        //放行OPTIONS请求
        if ("OPTIONS".equalsIgnoreCase(method)) {
            return true;
        }

        String token = request.getHeader("token");
        if (token == null) {
            //提示请先登录
            ResultVo resultVo = new ResultVo(ResStatus.LOGIN_FAIL_NOT, "请先登录!", null);
            doRespose(response, resultVo);
        } else {
            String tokenStr = stringRedisTemplate.boundValueOps(token).get();
            if(tokenStr==null){ //token不存在或者过期
                //提示请先登录
                ResultVo resultVo = new ResultVo(ResStatus.LOGIN_FAIL_NOT, "请先登录!", null);
                doRespose(response, resultVo);
            }else {
                //token存在,就续命30min
                stringRedisTemplate.boundValueOps(token).expire(30, TimeUnit.MINUTES);
                return true;
            }
/*  过去式写法
            //验证token
            JwtParser parser = Jwts.parser();
            parser.setSigningKey("jiangjiang520");  //解析token的SigningKey必须和生成的token的设置密码一致
            try {
                Jws<Claims> claimsJws = parser.parseClaimsJws(token);  //如果token正确,正常执行
//                Claims body = claimsJws.getBody();   //获取token的用户信息
//                String subject = body.getSubject();  //获取生成token设置的subject
//                String v1 = body.get("key1", String.class);  //获取生成token的Claims的map中的值
                return true;
            }catch (Exception e){
                ResultVo resultVo = new ResultVo(ResStatus.LOGIN_FAIL_OVERDUE, "登录过期,请重新登录", null);
                doRespose(response,resultVo);
                return false;

            }*/
        }
        return false;
    }

    //JSON写到前端提示
    private void doRespose(HttpServletResponse response, ResultVo resultVo) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        PrintWriter out = response.getWriter();
        String s = new ObjectMapper().writeValueAsString(resultVo);
        out.print(s);
        out.flush();
        out.close();
    }
}
