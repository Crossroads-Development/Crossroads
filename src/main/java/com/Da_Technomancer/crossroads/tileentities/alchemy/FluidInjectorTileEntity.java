package com.Da_Technomancer.crossroads.tileentities.alchemy;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Map;

import javax.annotation.Nullable;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.IInfoDevice;
import com.Da_Technomancer.crossroads.API.IInfoTE;
import com.Da_Technomancer.crossroads.API.alchemy.AlchemyCore;
import com.Da_Technomancer.crossroads.API.alchemy.AlchemyHelper;
import com.Da_Technomancer.crossroads.API.alchemy.EnumContainerType;
import com.Da_Technomancer.crossroads.API.alchemy.EnumMatterPhase;
import com.Da_Technomancer.crossroads.API.alchemy.EnumTransferMode;
import com.Da_Technomancer.crossroads.API.alchemy.IChemicalHandler;
import com.Da_Technomancer.crossroads.API.alchemy.IReagentType;
import com.Da_Technomancer.crossroads.API.alchemy.Reagent;
import com.Da_Technomancer.crossroads.API.alchemy.SolventType;
import com.Da_Technomancer.crossroads.API.technomancy.EnumGoggleLenses;
import com.Da_Technomancer.crossroads.items.ModItems;
import com.Da_Technomancer.crossroads.particles.ModParticles;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

public class FluidInjectorTileEntity extends TileEntity implements ITickable, IInfoTE{

	private static final double REAG_PER_MB = .05D;

