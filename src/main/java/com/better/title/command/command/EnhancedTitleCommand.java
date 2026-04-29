package com.better.title.command.command;

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

/**
 * 增强的title命令 - 支持文本组件和变换参数
 */
public class EnhancedTitleCommand {
    
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess, Commands.CommandSelection environment) {
        dispatcher.register(
            Commands.literal("enhanced_title")
                .then(Commands.argument("targets", EntityArgument.players())
                    .then(Commands.argument("text", ComponentArgument.textComponent(registryAccess))
                        .executes(context -> executeTitle(context, 0.0f, 0.0f, 1.0f, 1.0f))
                        .then(Commands.argument("offsetX", FloatArgumentType.floatArg())
                            .then(Commands.argument("offsetY", FloatArgumentType.floatArg())
                                .then(Commands.argument("scaleX", FloatArgumentType.floatArg())
                                    .then(Commands.argument("scaleY", FloatArgumentType.floatArg())
                                        .executes(context -> executeTitle(
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
    }
    
    private static int executeTitle(CommandContext<CommandSourceStack> context, float offsetX, float offsetY, float scaleX, float scaleY) throws CommandSyntaxException {
        Component text = ComponentArgument.getComponent(context, "text");
        Collection<ServerPlayer> targets = EntityArgument.getPlayers(context, "targets");
        
        for (ServerPlayer player : targets) {
            // 发送带变换的title（直接发送自定义数据包）
            TransformNetworkHandler.sendTitle(
                player, 
                text, 
                Component.empty(), // subtitle
                offsetX, offsetY, scaleX, scaleY,
                10, 60, 20 // fadeIn, stay, fadeOut
            );
        }
        
        return 1;
    }
}
