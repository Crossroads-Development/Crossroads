package com.Da_Technomancer.crossroads.blocks.alchemy;

import com.Da_Technomancer.crossroads.api.CRCapabilities;
import com.Da_Technomancer.crossroads.api.alchemy.*;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.blocks.CRTileEntity;
import com.Da_Technomancer.essentials.api.IItemCapable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import net.neoforged.neoforge.items.IItemHandler;
import org.apache.commons.lang3.tuple.Pair;
import org.joml.Vector3f;

import javax.annotation.Nullable;
import java.util.ArrayList;

public class ReagentTankTileEntity extends ReagentHolderTE implements IItemCapable{

	public static final BlockEntityType<ReagentTankTileEntity> TYPE = CRTileEntity.createType(ReagentTankTileEntity::new, CRBlocks.reagentTankGlass, CRBlocks.reagentTankCrystal);

	public static final int CAPACITY = 1024;

	private final IItemHandler itemHandler = new ItemHandler();

	private static final Pair<Vector3f, Vector3f>[] RENDER_SHAPE = new Pair[] {Pair.of(new Vector3f(0.02F, 0.02F, 0.02F), new Vector3f(0.98F, 0.98F, 0.98F))};

	public ReagentTankTileEntity(BlockPos pos, BlockState state){
		super(TYPE, pos, state);
	}

	public ReagentTankTileEntity(BlockPos pos, BlockState state, boolean glass){
		super(TYPE, pos, state, glass);
	}

	public float getReds(){
		return Math.min(CAPACITY, contents.getTotalQty());
	}

	@Override
	public int transferCapacity(){
		return CAPACITY;
	}

	@Override
	public Pair<Vector3f, Vector3f>[] getRenderVolumes(){
		return RENDER_SHAPE;
	}

	public ReagentMap getMap(){
		return contents;
	}

	public void setMap(ReagentMap map){
		contents = map;
		dirtyReag = true;
	}

	@Override
	public EnumContainerType getChannel(){
		return EnumContainerType.NONE;
	}

	@Override
	protected EnumTransferMode[] getModes(){
		return new EnumTransferMode[] {EnumTransferMode.BOTH, EnumTransferMode.BOTH, EnumTransferMode.BOTH, EnumTransferMode.BOTH, EnumTransferMode.BOTH, EnumTransferMode.BOTH};
	}

	@Override
	public void correctReag(){
		super.correctReag();
		correctTemp();

		boolean destroy = false;

		ArrayList<IReagent> toRemove = new ArrayList<>(1);

		for(IReagent type : contents.keySetReag()){
			ReagentStack reag = contents.getStack(type);
			if(reag.isEmpty()){
				continue;
			}
			if(glass && reag.getType().requiresCrystal()){
				destroy |= reag.getType().destroysBadContainer();
				toRemove.add(type);
			}
		}

		if(destroy){
			destroyChamber();
		}else{
			for(IReagent type : toRemove){
				contents.removeReagent(type, contents.get(type));
			}
		}
	}

	private boolean broken = false;

	private void destroyChamber(){
		if(!broken){
			broken = true;
			BlockState state = level.getBlockState(worldPosition);
			level.setBlockAndUpdate(worldPosition, Blocks.AIR.defaultBlockState());
			SoundType sound = state.getBlock().getSoundType(state, level, worldPosition, null);
			level.playSound(null, worldPosition, sound.getBreakSound(), SoundSource.BLOCKS, sound.getVolume(), sound.getPitch());
			AlchemyUtil.releaseChemical(level, worldPosition, contents);
		}
	}

	@Override
	@Nullable
	public IChemicalHandler getChemicalHandler(Direction dir){
		return chemHandler;
	}

	@Override
	@Nullable
	public IItemHandler getItemHandler(Direction direction){
		return itemHandler;
	}
}
