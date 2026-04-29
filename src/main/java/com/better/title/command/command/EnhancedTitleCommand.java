package com.better.title.command.command;

import com.better.title.command.component.TextGroup;
import com.better.title.command.component.TextSegment;
import com.better.title.command.network.TransformNetworkHandler;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
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
import java.util.Map;

/**
 * 增强的title命令 - 支持多个文本片段，每个片段有独立的变换参数
 */
public class EnhancedTitleCommand {
    
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess, Commands.CommandSelection environment) {
        // 简单模式：单个文本片段
        dispatcher.register(
            Commands.literal("enhanced_title")
                .then(Commands.argument("targets", EntityArgument.players())
                    .then(Commands.argument("text", ComponentArgument.textComponent(registryAccess))
                        .executes(context -> executeSimpleTitle(context, 0.0f, 0.0f, 1.0f, 1.0f))
                        .then(Commands.argument("offsetX", FloatArgumentType.floatArg())
                            .then(Commands.argument("offsetY", FloatArgumentType.floatArg())
                                .then(Commands.argument("scaleX", FloatArgumentType.floatArg())
                                    .then(Commands.argument("scaleY", FloatArgumentType.floatArg())
                                        .executes(context -> executeSimpleTitle(
                                            context,
                                            FloatArgumentType.getFloat(context, "offsetX"),
                                            FloatArgumentType.getFloat(context, "offsetY"),
                                            FloatArgumentType.getFloat(context, "scaleX"),
                                            FloatArgumentType.getFloat(context, "scaleY")
                                        ))
                                    )
                                )
                            )
                        )
                    )
                )
        );
        
        // TODO: 高级模式 - 支持多个文本片段（未来扩展）
    }
    
    /**
     * 执行简单title命令（单个文本片段）
     */
    private static int executeSimpleTitle(CommandContext<CommandSourceStack> context, float offsetX, float offsetY, float scaleX, float scaleY) throws CommandSyntaxException {
        Component text = ComponentArgument.getComponent(context, "text");
        Collection<ServerPlayer> targets = EntityArgument.getPlayers(context, "targets");
        
        // 创建文本片段和组
        TextSegment segment = new TextSegment(text, offsetX, offsetY, scaleX, scaleY);
        TextGroup group = new TextGroup("default");
        group.addSegment(segment);
        
        for (ServerPlayer player : targets) {
            Map<String, TextGroup> groups = new java.util.HashMap<>();
            groups.put("default", group);
            TransformNetworkHandler.sendTitle(player, groups, 10, 60, 20);
        }
        
        return 1;
    }
}
