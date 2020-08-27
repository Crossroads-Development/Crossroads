package com.Da_Technomancer.crossroads.API.alchemy;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.IInfoTE;
import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.API.heat.HeatUtil;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.items.alchemy.AbstractGlassware;
import com.Da_Technomancer.crossroads.particles.CRParticles;
import com.Da_Technomancer.crossroads.particles.ColorParticleData;
import com.Da_Technomancer.essentials.blocks.BlockUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.Explosion;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.Level;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.function.Predicate;

/**
 * Implementations must implement getCapability directly.
 */
public abstract class AlchemyCarrierTE extends TileEntity implements ITickableTileEntity, IInfoTE{

	protected boolean init = false;
	protected double cableTemp = 0;
	protected boolean glass;
	protected ReagentMap contents = new ReagentMap();
	protected boolean dirtyReag = false;
	protected boolean broken = false;

	/**
	 * Position to spawn particles for contents
	 * @return Position
	 */
	protected Vector3d getParticlePos(){
		return Vector3d.func_237489_a_(pos);
	}

	protected boolean useCableHeat(){
		return false;
	}

	protected void initHeat(){

	}

	@Override
	public void addInfo(ArrayList<ITextComponent> chat, PlayerEntity player, BlockRayTraceResult hit){
		double temp = correctTemp();
		if(contents.getTotalQty() != 0 || temp != HeatUtil.ABSOLUTE_ZERO){
			HeatUtil.addHeatInfo(chat, temp, Short.MIN_VALUE);
		}else{
			chat.add(new TranslationTextComponent("tt.crossroads.boilerplate.alchemy_empty"));
		}

		int total = 0;
		for(IReagent type : contents.keySetReag()){
			int qty = contents.getQty(type);
			if(qty > 0){
				total++;
				if(total <= 4){
					chat.add(new TranslationTextComponent("tt.crossroads.boilerplate.alchemy_content", type.getName(), qty));
				}else{
					break;
				}
			}
		}
		if(total > 4){
			chat.add(new TranslationTextComponent("tt.crossroads.boilerplate.alchemy_excess", total - 4));
		}
	}

	protected AlchemyCarrierTE(TileEntityType<? extends AlchemyCarrierTE> type){
		super(type);
	}

	protected AlchemyCarrierTE(TileEntityType<? extends AlchemyCarrierTE> type, boolean glass){
		this(type);
		this.glass = glass;
	}

	protected void destroyCarrier(float strength){
		if(!broken){
			broken = true;
			BlockState state = world.getBlockState(pos);
			world.setBlockState(pos, Blocks.AIR.getDefaultState());
			SoundType sound = state.getBlock().getSoundType(state, world, pos, null);
			world.playSound(null, pos, sound.getBreakSound(), SoundCategory.BLOCKS, sound.getVolume(), sound.getPitch());
			AlchemyUtil.releaseChemical(world, pos, contents);
			if(strength > 0F){
				world.createExplosion(null, pos.getX(), pos.getY(), pos.getZ(), strength, Explosion.Mode.BREAK);//We will drop items, because an explosion in your lab is devastating enough without having to re-craft everything
			}
		}
	}

