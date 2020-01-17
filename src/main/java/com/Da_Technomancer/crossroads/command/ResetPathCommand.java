package com.Da_Technomancer.crossroads.command;

import com.Da_Technomancer.crossroads.API.packets.StoreNBTToClient;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.StringTextComponent;

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
		return "/resetPath [player]";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException{
		if(args == null || args.length > 1){
			sender.sendMessage(new StringTextComponent("Invalid # of arguments!"));
			return;
		}
		
		ServerPlayerEntity target = args.length == 1 ? server.getPlayerList().getPlayerByUsername(args[0]) : sender instanceof ServerPlayerEntity ? ((ServerPlayerEntity) sender) : null;
		
		if(target != null){
			StoreNBTToClient.getPlayerTag(target).remove("path");
			StoreNBTToClient.syncNBTToClient(target, false);
			target.sendMessage(new StringTextComponent("Your path has been reset."));
		}else{
			sender.sendMessage(new StringTextComponent("Target player does not exist!"));
		}
	}

	@Override
	public int getRequiredPermissionLevel(){
		return 2;
	}
}
