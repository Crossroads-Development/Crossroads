package com.Da_Technomancer.crossroads.command;

import com.Da_Technomancer.crossroads.API.alchemy.AlchemyCore;
import com.Da_Technomancer.crossroads.API.alchemy.ReagentStack;
import com.Da_Technomancer.crossroads.items.ModItems;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

public class SpawnReagentCommand extends CommandBase{

	@Override
	public String getName(){
		return "spawnReagent";
	}

	@Override
	public String getUsage(ICommandSender sender){
		return "/spawnReagent <id> <temp(C)>";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException{
		if(args == null || args.length != 2 || !(sender instanceof EntityPlayerMP)){
			sender.sendMessage(new TextComponentString("Incorrect # of arguments!"));
			return;
		}
		
		int id;
		double temp;
		try{
			id = Integer.valueOf(args[0]);
			temp = Double.valueOf(args[1]);
			if(id >= AlchemyCore.REAGENT_COUNT || id < 0 || AlchemyCore.REAGENTS[id] == null || temp < -273D){
				sender.sendMessage(new TextComponentString("Invalid input!"));
				return;
			}
		}catch(NumberFormatException | NullPointerException e){
			sender.sendMessage(new TextComponentString("Invalid input!"));
			return;
		}

		ItemStack toGive = new ItemStack(ModItems.phial, 1, 1);
		
		ReagentStack[] reag = new ReagentStack[AlchemyCore.REAGENT_COUNT];
		reag[id] = new ReagentStack(AlchemyCore.REAGENTS[id], ModItems.phial.getCapacity());
		reag[id].updatePhase(temp);
		ModItems.phial.setReagents(toGive, reag, (temp + 273D) * ModItems.phial.getCapacity(), ModItems.phial.getCapacity());
		((EntityPlayerMP) sender).addItemStackToInventory(toGive);
	}

	@Override
	public int getRequiredPermissionLevel(){
		return 2;
	}
}
