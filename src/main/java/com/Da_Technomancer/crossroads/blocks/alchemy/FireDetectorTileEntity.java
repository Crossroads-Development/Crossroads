package com.Da_Technomancer.crossroads.blocks.alchemy;

import com.Da_Technomancer.crossroads.api.alchemy.FlameCoresSavedData;
import com.Da_Technomancer.crossroads.api.templates.IInfoTE;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.blocks.CRTileEntity;
import com.Da_Technomancer.essentials.api.ITickableTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import java.util.ArrayList;

public class FireDetectorTileEntity extends BlockEntity implements ITickableTileEntity, IInfoTE{

	public static final BlockEntityType<FireDetectorTileEntity> TYPE = CRTileEntity.createType(FireDetectorTileEntity::new, CRBlocks.fireDetector);

	public static final int RANGE = 100;

	private int redstone = 0;

	public FireDetectorTileEntity(BlockPos pos, BlockState state){
		super(TYPE, pos, state);
	}

	@Override
	public void addInfo(ArrayList<Component> chat, Player player, BlockHitResult hit){
		if(redstone >= RANGE){
			chat.add(Component.translatable("tt.crossroads.fire_detector.status.no", redstone));
		}else{
			chat.add(Component.translatable("tt.crossroads.fire_detector.status.yes", redstone));
		}
	}

	public int getRedstone(){
		return redstone;
	}

	@Override
	public void serverTick(){
		//Don't recheck every tick
		if(level.getGameTime() % 4 == 0){
			int prevReds = redstone;
			redstone = FlameCoresSavedData.getFlameCores((ServerLevel) level).stream().map(core -> {
				//This uses Chebyshev distance to the edge of the flame cloud
				int radius = core.getRadius();
				BlockPos relPos = core.blockPosition().subtract(worldPosition);
				int distance = Math.max(Math.abs(relPos.getX()), Math.max(Math.abs(relPos.getY()), Math.abs(relPos.getZ())));
				distance -= radius;
				return Math.min(distance, RANGE);
			}).min(Integer::compare).orElse(RANGE);
			if(prevReds != redstone){
				setChanged();
			}
		}
	}

	@Override
	public void setBlockState(BlockState pBlockState){
		super.setBlockState(pBlockState);
		//This is not, strictly speaking, optimized
		//Pre MC1.21, default behavior for all TEs was that changing blockstate invalidated capability caches
		//Post MC1.21, this is no longer the case, which opens up some opportunities for optimization
		//But everything was written with the assumption of invalidation on state change,
		//So anything other than re-implementing the old default is going to introduce a lot of new bugs
		level.invalidateCapabilities(worldPosition);
	}

	@Override
	public void loadAdditional(CompoundTag nbt, HolderLookup.Provider registries){
		super.loadAdditional(nbt, registries);
		redstone = nbt.getInt("reds");
	}

	@Override
	protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider pRegistries){
		super.saveAdditional(nbt, pRegistries);
		nbt.putInt("reds", redstone);
	}
}
