/**
 * lightingboot.com
 */
package com.wish.plat.p2p.util;

import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedGenerator;
import org.assertj.core.util.Preconditions;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.UUID;

/**
 * 实现描述：交易号生成器
 *
 * @author simon
 * @version v 0.1
 * @since 2017/09/29 下午3:02
 */
@Component
public class UniqueIdGenerator {

    private static final long SEQUENCE_MAX_VALUE = 999;

    private static final long IP_SEG_BITS = 8L;

    private static long workerId;

    private int sequence;

    private long lastTime;

    public UniqueIdGenerator() {
        initWorkId();
    }

    public synchronized String generateKey() {
        long currentMillis = getCurrentMillis();
        Preconditions
                .checkState(
                        lastTime <= currentMillis,
                        "Clock is moving backwards, last time is %d milliseconds, current time is %d milliseconds",
                        lastTime, currentMillis);
        if (lastTime == currentMillis) {
            if (++sequence > SEQUENCE_MAX_VALUE) {
                currentMillis = waitUntilNextTime(currentMillis);
            }
        } else {
            sequence = 0;
        }
        lastTime = currentMillis;
        //毫秒数据转化为日期格式的文本
        Date currentDate = com.wish.plat.p2p.util.DateUtils.parseToDate(currentMillis);
        String millSecondShortStr = com.wish.plat.p2p.util.DateUtils.formatDate(currentDate,
                DateUtils.DATETIME_MILL_SECOND_SHORT_PATTERN);

        //workId是10位，不足便补0
        String workIdStr = String.format("%10d", workerId).replace(" ", "0");

        //序号位是4位，不足便补0
        String sequenceStr = String.format("%3d", sequence).replace(" ", "0");

        //组装生成的KEY值
        StringBuilder key = new StringBuilder();
        key.append(workIdStr);

        key.append(millSecondShortStr);
        key.append(sequenceStr);
        return key.toString();
    }


    //前缀
    public synchronized String generateKeyWithPrefix(String prefix) {
        Preconditions.checkNotNull(prefix, "Args check failed, prefix is null");
        return prefix + generateKey();
    }

    public synchronized String generateNum() {
        String num = String.valueOf(System.currentTimeMillis());
        return num;
    }

    /**
     * 获得一个UUID
     *
     * @return String UUID
     */
    public synchronized String getUUID() {
        TimeBasedGenerator timeBasedGenerator = Generators.timeBasedGenerator();
        UUID uuid = timeBasedGenerator.generate();
        return uuid.toString().replaceAll("-", "");
    }

    //后缀
    public synchronized String generateKeyWithSuffix(String suffix) {
        Preconditions.checkNotNull(suffix, "Args check failed, suffix is null");
        return generateKey() + suffix;
    }

    /**
     * Set work process id.
     */
    private synchronized void initWorkId() {
        String ipAddr;
        try {
            ipAddr = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            throw new IllegalStateException(
                    "Cannot get LocalHost HostAddress, please check your network!");
        }
        //将目标IP地址字符串strIPAddress转换为数字
        String[] ipSection = ipAddr.split("\\.");
        long sip1 = Long.parseLong(ipSection[0]);
        long sip2 = Long.parseLong(ipSection[1]);
        long sip3 = Long.parseLong(ipSection[2]);
        long sip4 = Long.parseLong(ipSection[3]);
        workerId = sip1 << (IP_SEG_BITS * 3) | sip2 << (IP_SEG_BITS * 2) | sip3 << IP_SEG_BITS
                | sip4;
    }

    private long waitUntilNextTime(final long lastTime) {
        long time = getCurrentMillis();
        while (time <= lastTime) {
            time = getCurrentMillis();
        }
        return time;
    }

    private long getCurrentMillis() {
        return System.currentTimeMillis();
    }

}
