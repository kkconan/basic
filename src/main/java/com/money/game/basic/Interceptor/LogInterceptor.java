package com.money.game.basic.Interceptor;

/**
 * Created by kej on 2017/8/16.
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 日志拦截器
 */
public class LogInterceptor implements HandlerInterceptor {

    /**
     * 会话ID
     */
    private final static String LOGID = "logId";

    private static final transient Logger logger = LoggerFactory.getLogger(LogInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 放SessionId
        String token = java.util.UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
        MDC. put(LOGID, token);

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView)
            throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        // 其他逻辑代码

        // 最后执行MDC删除
        MDC. remove(LOGID);
    }
}
