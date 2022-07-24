package com.Da_Technomancer.crossroads.blocks.alchemy;

import com.Da_Technomancer.crossroads.api.MiscUtil;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.blocks.CRTileEntity;
import com.Da_Technomancer.essentials.api.ITickableTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;

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
	public void saveAdditional(CompoundTag nbt){
		super.saveAdditional(nbt);
		nbt.putInt("lif", lifespan);
		if(target != null){
			nbt.putString("tar", MiscUtil.getRegistryName(target.getBlock(), ForgeRegistries.BLOCKS).toString());
		}
	}
}
