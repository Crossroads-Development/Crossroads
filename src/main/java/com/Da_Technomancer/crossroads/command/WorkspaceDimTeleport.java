package com.Da_Technomancer.crossroads.command;

import javax.annotation.Nonnull;

import com.Da_Technomancer.crossroads.dimensions.ModDimensions;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Teleporter;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

/**
 * This command teleports the player to (0, 30, 0) in their personal Workspace Dimension. If none exists, one is created. 
 */
public class WorkspaceDimTeleport extends CommandBase{

	@Override
	public String getName(){
		return "tpWorkspaceDim";
	}

	@Override
	public String getUsage(ICommandSender sender){
		return "tpWorkspaceDim";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException{
		if(sender instanceof EntityPlayerMP){
			int dimId = ModDimensions.getDimForPlayer((EntityPlayerMP) sender);
			server.getPlayerList().transferPlayerToDimension((EntityPlayerMP) sender, dimId, new NoPortalTeleporter(server.worldServerForDimension(dimId)));
		}
	}

	@Override
	public int getRequiredPermissionLevel(){
		return 2;
	}

	private static class NoPortalTeleporter extends Teleporter{

		private final World world;
		
		public NoPortalTeleporter(WorldServer worldIn){
			super(worldIn);
			world = worldIn;
		}

		@Override
		public void placeInPortal(@Nonnull Entity entity, float rotationYaw) {
			world.getBlockState(new BlockPos(0, 33, 0));
			entity.setPosition(.5D, 33, .5D);
			entity.motionX = 0.0f;
			entity.motionY = 0.0f;
			entity.motionZ = 0.0f;
			if(entity instanceof EntityPlayer){
				((EntityPlayer) entity).addExperienceLevel(0);
			}
		}
	}
}
