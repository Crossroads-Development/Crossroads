package com.Da_Technomancer.crossroads.API.alchemy;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.EnergyConverters;
import com.Da_Technomancer.crossroads.API.IInfoTE;
import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.API.heat.HeatUtil;
import com.Da_Technomancer.crossroads.items.alchemy.AbstractGlassware;
import com.Da_Technomancer.crossroads.particles.ModParticles;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;

/**
 * Implementations must implement hasCapability and getCapability directly. 
 */
public abstract class AlchemyCarrierTE extends TileEntity implements ITickable, IInfoTE{

	protected boolean init = false;
	protected double cableTemp = 0;
	protected boolean glass;
	protected ReagentMap contents = new ReagentMap();
	protected boolean dirtyReag = false;

	protected Vec3d getParticlePos(){
		return new Vec3d(pos).add(0.5D, 0.5D, 0.5D);
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState){
		return oldState.getBlock() != newState.getBlock();
	}

	protected boolean useCableHeat(){
		return false;
	}

	protected void initHeat(){

	}

	/**
	 * @param chat Add info to this list, 1 line per entry.
	 * @param player The player using the info device.
	 * @param side The viewed EnumFacing (only used by goggles).
	 */
	@Override
	public void addInfo(ArrayList<String> chat, EntityPlayer player, @Nullable EnumFacing side, float hitX, float hitY, float hitZ){
		double temp = correctTemp();
		if(contents.getTotalQty() != 0 || temp != HeatUtil.ABSOLUTE_ZERO){
			chat.add("Temp: " + MiscUtil.betterRound(temp, 3) + "°C (" + MiscUtil.betterRound(HeatUtil.toKelvin(temp), 3) + "K)");
		}else{
			chat.add("No reagents");
		}
		for(IReagent reag : contents.keySet()){
			ReagentStack stack = contents.getStack(reag);
			if(stack != null && !stack.isEmpty()){
				chat.add(stack.toString());
			}
		}
	}

	public AlchemyCarrierTE(){
		super();
	}

	public AlchemyCarrierTE(boolean glass){
		super();
		this.glass = glass;
	}

	/**
	 * @return What the current temperature of this machine should be. Can be overwritten to allow external control of temperature
	 */
	protected double correctTemp(){
		if(useCableHeat()){
			initHeat();
			//Shares heat between internal cable & contents
			cableTemp = HeatUtil.toCelcius((HeatUtil.toKelvin(cableTemp) * EnergyConverters.ALCHEMY_TEMP_CONVERSION + contents.getTempK() * contents.getTotalQty()) / (EnergyConverters.ALCHEMY_TEMP_CONVERSION + contents.getTotalQty()));
			contents.setTemp(cableTemp);
			return cableTemp;
		}else{
			return contents.getTempC();
		}
	}

	/**
	 * Cleans up temperatures vs heat, refreshes amount based on contents, etc. Should be called after all changes to contents. Setting dirtyReag to true will queue up a correctReag call.
	 */
	protected void correctReag(){
		dirtyReag = false;
		contents.refresh();
		contents.setTemp(correctTemp());
	}

	@Override
	public void update(){
		if(world.isRemote){
			return;
		}

		if(!init && useCableHeat()){
			cableTemp = HeatUtil.convertBiomeTemp(world.getBiomeForCoordsBody(pos).getTemperature(pos));
		}
		init = true;

		if(dirtyReag){
			correctReag();
		}

		if(world.getTotalWorldTime() % AlchemyCore.ALCHEMY_TIME == 0){
			spawnParticles();
			performTransfer();
		}
	}

