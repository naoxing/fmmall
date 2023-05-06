package com.xiaojiang.fmmall;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import tk.mybatis.spring.annotation.MapperScan; //换成了tk.mybatis下的包

@SpringBootApplication
//@MapperScan("com.xiaojiang.fmmall.mapper")
@MapperScan("com.xiaojiang.fmmall.mapper")
@EnableScheduling  //开启定时任务
public class ApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiApplication.class, args);
    }

}
