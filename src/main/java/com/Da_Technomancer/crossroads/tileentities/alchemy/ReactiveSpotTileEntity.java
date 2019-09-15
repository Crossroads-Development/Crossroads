package com.Da_Technomancer.crossroads.tileentities.alchemy;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickableTileEntity;

public class ReactiveSpotTileEntity extends TileEntity implements ITickableTileEntity{

	private BlockState target;
	private int lifespan = 0;

	public void setTarget(BlockState targetIn){
		this.target = targetIn;
		markDirty();
	}

	@Override
	public void update(){
		if(target == null){
			world.setBlockState(pos, Blocks.AIR.getDefaultState());
		}else{
			if(lifespan++ >= 30){
				world.setBlockState(pos, target, 3);
			}
		}

	}

	@Override
	public void readFromNBT(CompoundNBT nbt){
		super.readFromNBT(nbt);
		lifespan = nbt.getInteger("lif");
		Block b = Block.getBlockFromName(nbt.getString("tar"));
		if(b == null){
			target = Blocks.AIR.getDefaultState();
		}else{
			target = b.getStateFromMeta(nbt.getInteger("met"));
		}
	}

	@Override
	public CompoundNBT writeToNBT(CompoundNBT nbt){
		super.writeToNBT(nbt);
		nbt.setInteger("lif", lifespan);
		if(target != null){
			nbt.setString("tar", target.getBlock().getRegistryName().toString());
			nbt.setInteger("met", target.getBlock().getMetaFromState(target));
		}
		return nbt;
	}
}
