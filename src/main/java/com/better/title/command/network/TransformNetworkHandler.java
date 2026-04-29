package com.better.title.command.network;

import com.better.title.command.component.TextGroup;
import com.better.title.command.component.TextSegment;
import com.mojang.serialization.JsonOps;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 服务器端网络包 - 发送带变换的title到客户端
 */
public class TransformNetworkHandler {
    
    // Title变换包 - 支持多个文本组
    public record TitleTransformPayload(Map<String, TextGroup> groups,
                                       int fadeIn, int stay, int fadeOut) implements CustomPacketPayload {
        public static final Type<TitleTransformPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("better-title-command", "title_transform"));
        
        public static final StreamCodec<FriendlyByteBuf, TitleTransformPayload> CODEC = StreamCodec.of(
            (buf, payload) -> {
                // 写入组的数量
                buf.writeInt(payload.groups.size());
                // 写入每个组
                for (Map.Entry<String, TextGroup> entry : payload.groups.entrySet()) {
                    TextGroup group = entry.getValue();
                    // 写入组ID
                    buf.writeUtf(entry.getKey());
                    // 写入组级别变换
                    buf.writeFloat(group.getGroupOffsetX());
                    buf.writeFloat(group.getGroupOffsetY());
                    buf.writeFloat(group.getGroupScaleX());
                    buf.writeFloat(group.getGroupScaleY());
                    buf.writeFloat(group.getGroupRotation());
                    // 写入片段数量
                    buf.writeInt(group.getSegments().size());
                    // 写入每个片段
                    for (TextSegment segment : group.getSegments()) {
                        // 使用Codec序列化Component为JSON字符串
                        var jsonResult = ComponentSerialization.CODEC.encodeStart(JsonOps.INSTANCE, segment.getText());
                        String json = jsonResult.getOrThrow().toString();
                        buf.writeUtf(json);
                        buf.writeFloat(segment.getOffsetX());
                        buf.writeFloat(segment.getOffsetY());
                        buf.writeFloat(segment.getScaleX());
                        buf.writeFloat(segment.getScaleY());
                        buf.writeFloat(segment.getRotation());
                    }
                }
                // 写入时间参数
                buf.writeInt(payload.fadeIn);
                buf.writeInt(payload.stay);
                buf.writeInt(payload.fadeOut);
            },
            buf -> {
                // 读取组的数量
                int groupCount = buf.readInt();
                Map<String, TextGroup> groups = new java.util.HashMap<>();
                
                // 读取每个组
                for (int i = 0; i < groupCount; i++) {
                    String groupId = buf.readUtf();
                    TextGroup group = new TextGroup(groupId);
                    
                    // 读取组级别变换
                    float groupOffsetX = buf.readFloat();
                    float groupOffsetY = buf.readFloat();
                    float groupScaleX = buf.readFloat();
                    float groupScaleY = buf.readFloat();
                    float groupRotation = buf.readFloat();
                    group.setGroupOffset(groupOffsetX, groupOffsetY);
                    group.setGroupScale(groupScaleX, groupScaleY);
                    group.setGroupRotation(groupRotation);
                    
                    // 读取片段数量
                    int segmentCount = buf.readInt();
                    // 读取每个片段
                    for (int j = 0; j < segmentCount; j++) {
                        String json = buf.readUtf();
                        // 使用Codec反序列化Component
                        var parseResult = ComponentSerialization.CODEC.parse(JsonOps.INSTANCE, com.google.gson.JsonParser.parseString(json));
                        Component text = parseResult.getOrThrow();
                        float offsetX = buf.readFloat();
                        float offsetY = buf.readFloat();
                        float scaleX = buf.readFloat();
                        float scaleY = buf.readFloat();
                        float rotation = buf.readFloat();
                        group.addSegment(new TextSegment(text, offsetX, offsetY, scaleX, scaleY, rotation));
                    }
                    
                    groups.put(groupId, group);
                }
                
                // 读取时间参数
                int fadeIn = buf.readInt();
                int stay = buf.readInt();
                int fadeOut = buf.readInt();
                return new TitleTransformPayload(groups, fadeIn, stay, fadeOut);
            }
        );
        
        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }
    
    /**
     * 发送单个文本组
     */
    public static void sendTitle(ServerPlayer player, TextGroup group,
                                int fadeIn, int stay, int fadeOut) {
        Map<String, TextGroup> groups = new java.util.HashMap<>();
        groups.put(group.getGroupId(), group);
        TitleTransformPayload payload = new TitleTransformPayload(groups, fadeIn, stay, fadeOut);
        ServerPlayNetworking.send(player, payload);
    }
    
    /**
     * 发送多个文本组
     */
    public static void sendTitle(ServerPlayer player, Map<String, TextGroup> groups,
                                int fadeIn, int stay, int fadeOut) {
        TitleTransformPayload payload = new TitleTransformPayload(groups, fadeIn, stay, fadeOut);
        ServerPlayNetworking.send(player, payload);
    }
    
    /**
     * 清除指定组（发送空Map，客户端需要特殊处理）
     */
    public static void clearGroup(ServerPlayer player, String groupId) {
        // 发送一个特殊的payload来清除指定组
        // 目前简单实现：发送空Map，客户端需要知道这是清除操作
        Map<String, TextGroup> emptyGroups = new java.util.HashMap<>();
        TitleTransformPayload payload = new TitleTransformPayload(emptyGroups, 0, 0, 0);
        ServerPlayNetworking.send(player, payload);
    }
    
    /**
     * 清除所有组
     */
    public static void clearAllGroups(ServerPlayer player) {
        // 发送空Map来表示清除所有
        Map<String, TextGroup> emptyGroups = new java.util.HashMap<>();
        TitleTransformPayload payload = new TitleTransformPayload(emptyGroups, 0, 0, 0);
        ServerPlayNetworking.send(player, payload);
    }
}
