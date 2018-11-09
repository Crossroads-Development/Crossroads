package com.Da_Technomancer.crossroads.API.alchemy;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.IInfoTE;
import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.API.heat.HeatUtil;
import com.Da_Technomancer.crossroads.items.alchemy.AbstractGlassware;
import com.Da_Technomancer.crossroads.particles.ModParticles;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.WorldServer;
import org.apache.commons.lang3.tuple.Triple;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Implementations must implement hasCapability and getCapability directly. 
 */
public abstract class AlchemyCarrierTE extends TileEntity implements ITickable, IInfoTE{

	protected boolean glass;
	protected ReagentStack[] contents = new ReagentStack[AlchemyCore.REAGENT_COUNT];
	protected double heat = 0;
	protected double amount = 0;
	protected boolean dirtyReag = false;

	protected Vec3d getParticlePos(){
		return new Vec3d(pos).addVector(0.5D, 0.5D, 0.5D);
	}

	/**
	 * @param chat Add info to this list, 1 line per entry. 
	 * @param device The device type calling this method. 
	 * @param player The player using the info device.
	 * @param side The viewed EnumFacing (only used by goggles).
	 */
	@Override
	public void addInfo(ArrayList<String> chat, EntityPlayer player, @Nullable EnumFacing side, float hitX, float hitY, float hitZ){
		if(amount != 0){
			chat.add("Temp: " + MiscUtil.betterRound(handler.getTemp(), 3) + "°C");
		}else{
			chat.add("No reagents");
		}
		for(ReagentStack reag : contents){
			if(reag != null){
				chat.add(reag.toString());
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

	protected double correctTemp(){
		return amount <= 0 ? -273D : (heat / amount) - 273D;
	}

	protected boolean correctReag(){
		dirtyReag = false;
		amount = 0;
		for(ReagentStack r : contents){
			if(r != null){
				amount += r.getAmount();
			}
		}
		if(amount == 0){
			return true;
		}
		double endTemp = correctTemp();

		for(int i = 0; i < AlchemyCore.REAGENT_COUNT; i++){
			ReagentStack reag = contents[i];
			if(reag != null && reag.getAmount() < AlchemyCore.MIN_QUANTITY){
				heat -= (endTemp + 273D) * reag.getAmount();
				contents[i] = null;
			}
		}

		for(ReagentStack reag : contents){
			if(reag == null){
				continue;
			}
			reag.updatePhase(endTemp);
		}
		return true;
	}

	@Override
	public void update(){
		if(world.isRemote){
			return;
		}
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
		for(ReagentStack r : contents){
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
	public ItemStack rightClickWithItem(ItemStack stack, boolean sneaking){
		double temp = correctTemp() + 273D;//Kelvin
		ItemStack out = stack.copy();

		//Move solids from carrier into hand
		if(stack.isEmpty()){
			for(int i = 0; i < contents.length; i++){
				if(contents[i] != null && contents[i].getPhase(temp) == EnumMatterPhase.SOLID){
					out = contents[i].getType().getStackFromReagent(contents[i]);
					out.setCount(Math.min(out.getMaxStackSize(), out.getCount()));
					if(!out.isEmpty()){
						double amountRemoved = AlchemyCore.REAGENTS[i].getReagentFromStack(out).getAmount() * (double) out.getCount();
						if(contents[i].increaseAmount(-amountRemoved) <= AlchemyCore.MIN_QUANTITY){
							amount -= contents[i].getAmount();
							heat -= temp * contents[i].getAmount();
							contents[i] = null;
						}
						amount -= amountRemoved;
						heat -= temp * amountRemoved;
						break;
					}
				}
			}
		}else if(stack.getItem() instanceof AbstractGlassware){
			//Move reagents between glassware and carrier
			Triple<ReagentStack[], Double, Double> phial = ((AbstractGlassware) stack.getItem()).getReagants(stack);
			ReagentStack[] reag = phial.getLeft();
			double phialHeat = phial.getMiddle();
			double phialAmount = phial.getRight();
			if(phialAmount <= AlchemyCore.MIN_QUANTITY){
				//Move from carrier to glassware
				if(stack.getMetadata() == 0){
					//Refuse if made of glass and cannot hold contents
					for(ReagentStack r : contents){
						if(r != null && !r.getType().canGlassContain()){
							return stack;
						}
					}
				}

				phialAmount = 0;
				phialHeat = 0;

				double ratioToMove = Math.max(0, Math.min(1D, ((AbstractGlassware) stack.getItem()).getCapacity() / amount));

				for(int i = 0; i < AlchemyCore.REAGENT_COUNT; i++){
					if(contents[i] != null){
						double toMove = contents[i].getAmount() * ratioToMove;
						reag[i] = new ReagentStack(AlchemyCore.REAGENTS[i], toMove);
						phialHeat += temp * toMove;
						phialAmount += toMove;
						if(contents[i].increaseAmount(-toMove) <= AlchemyCore.MIN_QUANTITY){
							amount -= contents[i].getAmount();
							heat -= temp * contents[i].getAmount();
							contents[i] = null;
						}
						heat -= temp *toMove;
						amount -= toMove;
					}
				}
				((AbstractGlassware) out.getItem()).setReagents(out, reag, phialHeat, phialAmount);
			}else{
				//Move from glassware to carrier
				double ratioToMove = Math.max(0, Math.min(1D, (transferCapacity() - amount) / phialAmount));
				double phialTemp = phialAmount == 0 ? 0 : phialHeat / phialAmount;//Kelvin
				for(int i = 0; i < AlchemyCore.REAGENT_COUNT; i++){
					if(reag[i] != null){
						double toMove = reag[i].getAmount() * ratioToMove;
						if(contents[i] == null){
							contents[i] = new ReagentStack(AlchemyCore.REAGENTS[i], toMove);
						}else{
							contents[i].increaseAmount(toMove);
						}
						heat += phialTemp * toMove;
						amount += toMove;
						if(reag[i].increaseAmount(-toMove) <= AlchemyCore.MIN_QUANTITY){
							phialAmount -= reag[i].getAmount();
							phialHeat -= phialTemp * reag[i].getAmount();
							reag[i] = null;
						}
						phialHeat -= phialTemp *toMove;
						phialAmount -= toMove;
					}
				}
				((AbstractGlassware) out.getItem()).setReagents(out, reag, phialHeat, phialAmount);
			}
		}else{
			//Move solids from hand into carrier
			IReagent typeProduced = AlchemyCore.ITEM_TO_REAGENT.get(stack);
			if(typeProduced != null){
				double amountProduced = typeProduced.getReagentFromStack(stack).getAmount();
				if(amountProduced <= transferCapacity() - amount){
					amount += amountProduced;
					heat += Math.max(0, Math.min(typeProduced.getMeltingPoint() + 273D, HeatUtil.convertBiomeTemp(world.getBiomeForCoordsBody(pos).getTemperature(pos)) + 273D)) * amountProduced;
					out.shrink(1);
					if(contents[typeProduced.getIndex()] == null){
						contents[typeProduced.getIndex()] = new ReagentStack(typeProduced, amountProduced);
					}else{
						contents[typeProduced.getIndex()].increaseAmount(amountProduced);
					}
				}
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

	protected double transferCapacity(){
		return 10D;
	}

	protected void performTransfer(){
		EnumTransferMode[] modes = getModes();
		for(int i = 0; i < 6; i++){
			if(modes[i].isOutput()){
				EnumFacing side = EnumFacing.getFront(i);
				TileEntity te = world.getTileEntity(pos.offset(side));
				if(amount <= 0 || te == null || !te.hasCapability(Capabilities.CHEMICAL_HANDLER_CAPABILITY, side.getOpposite())){
					continue;
				}

				IChemicalHandler otherHandler = te.getCapability(Capabilities.CHEMICAL_HANDLER_CAPABILITY, side.getOpposite());
				EnumContainerType cont = otherHandler.getChannel(side.getOpposite());
				if(cont != EnumContainerType.NONE && ((cont == EnumContainerType.GLASS) != glass)){
					continue;
				}

				if(amount != 0){
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
		heat = nbt.getDouble("heat");
		for(int i = 0; i < AlchemyCore.REAGENT_COUNT; i++){
			contents[i] = nbt.hasKey(i + "_am") ? new ReagentStack(AlchemyCore.REAGENTS[i], nbt.getDouble(i + "_am")) : null;
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

	protected EnumContainerType getChannel(){
		return glass ? EnumContainerType.GLASS : EnumContainerType.CRYSTAL;
	}

	protected IChemicalHandler handler = new AlchHandler();

	protected class AlchHandler implements IChemicalHandler{

		public AlchHandler(){

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
		public double getContent(){
			return amount;
		}

		@Override
		public double getTransferCapacity(){
			return transferCapacity();
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
		public boolean insertReagents(ReagentStack[] reag, EnumFacing side, IChemicalHandler caller, boolean ignorePhase){
			if(getMode(side).isInput()){
				double space = getTransferCapacity() - amount;
				if(space <= 0){
					return false;
				}
				double callerTemp = caller == null ? 293 : caller.getTemp() + 273D;
				boolean changed = false;

				HashSet<Integer> validIds = new HashSet<Integer>(4);
				double totalValid = 0;

				for(int i = 0; i < AlchemyCore.REAGENT_COUNT; i++){
					ReagentStack r = reag[i];
					if(r != null){
						EnumMatterPhase phase = r.getPhase(callerTemp - 273D);
						if(ignorePhase || (phase.flows() && (side != EnumFacing.UP || phase.flowsDown()) && (side != EnumFacing.DOWN || phase.flowsUp()))){
							validIds.add(i);
							totalValid += r.getAmount();
						}
					}
				}

				totalValid = Math.min(1D, space / totalValid);

				for(int i : validIds){
					ReagentStack r = reag[i];
					double moved = r.getAmount() * totalValid;
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
		public double getContent(int type){
			ReagentStack r = contents[type];
			return r == null ? 0 : r.getAmount();
		}
	}
}
