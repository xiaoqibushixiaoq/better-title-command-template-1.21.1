package com.better.title.command.client;

import com.better.title.command.client.network.TransformClientHandler;
import com.better.title.command.network.TransformNetworkHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BetterTitleCommandClient implements ClientModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("better-title-command-client");
	
	@Override
	public void onInitializeClient() {
		// 注册网络包接收器（不需要再次注册Payload类型，服务器端已经注册）
		ClientPlayNetworking.registerGlobalReceiver(TransformNetworkHandler.TitleTransformPayload.TYPE, TransformClientHandler::handleTitleTransform);
		
		LOGGER.info("Better Title Command client initialized!");
	}
}
