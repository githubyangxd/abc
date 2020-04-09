package com.wish.plat.p2p.controller;

import com.alibaba.fastjson.JSONObject;
import com.wish.plat.p2p.api.dto.GetTokenDTO;
import com.wish.plat.p2p.constant.PeerToPeerConstant;
import com.wish.plat.p2p.dao.RedisOperate;
import com.wish.plat.p2p.util.UniqueIdGenerator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ： yangxd
 * @date ：Created in 2019/12/12 10:26
 * @description ：柜员token管理控制层
 * @modified By：
 * @version: v1.0
 */
@RestController
@RequestMapping("/token")
@Slf4j
public class TokenController {
    @Autowired
    UniqueIdGenerator uniqueIdGenerator;

    @Autowired
    RedisOperate redisOperate;

    @RequestMapping("/getToken.json")
    public String getToken(@Validated @RequestBody GetTokenDTO getTokenDTO) {
        log.info("获取token入参：" + getTokenDTO.toString());
        //172.29.12.100

        JSONObject jsonObject = new JSONObject();
        String IMToken = null;
        String token = null;
        token = redisOperate.getUserToken(getTokenDTO);
        if (!StringUtils.isEmpty(token)) {
            jsonObject.put(PeerToPeerConstant.MSG, PeerToPeerConstant.MSG_SUCCESS);
            jsonObject.put(PeerToPeerConstant.CODE, PeerToPeerConstant.SUCCESS_CODE);
            jsonObject.put(PeerToPeerConstant.TOKEN, token);
            return jsonObject.toJSONString();
        }
        //生成uuid当做token
        token = uniqueIdGenerator.getUUID();
        IMToken = PeerToPeerConstant.TOKEN_PREFIX + token;
        //im识别的柜员号 必须纯数字
        String numUserId = uniqueIdGenerator.generateNum();
        redisOperate.cacheToken(IMToken, numUserId, getTokenDTO);
        redisOperate.cacheUserInfo(getTokenDTO, token, numUserId);

        jsonObject.put(PeerToPeerConstant.MSG, PeerToPeerConstant.MSG_SUCCESS);
        jsonObject.put(PeerToPeerConstant.CODE, PeerToPeerConstant.SUCCESS_CODE);
        jsonObject.put(PeerToPeerConstant.TOKEN, token);
        return jsonObject.toJSONString();
    }


}
