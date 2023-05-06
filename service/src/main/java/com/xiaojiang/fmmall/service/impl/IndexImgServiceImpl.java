package com.xiaojiang.fmmall.service.impl;

import com.xiaojiang.fmmall.entity.IndexImg;
import com.xiaojiang.fmmall.mapper.IndexImgMapper;
import com.xiaojiang.fmmall.service.IndexImgService;
import com.xiaojiang.fmmall.vo.ResStatus;
import com.xiaojiang.fmmall.vo.ResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author xiaojiang
 * @Date 2023-04-03 15:17
 * @Description
 **/
@Service
public class IndexImgServiceImpl implements IndexImgService {
    @Autowired
    private IndexImgMapper indexImgMapper;

    @Override
    public ResultVo listIndexImgs() {
        List<IndexImg> indexImgs = indexImgMapper.listIndexImgs();
        if(indexImgs.size()==0){
            return new ResultVo(ResStatus.NO,"fail",null);
        }
        return new ResultVo(ResStatus.OK,"success",indexImgs);
    }
}
