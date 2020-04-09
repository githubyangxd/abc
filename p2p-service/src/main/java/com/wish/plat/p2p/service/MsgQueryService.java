package com.wish.plat.p2p.service;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.wish.plat.p2p.constant.QueryParamsContant;
import com.wish.plat.p2p.dao.PushMsgToIMDao;
import com.wish.plat.p2p.enums.Peer2PeerResp;
import com.wish.plat.p2p.exception.MyException;
import com.wish.plat.p2p.model.PushMsgToIMModel;
import com.wish.plat.p2p.util.DateUtils;
import com.wish.plat.p2p.util.UniqueIdGenerator;
import com.wish.plat.p2p.vo.QueryPushMsgVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author ： yangxd
 * @date ：Created in 2019/12/19 9:52
 * @description ：
 * @modified By：
 * @version: v1.0
 */
@Service
@Slf4j
public class MsgQueryService {
    private final static String COLLECTION_NAME = "pushMsgToIMModel";

    @Autowired
    PushMsgToIMDao pushMsgToIMDao;

    @Autowired
    UniqueIdGenerator uniqueIdGenerator;

    public JSONObject queryMsg(String queryMsg) {
        JSONObject wholeJson = new JSONObject();
        JSONObject respCommonJson = new JSONObject();
        JSONObject businessJson = new JSONObject();
        JSONObject gmpJson = new JSONObject();
        JSONObject respPlatJson = new JSONObject();

        //解析请求报文
        Map<String, Object> map = JSONObject.parseObject(queryMsg, Map.class, Feature.OrderedField);
        JSONObject dataJson = (JSONObject) map.get("data");

        JSONObject reqCommonJson = null;
        JSONObject platJson = null;
        JSONObject reqGmpJson = null;

        try {
            reqCommonJson = (JSONObject) dataJson.get("common");
            platJson = (JSONObject) dataJson.get("plat");
            reqGmpJson = (JSONObject) platJson.get("gmp");
        } catch (Exception e) {
            log.info(Peer2PeerResp.PARAMS_FORMAT_ERROR.getCode() + " " + Peer2PeerResp.PARAMS_FORMAT_ERROR.getMsg());
            throw new MyException(Peer2PeerResp.PARAMS_FORMAT_ERROR.getCode(), Peer2PeerResp.PARAMS_FORMAT_ERROR.getMsg());
        }

        String channelSeq = null;

        //获取原渠道号，返回使用
        channelSeq = reqCommonJson.getString(QueryParamsContant.CHANNEL_SEQ);
        if (StringUtils.isEmpty(channelSeq)) {
            log.info(Peer2PeerResp.CHANNEL_SEQ_NOT_TRANSFER.getCode() + " " + Peer2PeerResp.CHANNEL_SEQ_NOT_TRANSFER.getMsg());
            throw new MyException(Peer2PeerResp.CHANNEL_SEQ_NOT_TRANSFER.getCode(), Peer2PeerResp.CHANNEL_SEQ_NOT_TRANSFER.getMsg());
        }

        //校验必传参数
       String businessSeq = reqCommonJson.getString(QueryParamsContant.BUSINESS_SEQ);
        if (StringUtils.isEmpty(businessSeq)) {
            log.info(Peer2PeerResp.PARAMS_ERROR.getCode() + " " + Peer2PeerResp.PARAMS_ERROR.getMsg());
            throw new MyException(Peer2PeerResp.PARAMS_ERROR.getCode(), Peer2PeerResp.PARAMS_ERROR.getMsg());
        }
        String eventCode = reqCommonJson.getString(QueryParamsContant.EVENT_CODE);
        if (StringUtils.isEmpty(eventCode)) {
            log.info(Peer2PeerResp.PARAMS_ERROR.getCode() + " " + Peer2PeerResp.PARAMS_ERROR.getMsg());
            throw new MyException(Peer2PeerResp.PARAMS_ERROR.getCode(), Peer2PeerResp.PARAMS_ERROR.getMsg());
        }
        String retimestamp = reqCommonJson.getString(QueryParamsContant.RE_TIME_STAMP);
        if (StringUtils.isEmpty(retimestamp)) {
            log.info(Peer2PeerResp.PARAMS_ERROR.getCode() + " " + Peer2PeerResp.PARAMS_ERROR.getMsg());
            throw new MyException(Peer2PeerResp.PARAMS_ERROR.getCode(), Peer2PeerResp.PARAMS_ERROR.getMsg());
        }

        //交易流水号
        String tranSeq = uniqueIdGenerator.generateKey();

        //不传输设置默认值
        int transferCurrentPage = 1;
        int pageSize = 10;
        if (!(null == reqGmpJson.getInteger(QueryParamsContant.CURRENT_PAGE))) {
            transferCurrentPage = reqGmpJson.getInteger(QueryParamsContant.CURRENT_PAGE);
        }

        if (1 > transferCurrentPage) {
            throw new MyException(Peer2PeerResp.CURRENT_PAGE_TRANSFER_ERROR.getCode(), Peer2PeerResp.CURRENT_PAGE_TRANSFER_ERROR.getMsg());
        }
        int currentPage = transferCurrentPage - 1;

        if (!(null == reqGmpJson.getInteger(QueryParamsContant.PAGE_SIZE))) {
            pageSize = reqGmpJson.getInteger(QueryParamsContant.PAGE_SIZE);
        }
        if (1 > pageSize) {
            throw new MyException(Peer2PeerResp.PAGE_SIZE_TRANSFER_ERROR.getCode(), Peer2PeerResp.PAGE_SIZE_TRANSFER_ERROR.getMsg());

        }
        //组装查询条件
        QueryPushMsgVO queryPushMsgVO = new QueryPushMsgVO();
        queryPushMsgVO.setType(reqGmpJson.getString(QueryParamsContant.TYPE));
        queryPushMsgVO.setPriority(reqGmpJson.getString(QueryParamsContant.PRIORITY));
        queryPushMsgVO.setStartTime(reqGmpJson.getString(QueryParamsContant.START_TIME));
        queryPushMsgVO.setEndTime(reqGmpJson.getString(QueryParamsContant.END_TIME));
        queryPushMsgVO.setReceiverId(reqGmpJson.getString(QueryParamsContant.RECEIVER_ID));
        queryPushMsgVO.setMsgContent(reqGmpJson.getString(QueryParamsContant.MSG_CONTENT));
        queryPushMsgVO.setCurrentPage(currentPage);
        queryPushMsgVO.setPageSize(pageSize);

        log.info("查询条件：" + queryPushMsgVO.toString());
        PageImpl<PushMsgToIMModel> selectResult = pushMsgToIMDao.pageQuery(queryPushMsgVO, COLLECTION_NAME);

        List<PushMsgToIMModel> list = selectResult.getContent();

        //组装返回common域
        respCommonJson.put(QueryParamsContant.CHANNEL_SEQ, channelSeq);
        respCommonJson.put(QueryParamsContant.OTHER_SEQ, "");
        respCommonJson.put(QueryParamsContant.TRAN_SEQ, tranSeq);
        respCommonJson.put(QueryParamsContant.BUSINESS_DATE, DateUtils.todayString());
        respCommonJson.put(QueryParamsContant.BUSINESS_SEQ, "");
        //返回business域为空
        //组装gmp域
        gmpJson.put(QueryParamsContant.DATA, list);
        gmpJson.put(QueryParamsContant.CURRENT_PAGE, currentPage + 1);
        gmpJson.put(QueryParamsContant.PAGE_SIZE, pageSize);
        gmpJson.put(QueryParamsContant.TOTAL, selectResult.getTotalElements());

        respPlatJson.put(QueryParamsContant.GMP, gmpJson);

        //组装总体返回json
        wholeJson.put(QueryParamsContant.COMMON, respCommonJson);
        wholeJson.put(QueryParamsContant.BUSINESS, businessJson);
        wholeJson.put(QueryParamsContant.PLAT, respPlatJson);


        return wholeJson;

    }
}
