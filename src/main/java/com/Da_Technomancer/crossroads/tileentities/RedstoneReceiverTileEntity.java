package com.Da_Technomancer.crossroads.tileentities;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.IInfoTE;
import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.API.redstone.IAdvancedRedstoneHandler;
import com.Da_Technomancer.crossroads.API.templates.ILinkTE;
import com.Da_Technomancer.crossroads.CrossroadsConfig;
import com.Da_Technomancer.crossroads.blocks.CrossroadsBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;
import java.util.ArrayList;

public class RedstoneReceiverTileEntity extends TileEntity implements IInfoTE, ILinkTE{

	private BlockPos src = null;

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, BlockState oldState, BlockState newState){
		return oldState.getBlock() != newState.getBlock();
	}

	@Override
	public void receiveLong(byte identifier, long message, @Nullable ServerPlayerEntity sendingPlayer){
		//No-Op, doesn't create links
	}

	@Override
	public void addInfo(ArrayList<String> chat, PlayerEntity player, Direction side, BlockRayTraceResult hit){
		if(src == null){
			chat.add("No linked transmitter");
		}else{
			chat.add("Linked Position: X=" + (pos.getX() + src.getX()) + " Y=" + (pos.getY() + src.getY()) + " Z=" + (pos.getZ() + src.getZ()));
		}
	}

	@Override
	public boolean canBeginLinking(){
		return false;
	}

	public void setSrc(BlockPos srcIn){
		src = srcIn;
		markDirty();
		world.notifyNeighborsOfStateChange(pos, CrossroadsBlocks.redstoneReceiver, true);
	}

	public void dye(DyeColor color){
		if(world.getBlockState(pos).get(Properties.COLOR) != color){
			world.setBlockState(pos, world.getBlockState(pos).with(Properties.COLOR, color));
			if(src != null){
				BlockPos worldSrc = pos.add(src);
				TileEntity srcTE = world.getTileEntity(worldSrc);
				if(srcTE instanceof RedstoneTransmitterTileEntity){
					((RedstoneTransmitterTileEntity) srcTE).dye(color);
				}
			}
		}
	}
	
	@Override
	public void read(CompoundNBT nbt){
		super.read(nbt);
		if(nbt.contains("src")){
			src = BlockPos.fromLong(nbt.getLong("src"));
		}
	}
	
	@Override
	public CompoundNBT write(CompoundNBT nbt){
		super.write(nbt);
		if(src != null){
			nbt.setLong("src", src.toLong());
		}
		return nbt;
	}

	public double getStrength(){
		if(src != null){
			TileEntity te = world.getTileEntity(pos.add(src));
			if(te instanceof RedstoneTransmitterTileEntity){
				return ((RedstoneTransmitterTileEntity) te).getOutput();
			}
		}
		return 0;
	}

	@Override
	public boolean hasCapability(Capability<?> capability, @Nullable Direction facing){
		return capability == Capabilities.ADVANCED_REDSTONE_CAPABILITY || super.hasCapability(capability, facing);
	}

	private final RedstoneHandler redsHandler = new RedstoneHandler();

	@Nullable
	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, @Nullable Direction facing){
		if(capability == Capabilities.ADVANCED_REDSTONE_CAPABILITY){
			return (T) redsHandler;
		}
		return super.getCapability(capability, facing);
	}

	@Override
	public TileEntity getTE(){
		return this;
	}

	@Override
	public boolean canLink(ILinkTE otherTE){
		return false;
	}

	@Override
	public ArrayList<BlockPos> getLinks(){
		return new ArrayList<>(1);
	}

	@Override
	public int getMaxLinks(){
		return 0;
	}

	@Override
	public int getRange(){
		return CrossroadsConfig.redstoneTransmitterRange.get();
	}

	private class RedstoneHandler implements IAdvancedRedstoneHandler{

		@Override
		public double getOutput(boolean measure){
			return getStrength();
		}
	}
}
