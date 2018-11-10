package com.Da_Technomancer.crossroads.tileentities.technomancy;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.EnergyConverters;
import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.API.magic.EnumMagicElements;
import com.Da_Technomancer.crossroads.API.magic.IMagicHandler;
import com.Da_Technomancer.crossroads.API.magic.MagicUnit;
import com.Da_Technomancer.crossroads.API.technomancy.FieldWorldSavedData;
import com.Da_Technomancer.crossroads.API.templates.InventoryTE;
import com.Da_Technomancer.crossroads.ModConfig;
import com.Da_Technomancer.crossroads.fluids.BlockMoltenCopshowium;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.ChunkPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

import javax.annotation.Nullable;

public class CopshowiumCreationChamberTileEntity extends InventoryTE{

	public CopshowiumCreationChamberTileEntity(){
		super(0);
		fluidProps[0] = new TankProperty(0, CAPACITY, true, true, (Fluid f) -> f != null && (f.getName().equals(ModConfig.getConfigString(ModConfig.cccExpenLiquid, false)) || f.getName().equals(ModConfig.getConfigString(ModConfig.cccFieldLiquid, false))));//Input
		fluidProps[1] = new TankProperty(1, CAPACITY, false, true);//Copshowium
	}

	@Override
	protected int fluidTanks(){
		return 2;
	}

	public static final int CAPACITY = 1_296;

	private final FluidHandler inputHandler = new FluidHandler(0);
	private final FluidHandler outputHandler = new FluidHandler(1);
	private final FluidHandler internalHandler = new FluidHandler(-1);
	private final IMagicHandler magicHandler = new MagicHandler();

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing){
		if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY){
			return facing == null ? (T) internalHandler : facing == EnumFacing.UP ? (T) inputHandler : facing == EnumFacing.DOWN ? (T) outputHandler : null;
		}

		if(capability == Capabilities.MAGIC_HANDLER_CAPABILITY && (facing == null || facing.getAxis() != EnumFacing.Axis.Y)){
			return (T) magicHandler;
		}

		return super.getCapability(capability, facing);
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction){
		return false;
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack){
		return false;
	}

	@Override
	public String getName(){
		return "container.copshowium_maker";
	}

	private class MagicHandler implements IMagicHandler{

		@Override
		public void setMagic(MagicUnit mag){
			if(EnumMagicElements.getElement(mag) == EnumMagicElements.TIME && fluids[0] != null){
				if(fluids[0].getFluid().getName().equals(ModConfig.getConfigString(ModConfig.cccExpenLiquid, false))){
					fluids[1] = new FluidStack(BlockMoltenCopshowium.getMoltenCopshowium(), (int) (((double) fluids[0].amount) * EnergyConverters.COPSHOWIUM_PER_COPPER) + (fluids[1] == null ? 0 : fluids[1].amount));
					fluids[0] = null;
					markDirty();
					if(fluids[1].amount > CAPACITY){
						world.setBlockState(pos, BlockMoltenCopshowium.getMoltenCopshowium().getBlock().getDefaultState());
					}
				}else if(fluids[0].getFluid().getName().equals(ModConfig.getConfigString(ModConfig.cccFieldLiquid, false))){
					FieldWorldSavedData data = FieldWorldSavedData.get(world);
					if(data.fieldNodes.containsKey(MiscUtil.getLongFromChunkPos(new ChunkPos(pos)))){
						if(data.fieldNodes.get(MiscUtil.getLongFromChunkPos(new ChunkPos(pos))).flux + 1 < 8 * (fluids[0].amount / 72)){
							return;
						}

						data.fieldNodes.get(MiscUtil.getLongFromChunkPos(new ChunkPos(pos))).fluxForce -= 8 * (fluids[0].amount / 72);

						fluids[1] = new FluidStack(BlockMoltenCopshowium.getMoltenCopshowium(), (int) (((double) fluids[0].amount) * EnergyConverters.COPSHOWIUM_PER_COPPER) + (fluids[1] == null ? 0 : fluids[1].amount));
						fluids[0] = null;
						markDirty();
						if(fluids[1].amount > CAPACITY){
							world.setBlockState(pos, BlockMoltenCopshowium.getMoltenCopshowium().getBlock().getDefaultState());
						}
					}
				}
			}
		}
	}
}