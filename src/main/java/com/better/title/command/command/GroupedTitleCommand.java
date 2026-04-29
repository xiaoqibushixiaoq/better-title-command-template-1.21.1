package com.better.title.command.command;

import com.better.title.command.component.TextGroup;
import com.better.title.command.component.TextSegment;
import com.better.title.command.network.TransformNetworkHandler;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ComponentArgument;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * 增强的title命令 - 支持文本组管理
 */
public class GroupedTitleCommand {
    
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess, Commands.CommandSelection environment) {
        // 添加文本到组的命令
        dispatcher.register(
            Commands.literal("title_group")
                .then(Commands.argument("targets", EntityArgument.players())
                    .then(Commands.argument("groupId", StringArgumentType.string())
                        .then(Commands.argument("text", ComponentArgument.textComponent(registryAccess))
                            .executes(context -> executeAddText(context, 0.0f, 0.0f, 1.0f, 1.0f, 10, 60, 20))
                            .then(Commands.argument("offsetX", FloatArgumentType.floatArg())
                                .then(Commands.argument("offsetY", FloatArgumentType.floatArg())
                                    .then(Commands.argument("scaleX", FloatArgumentType.floatArg())
                                        .then(Commands.argument("scaleY", FloatArgumentType.floatArg())
                                            .executes(context -> executeAddText(
                                                context,
                                                FloatArgumentType.getFloat(context, "offsetX"),
                                                FloatArgumentType.getFloat(context, "offsetY"),
                                                FloatArgumentType.getFloat(context, "scaleX"),
                                                FloatArgumentType.getFloat(context, "scaleY"),
                                                10, 60, 20
                                            ))
                                            .then(Commands.argument("fadeIn", IntegerArgumentType.integer(0))
                                                .then(Commands.argument("stay", IntegerArgumentType.integer(0))
                                                    .then(Commands.argument("fadeOut", IntegerArgumentType.integer(0))
                                                        .executes(context -> executeAddText(
                                                            context,
                                                            FloatArgumentType.getFloat(context, "offsetX"),
                                                            FloatArgumentType.getFloat(context, "offsetY"),
                                                            FloatArgumentType.getFloat(context, "scaleX"),
                                                            FloatArgumentType.getFloat(context, "scaleY"),
                                                            IntegerArgumentType.getInteger(context, "fadeIn"),
                                                            IntegerArgumentType.getInteger(context, "stay"),
                                                            IntegerArgumentType.getInteger(context, "fadeOut")
                                                        ))
                                                    )
                                                )
                                            )
                                        )
                                    )
                                )
                            )
                        )
                    )
                )
        );
        
        // 设置组级别变换的命令
        dispatcher.register(
            Commands.literal("title_group_transform")
                .then(Commands.argument("targets", EntityArgument.players())
                    .then(Commands.argument("groupId", StringArgumentType.string())
                        .then(Commands.argument("groupOffsetX", FloatArgumentType.floatArg())
                            .then(Commands.argument("groupOffsetY", FloatArgumentType.floatArg())
                                .then(Commands.argument("groupScaleX", FloatArgumentType.floatArg())
                                    .then(Commands.argument("groupScaleY", FloatArgumentType.floatArg())
                                        .executes(GroupedTitleCommand::executeSetGroupTransform)
                                    )
                                )
                            )
                        )
                    )
                )
        );
        
        // 清除组的命令
        dispatcher.register(
            Commands.literal("title_group_clear")
                .then(Commands.argument("targets", EntityArgument.players())
                    .then(Commands.argument("groupId", StringArgumentType.string())
                        .executes(GroupedTitleCommand::executeClearGroup)
                    )
                )
        );
        
        // 清除所有组的命令
        dispatcher.register(
            Commands.literal("title_group_clear_all")
                .then(Commands.argument("targets", EntityArgument.players())
                    .executes(GroupedTitleCommand::executeClearAll)
                )
        );
    }
    
    /**
     * 添加文本到组
     */
    private static int executeAddText(CommandContext<CommandSourceStack> context, float offsetX, float offsetY, float scaleX, float scaleY, int fadeIn, int stay, int fadeOut) throws CommandSyntaxException {
        Component text = ComponentArgument.getComponent(context, "text");
        String groupId = StringArgumentType.getString(context, "groupId");
        Collection<ServerPlayer> targets = EntityArgument.getPlayers(context, "targets");
        
        // 创建文本片段和组
        TextSegment segment = new TextSegment(text, offsetX, offsetY, scaleX, scaleY);
        TextGroup group = new TextGroup(groupId);
        group.addSegment(segment);
        
        for (ServerPlayer player : targets) {
            Map<String, TextGroup> groups = new HashMap<>();
            groups.put(groupId, group);
            TransformNetworkHandler.sendTitle(player, groups, fadeIn, stay, fadeOut);
        }
        
        return 1;
    }
    
    /**
     * 设置组级别变换
     */
    private static int executeSetGroupTransform(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        String groupId = StringArgumentType.getString(context, "groupId");
        float groupOffsetX = FloatArgumentType.getFloat(context, "groupOffsetX");
        float groupOffsetY = FloatArgumentType.getFloat(context, "groupOffsetY");
        float groupScaleX = FloatArgumentType.getFloat(context, "groupScaleX");
        float groupScaleY = FloatArgumentType.getFloat(context, "groupScaleY");
        Collection<ServerPlayer> targets = EntityArgument.getPlayers(context, "targets");
        
        // TODO: 需要在客户端存储组状态，这里只是示例
        // 实际实现需要更复杂的组管理机制
        
        return 1;
    }
    
    /**
     * 清除指定组
     */
    private static int executeClearGroup(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        String groupId = StringArgumentType.getString(context, "groupId");
        Collection<ServerPlayer> targets = EntityArgument.getPlayers(context, "targets");
        
        // 发送空的组来清除指定组（客户端需要特殊处理）
        // 目前先使用clearAll的方式
        for (ServerPlayer player : targets) {
            TransformNetworkHandler.clearGroup(player, groupId);
        }
        
        return 1;
    }
    
    /**
     * 清除所有组
     */
    private static int executeClearAll(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Collection<ServerPlayer> targets = EntityArgument.getPlayers(context, "targets");
        
        // 发送空的Map来清除所有组
        for (ServerPlayer player : targets) {
            TransformNetworkHandler.clearAllGroups(player);
        }
        
        return 1;
    }
}
