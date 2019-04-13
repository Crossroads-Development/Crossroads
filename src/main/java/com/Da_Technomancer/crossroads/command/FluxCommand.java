package com.Da_Technomancer.crossroads.command;

import com.Da_Technomancer.crossroads.API.alchemy.AlchemyCore;
import com.Da_Technomancer.crossroads.API.alchemy.IReagent;
import com.Da_Technomancer.crossroads.API.alchemy.ReagentMap;
import com.Da_Technomancer.crossroads.API.heat.HeatUtil;
import com.Da_Technomancer.crossroads.API.technomancy.EntropySavedData;
import com.Da_Technomancer.crossroads.items.ModItems;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

public class FluxCommand extends CommandBase{

	@Override
	public String getName(){
		return "setEntropy";
	}

	@Override
	public String getUsage(ICommandSender sender){
		return "/setEntropy <value>";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException{
		if(args.length != 1){
			throw new CommandException("Incorrect # of arguments! Should be 1");
		}

		double val;
		try{
			val = Double.valueOf(args[0]);
		}catch(NumberFormatException | NullPointerException e){
			throw new CommandException("Invalid input!");
		}

		EntropySavedData.addEntropy(sender.getEntityWorld(), EntropySavedData.getPts(val - EntropySavedData.getEntropy(sender.getEntityWorld())));
	}

	@Override
	public int getRequiredPermissionLevel(){
		return 2;
	}
}
