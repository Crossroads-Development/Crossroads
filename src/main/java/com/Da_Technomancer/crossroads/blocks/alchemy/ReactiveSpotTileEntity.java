package com.Da_Technomancer.crossroads.blocks.alchemy;

import com.Da_Technomancer.crossroads.api.MiscUtil;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.blocks.CRTileEntity;
import com.Da_Technomancer.essentials.api.ITickableTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class ReactiveSpotTileEntity extends BlockEntity implements ITickableTileEntity{

	public static final BlockEntityType<ReactiveSpotTileEntity> TYPE = CRTileEntity.createType(ReactiveSpotTileEntity::new, CRBlocks.reactiveSpot);

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
	public void loadAdditional(CompoundTag nbt, HolderLookup.Provider registries){
		super.loadAdditional(nbt, registries);
		lifespan = nbt.getInt("lif");
		Block b = BuiltInRegistries.BLOCK.get(ResourceLocation.parse(nbt.getString("tar")));
		if(b == null){
			target = Blocks.AIR.defaultBlockState();
		}else{
			target = b.defaultBlockState();
		}
	}

	@Override
	protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider pRegistries){
		super.saveAdditional(nbt, pRegistries);
		nbt.putInt("lif", lifespan);
		if(target != null){
			nbt.putString("tar", MiscUtil.getRegistryName(target.getBlock(), BuiltInRegistries.BLOCK).toString());
		}
	}
}
