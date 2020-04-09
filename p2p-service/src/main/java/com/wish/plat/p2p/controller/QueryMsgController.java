package com.wish.plat.p2p.controller;

import com.alibaba.fastjson.JSONObject;
import com.wish.plat.p2p.constant.PeerToPeerConstant;
import com.wish.plat.p2p.enums.Peer2PeerResp;
import com.wish.plat.p2p.service.MsgQueryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author ： yangxd
 * @date ：Created in 2019/12/18 15:13
 * @description ：柜员查询消息控制层
 * @modified By：
 * @version: v1.0
 */
@RestController
@RequestMapping("/query")
@Slf4j
public class QueryMsgController {


    @Autowired
    MsgQueryService msgQueryService;

    @PostMapping("/tellerMsg.json")
    public String msgQuery(@RequestBody String selectMsg) {
        JSONObject jsonObject = new JSONObject();
        log.info("查询柜员消息请求报文：" + selectMsg);
        JSONObject queryResultJson = msgQueryService.queryMsg(selectMsg);
        jsonObject.put(PeerToPeerConstant.CODE, Peer2PeerResp.SUCCESS_CODE.getCode());
        jsonObject.put(PeerToPeerConstant.MSG, Peer2PeerResp.SUCCESS_CODE.getMsg());
        jsonObject.put(PeerToPeerConstant.DATA,queryResultJson);
        return jsonObject.toJSONString();
    }

    @PostMapping("/tellerMsg0.json")
    public String msgQuery1(HttpServletRequest request) {
        InputStream is = null;
        try {
            is = request.getInputStream();
            StringBuilder sb = new StringBuilder();
            byte[] b = new byte[4096];
            for (int n; (n = is.read(b)) != -1; ) {
                sb.append(new String(b, 0, n));
            }
            System.out.println("000000000" + sb);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return "2";
    }
}
