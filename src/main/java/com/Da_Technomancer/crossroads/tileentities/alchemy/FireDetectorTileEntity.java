package com.Da_Technomancer.crossroads.tileentities.alchemy;

import com.Da_Technomancer.crossroads.API.IInfoTE;
import com.Da_Technomancer.crossroads.API.alchemy.FlameCoresSavedData;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.essentials.tileentities.ITickableTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.registries.ObjectHolder;

import java.util.ArrayList;

@ObjectHolder(Crossroads.MODID)
public class FireDetectorTileEntity extends BlockEntity implements ITickableTileEntity, IInfoTE{

	@ObjectHolder("fire_detector")
	public static BlockEntityType<FireDetectorTileEntity> TYPE = null;

	public static final int RANGE = 100;

	private int redstone = 0;

	public FireDetectorTileEntity(BlockPos pos, BlockState state){
		super(TYPE, pos, state);
	}

	@Override
	public void addInfo(ArrayList<Component> chat, Player player, BlockHitResult hit){
		if(redstone >= RANGE){
			chat.add(new TranslatableComponent("tt.crossroads.fire_detector.status.no", redstone));
		}else{
			chat.add(new TranslatableComponent("tt.crossroads.fire_detector.status.yes", redstone));
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
	public void load(CompoundTag nbt){
		super.load(nbt);
		redstone = nbt.getInt("reds");
	}

	@Override
	protected void saveAdditional(CompoundTag nbt){
		super.saveAdditional(nbt);
		nbt.putInt("reds", redstone);
	}
}
