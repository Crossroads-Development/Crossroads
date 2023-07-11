package com.Da_Technomancer.crossroads.api.alchemy;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.ambient.particles.CRParticles;
import com.Da_Technomancer.crossroads.ambient.particles.ColorParticleData;
import com.Da_Technomancer.crossroads.ambient.sounds.CRSounds;
import com.Da_Technomancer.crossroads.api.Capabilities;
import com.Da_Technomancer.crossroads.api.MiscUtil;
import com.Da_Technomancer.crossroads.api.crafting.CraftingUtil;
import com.Da_Technomancer.crossroads.api.crafting.FluidIngredient;
import com.Da_Technomancer.crossroads.api.heat.HeatUtil;
import com.Da_Technomancer.crossroads.api.templates.IInfoTE;
import com.Da_Technomancer.crossroads.items.alchemy.AbstractGlassware;
import com.Da_Technomancer.essentials.api.ITickableTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.Level;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

/**
 * Implementations must implement getCapability directly.
 */
public abstract class AlchemyCarrierTE extends BlockEntity implements ITickableTileEntity, IInfoTE{

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
	protected Vec3 getParticlePos(){
		return Vec3.atCenterOf(worldPosition);
	}

	protected boolean useCableHeat(){
		return false;
	}

	protected void initHeat(){

	}

	private Double biomeTempCache = null;

	protected double getBiomeTemp(){
		if(biomeTempCache == null){
			biomeTempCache = HeatUtil.convertBiomeTemp(level, worldPosition);
		}
		return biomeTempCache;
	}

	@Override
	public void addInfo(ArrayList<Component> chat, Player player, BlockHitResult hit){
		double temp = correctTemp();
		if(contents.getTotalQty() != 0 || temp != HeatUtil.ABSOLUTE_ZERO || useCableHeat()){
			HeatUtil.addHeatInfo(chat, temp, Short.MIN_VALUE);
		}else{
			chat.add(Component.translatable("tt.crossroads.boilerplate.alchemy_empty"));
		}

		int total = 0;
		for(IReagent type : contents.keySetReag()){
			int qty = contents.getQty(type);
			if(qty > 0){
				total++;
				if(total <= 4){
					chat.add(Component.translatable("tt.crossroads.boilerplate.alchemy_content", type.getName(), qty));
				}else{
					break;
				}
			}
		}
		if(total > 4){
			chat.add(Component.translatable("tt.crossroads.boilerplate.alchemy_excess", total - 4));
		}
	}

	protected AlchemyCarrierTE(BlockEntityType<?> type, BlockPos pos, BlockState state){
		super(type, pos, state);
	}

	protected AlchemyCarrierTE(BlockEntityType<?> type, BlockPos pos, BlockState state, boolean glass){
		this(type, pos, state);
		this.glass = glass;
	}

