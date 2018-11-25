package com.Da_Technomancer.crossroads.tileentities.alchemy;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.alchemy.*;
import com.Da_Technomancer.crossroads.API.heat.HeatUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

import java.util.Map;

public class FluidInjectorTileEntity extends AlchemyCarrierTE{

	private static final int MB_PER_REAG = 100;

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState){
		return oldState.getBlock() != newState.getBlock();
	}

	public FluidInjectorTileEntity(){
		super();
	}

	public FluidInjectorTileEntity(boolean glass){
		super(glass);
	}

	@Override
	protected EnumTransferMode[] getModes(){
		return new EnumTransferMode[] {EnumTransferMode.BOTH, EnumTransferMode.BOTH, EnumTransferMode.NONE, EnumTransferMode.NONE, EnumTransferMode.NONE, EnumTransferMode.NONE};
	}

	@Override
	protected void performTransfer(){
		for(int i = 0; i < 2; i++){
			if(amount != 0){
				EnumFacing side = EnumFacing.byIndex(i);
				TileEntity te = world.getTileEntity(pos.offset(side.getOpposite()));
				if(te != null && te.hasCapability(Capabilities.CHEMICAL_HANDLER_CAPABILITY, side)){
					if(te.getCapability(Capabilities.CHEMICAL_HANDLER_CAPABILITY, side).insertReagents(contents, side, handler)){
						correctReag();
						markDirty();
					}
				}
			}
		}
	}

	@Override
	public boolean hasCapability(Capability<?> cap, EnumFacing side){
		if(cap == Capabilities.CHEMICAL_HANDLER_CAPABILITY && (side == null || side.getAxis() == Axis.Y)){
			return true;
		}
		if(cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && (side == null || side.getAxis() == Axis.Y)){
			return true;
		}
		return super.hasCapability(cap, side);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> cap, EnumFacing side){
		if(cap == Capabilities.CHEMICAL_HANDLER_CAPABILITY && (side == null || side.getAxis() == Axis.Y)){
			return (T) handler;
		}
		if(cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY){
			if(side == null || side == EnumFacing.UP){
				return (T) fluidHandlerUp;
			}else if(side == EnumFacing.DOWN){
				return (T) fluidHandlerDown;
			}
		}
		return super.getCapability(cap, side);
	}

	private final FluidHandlerUp fluidHandlerUp = new FluidHandlerUp();
	private final FluidHandlerDown fluidHandlerDown = new FluidHandlerDown();

	private class FluidHandlerUp implements IFluidHandler{

		@Override
		public IFluidTankProperties[] getTankProperties(){
			return new FluidTankProperties[] {new FluidTankProperties(null, 100, true, false)};
		}

		@Override
		public int fill(FluidStack resource, boolean doFill){
			IReagent typ;
			if(resource != null && (typ = AlchemyCore.FLUID_TO_LIQREAGENT.get(resource.getFluid())) != null){
				int canAccept = Math.min((int) ((handler.getTransferCapacity() - amount) * MB_PER_REAG), resource.amount);
				if(canAccept > 0){
					if(doFill){
						int reagToFill = canAccept / MB_PER_REAG;
						contents.addReagent(typ, reagToFill);
						amount += reagToFill;
						double envTemp = HeatUtil.convertBiomeTemp(world.getBiomeForCoordsBody(pos).getTemperature(pos));
						if(typ.getBoilingPoint() <= envTemp || typ.getMeltingPoint() > envTemp){
							envTemp = (double) resource.getFluid().getTemperature();
						}else{
							envTemp -= HeatUtil.ABSOLUTE_ZERO;
						}
						heat += reagToFill * envTemp;
						dirtyReag = true;
						markDirty();
					}
					return canAccept;
				}
			}
			return 0;
		}

		@Override
		public FluidStack drain(FluidStack resource, boolean doDrain){
			if(resource == null || resource.amount <= 0 || !AlchemyCore.FLUID_TO_GASREAGENT.containsKey(resource.getFluid())){
				return null;
			}

			IReagent type = AlchemyCore.FLUID_TO_GASREAGENT.get(resource.getFluid());
			int qty = contents.getQty(type);
			if(qty > 0 && type.getPhase(handler.getTemp()) == EnumMatterPhase.GAS){
				int toDrain = (int) Math.min(resource.amount, qty * MB_PER_REAG);
				int reagToDrain = toDrain / MB_PER_REAG;
				if(doDrain){
					contents.addReagent(type, -reagToDrain);
					if(amount != 0){
						double endTemp = heat / amount;
						amount -= reagToDrain;
						heat -= reagToDrain * endTemp;
						heat = Math.max(heat, 0);
					}
					dirtyReag = true;
					markDirty();
				}
				return new FluidStack(resource.getFluid(), toDrain);
			}

			return null;
		}

		@Override
		public FluidStack drain(int maxDrain, boolean doDrain){
			if(maxDrain <= 0){
				return null;
			}

			//The Fluid-IReagentType BiMap is guaranteed to be equal in length to or shorter than REAGENT_COUNT (and in practice is substantially shorter),
			//so it's more efficient to iterate over the BiMap and check each IReagentType's index than to iterate over the reagent array and check each reagent in the BiMap. 
			for(Map.Entry<Fluid, IReagent> entry : AlchemyCore.FLUID_TO_GASREAGENT.entrySet()){
				IReagent type = entry.getValue();
				if(contents.getQty(type) > 0 && type.getPhase(handler.getTemp()) == EnumMatterPhase.GAS){
					int toDrain = (int) Math.min(maxDrain, contents.getQty(type) * MB_PER_REAG);
					if(doDrain){
						int reagToDrain = MB_PER_REAG * toDrain;
						contents.addReagent(type, -reagToDrain);
						if(amount != 0){
							double endTemp = heat / amount;
							amount -= reagToDrain;
							heat -= reagToDrain * endTemp;
							heat = Math.max(heat, 0D);
						}
						dirtyReag = true;
						markDirty();
					}
					return new FluidStack(entry.getKey(), toDrain);
				}
			}

			return null;
		}
	}

	private class FluidHandlerDown implements IFluidHandler{

		@Override
		public IFluidTankProperties[] getTankProperties(){
			return new FluidTankProperties[] {new FluidTankProperties(null, 100, true, true)};
		}

		@Override
		public int fill(FluidStack resource, boolean doFill){
			IReagent typ;
			if(resource != null && (typ = AlchemyCore.FLUID_TO_GASREAGENT.get(resource.getFluid())) != null){
				int canAccept = Math.min((int) ((handler.getTransferCapacity() - amount) * MB_PER_REAG), resource.amount);
				if(canAccept > 0){
					if(doFill){
						int reagToFill = canAccept / MB_PER_REAG;
						contents.addReagent(typ, reagToFill);
						amount += reagToFill;
						double envTemp = HeatUtil.convertBiomeTemp(world.getBiomeForCoordsBody(pos).getTemperature(pos));
						if(typ.getBoilingPoint() > envTemp){
							envTemp = (double) resource.getFluid().getTemperature();
						}else{
							envTemp -= HeatUtil.ABSOLUTE_ZERO;
						}
						heat += reagToFill * envTemp;
						dirtyReag = true;
						markDirty();
					}
					return canAccept;
				}
			}
			return 0;
		}

		@Override
		public FluidStack drain(FluidStack resource, boolean doDrain){
			if(resource == null || resource.amount <= 0 || !AlchemyCore.FLUID_TO_LIQREAGENT.containsKey(resource.getFluid())){
				return null;
			}

			IReagent type = AlchemyCore.FLUID_TO_LIQREAGENT.get(resource.getFluid());
			if(contents.getQty(type) > 0 && type.getPhase(handler.getTemp()) == EnumMatterPhase.LIQUID){
				int toDrain = Math.min(resource.amount, contents.getQty(type) * MB_PER_REAG);
				int reagToDrain = toDrain / MB_PER_REAG;
				if(doDrain){
					contents.addReagent(type, -reagToDrain);
					if(amount != 0){
						double endTemp = heat / amount;
						amount -= reagToDrain;
						heat -= reagToDrain * endTemp;
						heat = Math.max(heat, 0D);
					}
					dirtyReag = true;
					markDirty();
				}
				return new FluidStack(resource.getFluid(), toDrain);
			}

			return null;
		}

		@Override
		public FluidStack drain(int maxDrain, boolean doDrain){
			if(maxDrain <= 0){
				return null;
			}

			//The Fluid-IReagentType BiMap is guaranteed to be equal in length to or shorter than REAGENT_COUNT (and in practice is substantially shorter),
			//so it's more efficient to iterate over the BiMap and check each IReagentType's index than to iterate over the reagent array and check each reagent in the BiMap. 
			for(Map.Entry<Fluid, IReagent> entry : AlchemyCore.FLUID_TO_LIQREAGENT.entrySet()){
				IReagent type = entry.getValue();
				if(contents.getQty(type) > 0 && type.getPhase(handler.getTemp()) == EnumMatterPhase.LIQUID){
					int toDrain = Math.min(maxDrain, contents.getQty(type) * MB_PER_REAG);
					if(doDrain){
						int reagToDrain = MB_PER_REAG * toDrain;
						contents.addReagent(type, -reagToDrain);
						if(amount != 0){
							double endTemp = heat / amount;
							amount -= reagToDrain;
							heat -= reagToDrain * endTemp;
							heat = Math.max(heat, 0D);
						}
						dirtyReag = true;
						markDirty();
					}
					return new FluidStack(entry.getKey(), toDrain);
				}
			}

			return null;
		}
	}
}
