package com.wish.plat.p2p.rpc;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.wish.plat.p2p.api.SendMsgToImInterface;
import com.wish.plat.p2p.api.dto.SendMsgToImDTO;
import com.wish.plat.p2p.constant.PeerToPeerConstant;
import com.wish.plat.p2p.dao.PushMsgToIMDao;
import com.wish.plat.p2p.dao.RedisOperate;
import com.wish.plat.p2p.enums.Peer2PeerResp;
import com.wish.plat.p2p.service.ToIMService;
import com.wish.plat.p2p.util.UniqueIdGenerator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static com.wish.plat.p2p.constant.PeerToPeerConstant.*;

/**
 * @author ： yangxd
 * @date ：Created in 2019/12/16 10:48
 * @description ：接收向im推送消息 控制层
 * @modified By：
 * @version: v1.0
 */
@Slf4j
@Service("sendMsgToImService")
public class ReceiveMsgService implements SendMsgToImInterface {
    @Value("${self.url.im}")
    private String imUrl;

    @Autowired
    PushMsgToIMDao pushMsgToIMDao;

    @Autowired
    UniqueIdGenerator uniqueIdGenerator;

    @Autowired
    RedisOperate redisOperate;

    @Autowired
    ToIMService toIMService;

    private final static String P_TYPE = "type";
    private final static String P_MSGID = "msgId";
    private final static String P_TITLE = "title";
    private final static String P_CONTENT = "content";
    private final static String P_LINK = "link";
    private final static String P_FROM = "from";
    private final static String P_RECEIVETIME = "receiveTime";
    private final static String P_RECEIVERID = "receiverId";
    private final static String P_PRIORITY = "priority";
    private final static String P_FILEID = "fileId";
    private final static String P_LISTID = "listId";
    private final static String P_BUSINESSSEQ = "businessSeq";
    private final static String P_REQSEQ = "reqSeq";
    private final static String P_PROGRESS = "progress";

    @Override
    public String sendMsg(SendMsgToImDTO sendMsgToImDTO) {
//        return this.sendMsgToIm(sendMsgToImDTO.toString());
//    }
//
//    @Override
//    public String sendMsgToIm(String msg) {
        String msg = sendMsgToImDTO.toString();
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
        String citCode = storeJsonObject.getString(PeerToPeerConstant.CITY_CODE2);
        String channelCode = storeJsonObject.getString(CHANNEL_CODE);
        //接收者从缓存中获取IM识别的纯数字柜员号
        String receiverNumUserId = redisOperate.getNumUserId(userId, citCode, channelCode);

        if (StringUtils.isEmpty(receiverNumUserId)) {
            returnJson.put(PeerToPeerConstant.MSG, Peer2PeerResp.UN_FIND_USER_ID.getMsg());
            returnJson.put(PeerToPeerConstant.CODE, Peer2PeerResp.UN_FIND_USER_ID.getCode());
            log.info(returnJson.toJSONString());
            return returnJson.toJSONString();
        }
        //发送者IM识别的纯数字柜员号
        // TODO: 2019/12/18 后期点对点柜员通讯时候拓展
        String senderNumUserId = PeerToPeerConstant.BUSINESS_SYSTEM_IM_DEFAULT_USER_ID;

        JSONObject newStoreJson = new JSONObject();
        for (String key : storeJsonObject.keySet()) {
            if (!"null".equals(storeJsonObject.getString(key))) {
//                newStoreJson.put(key,storeJsonObject.getString(key));
                newStoreJson.put(key, storeJsonObject.get(key));
            } else {
                newStoreJson.put(key, "");
            }
        }
        //消息记录 存MongoDB
        pushMsgToIMDao.insertJson(newStoreJson);
        JSONObject sendJsonObject = this.sendJsonDeal(msg, msgId);
        String url = imUrl;
        String sendResult = toIMService.sendMsgToIM(url, citCode, senderNumUserId, receiverNumUserId, sendJsonObject);

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
                if (StringUtils.isEmpty(value)) {
                    value = "";
                }
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

        JSONObject newSendJson = new JSONObject();
        for (String key : sendJsonObject.keySet()) {
            if (!"null".equals(sendJsonObject.getString(key))) {
//                newStoreJson.put(key,storeJsonObject.getString(key));
                newSendJson.put(key, sendJsonObject.get(key));
            } else {
                newSendJson.put(key, "");
            }
        }

        return newSendJson;
//        return sendJsonObject;
    }

    /**
     * 校验必传参数
     *
     * @param msg
     * @return
     */
    private boolean checkParams(String msg) {
        Map map = JSONObject.parseObject(msg, Map.class, Feature.OrderedField);
        if (!map.containsKey(CONTENT) ||
                !map.containsKey(USER_ID2) ||
                !map.containsKey(CITY_CODE2) ||
                !map.containsKey(CHANNEL_CODE)
        ) {
            return true;
        }
        if ("null".equals(map.get(USER_ID2)) ||
                "null".equals(map.get(CHANNEL_CODE)) ||
                "null".equals(map.get(CITY_CODE2)) ||
                "null".equals(map.get(CONTENT))
        ) {
            return true;
        }
        Map contentMap = (Map) map.get(PeerToPeerConstant.CONTENT);
        if (!contentMap.containsKey(P_TYPE) || !contentMap.containsKey(P_TITLE) || !contentMap.containsKey(P_CONTENT)
                || !contentMap.containsKey(P_LINK) || !contentMap.containsKey(P_FROM) || !contentMap.containsKey(P_RECEIVETIME) || !contentMap.containsKey(P_RECEIVERID)
                || !contentMap.containsKey(P_PRIORITY) || !contentMap.containsKey(P_FILEID) || !contentMap.containsKey(P_LISTID) || !contentMap.containsKey(P_BUSINESSSEQ)
                || !contentMap.containsKey(P_REQSEQ) || !contentMap.containsKey(P_PROGRESS)) {
            return true;
        } else {
            return false;
        }
    }

}
