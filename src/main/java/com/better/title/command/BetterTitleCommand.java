package com.better.title.command;

import com.better.title.command.command.EnhancedTitleCommand;
import com.better.title.command.network.TransformNetworkHandler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BetterTitleCommand implements ModInitializer {
	public static final String MOD_ID = "better-title-command";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// 注册命令
		CommandRegistrationCallback.EVENT.register(EnhancedTitleCommand::register);
		
		// 注册网络包Payload类型（服务器端）
		PayloadTypeRegistry.playS2C().register(TransformNetworkHandler.TitleTransformPayload.TYPE, TransformNetworkHandler.TitleTransformPayload.CODEC);

		LOGGER.info("Better Title Command mod initialized!");
	}
}