package com.wish.plat.p2p.controller;

import com.wish.plat.p2p.api.dto.SendMsgToImDTO;
import com.wish.plat.p2p.dao.RedisOperate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author ： yangxd
 * @date ：Created in 2019/12/9 14:40
 * @description ：
 * @modified By：
 * @version: v1.0
 */
@RestController
@Slf4j
public class TestCon {

    @Autowired
    RedisOperate redisOperate;
    @GetMapping("/test3")
    public String abcd(@RequestParam("pro") String inStr){
        System.out.println("入参"+inStr);
        redisOperate.delete(inStr);
        log.info("测试1111111111111111111111");
        return "1";
    }

    @GetMapping("/test1")
    public String abc(){
        log.info("测试1111111111111111111111");
        return "1";
    }

    @PostMapping("/test2")
    public String testJson(@RequestBody SendMsgToImDTO sendMsgToImDTO) {
        System.out.println("0000000000000009999");
        System.out.println(sendMsgToImDTO.toString());
        return "000";
    }

}
