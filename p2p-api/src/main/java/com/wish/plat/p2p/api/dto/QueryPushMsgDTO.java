package com.wish.plat.p2p.api.dto;

import lombok.Data;

/**
 * @author ： yangxd
 * @date ：Created in 2020/3/9 14:27
 * @description ：柜员历史消息查询实体类
 * @modified By：
 * @version: v1.0
 */
@Data
public class QueryPushMsgDTO {
    /**
     * 以下四个字段为common域字段
     */
    private String retimestamp;
    private String channelseq;
    private String businessSeq;
    private String eventCode;

    /**
     * 消息类型（5类：0-待办消息 1-导入消息 2-导出消息 3-公告消息 4-业务提醒 5-异步交易进度）
     */
    private String type;
    /**
     * 消息优先级
     */
    private String priority;
    /**
     * 开始时间
     */
    private String startTime;
    /**
     * 结束时间
     */
    private String endTime;
    /**
     * 接受者ID
     */
    private String receiverId;
    /**
     * 消息内容（模糊查询内容）
     */
    private String msgContent;
    /**
     * 列表每页条数
     */
    private Integer pageSize;
    /**
     * currentPage
     */
    private Integer currentPage;
}
