package com.Da_Technomancer.crossroads.command;

import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.API.beams.EnumBeamAlignments;
import com.Da_Technomancer.crossroads.API.packets.StoreNBTToClient;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

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
		if(args == null || args.length != 1 || !(sender instanceof ServerPlayerEntity)){
			sender.sendMessage(new StringTextComponent("Incorrect # of arguments!"));
			return;
		}

		if(args[0].toUpperCase().equals("ALL")){
			CompoundNBT nbt = MiscUtil.getPlayerTag((PlayerEntity) sender);
			if(!nbt.hasKey("elements")){
				nbt.setTag("elements", new CompoundNBT());
			}
			nbt = nbt.getCompoundTag("elements");

			for(EnumBeamAlignments element : EnumBeamAlignments.values()){
				if(!nbt.hasKey(element.name()) && element != EnumBeamAlignments.NO_MATCH){
					nbt.setBoolean(element.name(), true);
					sender.sendMessage(new StringTextComponent(TextFormatting.BOLD.toString() + "New Element Discovered: " + element.getLocalName(false)));
				}
			}
			StoreNBTToClient.syncNBTToClient((ServerPlayerEntity) sender, false);
			return;
		}

		EnumBeamAlignments element;

		try{
			element = EnumBeamAlignments.valueOf(args[0].toUpperCase());
		}catch(IllegalArgumentException | NullPointerException e){
			sender.sendMessage(new StringTextComponent("That element does not exist!"));
			return;
		}

		CompoundNBT nbt = MiscUtil.getPlayerTag((PlayerEntity) sender);
		if(!nbt.hasKey("elements")){
			nbt.setTag("elements", new CompoundNBT());
		}
		nbt = nbt.getCompoundTag("elements");

		if(!nbt.hasKey(element.name())){
			nbt.setBoolean(element.name(), true);
			sender.sendMessage(new StringTextComponent(TextFormatting.BOLD.toString() + "New Element Discovered: " + element.getLocalName(false)));
			StoreNBTToClient.syncNBTToClient((ServerPlayerEntity) sender, false);
		}
	}

	@Override
	public int getRequiredPermissionLevel(){
		return 2;
	}
}
