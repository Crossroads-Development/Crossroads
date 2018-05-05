package com.Da_Technomancer.crossroads.tileentities.alchemy;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;

public class ReactiveSpotTileEntity extends TileEntity implements ITickable{

	private IBlockState target;
	private int lifespan = 0;

	public void setTarget(IBlockState targetIn){
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
	public void readFromNBT(NBTTagCompound nbt){
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
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		nbt.setInteger("lif", lifespan);
		if(target != null){
			nbt.setString("tar", target.getBlock().getRegistryName().toString());
			nbt.setInteger("met", target.getBlock().getMetaFromState(target));
		}
		return nbt;
	}
}
