package com.better.title.command.network;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;

/**
 * 服务器端网络包 - 发送带变换的title/actionbar到客户端
 */
public class TransformNetworkHandler {
    
    // Title变换包 - 使用String传输Component
    public record TitleTransformPayload(String titleJson, String subtitleJson,
                                       float offsetX, float offsetY, 
                                       float scaleX, float scaleY,
                                       int fadeIn, int stay, int fadeOut) implements CustomPacketPayload {
        public static final Type<TitleTransformPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("better-title-command", "title_transform"));
        
        public static final StreamCodec<FriendlyByteBuf, TitleTransformPayload> CODEC = StreamCodec.of(
            (buf, payload) -> {
                buf.writeUtf(payload.titleJson);
                buf.writeUtf(payload.subtitleJson);
                buf.writeFloat(payload.offsetX);
                buf.writeFloat(payload.offsetY);
                buf.writeFloat(payload.scaleX);
                buf.writeFloat(payload.scaleY);
                buf.writeInt(payload.fadeIn);
                buf.writeInt(payload.stay);
                buf.writeInt(payload.fadeOut);
            },
            buf -> new TitleTransformPayload(
                buf.readUtf(),
                buf.readUtf(),
                buf.readFloat(),
                buf.readFloat(),
                buf.readFloat(),
                buf.readFloat(),
                buf.readInt(),
                buf.readInt(),
                buf.readInt()
            )
        );
        
        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }
    
    /**
     * 发送带变换的title（可用于模拟actionbar，通过调整offsetY）
     */
    public static void sendTitle(ServerPlayer player, Component title, Component subtitle,
                                float offsetX, float offsetY, float scaleX, float scaleY,
                                int fadeIn, int stay, int fadeOut) {
        // 将Component转换为JSON字符串
        String titleJson = Component.Serializer.toJson(title, player.registryAccess());
        String subtitleJson = subtitle != null ? Component.Serializer.toJson(subtitle, player.registryAccess()) : "\"\"";
        
        TitleTransformPayload payload = new TitleTransformPayload(
            titleJson, subtitleJson,
            offsetX, offsetY, scaleX, scaleY,
            fadeIn, stay, fadeOut
        );
        ServerPlayNetworking.send(player, payload);
    }
}
