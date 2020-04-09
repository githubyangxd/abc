package com.wish.plat.p2p.service;

import com.alibaba.fastjson.JSONObject;
import com.wish.plat.p2p.enums.Peer2PeerResp;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import static com.wish.plat.p2p.constant.PeerToPeerConstant.*;


/**
 * @author ： yangxd
 * @date ：Created in 2019/12/17 18:11
 * @description ：向im推送消息
 * @modified By：
 * @version: v1.0
 */
@Service
@Slf4j
public class ToIMService {
    @Autowired
    RestTemplate restTemplate;

    /**
     * 向IM发送消息
     *
     * @param url
     * @param cityCode
     * @param senderNumUserId
     * @param receiverNumUserId
     * @param sendJsonObject
     * @return
     */
    public String sendMsgToIM(String url, String cityCode, String senderNumUserId, String receiverNumUserId, JSONObject sendJsonObject) {
        JSONObject jsonObject = new JSONObject();
        //预留SENDER
        jsonObject.put(SENDER, senderNumUserId);
        jsonObject.put(RECEIVER, Long.parseLong(receiverNumUserId));
        jsonObject.put(CONTENT, sendJsonObject.toJSONString());
        String realUrl = url + "?appid=" + cityCode + "&sender=" + senderNumUserId + "&class=" + PEER;
        log.info("发送json串：" + jsonObject);
        String sendResult = this.client(realUrl, HttpMethod.POST, jsonObject);
        JSONObject resultJson = new JSONObject();

        if (!StringUtils.isEmpty(sendResult)) {
            resultJson.put(MSG, Peer2PeerResp.SEND_ERROR.getMsg());
            resultJson.put(CODE, Peer2PeerResp.SEND_ERROR.getCode());
            log.info(resultJson.toJSONString());
            return resultJson.toJSONString();
        }
        resultJson.put(MSG, MSG_SUCCESS);
        resultJson.put(CODE, SUCCESS_CODE);
        return resultJson.toJSONString();
    }

    /**
     * 调用第三方接口
     *
     * @param url    请求渠道url
     * @param method 请求方式
     * @param params 请求参数
     * @return
     */
    private String client(String url, HttpMethod method, JSONObject params) {
        String result = null;
        HttpHeaders headers = new HttpHeaders();
        //请勿轻易改变此提交方式，大部分的情况下，提交方式都是表单提交
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);

        HttpEntity<JSONObject> requestEntity = new HttpEntity<JSONObject>(params, headers);
        log.info("向im推送参数url:" + url + " requestEntity:" + requestEntity);
        //  执行HTTP请求
        ResponseEntity<String> response = restTemplate.exchange(url, method, requestEntity, String.class);

        log.info("返回结果response：" + response);
        if (response != null) {
            result = response.getBody();
        }
        log.info("返回结果result：" + result);
        return result;
    }
}