	protected void spawnParticles(){
		double temp = handler.getTemp();
		WorldServer server = (WorldServer) world;
		float liqAmount = 0;
		float[] liqCol = new float[4];
		float gasAmount = 0;
		float[] gasCol = new float[4];
		float flameAmount = 0;
		float[] flameCol = new float[4];
		float solAmount = 0;
		float[] solCol = new float[4];
		for(IReagent type : contents.keySet()){
			ReagentStack r = contents.getStack(type);
			if(!r.isEmpty()){
				Color col = r.getType().getColor(type.getPhase(temp));
				switch(type.getPhase(temp)){
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
					case FLAME:
						flameAmount += r.getAmount();
						flameCol[0] += r.getAmount() * (double) col.getRed();
						flameCol[1] += r.getAmount() * (double) col.getGreen();
						flameCol[2] += r.getAmount() * (double) col.getBlue();
						flameCol[3] += r.getAmount() * (double) col.getAlpha();
						break;
					case SOLID:
						solAmount += r.getAmount();
						solCol[0] += r.getAmount() * (double) col.getRed();
						solCol[1] += r.getAmount() * (double) col.getGreen();
						solCol[2] += r.getAmount() * (double) col.getBlue();
						solCol[3] += r.getAmount() * (double) col.getAlpha();
						break;
					default:
						break;
				}
			}
		}

		Vec3d particlePos = getParticlePos();

		if(liqAmount > 0){
			server.spawnParticle(ModParticles.COLOR_LIQUID, false, particlePos.x, particlePos.y, particlePos.z, 0, (Math.random() * 2D - 1D) * 0.02D, (Math.random() - 1D) * 0.02D, (Math.random() * 2D - 1D) * 0.02D, 1F, (int) (liqCol[0] / liqAmount), (int) (liqCol[1] / liqAmount), (int) (liqCol[2] / liqAmount), (int) (liqCol[3] / liqAmount));
		}
		if(gasAmount > 0){
			server.spawnParticle(ModParticles.COLOR_GAS, false, particlePos.x, particlePos.y, particlePos.z, 0, (Math.random() * 2D - 1D) * 0.015D, Math.random() * 0.015D, (Math.random() * 2D - 1D) * 0.015D, 1F, (int) (gasCol[0] / gasAmount), (int) (gasCol[1] / gasAmount), (int) (gasCol[2] / gasAmount), (int) (gasCol[3] / gasAmount));
		}
		if(flameAmount > 0){
			server.spawnParticle(ModParticles.COLOR_FLAME, false, particlePos.x, particlePos.y, particlePos.z, 0, (Math.random() * 2D - 1D) * 0.015D, Math.random() * 0.015D, (Math.random() * 2D - 1D) * 0.015D, 1F, (int) (flameCol[0] / flameAmount), (int) (flameCol[1] / flameAmount), (int) (flameCol[2] / flameAmount), (int) (flameCol[3] / flameAmount));
		}
		if(solAmount > 0){
			server.spawnParticle(ModParticles.COLOR_SOLID, false, particlePos.x - 0.25D + world.rand.nextFloat() / 2F, particlePos.y - 0.1F, particlePos.z - 0.25D + world.rand.nextFloat() / 2F, 0, 0, 0, 0, 1F, (int) (solCol[0] / solAmount), (int) (solCol[1] / solAmount), (int) (solCol[2] / solAmount), (int) (solCol[3] / solAmount));
		}
	}

