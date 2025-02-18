package com.bizzan.er.price.event;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.bizzan.er.price.entity.PriceRobotParams;
import com.bizzan.er.price.robot.ExchangeRobot;
import com.bizzan.er.price.robot.ExchangeRobotFactory;
import com.bizzan.er.price.robot.ExchangeRobotNormal;
import com.bizzan.er.price.service.RobotParamService;

@Component
public class ApplicationEvent implements ApplicationListener<ContextRefreshedEvent> {
	
	private final static  Logger logger  =  LoggerFactory.getLogger(ApplicationEvent.class);
	
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		logger.info("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
		logger.info("===============================================");
		logger.info("===============交易机器人初始化===============");
		logger.info("===============================================");
	}

}
