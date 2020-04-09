package com.wish.plat.p2p.rpc;

import com.alibaba.fastjson.JSONObject;
import com.wish.plat.p2p.api.ImTokenInterface;
import com.wish.plat.p2p.api.dto.GetTokenDTO;
import com.wish.plat.p2p.constant.PeerToPeerConstant;
import com.wish.plat.p2p.dao.RedisOperate;
import com.wish.plat.p2p.enums.Peer2PeerResp;
import com.wish.plat.p2p.util.UniqueIdGenerator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.HashMap;
import java.util.Map;

import static com.wish.plat.p2p.constant.PeerToPeerConstant.*;

/**
 * @author ： yangxd
 * @date ：Created in 2020/3/4 14:55
 * @description ：获取im token rpc服务
 * @modified By：
 * @version: v1.0
 */
@Service
@Slf4j
public class GetTokenService implements ImTokenInterface {

    @Autowired
    UniqueIdGenerator uniqueIdGenerator;

    @Autowired
    RedisOperate redisOperate;

    @Override
    public JSONObject getToken(GetTokenDTO getTokenDTO) {
        log.info("获取token入参：" + getTokenDTO.toString());
        JSONObject jsonObject = new JSONObject();
        //172.29.12.100
        boolean checkFlag = this.checkParams(getTokenDTO);
        if (checkFlag) {
            jsonObject.put(PeerToPeerConstant.MSG, Peer2PeerResp.PARAMS_ERROR.getMsg());
            jsonObject.put(PeerToPeerConstant.CODE, Peer2PeerResp.PARAMS_ERROR.getCode());
            return jsonObject;
        }

        String IMToken = null;
        String token = null;
        token = redisOperate.getUserToken(getTokenDTO);
        if (!StringUtils.isEmpty(token)) {
            jsonObject.put(PeerToPeerConstant.MSG, PeerToPeerConstant.MSG_SUCCESS);
            jsonObject.put(PeerToPeerConstant.CODE, PeerToPeerConstant.SUCCESS_CODE);
            jsonObject.put(PeerToPeerConstant.TOKEN, token);
            return jsonObject;
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
        return jsonObject;
    }

    @Override
    public Map getToken(Map getTokenMap) {
        Map respMap = new HashMap(4);
        log.info("获取token入参（map请求参数）：" + getTokenMap);
        boolean checkFlag = this.checkMapParams(getTokenMap);
        if(checkFlag){
            respMap.put(PeerToPeerConstant.MSG, Peer2PeerResp.PARAMS_ERROR.getMsg());
            respMap.put(PeerToPeerConstant.CODE, Peer2PeerResp.PARAMS_ERROR.getCode());
            return respMap;
        }
        GetTokenDTO getTokenDTO =new GetTokenDTO();
        getTokenDTO.setUserId((String)getTokenMap.get(USERID));
        getTokenDTO.setChannelCode((String)getTokenMap.get(CHANNELCODE));
        getTokenDTO.setCityCode((String)getTokenMap.get(CITYCODE));

        String IMToken = null;
        String token = null;
        token = redisOperate.getUserToken(getTokenDTO);
        if (!StringUtils.isEmpty(token)) {
            respMap.put(PeerToPeerConstant.MSG, PeerToPeerConstant.MSG_SUCCESS);
            respMap.put(PeerToPeerConstant.CODE, PeerToPeerConstant.SUCCESS_CODE);
            respMap.put(PeerToPeerConstant.TOKEN, token);
            log.info("返回："+respMap);
            return respMap;
        }
        //生成uuid当做token
        token = uniqueIdGenerator.getUUID();
        IMToken = PeerToPeerConstant.TOKEN_PREFIX + token;
        //im识别的柜员号 必须纯数字
        String numUserId = uniqueIdGenerator.generateNum();
        redisOperate.cacheToken(IMToken, numUserId, getTokenDTO);
        redisOperate.cacheUserInfo(getTokenDTO, token, numUserId);

        respMap.put(PeerToPeerConstant.MSG, PeerToPeerConstant.MSG_SUCCESS);
        respMap.put(PeerToPeerConstant.CODE, PeerToPeerConstant.SUCCESS_CODE);
        respMap.put(PeerToPeerConstant.TOKEN, token);
        log.info("新生成返回："+respMap);
        return respMap;
    }

    private boolean checkParams(GetTokenDTO getTokenDTO) {
        if (StringUtils.isEmpty(getTokenDTO.getCityCode()) ||
                StringUtils.isEmpty(getTokenDTO.getChannelCode()) ||
                StringUtils.isEmpty(getTokenDTO.getUserId())
        ) {
            return true;
        } else {
            return false;
        }
    }

    private boolean checkMapParams(Map getTokenMap) {
        if (!getTokenMap.containsKey(CITYCODE) ||
                !getTokenMap.containsKey(USERID) ||
                !getTokenMap.containsKey(CHANNELCODE)
        ) {
            return true;
        } else {
            return false;
        }
    }
}
