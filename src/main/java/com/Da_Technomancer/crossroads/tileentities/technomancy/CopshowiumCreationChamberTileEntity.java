package com.Da_Technomancer.crossroads.tileentities.technomancy;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.EnergyConverters;
import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.API.magic.EnumMagicElements;
import com.Da_Technomancer.crossroads.API.magic.IMagicHandler;
import com.Da_Technomancer.crossroads.API.magic.MagicUnit;
import com.Da_Technomancer.crossroads.API.technomancy.FieldWorldSavedData;
import com.Da_Technomancer.crossroads.ModConfig;
import com.Da_Technomancer.crossroads.fluids.BlockMoltenCopshowium;
import com.Da_Technomancer.crossroads.fluids.ModFluids;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.ChunkPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

import javax.annotation.Nullable;

public class CopshowiumCreationChamberTileEntity extends TileEntity{

	private FluidStack content = null;
	private static final int CAPACITY = 1_296;

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		content = FluidStack.loadFluidStackFromNBT(nbt);

	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);

		if(content != null){
			content.writeToNBT(nbt);
		}

		return nbt;
	}

	private final IFluidHandler mainHandler = new MainHandler();
	private final IMagicHandler magicHandler = new MagicHandler();

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing){
		if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY){
			return facing == null || facing.getAxis() == EnumFacing.Axis.Y ? (T) mainHandler : null;
		}

		if(capability == Capabilities.MAGIC_HANDLER_CAPABILITY && (facing == null || facing.getAxis() != EnumFacing.Axis.Y)){
			return (T) magicHandler;
		}

		return super.getCapability(capability, facing);
	}

	@Override
	public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing){
		if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && (facing == null || facing.getAxis() == EnumFacing.Axis.Y)){
			return true;
		}
		if(capability == Capabilities.MAGIC_HANDLER_CAPABILITY && (facing == null || facing.getAxis() != EnumFacing.Axis.Y)){
			return true;
		}
		return super.hasCapability(capability, facing);
	}

	private class MagicHandler implements IMagicHandler{

		@Override
		public void setMagic(MagicUnit mag){
			if(EnumMagicElements.getElement(mag) == EnumMagicElements.TIME && content != null){
				if(content.getFluid().getName().equals(ModConfig.getConfigString(ModConfig.cccExpenLiquid, false))){
					content = new FluidStack(BlockMoltenCopshowium.getMoltenCopshowium(), (int) (((double) content.amount) * EnergyConverters.COPSHOWIUM_PER_COPPER));
					markDirty();
					if(content.amount > CAPACITY){
						world.setBlockState(pos, ModFluids.moltenCopshowium.getDefaultState());
					}
				}else if(content.getFluid().getName().equals(ModConfig.getConfigString(ModConfig.cccFieldLiquid, false))){
					FieldWorldSavedData data = FieldWorldSavedData.get(world);
					if(data.fieldNodes.containsKey(MiscUtil.getLongFromChunkPos(new ChunkPos(pos)))){
						if(data.fieldNodes.get(MiscUtil.getLongFromChunkPos(new ChunkPos(pos))).flux + 1 < 8 * (content.amount / 72)){
							return;
						}

						data.fieldNodes.get(MiscUtil.getLongFromChunkPos(new ChunkPos(pos))).fluxForce -= 8 * (content.amount / 72);

						content = new FluidStack(BlockMoltenCopshowium.getMoltenCopshowium(), (int) (((double) content.amount) * EnergyConverters.COPSHOWIUM_PER_COPPER));
						markDirty();
						if(content.amount > CAPACITY){
							world.setBlockState(pos, ModFluids.moltenCopshowium.getDefaultState());
						}
					}
				}
			}
		}
	}

	private class MainHandler implements IFluidHandler{

		@Override
		public IFluidTankProperties[] getTankProperties(){
			return new IFluidTankProperties[] {new FluidTankProperties(content, CAPACITY, true, true)};
		}

		@Override
		public int fill(FluidStack resource, boolean doFill){
			if(content != null && resource != null && (content.getFluid() == BlockMoltenCopshowium.getMoltenCopshowium() || resource.getFluid() == BlockMoltenCopshowium.getMoltenCopshowium()) && resource.getFluid() != content.getFluid()){
				if(content.getFluid() == BlockMoltenCopshowium.getMoltenCopshowium()){
					if(doFill){
						content = null;
						markDirty();
					}
				}else{
					return resource.amount;
				}
			}

			if(resource != null && (content == null || resource.isFluidEqual(content))){
				int amount = Math.min(resource.amount, CAPACITY - (content == null ? 0 : content.amount));

				if(doFill && amount != 0){
					content = new FluidStack(resource.getFluid(), amount + (content == null ? 0 : content.amount), resource.tag);
					markDirty();
				}

				return amount;
			}

			return 0;
		}

		@Override
		public FluidStack drain(FluidStack resource, boolean doDrain){
			if(resource == null || content == null || resource.getFluid() != content.getFluid()){
				return null;
			}
			int amount = Math.min(resource.amount, content.amount);

			if(doDrain){
				content.amount -= amount;
				if(content.amount <= 0){
					content = null;
				}
				markDirty();
			}

			return new FluidStack(resource.getFluid(), amount);
		}

		@Override
		public FluidStack drain(int maxDrain, boolean doDrain){
			if(maxDrain <= 0 || content == null){
				return null;
			}
			int amount = Math.min(maxDrain, content.amount);

			Fluid fluid = content.getFluid();

			if(doDrain){
				content.amount -= amount;
				if(content.amount <= 0){
					content = null;
				}
				markDirty();
			}

			return new FluidStack(fluid, amount);
		}
	}
}