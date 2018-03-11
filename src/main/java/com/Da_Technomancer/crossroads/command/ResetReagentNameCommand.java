package com.Da_Technomancer.crossroads.command;

import com.Da_Technomancer.crossroads.API.alchemy.AlchemWorldSavedData;
import com.Da_Technomancer.crossroads.API.alchemy.AlchemyCore;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

public class ResetReagentNameCommand extends CommandBase{

	@Override
	public String getName(){
		return "resetReagentName";
	}

	@Override
	public String getUsage(ICommandSender sender){
		return "/resetReagentName <index (" + AlchemyCore.RESERVED_REAGENT_COUNT + "-" + (AlchemyCore.REAGENT_COUNT - 1) +  ")>";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException{
		if(args == null || args.length != 1){
			sender.sendMessage(new TextComponentString("Incorrect # of arguments!"));
			return;
		}

		int id;
		try{
			id = Integer.valueOf(args[0]);
			if(id >= AlchemyCore.REAGENT_COUNT || id < AlchemyCore.RESERVED_REAGENT_COUNT){
				sender.sendMessage(new TextComponentString("Invalid id!"));
				return;
			}
		}catch(NumberFormatException | NullPointerException e){
			sender.sendMessage(new TextComponentString("Invalid input!"));
			return;
		}

		AlchemyCore.CUST_REAG_NAMES[id - AlchemyCore.RESERVED_REAGENT_COUNT] = null;
		AlchemWorldSavedData.saveData(server.getWorld(0));
	}

	@Override
	public int getRequiredPermissionLevel(){
		return 2;
	}
}
