package com.wish.plat.p2p.exception;

import com.alibaba.fastjson.JSONObject;
import com.wish.plat.p2p.constant.PeerToPeerConstant;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author ： yangxd
 * @date ：Created in 2019/12/12 14:41
 * @description ：统一异常处理
 * @modified By：
 * @version: v1.0
 */
@RestControllerAdvice
public class ExceptionHandler {

    /**
     * 处理不带任何注解的参数绑定校验异常
     * @param e
     * @return
     */
    @org.springframework.web.bind.annotation.ExceptionHandler(BindException.class)
    public String handleBingException(BindException e) {
        String errorMsg = e.getBindingResult().getAllErrors()
                .stream()
                .map(objectError -> ((FieldError)objectError).getField() + ((FieldError)objectError).getDefaultMessage())
                .collect(Collectors.joining(","));
        //"errorMsg": "name不能为空,age最小不能小于18"
//        return new ResponseData<>().fail(errorMsg);
        return errorMsg;
    }

    /**
     * 处理 @RequestBody参数校验异常
     * @param e
     * @return
     */
    @org.springframework.web.bind.annotation.ExceptionHandler(MethodArgumentNotValidException.class)
    public String handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String errorMsg = e.getBindingResult().getAllErrors()
                .stream()
                .map(objectError -> ((FieldError)objectError).getField() + ((FieldError)objectError).getDefaultMessage())
                .collect(Collectors.joining(","));
        //"errorMsg": "name不能为空,age最小不能小于18"
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(PeerToPeerConstant.MSG,errorMsg);
        jsonObject.put(PeerToPeerConstant.CODE, PeerToPeerConstant.ERROR_CODE);

        return jsonObject.toJSONString();
    }

    /**
     * Description : 全局异常捕捉处理
     * @param ex
     * @return
     */
    @org.springframework.web.bind.annotation.ExceptionHandler(com.wish.plat.p2p.exception.MyException.class)
    public Map errorHandler(MyException ex) {
        Map map = new HashMap();
        map.put(PeerToPeerConstant.CODE, ex.getCode());
        map.put(PeerToPeerConstant.MSG, ex.getMessage());
        JSONObject jsonObject= new JSONObject();
        jsonObject.put(PeerToPeerConstant.SUB_MSG,"");
        jsonObject.put(PeerToPeerConstant.SUB_CODE,"");
        jsonObject.put(PeerToPeerConstant.SUB_SOLUTION,"");
        map.put(PeerToPeerConstant.DATA, jsonObject);
        return map;
    }

}
