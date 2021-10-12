package com.Da_Technomancer.crossroads.tileentities.alchemy;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.essentials.tileentities.ITickableTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.nbt.CompoundTag;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(Crossroads.MODID)
public class ReactiveSpotTileEntity extends BlockEntity implements ITickableTileEntity{

	@ObjectHolder("reactive_spot")
	public static BlockEntityType<ReactiveSpotTileEntity> TYPE = null;

	private BlockState target;
	private int lifespan = 0;

	public ReactiveSpotTileEntity(BlockPos pos, BlockState state){
		super(TYPE, pos, state);
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
	public void load(CompoundTag nbt){
		super.load(nbt);
		lifespan = nbt.getInt("lif");
		Block b = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(nbt.getString("tar")));
		if(b == null){
			target = Blocks.AIR.defaultBlockState();
		}else{
			target = b.defaultBlockState();
		}
	}

	@Override
	public CompoundTag save(CompoundTag nbt){
		super.save(nbt);
		nbt.putInt("lif", lifespan);
		if(target != null){
			nbt.putString("tar", target.getBlock().getRegistryName().toString());
		}
		return nbt;
	}
}
