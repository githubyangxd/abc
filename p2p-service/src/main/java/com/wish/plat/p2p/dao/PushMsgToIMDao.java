package com.wish.plat.p2p.dao;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.wish.plat.p2p.constant.QueryParamsContant;
import com.wish.plat.p2p.model.PushMsgToIMModel;
import com.wish.plat.p2p.util.UniqueIdGenerator;
import com.wish.plat.p2p.vo.QueryPushMsgVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.repository.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static com.wish.plat.p2p.constant.PeerToPeerConstant.*;


/**
 * @author ： yangxd
 * @date ：Created in 2019/12/17 20:21
 * @description ：
 * @modified By：
 * @version: v1.0
 */
@Repository
public class PushMsgToIMDao {
    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    UniqueIdGenerator uniqueIdGenerator;

    public void insert(String receiveMsg) {
        //生成消息id
        String msgId = uniqueIdGenerator.generateKey();
        Map<String, Object> map = new HashMap();
        map = JSONObject.parseObject(receiveMsg, Map.class, Feature.OrderedField);
        JSONObject storeJsonObject = (JSONObject) map.get(CONTENT);
        storeJsonObject.put(MSG_ID, msgId);
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (!CONTENT.equals(entry.getKey())) {
                String key = entry.getKey();
                String value = String.valueOf(entry.getValue());
                storeJsonObject.put(key, value);
            }
        }
        mongoTemplate.insert(storeJsonObject, "pushMsgToIMModel");

    }

    public void insertJson(JSONObject storeJsonObject) {
        mongoTemplate.insert(storeJsonObject, "pushMsgToIMModel");
    }


    @Autowired
    private MongoOperations mongoOperations;

    /**
     * 分页查询
     */
    public PageImpl<PushMsgToIMModel> pageQuery(QueryPushMsgVO queryPushMsgVO, String collectionName) {
        Query query = new Query();

        if (!StringUtils.isEmpty(queryPushMsgVO.getType())) {
//            criteria.and(QueryParamsContant.TYPE).is(queryPushMsgVO.getType());
            query.addCriteria(Criteria.where(QueryParamsContant.TYPE).is(queryPushMsgVO.getType()));
        }
        if (!StringUtils.isEmpty(queryPushMsgVO.getPriority())) {
//            criteria.and(QueryParamsContant.PRIORITY).is(queryPushMsgVO.getPriority());
            query.addCriteria(Criteria.where(QueryParamsContant.PRIORITY).is(queryPushMsgVO.getPriority()));
        }

        //特殊处理-解决结束时间当天查不出来问题
        if(!StringUtils.isEmpty(queryPushMsgVO.getEndTime())){
            String endTime = queryPushMsgVO.getEndTime();
            String endTime0_8 = endTime.substring(0, 8);
            String endTime8_10 = endTime.substring(8, 10);
            String newTime8_10 = String.valueOf(Integer.valueOf(endTime8_10) + 1);
            String newEndTime = endTime0_8 + newTime8_10;
            queryPushMsgVO.setEndTime(newEndTime);
        }
        if (!StringUtils.isEmpty(queryPushMsgVO.getStartTime()) && !StringUtils.isEmpty(queryPushMsgVO.getEndTime())) {


            Criteria criteria = new Criteria();
            criteria.andOperator(Criteria.where(QueryParamsContant.RECEIVE_TIME).gte(queryPushMsgVO.getStartTime()),Criteria.where(QueryParamsContant.RECEIVE_TIME).lte(queryPushMsgVO.getEndTime()));
            query.addCriteria(criteria);
        } else {
            if (!StringUtils.isEmpty(queryPushMsgVO.getStartTime())) {
                query.addCriteria(Criteria.where(QueryParamsContant.RECEIVE_TIME).gte(queryPushMsgVO.getStartTime()));
            }
            if (!StringUtils.isEmpty(queryPushMsgVO.getEndTime())) {
                query.addCriteria(Criteria.where(QueryParamsContant.RECEIVE_TIME).lte(queryPushMsgVO.getEndTime()));
            }
        }

        if (!StringUtils.isEmpty(queryPushMsgVO.getReceiverId())) {
            query.addCriteria(Criteria.where(QueryParamsContant.RECEIVER_ID).is(queryPushMsgVO.getReceiverId()));
        }

        if (!StringUtils.isEmpty(queryPushMsgVO.getMsgContent())) {
            Pattern pattern = Pattern.compile("^.*" + queryPushMsgVO.getMsgContent() + ".*$", Pattern.CASE_INSENSITIVE);
            Criteria criteria = new Criteria();
            criteria.orOperator(Criteria.where(QueryParamsContant.CONTENT).regex(pattern),
                    Criteria.where(QueryParamsContant.TITLE).regex(pattern));
            query.addCriteria(criteria);
        }

        Pageable pageable = PageRequest.of(queryPushMsgVO.getCurrentPage(), queryPushMsgVO.getPageSize());
        query.with(pageable);
        // 排序
        query.with(new Sort(Sort.Direction.ASC, QueryParamsContant.RECEIVE_TIME));
        System.err.println(query);
        // 查询总数
        int count = (int) mongoOperations.count(query, PushMsgToIMModel.class, collectionName);
//        List<PushMsgToIMModel> items = mongoOperations.find(query, PushMsgToIMModel.class, collectionName);
        List<PushMsgToIMModel> items = mongoTemplate.find(query, PushMsgToIMModel.class, collectionName);
        System.err.println("条数：" + items.size() + " == " + items);
        // System.out.println("stories:" + stories.size() + " count:" + count);
        return (PageImpl<PushMsgToIMModel>) PageableExecutionUtils.getPage(items, pageable, () -> count);
    }
}