	/**
	 * @return What the current temperature of this machine should be. Can be overwritten to allow external control of temperature
	 */
	protected double correctTemp(){
		if(useCableHeat()){
			initHeat();
			//Shares heat between internal cable & contents
			cableTemp = HeatUtil.toCelcius((HeatUtil.toKelvin(cableTemp) * AlchemyUtil.ALCHEMY_TEMP_CONVERSION + contents.getTempK() * contents.getTotalQty()) / (AlchemyUtil.ALCHEMY_TEMP_CONVERSION + contents.getTotalQty()));
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

		//Check for uncontainable reagents
		if(glass){
			boolean destroy = false;
			ArrayList<IReagent> toRemove = new ArrayList<>(1);//Rare that there is more than 1

			for(IReagent type : contents.keySetReag()){
				if(contents.getQty(type) > 0 && type.requiresCrystal()){
					toRemove.add(type);
					if(type.destroysBadContainer()){
						destroy = true;
						break;
					}
				}
			}

			if(destroy){
				destroyCarrier(0);
			}else{
				for(IReagent type : toRemove){
					contents.remove(type);
				}
			}
		}
	}

	@Override
	public void tick(){
		if(world.isRemote){
			return;
		}

		if(!init && useCableHeat()){
			cableTemp = HeatUtil.convertBiomeTemp(world, pos);
		}
		init = true;

		if(dirtyReag){
			correctReag();
		}

		if(world.getGameTime() % AlchemyUtil.ALCHEMY_TIME == 0){
			spawnParticles();
			performTransfer();
		}
	}

	/**
	 * Spawns cosmetic particles representing contents
	 */
	protected void spawnParticles(){
		double temp = handler.getTemp();
		ServerWorld server = (ServerWorld) world;
		float liqAmount = 0;
		float[] liqCol = new float[4];
		float gasAmount = 0;
		float[] gasCol = new float[4];
		float flameAmount = 0;
		float[] flameCol = new float[4];
		float solAmount = 0;
		float[] solCol = new float[4];
		for(IReagent type : contents.keySetReag()){
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

		Vector3d particlePos = getParticlePos();

		if(liqAmount > 0){
			server.spawnParticle(new ColorParticleData(CRParticles.COLOR_LIQUID, new Color((int) (liqCol[0] / liqAmount), (int) (liqCol[1] / liqAmount), (int) (liqCol[2] / liqAmount), (int) (liqCol[3] / liqAmount))), particlePos.x, particlePos.y, particlePos.z, 0, (Math.random() * 2D - 1D) * 0.02D, (Math.random() - 1D) * 0.02D, (Math.random() * 2D - 1D) * 0.02D, 1F);
		}
		if(gasAmount > 0){
			server.spawnParticle(new ColorParticleData(CRParticles.COLOR_GAS, new Color((int) (gasCol[0] / gasAmount), (int) (gasCol[1] / gasAmount), (int) (gasCol[2] / gasAmount), (int) (gasCol[3] / gasAmount))), particlePos.x, particlePos.y, particlePos.z, 0, (Math.random() * 2D - 1D) * 0.015D, Math.random() * 0.015D, (Math.random() * 2D - 1D) * 0.015D, 1F);
		}
		if(flameAmount > 0){
			server.spawnParticle(new ColorParticleData(CRParticles.COLOR_FLAME, new Color((int) (flameCol[0] / flameAmount), (int) (flameCol[1] / flameAmount), (int) (flameCol[2] / flameAmount), (int) (flameCol[3] / flameAmount))), particlePos.x, particlePos.y, particlePos.z, 0, (Math.random() * 2D - 1D) * 0.015D, Math.random() * 0.015D, (Math.random() * 2D - 1D) * 0.015D, 1F);
		}
		if(solAmount > 0){
			server.spawnParticle(new ColorParticleData(CRParticles.COLOR_SOLID, new Color((int) (solCol[0] / solAmount), (int) (solCol[1] / solAmount), (int) (solCol[2] / solAmount), (int) (solCol[3] / solAmount))), particlePos.x - 0.25D + world.rand.nextFloat() / 2F, particlePos.y - 0.1F, particlePos.z - 0.25D + world.rand.nextFloat() / 2F, 0, 0, 0, 0, 1F);
		}
	}

	/*
	 * Helper method for moving reagents with glassware/solid items. Use is optional, and must be added in the block if used.
	 */
	@Nonnull
	public ItemStack rightClickWithItem(ItemStack stack, boolean sneaking, PlayerEntity player, Hand hand){
		if(dirtyReag){
			correctReag();
		}

		double temp = HeatUtil.toKelvin(correctTemp());
		ItemStack out = stack.copy();

		//Move solids from carrier into hand
		if(stack.isEmpty()){
			for(IReagent type : contents.keySetReag()){
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
					for(IReagent r : contents.keySetReag()){
						if(r != null && contents.getQty(r) > 0 && r.requiresCrystal()){
							return stack;
						}
					}
				}

				//Maps each reagent type to an integer index. Allows us to use the helper method in MiscUtil
				String[] transferableTypes = new String[contents.size()];
				int[] transferableQty = new int[contents.size()];
				int index = 0;
				for(Map.Entry<String, Integer> entry : contents.entrySet()){
					transferableTypes[index] = entry.getKey();
					transferableQty[index] = entry.getValue();
					index++;
				}

				//Transfer the full available capacity to the glassware, at portions specified by the withdrawExact method
				int[] toMove = MiscUtil.withdrawExact(transferableQty, ((AbstractGlassware) stack.getItem()).getCapacity());
				for(int i = 0; i < toMove.length; i++){
					if(toMove[i] > 0){
						phial.transferReagent(transferableTypes[i], toMove[i], contents);
					}
				}
				((AbstractGlassware) out.getItem()).setReagents(out, phial);
//				double portion = Math.min(1D, (double) ((AbstractGlassware) stack.getItem()).getCapacity() / (double) contents.getTotalQty());
//				for(IReagent type : contents.keySetReag()){
//					phial.transferReagent(type, (int) (contents.getQty(type) * portion), contents);
//				}
			}else if(transferCapacity() > contents.getTotalQty()){
				//Move from glassware to carrier

				//Maps each reagent type to an integer index. Allows us to use the helper method in MiscUtil
				String[] transferableTypes = new String[phial.size()];
				int[] transferableQty = new int[phial.size()];
				int index = 0;
				for(Map.Entry<String, Integer> entry : phial.entrySet()){
					transferableTypes[index] = entry.getKey();
					transferableQty[index] = entry.getValue();
					index++;
				}

				//Transfer the full available capacity from the glassware, at portions specified by the withdrawExact method
				int[] toMove = MiscUtil.withdrawExact(transferableQty, transferCapacity() - contents.getTotalQty());
				for(int i = 0; i < toMove.length; i++){
					if(toMove[i] > 0){
						contents.transferReagent(transferableTypes[i], toMove[i], phial);
					}
				}

//				double portion = Math.max(0, Math.min(1D, (double) (transferCapacity() - contents.getTotalQty()) / (double) phial.getTotalQty()));
//
//				for(IReagent type : phial.keySetReag()){
//					contents.transferReagent(type, (int) (phial.getQty(type) * portion), phial);
//				}
				((AbstractGlassware) out.getItem()).setReagents(out, phial);
			}
		}else if(FluidUtil.interactWithFluidHandler(player, hand, falseFluidHandler)){
			//Attempt to interact with fluid carriers
			out = player.getHeldItem(hand);
		}else{
			//Move solids from hand into carrier
			IReagent typeProduced = AlchemyCore.getItemToReagent().get(stack.getItem());
			if(typeProduced != null && contents.getTotalQty() < transferCapacity()){
				double itemTemp;
				double biomeTemp = HeatUtil.convertBiomeTemp(world, pos);
				if(biomeTemp < typeProduced.getMeltingPoint()){
					itemTemp = biomeTemp;
				}else{
					itemTemp = Math.max(typeProduced.getMeltingPoint() - 100D, HeatUtil.ABSOLUTE_ZERO);
				}

				out.shrink(1);
				contents.addReagent(typeProduced, 1, itemTemp);
			}
		}

		if(!ItemStack.areItemsEqual(out, stack) || !ItemStack.areItemStackTagsEqual(out, stack) || out.getCount() != stack.getCount()){
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

	/**
	 * Controls maximum amount of reagent this block can hold before it stops accepting more
	 * @return Maximum capacity
	 */
	protected int transferCapacity(){
		return 10;
	}

	protected void performTransfer(){
		EnumTransferMode[] modes = getModes();
		for(int i = 0; i < 6; i++){
			if(modes[i].isOutput()){
				Direction side = Direction.byIndex(i);
				TileEntity te = world.getTileEntity(pos.offset(side));
				LazyOptional<IChemicalHandler> otherOpt;
				if(contents.getTotalQty() <= 0 || te == null || !(otherOpt = te.getCapability(Capabilities.CHEMICAL_CAPABILITY, side.getOpposite())).isPresent()){
					continue;
				}
				IChemicalHandler otherHandler = otherOpt.orElseThrow(NullPointerException::new);

				EnumContainerType cont = otherHandler.getChannel(side.getOpposite());
				if(cont != EnumContainerType.NONE && ((cont == EnumContainerType.GLASS) != glass) || otherHandler.getMode(side.getOpposite()) == EnumTransferMode.BOTH && modes[i] == EnumTransferMode.BOTH){
					continue;
				}

				if(otherHandler.insertReagents(contents, side.getOpposite(), handler)){
					correctReag();
					markDirty();
				}
			}
		}
	}

	@Override
	public void read(BlockState state, CompoundNBT nbt){
		super.read(state, nbt);
		glass = nbt.getBoolean("glass");
		contents = ReagentMap.readFromNBT(nbt);
		cableTemp = nbt.getDouble("temp");
		init = nbt.getBoolean("initHeat");

		dirtyReag = true;
	}

	@Override
	public CompoundNBT write(CompoundNBT nbt){
		super.write(nbt);
		nbt.putBoolean("glass", glass);
		contents.write(nbt);
		nbt.putDouble("temp", cableTemp);
		nbt.putBoolean("initHeat", init);

		return nbt;
	}

	protected EnumContainerType getChannel(){
		return glass ? EnumContainerType.GLASS : EnumContainerType.CRYSTAL;
	}

	@Override
	public void remove(){
		super.remove();
		chemOpt.invalidate();
	}

	protected IChemicalHandler handler = new AlchHandler();
	protected LazyOptional<IChemicalHandler> chemOpt = LazyOptional.of(() -> handler);

	protected class AlchHandler implements IChemicalHandler{

		public AlchHandler(){

		}

		@Override
		public double getTemp(){
			return correctTemp();
		}

		@Override
		public EnumTransferMode getMode(Direction side){
			return getModes()[side.getIndex()];
		}

		@Override
		public EnumContainerType getChannel(Direction side){
			return AlchemyCarrierTE.this.getChannel();
		}

		@Override
		public int getTransferCapacity(){
			return transferCapacity();
		}

		@Override
		public boolean insertReagents(ReagentMap reag, Direction side, IChemicalHandler caller, boolean ignorePhase){
			if(getMode(side).isInput()){
				int space = getTransferCapacity() - contents.getTotalQty();
				if(space <= 0){
					return false;
				}
				double callerTemp = reag.getTempC();
				boolean changed = false;

				//Map the moveable reags to an int array of quantities so we can use the relevant MiscUtil method
				IReagent[] mapping = new IReagent[reag.size()];
				int[] preQty = new int[mapping.length];
				int index = 0;
				for(IReagent type : reag.keySetReag()){
					mapping[index] = type;
					ReagentStack r = reag.getStack(type);
					EnumMatterPhase phase;
					if(!r.isEmpty() && (ignorePhase || (phase = type.getPhase(callerTemp)).flows() && (side != Direction.UP || phase.flowsDown()) && (side != Direction.DOWN || phase.flowsUp()))){
						preQty[index] = r.getAmount();
					}else{
						preQty[index] = 0;//Set the pre-qty to 0 to indicate no transfer of that type is allowed
					}
					index++;
				}

				//Use the MiscUtil method
				int[] toTrans = MiscUtil.withdrawExact(preQty, space);
				for(int i = 0; i < toTrans.length; i++){
					if(toTrans[i] > 0){
						//Transfer each reagent individually, in qty calculated by withdrawExact
						changed = true;
						contents.transferReagent(mapping[i], toTrans[i], reag);
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
		public int getContent(IReagent id){
			return contents.getQty(id);
		}
	}

	protected final FalseFluidHandler falseFluidHandler = new FalseFluidHandler();

	private class FalseFluidHandler implements IFluidHandler{

		private FluidStack simulateFluid(int tank){
			if(tank >= 0 && tank < getTanks()){
				IReagent r = AlchemyCore.getFluidToLiqreagent().get(tank).getRight();
				FluidStack refStack = AlchemyCore.getFluidToLiqreagent().get(tank).getLeft();
				int qty = contents.getQty(r);
				if(qty >= refStack.getAmount() && r.getPhase(contents.getTempC()) == EnumMatterPhase.LIQUID){
					FluidStack out = refStack.copy();
					out.setAmount(qty / refStack.getAmount());
					return out;
				}
			}
			return FluidStack.EMPTY;
		}

		@Override
		public int getTanks(){
			return AlchemyCore.getFluidToLiqreagent().size();
		}

		@Nonnull
		@Override
		public FluidStack getFluidInTank(int tank){
			return simulateFluid(tank);
		}

		@Override
		public int getTankCapacity(int tank){
			if(tank >= 0 && tank < AlchemyCore.getFluidToLiqreagent().size()){
				return AlchemyCore.getFluidToLiqreagent().get(tank).getLeft().getAmount() * transferCapacity();
			}else{
				return 0;
			}
		}

		@Override
		public boolean isFluidValid(int tank, @Nonnull FluidStack stack){
			return tank >= 0 && tank < AlchemyCore.getFluidToLiqreagent().size() && BlockUtil.sameFluid(AlchemyCore.getFluidToLiqreagent().get(tank).getLeft(), stack);
		}

		@Override
		public int fill(FluidStack resource, FluidAction action){
			//The list is unsorted, so we need a sequential search to find the matching fluid type
			for(int i = 0; i < AlchemyCore.getFluidToLiqreagent().size(); i++){
				Pair<FluidStack, IReagent> mapping = AlchemyCore.getFluidToLiqreagent().get(i);
				if(BlockUtil.sameFluid(mapping.getLeft(), resource)){
					int toFillReag = Math.min(resource.getAmount() / mapping.getLeft().getAmount(), transferCapacity() - contents.getTotalQty());
					//Note: toFillReag could be negative
					if(toFillReag <= 0){
						return 0;
					}
					if(action.execute()){
						contents.addReagent(mapping.getRight(), toFillReag, calcInputTemp(mapping.getRight(), mapping.getLeft().getFluid()));
						markDirty();
					}
					return toFillReag * mapping.getLeft().getAmount();
				}
			}
			return 0;
		}

		private double calcInputTemp(IReagent reag, Fluid fluid){
			//Calculates the effective input temperature (in C) for the reagents derived from a fluid piped in
			//Returns a value which is reagent and location dependent but state independent

			Predicate<Double> legal = (temp) -> temp >= reag.getMeltingPoint() && temp < reag.getBoilingPoint();
			//Try the fluid's modder-defined temperature
			double temp = fluid.getAttributes().getTemperature();
			if(legal.test(temp)){
				return temp;
			}
			//Check biome temperature
			temp = HeatUtil.convertBiomeTemp(world, pos);
			if(legal.test(temp)){
				return temp;
			}
			//100*C above the melting point
			temp = Math.min(HeatUtil.ABSOLUTE_ZERO, reag.getMeltingPoint()) + 100;
			if(legal.test(temp)){
				return temp;
			}
			//The exact melting point
			return Math.min(HeatUtil.ABSOLUTE_ZERO, reag.getMeltingPoint());
		}

		@Nonnull
		@Override
		public FluidStack drain(FluidStack resource, FluidAction action){
			//The list is unsorted, so we need a sequential search to find the matching fluid type
			for(int i = 0; i < AlchemyCore.getFluidToLiqreagent().size(); i++){
				Pair<FluidStack, IReagent> mapping = AlchemyCore.getFluidToLiqreagent().get(i);
				if(BlockUtil.sameFluid(mapping.getLeft(), resource)){

					FluidStack tank = simulateFluid(i);
					if(!tank.isEmpty()){
						int drained = Math.min(tank.getAmount(), resource.getAmount());

						//It isn't necessary to modify tank as we normally would, as it's only a conversion
						tank.setAmount(drained);
						if(action.execute()){
							contents.removeReagent(mapping.getRight(), drained / mapping.getLeft().getAmount());
							markDirty();
						}
						return tank;
					}else{
						return FluidStack.EMPTY;
					}
				}
			}

			return FluidStack.EMPTY;
		}

		@Nonnull
		@Override
		public FluidStack drain(int maxDrain, FluidAction action){
			for(int i = 0; i < AlchemyCore.getFluidToLiqreagent().size(); i++){
				FluidStack tank = simulateFluid(i);
				if(!tank.isEmpty()){
					int drained = Math.min(tank.getAmount(), maxDrain);

					//It isn't necessary to modify tank as we normally would, as it's only a conversion
					tank.setAmount(drained);
					if(action.execute()){
						Pair<FluidStack, IReagent> mapping = AlchemyCore.getFluidToLiqreagent().get(i);
						contents.removeReagent(mapping.getRight(), drained / mapping.getLeft().getAmount());
						markDirty();
					}
					return tank;
				}
			}

			return FluidStack.EMPTY;
		}
	}

	protected class ItemHandler implements IItemHandler{

		private ItemStack[] fakeInventory = new ItemStack[AlchemyCore.getItemToReagent().size()];

		public ItemHandler(){

		}

		private void updateFakeInv(){
			fakeInventory = new ItemStack[AlchemyCore.getItemToReagent().size()];
			int index = 0;
			double endTemp = handler.getTemp();
			for(IReagent reag : AlchemyCore.getItemToReagent().values()){
				int qty = contents.getQty(reag);
				ReagentStack rStack = contents.getStack(reag);
				fakeInventory[index] = qty != 0 && reag.getPhase(endTemp) == EnumMatterPhase.SOLID ? reag.getStackFromReagent(rStack) : ItemStack.EMPTY;
				index++;
			}
		}

		@Override
		public int getSlots(){
			return AlchemyCore.getItemToReagent().size();
		}

		@Override
		public ItemStack getStackInSlot(int slot){
			updateFakeInv();
			return fakeInventory[slot];
		}

		@Override
		public ItemStack insertItem(int slot, ItemStack stack, boolean simulate){
			if(!stack.isEmpty()){
				IReagent reag = AlchemyCore.getItemToReagent().get(stack.getItem());
				if(reag != null){
					if(dirtyReag){
						correctReag();
					}
					ItemStack testStack = stack.copy();
					testStack.setCount(1);
					int trans = Math.min(stack.getCount(), transferCapacity() - contents.getTotalQty());
					if(!simulate){
						double itemTemp = HeatUtil.convertBiomeTemp(world, pos);
						if(itemTemp >= reag.getMeltingPoint()){
							itemTemp = Math.min(HeatUtil.ABSOLUTE_ZERO, reag.getMeltingPoint() - 100D);
						}
						contents.addReagent(reag, trans, itemTemp);
						dirtyReag = true;
						markDirty();
					}
					testStack.setCount(stack.getCount() - trans);
					return testStack;
				}
			}
			return stack;
		}

		@Override
		public ItemStack extractItem(int slot, int amount, boolean simulate){
			updateFakeInv();
			int canExtract = Math.min(fakeInventory[slot].getCount(), amount);
			if(canExtract > 0){
				try{
					ItemStack outStack = fakeInventory[slot].copy();
					outStack.setCount(canExtract);
					if(!simulate){
						IReagent reag = AlchemyCore.getItemToReagent().get(fakeInventory[slot].getItem());
						contents.removeReagent(reag, canExtract);
						dirtyReag = true;
						markDirty();
					}
					return outStack;
				}catch(NullPointerException e){
					Crossroads.logger.log(Level.FATAL, "Alchemy Item/Reagent map error. Slot: " + slot + ", Stack: " + fakeInventory[slot], e);
				}
			}

			return ItemStack.EMPTY;
		}

		@Override
		public int getSlotLimit(int slot){
			return 10;
		}

		@Override
		public boolean isItemValid(int slot, @Nonnull ItemStack stack){
			return AlchemyCore.getItemToReagent().get(stack.getItem()) != null;
		}
	}
}
