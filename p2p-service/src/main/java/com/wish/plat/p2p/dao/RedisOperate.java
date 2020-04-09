package com.wish.plat.p2p.dao;

import com.wish.plat.p2p.api.dto.GetTokenDTO;
import com.wish.plat.p2p.util.UniqueIdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Set;

import static com.wish.plat.p2p.constant.PeerToPeerConstant.*;


/**
 * @author ： yangxd
 * @date ：Created in 2019/12/17 21:26
 * @description ：redis操作
 * @modified By：
 * @version: v1.0
 */
@Repository
public class RedisOperate {

    @Autowired
    UniqueIdGenerator uniqueIdGenerator;

    @Autowired
    private RedisTemplate redisTemplate;

    public void cacheToken(String token, String numUserId, GetTokenDTO getTokenDTO) {
        //标志map的键、标志value的key、value
        HashOperations<String, String, String> map = redisTemplate.opsForHash();
        map.put(token, USER_ID, numUserId);
//        map.put(token, CITY_CODE, getTokenDTO.getCityCode());
        map.put(token, APP_ID, getTokenDTO.getCityCode());
        map.put(token, NOTIFICATION_ON, NOTIFICATION_ON_FLAG1);
        map.put(token, FORBIDDEN, FORBIDDEN_FLAG_1);


    }

    public void cacheUserInfo(GetTokenDTO getTokenDTO, String token, String numUserId) {
        String userId = getTokenDTO.getUserId();
        String cityCode = getTokenDTO.getCityCode();
        String channelCode = getTokenDTO.getChannelCode();
        String key = cityCode + "-" + channelCode + "-" + userId;
        //标志map的键、标志value的key、value
        HashOperations<String, String, String> map = redisTemplate.opsForHash();
        map.put(key, USER_ID, userId);
//        map.put(key, CITY_CODE, cityCode);
        map.put(key, APP_ID, cityCode);
        map.put(key, TOKEN, token);
        map.put(key, CHANNEL_CODE, channelCode);
        map.put(key, NUM_USER_ID, numUserId);
    }

    public String getUserToken(GetTokenDTO getTokenDTO) {
        String userId = getTokenDTO.getUserId();
        String cityCode = getTokenDTO.getCityCode();
        String channelCode = getTokenDTO.getChannelCode();
        String key = cityCode + "-" + channelCode + "-" + userId;

        HashOperations<String, String, String> map = redisTemplate.opsForHash();
        // 获取 Hash 类型所有的数据
        Map<String, String> userMap = map.entries(key);
        String token = userMap.get(TOKEN);
        return token;
    }

    public String getNumUserId(String userId, String cityCode, String channelCode) {
//        String userId = getTokenDTO.getUserId();
//        String appId = getTokenDTO.getAppId();
//        String channelCode = getTokenDTO.getChannelCode();
        String key = cityCode + "-" + channelCode + "-" + userId;

        HashOperations<String, String, String> map = redisTemplate.opsForHash();
        // 获取 Hash 类型所有的数据
        Map<String, String> userMap = map.entries(key);
        String numUserId = userMap.get(NUM_USER_ID);
        return numUserId;
    }

    public void delete(String indistinct){
//        redisTemplate.delete(indistinct);

        Set<String> keys = redisTemplate.keys(indistinct + "*");
        redisTemplate.delete(keys);
        System.err.println("删除了");
    }
}
