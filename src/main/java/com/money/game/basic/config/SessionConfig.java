package com.money.game.basic.config;

import com.money.game.basic.component.ext.web.PersistentCookieHttpSessionStrategy;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.session.data.redis.RedisFlushMode;
import org.springframework.session.data.redis.RedisOperationsSessionRepository;
import org.springframework.session.data.redis.config.ConfigureNotifyKeyspaceEventsAction;
import org.springframework.session.data.redis.config.ConfigureRedisAction;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.web.http.HttpSessionStrategy;
import org.springframework.util.StringUtils;

@EnableRedisHttpSession
public class SessionConfig {

	@Value("${redis.session.maxInactiveInterval:1800}")
	private int maxInactiveInterval;

	@Value("${redis.session.namespace:}")
	private String redisNamespace;

	@Value("${redis.no_op:no}")
	String noOp;

	@Bean
	public ConfigureRedisAction configureRedisAction() {
		if (noOp.equals("no")) {
			return new ConfigureNotifyKeyspaceEventsAction();
		}
		return ConfigureRedisAction.NO_OP;
	}

	@Primary
	@Bean
	public RedisOperationsSessionRepository sessionRepository(@Qualifier("sessionRedisTemplate") RedisOperations<Object, Object> sessionRedisTemplate, ApplicationEventPublisher applicationEventPublisher) {
		RedisOperationsSessionRepository sessionRepository = new RedisOperationsSessionRepository(sessionRedisTemplate);
		sessionRepository.setApplicationEventPublisher(applicationEventPublisher);
		sessionRepository.setDefaultMaxInactiveInterval(maxInactiveInterval);

		if (StringUtils.hasText(redisNamespace)) {
			sessionRepository.setRedisKeyNamespace(redisNamespace);
		}

		sessionRepository.setRedisFlushMode(RedisFlushMode.ON_SAVE);
		return sessionRepository;
	}

	@Bean
	public HttpSessionStrategy httpSessionStrategy() {
		return new PersistentCookieHttpSessionStrategy();
	}

}