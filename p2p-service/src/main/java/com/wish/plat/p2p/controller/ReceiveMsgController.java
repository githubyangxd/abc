package com.wish.plat.p2p.controller;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.wish.plat.p2p.constant.PeerToPeerConstant;
import com.wish.plat.p2p.dao.PushMsgToIMDao;
import com.wish.plat.p2p.dao.RedisOperate;
import com.wish.plat.p2p.enums.Peer2PeerResp;
import com.wish.plat.p2p.service.ToIMService;
import com.wish.plat.p2p.util.UniqueIdGenerator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ： yangxd
 * @date ：Created in 2019/12/16 10:48
 * @description ：接收向im推送消息 控制层
 * @modified By：
 * @version: v1.0
 */
@RestController
@RequestMapping("/sendMsg")
@Slf4j
public class ReceiveMsgController {

    @Autowired
    PushMsgToIMDao pushMsgToIMDao;

    @Autowired
    UniqueIdGenerator uniqueIdGenerator;

    @Autowired
    RedisOperate redisOperate;

    @Autowired
    ToIMService toIMService;

    @PostMapping("/toIM.json")
    public String sendMsgToIM(@RequestBody String msg) {
//    public String sendMsgToIM(@RequestBody SendMsgDTO sendMsgDTO) {
//        System.err.println(sendMsgDTO);
//        System.err.println(sendMsgDTO.toString());
//        String msg=sendMsgDTO.toString();
        log.info("接收消息：" + msg);
        JSONObject returnJson = new JSONObject();
        //校验参数
        boolean checkFlag = this.checkParams(msg);
        if (checkFlag) {
            returnJson.put(PeerToPeerConstant.MSG, Peer2PeerResp.PARAMS_ERROR.getMsg());
            returnJson.put(PeerToPeerConstant.CODE, Peer2PeerResp.PARAMS_ERROR.getCode());
            log.info(returnJson.toJSONString());
            return returnJson.toJSONString();
        }
        //生成消息id
        String msgId = uniqueIdGenerator.generateKey();
        JSONObject storeJsonObject = this.storeJsonDeal(msg, msgId);

        String userId = storeJsonObject.getString(PeerToPeerConstant.USER_ID2);
        String cityCode = storeJsonObject.getString(PeerToPeerConstant.CITY_CODE2);
        String channelCode = storeJsonObject.getString(PeerToPeerConstant.CHANNEL_CODE);
        //接收者从缓存中获取IM识别的纯数字柜员号
        String receiverNumUserId = redisOperate.getNumUserId(userId, cityCode, channelCode);

        if (StringUtils.isEmpty(receiverNumUserId)) {
            returnJson.put(PeerToPeerConstant.MSG, Peer2PeerResp.UN_FIND_USER_ID.getMsg());
            returnJson.put(PeerToPeerConstant.CODE, Peer2PeerResp.UN_FIND_USER_ID.getCode());
            log.info(returnJson.toJSONString());
            return returnJson.toJSONString();
        }
        //发送者IM识别的纯数字柜员号
        // TODO: 2019/12/18 后期点对点柜员通讯时候拓展
        String senderNumUserId = PeerToPeerConstant.BUSINESS_SYSTEM_IM_DEFAULT_USER_ID;

        //消息记录 存MongoDB
        pushMsgToIMDao.insertJson(storeJsonObject);
        JSONObject sendJsonObject = this.sendJsonDeal(msg, msgId);
        // TODO: 2019/12/17 im服务器url管理 先写死
        String url = "http://172.29.12.100/post_im_message";
        String sendResult = toIMService.sendMsgToIM(url, cityCode, senderNumUserId, receiverNumUserId, sendJsonObject);

        return sendResult;
    }

    /**
     * 存储MongoDB消息信息转换
     *
     * @param receiveMsg
     * @param msgId
     * @return
     */
    private JSONObject storeJsonDeal(String receiveMsg, String msgId) {
        Map<String, Object> map = new HashMap();
        map = JSONObject.parseObject(receiveMsg, Map.class, Feature.OrderedField);
        //获取接收报文中content内容，遍历，与外层报文整合到一层，便于存储
        JSONObject storeJsonObject = (JSONObject) map.get(PeerToPeerConstant.CONTENT);
        //存储信息添加msg_id
        storeJsonObject.put(PeerToPeerConstant.MSG_ID, msgId);
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (!PeerToPeerConstant.CONTENT.equals(entry.getKey())) {
                String key = entry.getKey();
                String value = String.valueOf(entry.getValue());
                storeJsonObject.put(key, value);
            }
        }
        return storeJsonObject;
    }

    /**
     * 发送给IM信息转换
     *
     * @param receiveMsg
     * @param msgId
     * @return
     */
    private JSONObject sendJsonDeal(String receiveMsg, String msgId) {
        Map<String, Object> map = new HashMap();
        map = JSONObject.parseObject(receiveMsg, Map.class, Feature.OrderedField);
        JSONObject sendJsonObject = (JSONObject) map.get(PeerToPeerConstant.CONTENT);
        sendJsonObject.put(PeerToPeerConstant.MSG_ID, msgId);

        return sendJsonObject;
    }

    private boolean checkParams(String msg) {
        Map map = JSONObject.parseObject(msg, Map.class, Feature.OrderedField);
        if (!map.containsKey(PeerToPeerConstant.CONTENT) ||
                !map.containsKey(PeerToPeerConstant.USER_ID2) ||
                !map.containsKey(PeerToPeerConstant.CITY_CODE2) ||
                !map.containsKey(PeerToPeerConstant.CHANNEL_CODE)
        )
    /*    if (StringUtils.isEmpty((String) paramsJson.get(CONTENT)) ||
                StringUtils.isEmpty((String) paramsJson.get(USER_ID2)) ||
                StringUtils.isEmpty((String) paramsJson.get(APP_ID2)) ||
                StringUtils.isEmpty((String) paramsJson.get(CHANNEL_CODE))
        )*/ {
            return true;
        }
        return false;
    }
}
