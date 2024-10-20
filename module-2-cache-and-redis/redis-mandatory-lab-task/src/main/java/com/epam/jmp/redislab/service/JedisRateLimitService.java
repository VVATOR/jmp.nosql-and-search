package com.epam.jmp.redislab.service;

import com.epam.jmp.redislab.api.RequestDescriptor;
import com.epam.jmp.redislab.configuration.ratelimit.RateLimitRule;
import com.epam.jmp.redislab.configuration.ratelimit.RateLimitTimeInterval;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisCluster;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Component
public class JedisRateLimitService implements RateLimitService {
    private static final String ONE_AS_STRING = String.valueOf(1);
    private static final int MILLIS_IN_SECOND = 1_000;
    private static final int SECONDS_IN_MINUTE = 60;
    private static final int MINUTES_IN_HOUR = 60;
    private static final int SECONDS_IN_HOUR = SECONDS_IN_MINUTE * 60;
    private static final String REDIS_KEY_DELIMITER = ":";
    private final Set<RateLimitRule> rateLimitRules;
    private final JedisCluster jedisCluster;

    public JedisRateLimitService(Set<RateLimitRule> rateLimitRules, JedisCluster jedisCluster) {
        this.rateLimitRules = rateLimitRules;
        this.jedisCluster = jedisCluster;
    }

    @Override
    public boolean shouldLimit(Set<RequestDescriptor> requestDescriptors) {
        long currentMinuteInUnix = System.currentTimeMillis() / MILLIS_IN_SECOND / SECONDS_IN_MINUTE;
        long currentHourInUnix = currentMinuteInUnix / MINUTES_IN_HOUR;

        for (RequestDescriptor requestDescriptor : requestDescriptors) {
            RateLimitRule appropriateRateLimitRule = findAppropriateRateLimitRule(requestDescriptor);
            if (appropriateRateLimitRule != null) {
                RateLimitTimeInterval timeInterval = appropriateRateLimitRule.getTimeInterval();
                long unixTime = timeInterval == RateLimitTimeInterval.HOUR ? currentHourInUnix : currentMinuteInUnix;

                String rateLimitRuleKeyStr = getRateLimitRuleKey(requestDescriptor, timeInterval, unixTime);

                String currentRequestAmount = jedisCluster.get(rateLimitRuleKeyStr);
                if (currentRequestAmount != null && Integer.parseInt(currentRequestAmount) >= appropriateRateLimitRule.getAllowedNumberOfRequests()) {
                    return true;
                }
                int expireSeconds = timeInterval == RateLimitTimeInterval.HOUR ? SECONDS_IN_HOUR : SECONDS_IN_MINUTE;
                updateRedisKeyValue(rateLimitRuleKeyStr, expireSeconds);
            }
        }
        return false;
    }

    private String getRateLimitRuleKey(RequestDescriptor requestDescriptor, RateLimitTimeInterval timeInterval, long unixTime) {
        String accountIdStr = requestDescriptor.getAccountId().orElse("");
        String clientIpStr = requestDescriptor.getClientIp().orElse("");
        String requestTypeStr = requestDescriptor.getRequestType().orElse("");
        StringBuilder rateLimitRuleKey = new StringBuilder();
        if (isNotBlank(accountIdStr)) {
            rateLimitRuleKey.append("accountId:").append(accountIdStr).append(REDIS_KEY_DELIMITER);
        }
        if (isNotBlank(clientIpStr)) {
            rateLimitRuleKey.append("clientIp:").append(clientIpStr).append(REDIS_KEY_DELIMITER);
        }
        if (isNotBlank(requestTypeStr)) {
            rateLimitRuleKey.append("requestType:").append(requestTypeStr).append(REDIS_KEY_DELIMITER);
        }

        rateLimitRuleKey.append(timeInterval.name().toLowerCase());
        rateLimitRuleKey.append(REDIS_KEY_DELIMITER);

        rateLimitRuleKey.append(unixTime);

        return rateLimitRuleKey.toString();
    }

    private RateLimitRule findAppropriateRateLimitRule(RequestDescriptor requestDescriptor) {
        Optional<String> reqDescAccountId = requestDescriptor.getAccountId();
        Optional<String> reqDescClientIp = requestDescriptor.getClientIp();
        Optional<String> reqDescRequestType = requestDescriptor.getRequestType();

        RateLimitRule appropriateRateLimitRule = null;

        for (RateLimitRule rateLimitRule : rateLimitRules) {
            Optional<String> rateLimitRuleAccountId = rateLimitRule.getAccountId();
            Optional<String> rateLimitRuleClientIp = rateLimitRule.getClientIp();
            Optional<String> rateLimitRuleRequestType = rateLimitRule.getRequestType();
            String rateLimitRuleAccountIdStr = rateLimitRuleAccountId.orElse("");
            String rateLimitRuleClientIpStr = rateLimitRuleClientIp.orElse("");
            String rateLimitRuleRequestTypeStr = rateLimitRuleRequestType.orElse("");

            boolean isAccountIdAppropriate = reqDescAccountId.isPresent() == rateLimitRuleAccountId.isPresent();
            boolean isAccountsEqual = false;
            if (reqDescAccountId.isPresent() && isAccountIdAppropriate && isNotBlank(rateLimitRuleAccountIdStr)) {
                isAccountsEqual = Objects.equals(reqDescAccountId.orElse(""), rateLimitRuleAccountIdStr);
                isAccountIdAppropriate = isAccountsEqual;
            }

            boolean isClientIpAppropriate = reqDescClientIp.isPresent() == rateLimitRuleClientIp.isPresent();
            boolean isClientIpsEqual = false;
            if (reqDescClientIp.isPresent() && isClientIpAppropriate && isNotBlank(rateLimitRuleClientIpStr)) {
                isClientIpsEqual = Objects.equals(reqDescClientIp.orElse(""), rateLimitRuleClientIpStr);
                isClientIpAppropriate = isClientIpsEqual;
            }

            boolean isRequestTypeAppropriate = reqDescRequestType.isPresent() == rateLimitRuleRequestType.isPresent();
            boolean isRequestTypesEqual = false;
            if (reqDescRequestType.isPresent() && isRequestTypeAppropriate && isNotBlank(rateLimitRuleRequestTypeStr)) {
                isRequestTypesEqual = Objects.equals(reqDescRequestType.orElse(""), rateLimitRuleRequestTypeStr);
                isRequestTypeAppropriate = isRequestTypesEqual;
            }

            if (isAccountIdAppropriate && isClientIpAppropriate && isRequestTypeAppropriate) {
                if (appropriateRateLimitRule == null || (isAccountsEqual || isClientIpsEqual || isRequestTypesEqual)) {
                    appropriateRateLimitRule = rateLimitRule;
                }
            }
        }
        return appropriateRateLimitRule;
    }

    private boolean isNotBlank(String str) {
        return str != null && str.trim().length() != 0;
    }

    private void updateRedisKeyValue(String key, long expireSeconds) {
        if (jedisCluster.exists(key)) {
            jedisCluster.incr(key);
        } else {
            jedisCluster.setex(key, expireSeconds, ONE_AS_STRING);
        }
    }
}