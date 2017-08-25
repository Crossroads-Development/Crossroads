package com.Da_Technomancer.crossroads.command;

import com.Da_Technomancer.crossroads.API.MiscOp;
import com.Da_Technomancer.crossroads.API.packets.StoreNBTToClient;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

/**
 * This command resets the player's chosen path(s), if any. 
 */
public class ResetPathCommand extends CommandBase{

	@Override
	public String getName(){
		return "resetPath";
	}

	@Override
	public String getUsage(ICommandSender sender){
		return "/resetPath";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException{
		if(sender instanceof EntityPlayerMP){
			MiscOp.getPlayerTag((EntityPlayer) sender).removeTag("path");
			StoreNBTToClient.syncNBTToClient((EntityPlayerMP) sender, false);
		}
	}

	@Override
	public int getRequiredPermissionLevel(){
		return 2;
	}
}
