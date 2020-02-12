package com.Da_Technomancer.crossroads.command;

import com.Da_Technomancer.crossroads.API.EnumPath;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Locale;

/**
 * This command (re)-sets the player's chosen path(s), if any.
 */
public class SetPathCommand implements Command<CommandSource>{

	private static final SetPathCommand INSTANCE = new SetPathCommand();
	private static final String RESET = "NONE";//Argument that can be passed to reset player path(s)

	public static ArgumentBuilder<CommandSource, ?> register(CommandDispatcher<CommandSource> dispatcher){
		//Suggested values: lowercase names of all alignments other than no match, and the RESET command
		String[] suggestVals = new String[EnumPath.values().length + 1];
		suggestVals[0] = RESET.toLowerCase(Locale.US);
		int ind = 1;
		for(EnumPath align : EnumPath.values()){
			suggestVals[ind] = align.name().toLowerCase(Locale.US);
			ind++;
		}
		return Commands.literal("paths").requires(cs -> cs.getEntity() instanceof PlayerEntity && cs.hasPermissionLevel(2))
				.then(Commands.argument("path", StringArgumentType.word())
						.suggests((context, builder) -> ISuggestionProvider.suggest(suggestVals, builder)))
				.executes(INSTANCE);

	}


	@Override
	public int run(CommandContext<CommandSource> context) throws CommandSyntaxException{
		String arg = context.getArgument("path", String.class);
		arg = arg.toUpperCase(Locale.US);
		PlayerEntity player = context.getSource().asPlayer();
		if(arg.equals(RESET)){
			for(EnumPath path : EnumPath.values()){
				path.setUnlocked(player, false);
			}
		}else{
			try{
				EnumPath align = EnumPath.valueOf(arg);
				align.setUnlocked(player, true);
			}catch(IllegalArgumentException e){
				TranslationTextComponent message = new TranslationTextComponent("crossroads.command.paths.no_path");
				throw new CommandSyntaxException(new SimpleCommandExceptionType(message), message);
			}
		}
		return 0;
	}
}