	/*
	 * Helper method for moving reagents with glassware/solid items. Use is optional, and must be added in the block if used.
	 */
	@Nonnull
	public ItemStack rightClickWithItem(ItemStack stack, boolean sneaking, EntityPlayer player, EnumHand hand){
		if(dirtyReag){
			correctReag();
		}

		double temp = HeatUtil.toKelvin(correctTemp());
		ItemStack out = stack.copy();

		//Move solids from carrier into hand
		if(stack.isEmpty()){
			for(IReagent type : contents.keySet()){
				ReagentStack rStack = contents.getStack(type);
				if(!rStack.isEmpty() && type.getPhase(HeatUtil.toCelcius(temp)) == EnumMatterPhase.SOLID){
					out = type.getStackFromReagent(rStack);
					out.setCount(Math.min(out.getMaxStackSize(), out.getCount()));
					if(!out.isEmpty()){
						contents.removeReagent(type, out.getCount());
						break;
					}
				}
			}
		}else if(stack.getItem() instanceof AbstractGlassware){
			boolean crystal = ((AbstractGlassware) stack.getItem()).isCrystal();
			//Move reagents between glassware and carrier
			ReagentMap phial = ((AbstractGlassware) stack.getItem()).getReagants(stack);
			if(phial.getTotalQty() <= 0){
				phial.clear();
				//Move from carrier to glassware
				if(!crystal){
					//Refuse if made of glass and cannot hold contents
					for(IReagent r : contents.keySet()){
						if(r != null && contents.getQty(r) > 0 && !r.canGlassContain()){
							return stack;
						}
					}
				}

				double portion = Math.min(1D, (double) ((AbstractGlassware) stack.getItem()).getCapacity() / (double) contents.getTotalQty());

				for(IReagent type : contents.keySet()){
					phial.transferReagent(type, (int) (contents.getQty(type) * portion), contents);
				}

				((AbstractGlassware) out.getItem()).setReagents(out, phial);
			}else{
				//Move from glassware to carrier
				double portion = Math.max(0, Math.min(1D, (double) (transferCapacity() - contents.getTotalQty()) / (double) phial.getTotalQty()));

				for(IReagent type : phial.keySet()){
					contents.transferReagent(type, (int) (phial.getQty(type) * portion), phial);
				}

				((AbstractGlassware) out.getItem()).setReagents(out, phial);
			}
		}else if(FluidUtil.interactWithFluidHandler(player, hand, falseFluidHandler)){
			//Attempt to interact with fluid carriers
			out = player.getHeldItem(hand);
		}else{
			//Move solids from hand into carrier
			IReagent typeProduced = AlchemyCore.ITEM_TO_REAGENT.get(stack);
			if(typeProduced != null && contents.getTotalQty() < transferCapacity()){
				double itemTemp;
				double biomeTemp = HeatUtil.convertBiomeTemp(world.getBiomeForCoordsBody(pos).getTemperature(pos));
				if(biomeTemp < typeProduced.getMeltingPoint()){
					itemTemp = biomeTemp;
				}else{
					itemTemp = Math.max(typeProduced.getMeltingPoint() - 100D, HeatUtil.ABSOLUTE_ZERO);
				}

				out.shrink(1);
				contents.addReagent(typeProduced, 1, itemTemp);
			}
		}

		if(!ItemStack.areItemsEqual(out, stack) || !ItemStack.areItemStackTagsEqual(out, stack)){
			markDirty();
			dirtyReag = true;
		}

		return out;
	}

	/**
	 * Returned array must be size six.
	 * @return An array where each EnumTransferMode specifies the mode of the side with the same index. 
	 */
	@Nonnull
	protected abstract EnumTransferMode[] getModes();

	protected int transferCapacity(){
		return 10;
	}

