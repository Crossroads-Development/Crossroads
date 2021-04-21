package com.Da_Technomancer.crossroads.tileentities.alchemy;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.alchemy.*;
import com.Da_Technomancer.crossroads.Crossroads;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.registries.ObjectHolder;

import java.util.ArrayList;

@ObjectHolder(Crossroads.MODID)
public class ReagentTankTileEntity extends AlchemyCarrierTE{

	@ObjectHolder("reagent_tank")
	private static TileEntityType<ReagentTankTileEntity> type = null;

	public static final int CAPACITY = 1_000;

	public ReagentTankTileEntity(){
		super(type);
	}

	public ReagentTankTileEntity(boolean glass){
		super(type, glass);
	}

	public float getReds(){
		return Math.min(CAPACITY, contents.getTotalQty());
	}

	@Override
	public int transferCapacity(){
		return CAPACITY;
	}

	public ReagentMap getMap(){
		return contents;
	}

	public void writeContentNBT(CompoundNBT nbt){
		contents = ReagentMap.readFromNBT(nbt);
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
	protected void performTransfer(){
		EnumTransferMode[] modes = getModes();
		for(int i = 0; i < 6; i++){
			if(modes[i].isOutput()){
				Direction side = Direction.from3DDataValue(i);
				TileEntity te = level.getBlockEntity(worldPosition.relative(side));
				LazyOptional<IChemicalHandler> otherOpt;
				if(contents.getTotalQty() <= 0 || te == null || !(otherOpt = te.getCapability(Capabilities.CHEMICAL_CAPABILITY, side.getOpposite())).isPresent()){
					continue;
				}

				IChemicalHandler otherHandler = otherOpt.orElseThrow(NullPointerException::new);
				if(otherHandler.getMode(side.getOpposite()) == EnumTransferMode.BOTH && modes[i] == EnumTransferMode.BOTH){
					continue;
				}

				if(contents.getTotalQty() != 0){
					if(otherHandler.insertReagents(contents, side.getOpposite(), handler)){
						correctReag();
						setChanged();
					}
				}
			}
		}
	}

	@Override
	public void setRemoved(){
		super.setRemoved();
		itemOpt.invalidate();
	}

	private final LazyOptional<IItemHandler> itemOpt = LazyOptional.of(ItemHandler::new);

	@SuppressWarnings("unchecked")
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side){
		if(cap == Capabilities.CHEMICAL_CAPABILITY){
			return (LazyOptional<T>) chemOpt;
		}
		if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY){
			return (LazyOptional<T>) itemOpt;
		}
		return super.getCapability(cap, side);
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
			level.playSound(null, worldPosition, sound.getBreakSound(), SoundCategory.BLOCKS, sound.getVolume(), sound.getPitch());
			AlchemyUtil.releaseChemical(level, worldPosition, contents);
		}
	}
}
