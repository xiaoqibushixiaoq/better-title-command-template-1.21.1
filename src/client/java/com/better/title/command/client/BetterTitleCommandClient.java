package com.better.title.command.client;

import com.better.title.command.client.network.TransformClientHandler;
import com.better.title.command.network.TransformNetworkHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class BetterTitleCommandClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // 注册网络包接收器
        ClientPlayNetworking.registerGlobalReceiver(TransformNetworkHandler.TitleTransformPayload.TYPE, TransformClientHandler::handleTitleTransform);
    }
}
