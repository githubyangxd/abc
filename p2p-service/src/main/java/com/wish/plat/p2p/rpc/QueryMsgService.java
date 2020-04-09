package com.wish.plat.p2p.rpc;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonObject;
import com.google.gson.annotations.JsonAdapter;
import com.wish.plat.p2p.api.QueryMsgInterface;
import com.wish.plat.p2p.api.dto.GetTokenDTO;
import com.wish.plat.p2p.api.dto.QueryPushMsgDTO;
import com.wish.plat.p2p.constant.PeerToPeerConstant;
import com.wish.plat.p2p.enums.Peer2PeerResp;
import com.wish.plat.p2p.service.MsgQueryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static com.wish.plat.p2p.enums.Peer2PeerResp.PARAMS_ERROR;

/**
 * @author ： yangxd
 * @date ：Created in 2020/3/5 13:22
 * @description ：查询柜员历史消息rpc服务
 * @modified By：
 * @version: v1.0
 */
@Service
@Slf4j
public class QueryMsgService implements QueryMsgInterface {

    @Autowired
    MsgQueryService msgQueryService;


    @Override
    public String queryTellerMsg(QueryPushMsgDTO queryPushMsgDTO) {
        log.info("查询接收参数：" + queryPushMsgDTO);
        JSONObject resultJson = new JSONObject();

        JSONObject dataJson = new JSONObject();
        //data层-0层
        JSONObject dataJson0 = new JSONObject();
        //common层-1层
        JSONObject commonJson1 = new JSONObject();
        //plat层-1层
        JSONObject platJson1 = new JSONObject();
        //gmp层（plat子节点）-2层
        JSONObject gmpJson2 = new JSONObject();
        boolean chekFlag = this.checkParams(queryPushMsgDTO);
        if (chekFlag) {
            resultJson.put(PeerToPeerConstant.CODE, PARAMS_ERROR.getCode());
            resultJson.put(PeerToPeerConstant.MSG, PARAMS_ERROR.getMsg());
            return resultJson.toJSONString();
        }

        commonJson1.put("retimestamp", queryPushMsgDTO.getRetimestamp());
        commonJson1.put("channelseq", queryPushMsgDTO.getChannelseq());
        commonJson1.put("businessSeq", queryPushMsgDTO.getBusinessSeq());
        commonJson1.put("eventCode", queryPushMsgDTO.getEventCode());

        gmpJson2.put("type", queryPushMsgDTO.getType());
        gmpJson2.put("priority", queryPushMsgDTO.getPriority());
        gmpJson2.put("startTime", queryPushMsgDTO.getStartTime());
        gmpJson2.put("endTime", queryPushMsgDTO.getEndTime());
        gmpJson2.put("receiverId", queryPushMsgDTO.getReceiverId());
        gmpJson2.put("msgContent", queryPushMsgDTO.getMsgContent());
        gmpJson2.put("pageSize", queryPushMsgDTO.getPageSize());
        gmpJson2.put("currentPage", queryPushMsgDTO.getCurrentPage());

        platJson1.put("gmp", gmpJson2);
        dataJson0.put("common", commonJson1);
        dataJson0.put("plat", platJson1);

        dataJson.put("data", dataJson0);
        return this.queryTellerMsg(dataJson.toString());

    }

    @Override
    public Map queryTellerMsg(Map selectMap) {
        String selectMsg = (String) selectMap.get("selectMsg");
        log.info("查询柜员消息请求报文：" + selectMsg);

        JSONObject queryResultJson = msgQueryService.queryMsg(selectMsg);
        Map respMap = new HashMap();
        respMap.put(PeerToPeerConstant.CODE, Peer2PeerResp.SUCCESS_CODE.getCode());
        respMap.put(PeerToPeerConstant.MSG, Peer2PeerResp.SUCCESS_CODE.getMsg());
        respMap.put(PeerToPeerConstant.DATA, queryResultJson);
        log.info("返回响应数据：" + respMap);
        return respMap;
    }

    @Override
    public String queryTellerMsg(String selectMsg) {

        JSONObject jsonObject = new JSONObject();
        log.info("查询柜员消息请求报文：" + selectMsg);
        JSONObject queryResultJson = msgQueryService.queryMsg(selectMsg);

        jsonObject.put(PeerToPeerConstant.CODE, Peer2PeerResp.SUCCESS_CODE.getCode());
        jsonObject.put(PeerToPeerConstant.MSG, Peer2PeerResp.SUCCESS_CODE.getMsg());
        jsonObject.put(PeerToPeerConstant.DATA, queryResultJson);

        log.info("返回响应数据：" + jsonObject.toJSONString());
        return jsonObject.toJSONString();
    }

    private boolean checkParams(QueryPushMsgDTO queryPushMsgDTO) {
        if (StringUtils.isEmpty(queryPushMsgDTO.getBusinessSeq()) ||
                StringUtils.isEmpty(queryPushMsgDTO.getChannelseq()) ||
                StringUtils.isEmpty(queryPushMsgDTO.getRetimestamp()) ||
                StringUtils.isEmpty(queryPushMsgDTO.getEventCode()) ||
                StringUtils.isEmpty(String.valueOf(queryPushMsgDTO.getCurrentPage())) ||
                StringUtils.isEmpty(String.valueOf(queryPushMsgDTO.getPageSize()))
        ) {
            return true;
        }
        return false;
    }


}
