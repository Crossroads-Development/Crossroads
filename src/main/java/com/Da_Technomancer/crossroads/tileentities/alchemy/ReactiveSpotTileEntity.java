package com.Da_Technomancer.crossroads.tileentities.alchemy;

import com.Da_Technomancer.crossroads.Crossroads;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(Crossroads.MODID)
public class ReactiveSpotTileEntity extends TileEntity implements ITickableTileEntity{

	@ObjectHolder("reactive_spot")
	private static TileEntityType<ReactiveSpotTileEntity> type = null;

	private BlockState target;
	private int lifespan = 0;

	public ReactiveSpotTileEntity(){
		super(type);
	}

	public void setTarget(BlockState targetIn){
		this.target = targetIn;
		setChanged();
	}

	@Override
	public void tick(){
		if(target == null){
			level.setBlockAndUpdate(worldPosition, Blocks.AIR.defaultBlockState());
		}else{
			if(lifespan++ >= 30){
				level.setBlock(worldPosition, target, 3);
			}
		}

	}

	@Override
	public void load(BlockState state, CompoundNBT nbt){
		super.load(state, nbt);
		lifespan = nbt.getInt("lif");
		Block b = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(nbt.getString("tar")));
		if(b == null){
			target = Blocks.AIR.defaultBlockState();
		}else{
			target = b.defaultBlockState();
		}
	}

	@Override
	public CompoundNBT save(CompoundNBT nbt){
		super.save(nbt);
		nbt.putInt("lif", lifespan);
		if(target != null){
			nbt.putString("tar", target.getBlock().getRegistryName().toString());
		}
		return nbt;
	}
}
