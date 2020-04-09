package com.wish.plat.p2p.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

/**
 * @author ： yangxd
 * @date ：Created in 2019/12/16 10:24
 * @description ：向im推消息model
 * @modified By：
 * @version: v1.0
 */
@Document(collection = "pushMsgToIMModel")
@Data
public class PushMsgToIMModel implements Serializable {
    private static final long serialVersionUID = -5538500775033871788L;
    //************begin 字段为推送到im中的Content内容*********
    @Id
    private String msgId;
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
    //************end 字段为推送到im中的Content内容*********

    private String cityCode;
    //    private String receiverId;
    private String senderId;
    private String channelCode;
    /**
     * 目前固定为 peer
     */
    private String imClazz;


}
