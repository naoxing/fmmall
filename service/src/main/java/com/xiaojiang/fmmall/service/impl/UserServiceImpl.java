package com.xiaojiang.fmmall.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xiaojiang.fmmall.entity.Users;
import com.xiaojiang.fmmall.mapper.UsersMapper;
import com.xiaojiang.fmmall.service.UserService;
import com.xiaojiang.fmmall.utils.MD5Utils;
import com.xiaojiang.fmmall.vo.ResStatus;
import com.xiaojiang.fmmall.vo.ResultVo;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Author xiaojiang
 * @Date 2023-03-23 20:52
 * @Description
 **/
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UsersMapper usersMapper;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    @Transactional
    public ResultVo  userResgit(String name, String pwd) {
        synchronized (this) { //spring注册bean默认单例
            //1.根据用户名查询用户
            Example example = new Example(Users.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("username",name);
            List<Users> users = usersMapper.selectByExample(example);
            //2.没有注册就进行保存操作
            if(users.size()==0){
                Users user = new Users();
                String md5Pwd = MD5Utils.md5(pwd);
                user.setUsername(name);
                user.setPassword(md5Pwd);
                user.setUserImg("img/default.png");
                user.setUserModtime(new Date());
                user.setUserRegtime(new Date());
                int result = usersMapper.insert(user);
                if(result>0){
                    return new ResultVo(ResStatus.OK,"注册成功",null);
                }else {
                    return new ResultVo(ResStatus.NO,"注册失败",null);
                }
            }else {
                return new ResultVo(ResStatus.NO,"用户名已经被注册!",null);
            }
        }
    }

    @Override
    public ResultVo checkLogin(String name, String pwd) {
        //1.通过用户名查找用户
        //1.根据用户名查询用户
        Example example = new Example(Users.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("username",name);
        Users user = usersMapper.selectByExample(example).get(0);

        //2.如果用户存在,验证密码是否正确;不存在,失败
        if(user==null){
            return new ResultVo(ResStatus.NO,"登录失败,用户名不存在",null);
        }else {
            String md5Pwd = MD5Utils.md5(pwd);
            if(md5Pwd.equals(user.getPassword())){
                //如果登录成功,则需要验证生成令牌token(token就是按照特定规则生成的字符串)
                HashMap<String, Object> map = new HashMap<>();
                map.put("key1","value1");
                //使用jwt规则生成token
                JwtBuilder builder = Jwts.builder();
                String token = builder.setSubject(name)     //主题,就是token中携带的数据
                        .setIssuedAt(new Date())            //设置token生成时间
                        .setId(user.getUserId() + "")       //设置用户id为token的id
                        .setClaims(map)                 //map存放用户权限信息
                        //.setExpiration(new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000))  //设置token过期时间
                        .signWith(SignatureAlgorithm.HS256, "jiangjiang520")   //设置加密方式和加密密码
                        .compact();
                //当用户登陆成功,将token信息写入到redis中,设置过期时间为30min
                try {
                    stringRedisTemplate.boundValueOps(token).set(objectMapper.writeValueAsString(user),30, TimeUnit.MINUTES);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
                return new ResultVo(ResStatus.OK,token,user);
            }else {
                return new ResultVo(ResStatus.NO,"登录失败,密码不匹配!",null);
            }

        }
    }
}
