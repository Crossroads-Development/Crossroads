package com.Da_Technomancer.crossroads.command;

import com.Da_Technomancer.crossroads.API.alchemy.AlchemyCore;
import com.Da_Technomancer.crossroads.API.alchemy.IReagent;
import com.Da_Technomancer.crossroads.API.alchemy.ReagentMap;
import com.Da_Technomancer.crossroads.API.heat.HeatUtil;
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
		if(args.length != 2 || !(sender instanceof EntityPlayerMP)){
			throw new CommandException("Incorrect # of arguments!");
		}
		
		String id = args[0];
		double temp;
		IReagent type = AlchemyCore.REAGENTS.get(id);
		try{
			temp = Double.valueOf(args[1]);
			if(type == null || temp < HeatUtil.ABSOLUTE_ZERO){
				sender.sendMessage(new TextComponentString("Invalid input!"));
				return;
			}
		}catch(NumberFormatException | NullPointerException e){
			throw new CommandException("Invalid input!");
		}

		ItemStack toGive = new ItemStack(ModItems.phial, 1, 1);
		
		ReagentMap reag = new ReagentMap();
		reag.put(type, ModItems.phial.getCapacity());
		ModItems.phial.setReagents(toGive, reag, HeatUtil.toKelvin(temp) * ModItems.phial.getCapacity());
		((EntityPlayerMP) sender).addItemStackToInventory(toGive);
	}

	@Override
	public int getRequiredPermissionLevel(){
		return 2;
	}
}
