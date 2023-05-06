package com.xiaojiang.fmmall.service.utils;

import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @Author xiaojiang
 * @Date 2023-05-03 09:42
 * @Description 日志打印
 **/
@Component
public class LogUtil {
    private static String path = "d:/aaa.log";
    private static FileOutputStream fas;

    static {
        try {
            fas = new FileOutputStream(path);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void log(String msg){
        try {
            fas.write(msg.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
