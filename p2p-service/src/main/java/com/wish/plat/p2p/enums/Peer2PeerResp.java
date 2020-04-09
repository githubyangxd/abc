package com.wish.plat.p2p.enums;

import lombok.Data;

public enum Peer2PeerResp {
    OTHER_ERROR("P99999", "其他错误"),
    SUCCESS_CODE("000000", "成功"),
    PARAMS_ERROR("P90001","请求缺少必传参数"),
    SEND_ERROR("P90003","向IM发送消息异常"),
    PARAMS_FORMAT_ERROR("P90004","参数格式不匹配"),
    CHANNEL_SEQ_NOT_TRANSFER("P90005","channelseq流水号未传入"),
    CURRENT_PAGE_TRANSFER_ERROR("P90006","查询传入currentPage，数字小于1"),
    PAGE_SIZE_TRANSFER_ERROR("P90006","查询传入pageSize，数字小于1"),
    UN_FIND_USER_ID("P90002","未查询到接收消息柜员信息");
    private String code;
    private String msg;

    Peer2PeerResp(String code , String msg) {
        this.msg = msg;
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
