package com.Da_Technomancer.crossroads.command;

import com.Da_Technomancer.crossroads.API.MiscOp;
import com.Da_Technomancer.crossroads.API.enums.MagicElements;
import com.Da_Technomancer.crossroads.API.packets.StoreNBTToClient;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

/**
 * This command resets the player's chosen path(s), if any. 
 */
public class DiscoverElementCommand extends CommandBase{

	@Override
	public String getName(){
		return "discoverElement";
	}

	@Override
	public String getUsage(ICommandSender sender){
		return "/discoverElement <element OR all>";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException{
		if(args == null || args.length != 1 || !(sender instanceof EntityPlayerMP)){
			sender.sendMessage(new TextComponentString("Incorrect # of arguments!"));
			return;
		}

		if(args[0].toUpperCase().equals("ALL")){
			NBTTagCompound nbt = MiscOp.getPlayerTag((EntityPlayer) sender);
			if(!nbt.hasKey("elements")){
				nbt.setTag("elements", new NBTTagCompound());
			}
			nbt = nbt.getCompoundTag("elements");

			for(MagicElements element : MagicElements.values()){
				if(!nbt.hasKey(element.name()) && element != MagicElements.NO_MATCH){
					nbt.setBoolean(element.name(), true);
					sender.sendMessage(new TextComponentString(TextFormatting.BOLD.toString() + "New Element Discovered: " + element.toString()));
				}
			}
			StoreNBTToClient.syncNBTToClient((EntityPlayerMP) sender, false);
			return;
		}

		MagicElements element;

		try{
			element = MagicElements.valueOf(args[0].toUpperCase());
		}catch(IllegalArgumentException | NullPointerException e){
			sender.sendMessage(new TextComponentString("That element does not exist!"));
			return;
		}

		NBTTagCompound nbt = MiscOp.getPlayerTag((EntityPlayer) sender);
		if(!nbt.hasKey("elements")){
			nbt.setTag("elements", new NBTTagCompound());
		}
		nbt = nbt.getCompoundTag("elements");

		if(!nbt.hasKey(element.name())){
			nbt.setBoolean(element.name(), true);
			sender.sendMessage(new TextComponentString(TextFormatting.BOLD.toString() + "New Element Discovered: " + element.toString()));
			StoreNBTToClient.syncNBTToClient((EntityPlayerMP) sender, false);
		}
	}

	@Override
	public int getRequiredPermissionLevel(){
		return 2;
	}
}