	protected void performTransfer(){
		EnumTransferMode[] modes = getModes();
		for(int i = 0; i < 6; i++){
			if(modes[i].isOutput()){
				EnumFacing side = EnumFacing.byIndex(i);
				TileEntity te = world.getTileEntity(pos.offset(side));
				if(contents.getTotalQty() <= 0 || te == null || !te.hasCapability(Capabilities.CHEMICAL_HANDLER_CAPABILITY, side.getOpposite())){
					continue;
				}

				IChemicalHandler otherHandler = te.getCapability(Capabilities.CHEMICAL_HANDLER_CAPABILITY, side.getOpposite());
				EnumContainerType cont = otherHandler.getChannel(side.getOpposite());
				if(cont != EnumContainerType.NONE && ((cont == EnumContainerType.GLASS) != glass) || otherHandler.getMode(side.getOpposite()) == EnumTransferMode.BOTH && modes[i] == EnumTransferMode.BOTH){
					continue;
				}

				if(contents.getTotalQty() != 0){
					if(otherHandler.insertReagents(contents, side.getOpposite(), handler)){
						correctReag();
						markDirty();
					}
				}
			}
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		glass = nbt.getBoolean("glass");
		contents = ReagentMap.readFromNBT(nbt);
		cableTemp = nbt.getDouble("temp");
		init = nbt.getBoolean("initHeat");

		dirtyReag = true;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		nbt.setBoolean("glass", glass);
		contents.writeToNBT(nbt);
		nbt.setDouble("temp", cableTemp);
		nbt.setBoolean("initHeat", init);

		return nbt;
	}


	@Override
	public boolean hasCapability(Capability<?> cap, EnumFacing side){
		return getCapability(cap, side) != null;
	}

	protected EnumContainerType getChannel(){
		return glass ? EnumContainerType.GLASS : EnumContainerType.CRYSTAL;
	}

	protected IChemicalHandler handler = new AlchHandler();

	protected class AlchHandler implements IChemicalHandler{

		public AlchHandler(){

		}

		@Override
		public double getTemp(){
			return correctTemp();
		}

		@Override
		public EnumTransferMode getMode(EnumFacing side){
			return getModes()[side.getIndex()];
		}

		@Override
		public EnumContainerType getChannel(EnumFacing side){
			return AlchemyCarrierTE.this.getChannel();
		}

		@Override
		public int getTransferCapacity(){
			return transferCapacity();
		}

		@Override
		public boolean insertReagents(ReagentMap reag, EnumFacing side, IChemicalHandler caller, boolean ignorePhase){
			if(getMode(side).isInput()){
				int space = getTransferCapacity() - contents.getTotalQty();
				if(space <= 0){
					return false;
				}
				double callerTemp = HeatUtil.toKelvin(caller.getTemp());
				boolean changed = false;

				HashSet<String> validIds = new HashSet<>(4);
				int totalValid = 0;

				for(IReagent type : reag.keySet()){
					ReagentStack r = reag.getStack(type);
					if(!r.isEmpty()){
						EnumMatterPhase phase = type.getPhase(HeatUtil.toCelcius(callerTemp));
						if(ignorePhase || (phase.flows() && (side != EnumFacing.UP || phase.flowsDown()) && (side != EnumFacing.DOWN || phase.flowsUp()))){
							validIds.add(type.getId());
							totalValid += r.getAmount();
						}
					}
				}

				double portion = Math.min(1D, (double) space / (double) totalValid);
				for(String id : validIds){
					int moved = (int) (reag.getQty(id) * portion);
					if(moved <= 0){
						continue;
					}
					changed = true;

					contents.transferReagent(id, moved, reag);
				}

				if(changed){
					dirtyReag = true;
					markDirty();
				}
				return changed;
			}

			return false;
		}

		@Override
		public int getContent(IReagent id){
			return contents.getQty(id);
		}
	}

	protected final FalseFluidHandler falseFluidHandler = new FalseFluidHandler();

	private class FalseFluidHandler implements IFluidHandler{

		@Override
		public IFluidTankProperties[] getTankProperties(){
			return new FluidTankProperties[] {new FluidTankProperties(null, 100, true, true)};
		}

		@Override
		public int fill(FluidStack resource, boolean doFill){
			IReagent typ;
			if(resource != null && (typ = AlchemyCore.FLUID_TO_LIQREAGENT.get(resource.getFluid())) != null){
				int canAccept = Math.min((int) ((handler.getTransferCapacity() - contents.getTotalQty()) * AlchemyCore.MB_PER_REAG), resource.amount);
				canAccept -= canAccept % AlchemyCore.MB_PER_REAG;
				if(canAccept > 0){
					if(doFill){
						int reagToFill = canAccept / AlchemyCore.MB_PER_REAG;
						double fluidTemp;
						double biomeTemp = HeatUtil.convertBiomeTemp(world.getBiomeForCoordsBody(pos).getTemperature(pos));
						if(biomeTemp < typ.getBoilingPoint() && biomeTemp >= typ.getMeltingPoint()){
							fluidTemp = biomeTemp;
						}else if(resource.getFluid().getTemperature(resource) < typ.getBoilingPoint() && resource.getFluid().getTemperature(resource) >= typ.getMeltingPoint()){
							fluidTemp = resource.getFluid().getTemperature(resource);
						}else if(typ.getMeltingPoint() + 100D < typ.getBoilingPoint()){
							fluidTemp = typ.getMeltingPoint() + 100D;
						}else{
							fluidTemp = (typ.getMeltingPoint() + typ.getBoilingPoint()) / 2D;
						}

						contents.addReagent(typ, reagToFill, fluidTemp);
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
				int toDrain = Math.min(resource.amount, contents.getQty(type) * AlchemyCore.MB_PER_REAG);
				int reagToDrain = toDrain / AlchemyCore.MB_PER_REAG;
				toDrain = reagToDrain * AlchemyCore.MB_PER_REAG;
				if(doDrain){
					contents.removeReagent(type, reagToDrain);
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
					int toDrain = Math.min(maxDrain, contents.getQty(type) * AlchemyCore.MB_PER_REAG);
					int reagToDrain = toDrain / AlchemyCore.MB_PER_REAG;
					toDrain = reagToDrain * AlchemyCore.MB_PER_REAG;
					if(doDrain){
						contents.removeReagent(type, reagToDrain);
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
