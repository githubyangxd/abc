package com.wish.plat.p2p.api.dto;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

/**
 * @author ： yangxd
 * @date ：Created in 2020/3/3 14:03
 * @description ：业务参数
 * @modified By：
 * @version: v1.0
 */
@Data
public class ContentVO {
    private String type;
    private String title;
    private String content;
    private String link;
    private String from;
    private String receiveTime;
    private String receiverId;
    private String priority;
    private String fileId;
    private String listId;
    private String businessSeq;
    private String reqSeq;
    private int progress;

    @Override
    public String toString() {
        return "{" +
                "\"type\":\"" + type + '\"' +
                ", \"title\":\"" + title + '\"' +
                ", \"content\":\"" + content + '\"' +
                ", \"link\":\"" + link + '\"' +
                ", \"from\":\"" + from + '\"' +
                ", \"receiveTime\":\"" + receiveTime + '\"' +
                ", \"receiverId\":\"" + receiverId + '\"' +
                ", \"priority\":\"" + priority + '\"' +
                ", \"fileId\":\"" + fileId + '\"' +
                ", \"listId\":\"" + listId + '\"' +
                ", \"businessSeq\":\"" + businessSeq + '\"' +
                ", \"reqSeq\":\"" + reqSeq + '\"' +
                ", \"progress\":" + progress + /*'\"' +*/
                '}';
    }


}
