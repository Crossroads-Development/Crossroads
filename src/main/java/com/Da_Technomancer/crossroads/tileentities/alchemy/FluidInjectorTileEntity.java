package com.Da_Technomancer.crossroads.tileentities.alchemy;

import java.util.Map;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.alchemy.AbstractAlchemyCarrierTE;
import com.Da_Technomancer.crossroads.API.alchemy.AlchemyCore;
import com.Da_Technomancer.crossroads.API.alchemy.EnumMatterPhase;
import com.Da_Technomancer.crossroads.API.alchemy.EnumTransferMode;
import com.Da_Technomancer.crossroads.API.alchemy.IChemicalHandler;
import com.Da_Technomancer.crossroads.API.alchemy.IReagent;
import com.Da_Technomancer.crossroads.API.alchemy.ReagentStack;

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

public class FluidInjectorTileEntity extends AbstractAlchemyCarrierTE{

	private static final double REAG_PER_MB = .05D;

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState){
		return oldState.getBlock() != newState.getBlock();
	}

	public FluidInjectorTileEntity(){
		super();
		this.handler = new AlchHandlerInjector();
	}

	public FluidInjectorTileEntity(boolean glass){
		super(glass);
		this.handler = new AlchHandlerInjector();
	}

	@Override
	protected void performTransfer(){
		for(int i = 0; i < 2; i++){
			if(amount != 0){
				EnumFacing side = EnumFacing.getFront(i);
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

	private class AlchHandlerInjector extends AlchHandler{
		@Override
		public boolean insertReagents(ReagentStack[] reag, EnumFacing side, IChemicalHandler caller){
			//if(getMode(side) == EnumTransferMode.INPUT){
				double space = getTransferCapacity() - amount;
				if(space <= 0){
					return false;
				}
				double callerTemp = caller == null ? 293 : caller.getTemp() + 273D;
				boolean changed = false;
				for(int i = 0; i < AlchemyCore.REAGENT_COUNT; i++){
					ReagentStack r = reag[i];
					if(r != null){
						EnumMatterPhase phase = r.getPhase(0);
						if(phase.flows() && (side != EnumFacing.UP || phase.flowsDown()) && (side != EnumFacing.DOWN || phase.flowsUp())){
							double moved = Math.min(space, r.getAmount());
							if(moved <= 0D){
								continue;
							}
							amount += moved;
							changed = true;
							space -= moved;
							double heatTrans = moved * callerTemp;
							if(r.increaseAmount(-moved) <= 0){
								reag[i] = null;
							}
							heat += heatTrans;
							if(caller != null){
								caller.addHeat(-heatTrans);
							}
							if(contents[i] == null){
								contents[i] = new ReagentStack(AlchemyCore.REAGENTS[i], moved);
							}else{
								contents[i].increaseAmount(moved);
							}

							if(space <= 0){
								break;
							}
						}
					}
				}

				if(changed){
					dirtyReag = true;
					markDirty();
				}
				return changed;
			//}

			//return false;
		}
	}
	
	private class FluidHandlerUp implements IFluidHandler{

		@Override
		public IFluidTankProperties[] getTankProperties(){
			return new FluidTankProperties[] {new FluidTankProperties(null, 100, true, false)};
		}

		@Override
		public int fill(FluidStack resource, boolean doFill){
			IReagent typ;
			if(resource != null && (typ = AlchemyCore.FLUID_TO_LIQREAGENT.get(resource.getFluid())) != null){
				int canAccept = Math.min((int) ((handler.getTransferCapacity() - amount) / REAG_PER_MB), resource.amount);
				if(canAccept > 0){
					if(doFill){
						double reagToFill = REAG_PER_MB * (double) canAccept;
						if(contents[typ.getIndex()] == null){
							contents[typ.getIndex()] = new ReagentStack(typ, reagToFill);
						}else{
							contents[typ.getIndex()].increaseAmount(reagToFill);
						}
						amount += reagToFill;
						heat += reagToFill * (double) resource.getFluid().getTemperature();
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

			int index = AlchemyCore.FLUID_TO_GASREAGENT.get(resource.getFluid()).getIndex();
			if(contents[index] != null && contents[index].getPhase(handler.getTemp()) == EnumMatterPhase.GAS){
				int toDrain = (int) Math.min(resource.amount, contents[index].getAmount() / REAG_PER_MB);
				double reagToDrain = REAG_PER_MB * (double) toDrain;
				if(doDrain){
					contents[index].increaseAmount(-reagToDrain);
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
			for(Map.Entry<Fluid, IReagent> entry : AlchemyCore.FLUID_TO_GASREAGENT.entrySet()){
				int index = entry.getValue().getIndex();
				if(contents[index] != null && contents[index].getPhase(handler.getTemp()) == EnumMatterPhase.GAS){
					int toDrain = (int) Math.min(maxDrain, contents[index].getAmount() / REAG_PER_MB);
					if(doDrain){
						double reagToDrain = REAG_PER_MB * (double) toDrain;
						contents[index].increaseAmount(-reagToDrain);
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
				int canAccept = Math.min((int) ((handler.getTransferCapacity() - amount) / REAG_PER_MB), resource.amount);
				if(canAccept > 0){
					if(doFill){
						double reagToFill = REAG_PER_MB * (double) canAccept;
						if(contents[typ.getIndex()] == null){
							contents[typ.getIndex()] = new ReagentStack(typ, reagToFill);
						}else{
							contents[typ.getIndex()].increaseAmount(reagToFill);
						}
						amount += reagToFill;
						heat += reagToFill * (double) resource.getFluid().getTemperature();
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

			int index = AlchemyCore.FLUID_TO_LIQREAGENT.get(resource.getFluid()).getIndex();
			if(contents[index] != null && contents[index].getPhase(handler.getTemp()) == EnumMatterPhase.LIQUID){
				int toDrain = (int) Math.min(resource.amount, contents[index].getAmount() / REAG_PER_MB);
				double reagToDrain = REAG_PER_MB * (double) toDrain;
				if(doDrain){
					contents[index].increaseAmount(-reagToDrain);
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
				int index = entry.getValue().getIndex();
				if(contents[index] != null && contents[index].getPhase(handler.getTemp()) == EnumMatterPhase.LIQUID){
					int toDrain = (int) Math.min(maxDrain, contents[index].getAmount() / REAG_PER_MB);
					if(doDrain){
						double reagToDrain = REAG_PER_MB * (double) toDrain;
						contents[index].increaseAmount(-reagToDrain);
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

	@Override
	protected EnumTransferMode[] getModes(){
		return new EnumTransferMode[] {EnumTransferMode.NONE, EnumTransferMode.NONE, EnumTransferMode.NONE, EnumTransferMode.NONE, EnumTransferMode.NONE, EnumTransferMode.NONE};
	}
}
