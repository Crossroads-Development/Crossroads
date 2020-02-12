package com.Da_Technomancer.crossroads.command;

import com.Da_Technomancer.crossroads.Crossroads;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;

public class CRCommands{

	public static void init(CommandDispatcher<CommandSource> dispatcher){
		//Register our commands, which are executed by /crossroads <name of command> <command args>
		dispatcher.register(Commands.literal(Crossroads.MODID)
				.then(DiscoverElementCommand.register(dispatcher))
				.then(SetPathCommand.register(dispatcher)));
	}
}
