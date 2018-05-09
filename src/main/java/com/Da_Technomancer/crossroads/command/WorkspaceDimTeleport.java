package com.Da_Technomancer.crossroads.command;

import com.Da_Technomancer.crossroads.dimensions.ModDimensions;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ITeleporter;

/**
 * This command teleports the player to (0, 33, 0) in their personal Workspace Dimension. If none exists, one is created. 
 */
public class WorkspaceDimTeleport extends CommandBase{

	@Override
	public String getName(){
		return "tpWorkspaceDim";
	}

	@Override
	public String getUsage(ICommandSender sender){
		return "/tpWorkspaceDim";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args){
		if(sender instanceof EntityPlayerMP){
			int dimId = ModDimensions.getDimForPlayer((EntityPlayerMP) sender);
			server.getPlayerList().transferPlayerToDimension((EntityPlayerMP) sender, dimId, new NoPortalTeleporter());
		}
	}

	@Override
	public int getRequiredPermissionLevel(){
		return 2;
	}

	private static class NoPortalTeleporter implements ITeleporter{

		@Override
		public void placeEntity(World world, Entity entity, float yaw){
			world.getBlockState(new BlockPos(0, 33, 0));
			entity.setPosition(.5D, 33, .5D);
			entity.motionX = 0;
			entity.motionY = 0;
			entity.motionZ = 0;

			if(entity instanceof EntityPlayer){
				((EntityPlayer) entity).addExperienceLevel(0);
			}
		}
	}
}