	private boolean glass;
	private final Reagent[] contents = new Reagent[AlchemyCore.REAGENT_COUNT];
	private double heat = 0;
	private double amount = 0;
	private boolean dirtyReag = false;

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState){
		return oldState.getBlock() != newState.getBlock();
	}

	/**
	 * @param chat Add info to this list, 1 line per entry. 
	 * @param device The device type calling this method. 
	 * @param player The player using the info device.
	 * @param side The viewed EnumFacing (only used by goggles).
	 */
	public void addInfo(ArrayList<String> chat, IInfoDevice device, EntityPlayer player, @Nullable EnumFacing side){
		if(device == ModItems.omnimeter || device == EnumGoggleLenses.DIAMOND){
			if(amount != 0){
				chat.add("Temp: " + chemHandler.getTemp() + "°C");
			}else{
				chat.add("No reagents");
			}
			for(Reagent reag : contents){
				if(reag != null){
					chat.add(reag.toString());
				}
			}
		}
	}

	public FluidInjectorTileEntity(){
		super();
	}

	public FluidInjectorTileEntity(boolean glass){
		super();
		this.glass = glass;
	}

	private void correctReag(){
		amount = 0;
		for(Reagent r : contents){
			if(r != null){
				amount += r.getAmount();
			}
		}
		if(amount == 0){
			return;
		}
		double endTemp = (heat / amount) - 273D;

		boolean hasPolar = false;
		boolean hasNonPolar = false;
		boolean hasAquaRegia = false;//Aqua regia is a special case where it works no matter the phase, but ONLY works at all if a polar solvent is present. 

		for(int i = 0; i < AlchemyCore.REAGENT_COUNT; i++){
			Reagent reag = contents[i];
			if(reag != null){
				if(reag.getAmount() >= AlchemyHelper.MIN_QUANTITY){
					IReagentType type = reag.getType();
					hasAquaRegia |= i == 11;

					if(type.getMeltingPoint() <= endTemp && type.getBoilingPoint() > endTemp){
						SolventType solv = type.solventType();
						hasPolar |= solv == SolventType.POLAR || solv == SolventType.MIXED_POLAR;
						hasNonPolar |= solv == SolventType.NON_POLAR || solv == SolventType.MIXED_POLAR;
						hasAquaRegia |= solv == SolventType.AQUA_REGIA;
					}
				}else{
					heat -= (endTemp + 273D) * reag.getAmount();
					contents[i] = null;
				}
			}
		}

		hasAquaRegia &= hasPolar;

		for(int i = 0; i < contents.length; i++){
			Reagent reag = contents[i];
			if(reag == null){
				continue;
			}
			reag.updatePhase(endTemp, hasPolar, hasNonPolar, hasAquaRegia);
		}
	}

	@Override
	public void update(){
		if(world.isRemote){
			return;
		}
		
		if(dirtyReag){
			correctReag();
			dirtyReag = false;
		}

		if(world.getTotalWorldTime() % AlchemyCore.ALCHEMY_TIME == 0){
			if(amount != 0){
				TileEntity te = world.getTileEntity(pos.offset(EnumFacing.DOWN));
				if(te != null && te.hasCapability(Capabilities.CHEMICAL_HANDLER_CAPABILITY, EnumFacing.UP)){
					IChemicalHandler otherHandler = te.getCapability(Capabilities.CHEMICAL_HANDLER_CAPABILITY, EnumFacing.UP);
					if(otherHandler.insertReagents(contents, EnumFacing.UP, chemHandler)){
						correctReag();
						markDirty();
					}
				}
			}

			double temp = chemHandler.getTemp();
			WorldServer server = (WorldServer) world;
			float liqAmount = 0;
			float[] liqCol = new float[4];
			float gasAmount = 0;
			float[] gasCol = new float[4];
			for(Reagent r : contents){
				if(r != null){
					Color col = r.getType().getColor(r.getPhase(temp));
					switch(r.getPhase(temp)){
						case LIQUID:
							liqAmount += r.getAmount();
							liqCol[0] += r.getAmount() * (double) col.getRed();
							liqCol[1] += r.getAmount() * (double) col.getGreen();
							liqCol[2] += r.getAmount() * (double) col.getBlue();
							liqCol[3] += r.getAmount() * (double) col.getAlpha();
							break;
						case GAS:
							gasAmount += r.getAmount();
							gasCol[0] += r.getAmount() * (double) col.getRed();
							gasCol[1] += r.getAmount() * (double) col.getGreen();
							gasCol[2] += r.getAmount() * (double) col.getBlue();
							gasCol[3] += r.getAmount() * (double) col.getAlpha();
							break;
						default:
							break;
					}
				}
			}
			if(liqAmount > 0){
				server.spawnParticle(ModParticles.COLOR_LIQUID, false, (float) pos.getX() + .5F, (float) pos.getY() + .5F, (float) pos.getZ() + .5F, 0, (float) liqCol[0] / (255F * liqAmount), (float) liqCol[1] / (255F * liqAmount), (float) liqCol[2] / (255F * liqAmount), 1F, new int[] {((int) ((float) liqCol[3] / liqAmount))});
			}
			if(gasAmount > 0){
				server.spawnParticle(ModParticles.COLOR_GAS, false, (float) pos.getX() + .5F, (float) pos.getY() + .5F, (float) pos.getZ() + .5F, 0, (float) gasCol[0] / (255F * gasAmount), (float) gasCol[1] / (255F * gasAmount), (float) gasCol[2] / (255F * gasAmount), 1F, new int[] {((int) ((float) gasCol[3] / gasAmount))});
			}
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		glass = nbt.getBoolean("glass");
		heat = nbt.getDouble("heat");
		for(int i = 0; i < AlchemyCore.REAGENT_COUNT; i++){
			contents[i] = nbt.hasKey(i + "_am") ? new Reagent(AlchemyCore.REAGENTS[i], nbt.getDouble(i + "_am")) : null;
		}
		dirtyReag = true;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		nbt.setBoolean("glass", glass);
		for(int i = 0; i < AlchemyCore.REAGENT_COUNT; i++){
			if(contents[i] != null){
				nbt.setDouble(i + "_am", contents[i].getAmount());
			}
		}
		nbt.setDouble("heat", heat);
		return nbt;
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
			return (T) chemHandler;
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

	private final AlchHandler chemHandler = new AlchHandler();
	private final FluidHandlerUp fluidHandlerUp = new FluidHandlerUp();
	private final FluidHandlerDown fluidHandlerDown = new FluidHandlerDown();

	private class FluidHandlerUp implements IFluidHandler{

		@Override
		public IFluidTankProperties[] getTankProperties(){
			return new FluidTankProperties[] {new FluidTankProperties(null, 100, true, false)};
		}

		@Override
		public int fill(FluidStack resource, boolean doFill){
			IReagentType typ;
			if(resource != null && (typ = AlchemyCore.FLUID_TO_REAGENT.get(resource.getFluid())) != null){
				int canAccept = Math.min((int) ((chemHandler.getTransferCapacity() - amount) / REAG_PER_MB), resource.amount);
				if(canAccept > 0){
					if(doFill){
						double reagToFill = REAG_PER_MB * (double) canAccept;
						if(contents[typ.getIndex()] == null){
							contents[typ.getIndex()] = new Reagent(typ, reagToFill);
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
			return null;
		}

		@Override
		public FluidStack drain(int maxDrain, boolean doDrain){
			return null;
		}
	}

	private class FluidHandlerDown implements IFluidHandler{

		@Override
		public IFluidTankProperties[] getTankProperties(){
			return new FluidTankProperties[] {new FluidTankProperties(null, 100, false, true)};
		}

		@Override
		public int fill(FluidStack resource, boolean doFill){
			return 0;
		}

		@Override
		public FluidStack drain(FluidStack resource, boolean doDrain){
			if(resource == null || resource.amount <= 0 || !AlchemyCore.FLUID_TO_REAGENT.containsKey(resource.getFluid())){
				return null;
			}

			int index = AlchemyCore.FLUID_TO_REAGENT.get(resource.getFluid()).getIndex();
			if(contents[index] != null && contents[index].getPhase(chemHandler.getTemp()) == EnumMatterPhase.LIQUID){
				int toDrain = (int) Math.min(resource.amount, contents[index].getAmount() / REAG_PER_MB);
				double reagToDrain = REAG_PER_MB * (double) toDrain;
				if(doDrain){
					contents[index].increaseAmount(-reagToDrain);
					amount -= reagToDrain;
					heat -= reagToDrain * (double) resource.getFluid().getTemperature();
					heat = Math.max(heat, 0D);
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
			for(Map.Entry<Fluid, IReagentType> entry : AlchemyCore.FLUID_TO_REAGENT.entrySet()){
				int index = entry.getValue().getIndex();
				if(contents[index] != null && contents[index].getPhase(chemHandler.getTemp()) == EnumMatterPhase.LIQUID){
					int toDrain = (int) Math.min(maxDrain, contents[index].getAmount() / REAG_PER_MB);
					if(doDrain){
						double reagToDrain = REAG_PER_MB * (double) toDrain;
						contents[index].increaseAmount(-reagToDrain);
						amount -= reagToDrain;
						heat -= reagToDrain * (double) entry.getKey().getTemperature();
						heat = Math.max(heat, 0D);
						dirtyReag = true;
						markDirty();
					}
					return new FluidStack(entry.getKey(), toDrain);
				}
			}

			return null;
		}
	}

	private class AlchHandler implements IChemicalHandler{

		@Override
		public EnumTransferMode getMode(EnumFacing side){
			return side == EnumFacing.DOWN ? EnumTransferMode.OUTPUT : EnumTransferMode.INPUT;
		}

		@Override
		public EnumContainerType getChannel(EnumFacing side){
			return glass ? EnumContainerType.GLASS : EnumContainerType.CRYSTAL;
		}

		@Override
		public double getContent(){
			return amount;
		}

		@Override
		public double getTransferCapacity(){
			return 10D;
		}

		@Override
		public double getHeat(){
			return heat;
		}

		@Override
		public void setHeat(double heatIn){
			heat = heatIn;
			markDirty();
		}

		@Override
		public boolean insertReagents(Reagent[] reag, EnumFacing side, IChemicalHandler caller){
			if(getMode(side) == EnumTransferMode.INPUT){
				double space = getTransferCapacity() - amount;
				if(space <= 0){
					return false;
				}
				double callerTemp = caller == null ? 293 : caller.getTemp() + 273D;
				boolean changed = false;
				for(int i = 0; i < AlchemyCore.REAGENT_COUNT; i++){
					Reagent r = reag[i];
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
								contents[i] = new Reagent(AlchemyCore.REAGENTS[i], moved);
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
			}

			return false;
		}
	}
}
