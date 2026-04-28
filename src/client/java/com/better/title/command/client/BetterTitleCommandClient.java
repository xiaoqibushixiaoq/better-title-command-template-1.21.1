package com.better.title.command.client;

import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BetterTitleCommandClient implements ClientModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("better-title-command-client");
	
	@Override
	public void onInitializeClient() {
		// 客户端初始化
		LOGGER.info("Better Title Command client initialized!");
		
		// 运行示例代码（仅在开发时）
		// TransformedTextExample.demonstrateUsage();
	}
}