	protected void destroyCarrier(float strength){
		if(!broken){
			broken = true;
			BlockState state = getBlockState();
			level.setBlockAndUpdate(worldPosition, Blocks.AIR.defaultBlockState());
			SoundType sound = state.getBlock().getSoundType(state, level, worldPosition, null);
			CRSounds.playSoundServer(level, worldPosition, sound.getBreakSound(), SoundSource.BLOCKS, sound.getVolume(), sound.getPitch());
			AlchemyUtil.releaseChemical(level, worldPosition, contents);
			if(strength > 0F){
				level.explode(null, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), strength, net.minecraft.world.level.Level.ExplosionInteraction.TNT);//We will drop items, because an explosion in your lab is devastating enough without having to re-craft everything
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
	public void serverTick(){
		ITickableTileEntity.super.serverTick();

		if(!init && useCableHeat()){
			cableTemp = getBiomeTemp();
		}
		init = true;

		if(dirtyReag){
			correctReag();
		}

		if(level.getGameTime() % AlchemyUtil.ALCHEMY_TIME == 0){
			spawnParticles();
			performTransfer();
		}
	}

	/**
	 * Spawns cosmetic particles representing contents
	 */
	protected void spawnParticles(){
		double temp = handler.getTemp();
		ServerLevel server = (ServerLevel) level;
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

		Vec3 particlePos = getParticlePos();

		if(liqAmount > 0){
			server.sendParticles(new ColorParticleData(CRParticles.COLOR_LIQUID, new Color((int) (liqCol[0] / liqAmount), (int) (liqCol[1] / liqAmount), (int) (liqCol[2] / liqAmount), (int) (liqCol[3] / liqAmount))), particlePos.x, particlePos.y, particlePos.z, 0, (Math.random() * 2D - 1D) * 0.02D, (Math.random() - 1D) * 0.02D, (Math.random() * 2D - 1D) * 0.02D, 1F);
		}
		if(gasAmount > 0){
			server.sendParticles(new ColorParticleData(CRParticles.COLOR_GAS, new Color((int) (gasCol[0] / gasAmount), (int) (gasCol[1] / gasAmount), (int) (gasCol[2] / gasAmount), (int) (gasCol[3] / gasAmount))), particlePos.x, particlePos.y, particlePos.z, 0, (Math.random() * 2D - 1D) * 0.015D, Math.random() * 0.015D, (Math.random() * 2D - 1D) * 0.015D, 1F);
		}
		if(flameAmount > 0){
			server.sendParticles(new ColorParticleData(CRParticles.COLOR_FLAME, new Color((int) (flameCol[0] / flameAmount), (int) (flameCol[1] / flameAmount), (int) (flameCol[2] / flameAmount), (int) (flameCol[3] / flameAmount))), particlePos.x, particlePos.y, particlePos.z, 0, (Math.random() * 2D - 1D) * 0.015D, Math.random() * 0.015D, (Math.random() * 2D - 1D) * 0.015D, 1F);
		}
		if(solAmount > 0){
			server.sendParticles(new ColorParticleData(CRParticles.COLOR_SOLID, new Color((int) (solCol[0] / solAmount), (int) (solCol[1] / solAmount), (int) (solCol[2] / solAmount), (int) (solCol[3] / solAmount))), particlePos.x - 0.25D + level.random.nextFloat() / 2F, particlePos.y - 0.1F, particlePos.z - 0.25D + level.random.nextFloat() / 2F, 0, 0, 0, 0, 1F);
		}
	}

	/*
	 * Helper method for moving reagents with glassware/solid items. Use is optional, and must be added in the block if used.
	 */
	@Nonnull
	public ItemStack rightClickWithItem(ItemStack stack, boolean sneaking, Player player, InteractionHand hand){
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
			out = player.getItemInHand(hand);
		}else{
			//Move solids from hand into carrier
			IReagent typeProduced = ReagentManager.findReagentForItem(stack.getItem());
			if(typeProduced != null && contents.getTotalQty() < transferCapacity()){
				out.shrink(1);
				contents.addReagent(typeProduced, 1, AlchemyUtil.getInputItemTemp(typeProduced, getBiomeTemp()));
			}
		}

		if(!ItemStack.isSame(out, stack) || !ItemStack.tagMatches(out, stack) || out.getCount() != stack.getCount()){
			setChanged();
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
				Direction side = Direction.from3DDataValue(i);
				BlockEntity te = level.getBlockEntity(worldPosition.relative(side));
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
					setChanged();
				}
			}
		}
	}

	/**
	 * Common implementation for performTransfer() used by multiple subclasses (vessel-type blocks)
	 * @param self The instance calling this method
	 */
	protected static void vesselTransfer(AlchemyCarrierTE self){
		EnumTransferMode[] modes = self.getModes();
		for(int i = 0; i < 6; i++){
			if(modes[i].isOutput()){
				Direction side = Direction.from3DDataValue(i);
				BlockEntity te = self.level.getBlockEntity(self.worldPosition.relative(side));
				LazyOptional<IChemicalHandler> otherOpt;
				if(self.contents.getTotalQty() <= 0 || te == null || !(otherOpt = te.getCapability(Capabilities.CHEMICAL_CAPABILITY, side.getOpposite())).isPresent()){
					continue;
				}

				IChemicalHandler otherHandler = otherOpt.orElseThrow(NullPointerException::new);
				if(otherHandler.getMode(side.getOpposite()) == EnumTransferMode.BOTH && modes[i] == EnumTransferMode.BOTH){
					continue;
				}

				if(self.contents.getTotalQty() != 0){
					if(otherHandler.insertReagents(self.contents, side.getOpposite(), self.handler)){
						self.correctReag();
						self.setChanged();
					}
				}
			}
		}
	}

	@Override
	public void load(CompoundTag nbt){
		super.load(nbt);
		glass = nbt.getBoolean("glass");
		contents = ReagentMap.readFromNBT(nbt);
		cableTemp = nbt.getDouble("temp");
		init = nbt.getBoolean("initHeat");

		dirtyReag = true;
	}

	@Override
	public void saveAdditional(CompoundTag nbt){
		super.saveAdditional(nbt);
		nbt.putBoolean("glass", glass);
		contents.write(nbt);
		nbt.putDouble("temp", cableTemp);
		nbt.putBoolean("initHeat", init);
	}

	protected EnumContainerType getChannel(){
		return glass ? EnumContainerType.GLASS : EnumContainerType.CRYSTAL;
	}

	@Override
	public void setRemoved(){
		super.setRemoved();
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
			return getModes()[side.get3DDataValue()];
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

				//Map the movable reags to an int array of quantities so we can use the relevant MiscUtil method
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
					setChanged();
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

	/**
	 * This class doesn't allow actual storage of fluids
	 * Instead, it assumes storage of reagents, and creates fake internal fluid tanks backed by the reagents
	 * Liquid can only be added and removed in integer quantities of reagents
	 * One fluid tank is created per reagent type with a liquid equivalent
	 * Matters are complicated by multiple fluids possibly mapping to the same reagent, if backed by a fluid ingredient
	 * This class attempts to auto-convert when dealing with mixed fluids it considers equivalent
	 */
	private class FalseFluidHandler implements IFluidHandler{

		private FluidStack simulateFluid(int tank){
			if(tank >= 0 && tank < getTanks()){
				String id = ReagentManager.getFluidReags().get(tank);
				IReagent r = ReagentManager.getReagent(id);
				FluidIngredient refStack = r.getFluid();
				int refQty = r.getFluidQty();
				int qty = contents.getQty(id);
				if(qty > 0 && r.getPhase(contents.getTempC()) == EnumMatterPhase.LIQUID){
					return new FluidStack(CraftingUtil.getPreferredEntry(refStack.getMatchedFluids(), ForgeRegistries.Keys.FLUIDS), qty * refQty);
				}
			}
			return FluidStack.EMPTY;
		}

		@Override
		public int getTanks(){
			return ReagentManager.getFluidReags().size();
		}

		@Nonnull
		@Override
		public FluidStack getFluidInTank(int tank){
			return simulateFluid(tank);
		}

		@Override
		public int getTankCapacity(int tank){
			if(tank >= 0 && tank < ReagentManager.getFluidReags().size()){
				return ReagentManager.getReagent(ReagentManager.getFluidReags().get(tank)).getFluidQty() * transferCapacity();
			}else{
				return 0;
			}
		}

		@Override
		public boolean isFluidValid(int tank, @Nonnull FluidStack stack){
			return tank >= 0 && tank < ReagentManager.getFluidReags().size() && ReagentManager.getReagent(ReagentManager.getFluidReags().get(tank)).getFluid().test(stack);
		}

		@Override
		public int fill(FluidStack resource, FluidAction action){
			//The list is unsorted, so we need a sequential search to find the matching fluid type
			for(int i = 0; i < ReagentManager.getFluidReags().size(); i++){
				String id = ReagentManager.getFluidReags().get(i);
				IReagent reag = ReagentManager.getReagent(id);
				if(reag.getFluid().test(resource)){
					int toFillReag = Math.min(resource.getAmount() / reag.getFluidQty(), transferCapacity() - contents.getTotalQty());
					//Note: toFillReag could be negative
					if(toFillReag <= 0){
						return 0;
					}
					if(action.execute()){
						contents.addReagent(id, toFillReag, AlchemyUtil.getInputFluidTemp(reag, getBiomeTemp()));
						setChanged();
					}
					return toFillReag * reag.getFluidQty();
				}
			}
			return 0;
		}

		@Nonnull
		@Override
		public FluidStack drain(FluidStack resource, FluidAction action){
			//The list is unsorted, so we need a sequential search to find the matching fluid type
			for(int i = 0; i < ReagentManager.getFluidReags().size(); i++){
				String id = ReagentManager.getFluidReags().get(i);
				IReagent reag = ReagentManager.getReagent(id);
				if(reag.getFluid().test(resource)){
					FluidStack tank = simulateFluid(i);
					if(!tank.isEmpty()){
						int drained = Math.min(tank.getAmount(), resource.getAmount());
						int reagQtyDrained = drained / reag.getFluidQty();
						drained = reagQtyDrained * reag.getFluidQty();//Forces rounding down to the nearest full reagent unit
						resource.setAmount(drained);
						//It isn't necessary to modify tank as we normally would, as it's only a conversion and modifying the reagent is sufficient
						if(action.execute()){
							contents.removeReagent(id, reagQtyDrained);
							setChanged();
						}
						return resource;
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
			for(int i = 0; i < ReagentManager.getFluidReags().size(); i++){
				FluidStack tank = simulateFluid(i);
				if(!tank.isEmpty()){
					int drained = Math.min(tank.getAmount(), maxDrain);

					//It isn't necessary to modify tank as we normally would, as it's only a conversion
					tank.setAmount(drained);
					if(action.execute()){
						String id = ReagentManager.getFluidReags().get(i);
						contents.removeReagent(id, drained / ReagentManager.getReagent(id).getFluidQty());
						setChanged();
					}
					return tank;
				}
			}

			return FluidStack.EMPTY;
		}
	}

	protected class ItemHandler implements IItemHandler{

		private final ItemStack[] fakeInventory = new ItemStack[ReagentManager.getRegisteredReags().size()];

		public ItemHandler(){

		}

		private void updateFakeInv(){
			Arrays.fill(fakeInventory, ItemStack.EMPTY);
			int index = 0;
			double endTemp = handler.getTemp();
			for(IReagent reag : contents.keySetReag()){
				ReagentStack rStack = contents.getStack(reag);
				fakeInventory[index] = !rStack.isEmpty() && reag.getPhase(endTemp) == EnumMatterPhase.SOLID ? reag.getStackFromReagent(rStack) : ItemStack.EMPTY;
				index++;
			}
		}

		@Override
		public int getSlots(){
			return fakeInventory.length;
		}

		@Override
		public ItemStack getStackInSlot(int slot){
			updateFakeInv();
			return fakeInventory[slot];
		}

		@Override
		public ItemStack insertItem(int slot, ItemStack stack, boolean simulate){
			if(!stack.isEmpty() && slot == 0){
				//Force to only accept into slot 0.
				//Otherwise, machines that simulate several slots before performing any transfers (ex. hoppers) won't realize that moving items into one slot will decrease capacity in the others.
				IReagent reag = ReagentManager.findReagentForItem(stack.getItem());
				if(reag != null){
					if(dirtyReag){
						correctReag();
					}
					int trans = Math.max(0, Math.min(stack.getCount(), transferCapacity() - contents.getTotalQty()));
					if(!simulate){
						contents.addReagent(reag, trans, AlchemyUtil.getInputItemTemp(reag, getBiomeTemp()));
						dirtyReag = true;
						setChanged();
					}
					ItemStack rejected = stack.copy();
					rejected.setCount(stack.getCount() - trans);
					return rejected;
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
						IReagent reag = ReagentManager.findReagentForItem(fakeInventory[slot].getItem());
						contents.removeReagent(reag, canExtract);
						dirtyReag = true;
						setChanged();
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
			return transferCapacity();
		}

		@Override
		public boolean isItemValid(int slot, @Nonnull ItemStack stack){
			return ReagentManager.findReagentForItem(stack.getItem()) != null;
		}
	}
}
