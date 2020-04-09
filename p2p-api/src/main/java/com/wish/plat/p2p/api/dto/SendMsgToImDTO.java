package com.wish.plat.p2p.api.dto;


import lombok.Data;

/**
 * @author ： yangxd
 * @date ：Created in 2020/3/3 13:41
 * @description ：向柜员推送消息实体类
 * @modified By：
 * @version: v1.0
 */
@Data
public class SendMsgToImDTO {
    private String cityCode;
    private String userId;
    private String channelCode;
    private ContentVO content;

    @Override
    public String toString() {
        return "{" +
                "\"cityCode\":\"" + cityCode + '\"' +
                ", \"userId\":\"" + userId + '\"' +
                ", \"channelCode\":\"" + channelCode + '\"' +
                ", \"content\":" + content.toString() + /*'' +*/
                '}';
    }


}
