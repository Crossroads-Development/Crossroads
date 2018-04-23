package com.Da_Technomancer.crossroads.tileentities.alchemy;

import com.Da_Technomancer.crossroads.API.*;
import com.Da_Technomancer.crossroads.API.alchemy.*;
import com.Da_Technomancer.crossroads.API.heat.IHeatHandler;
import com.Da_Technomancer.crossroads.API.technomancy.EnumGoggleLenses;
import com.Da_Technomancer.crossroads.items.ModItems;
import com.Da_Technomancer.crossroads.items.alchemy.AbstractGlassware;
import com.Da_Technomancer.crossroads.particles.ModParticles;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.capabilities.Capability;
import org.apache.commons.lang3.tuple.Triple;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;

public class AlembicTileEntity extends TileEntity implements IReactionChamber, ITickable, IInfoTE{

	protected ReagentStack[] contents = new ReagentStack[AlchemyCore.REAGENT_COUNT];
	protected double heat = 0;
	protected double amount = 0;
	private boolean dirtyReag = false;
	private static final double CAPACITY = 3000;
	private double cableTemp = 0;
	private boolean init = false;

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState){
		return oldState.getBlock() != newState.getBlock();
	}

	@Override
	public void addInfo(ArrayList<String> chat, IInfoDevice device, EntityPlayer player, @Nullable EnumFacing side){
		if(device == ModItems.omnimeter || device == EnumGoggleLenses.RUBY){
			chat.add("Temp: " + MiscOp.betterRound(cableTemp, 3) + "°C");
		}
		if(device == ModItems.omnimeter || device == EnumGoggleLenses.DIAMOND){
			if(amount == 0){
				chat.add("No reagents");
			}
			for(ReagentStack reag : contents){
				if(reag != null){
					chat.add(reag.toString());
				}
			}
		}
	}

	@Override
	public void update(){
		if(world.isRemote){
			return;
		}
		if(!init){
			cableTemp = EnergyConverters.convertBiomeTemp(world.getBiomeForCoordsBody(pos).getTemperature(pos));
			init = true;
		}
		
		if(dirtyReag){
			correctReag();
		}

		if(world.getTotalWorldTime() % AlchemyCore.ALCHEMY_TIME == 0){
			performReaction();
		}
	}
	
	@Override
	public void dropItem(ItemStack stack){
		InventoryHelper.spawnItemStack(world, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, stack);
	}

	/**
	 * Normal behavior: 
	 * Shift click with empty hand: Remove phial
	 * Normal click with empty hand: Try to remove solid reagent from phial
	 * Normal click with phial: Add phial to stand, or merge phial contents
	 * Normal click with non-phial item: Try to add solid reagent to alembic
	 */
	@Nonnull
	public ItemStack rightClickWithItem(ItemStack stack, boolean sneaking){
		if(stack.isEmpty()){
			double temp = getTemp();

			for(int i = 0; i < contents.length; i++){
				ReagentStack reag = contents[i];
				if(reag != null && reag.getPhase(temp) == EnumMatterPhase.SOLID){
					ItemStack toRemove = reag.getType().getStackFromReagent(reag);
					if(!toRemove.isEmpty()){
						double amountDecrease = reag.getType().getReagentFromStack(toRemove).getAmount();
						if(contents[i].increaseAmount(-amountDecrease) <= 0){
							contents[i] = null;
						}
						heat -= (temp + 273D) * amountDecrease;
						markDirty();
						dirtyReag = true;

						return toRemove;
					}
				}
			}				
		}else if(stack.getItem() instanceof AbstractGlassware){
			Triple<ReagentStack[], Double, Double> other_phial = ((AbstractGlassware) stack.getItem()).getReagants(stack);
			ReagentStack[] reag = other_phial.getLeft();
			double endHeat = other_phial.getMiddle();
			double endAmount = other_phial.getRight();

			if(other_phial.getRight() <= 0){
				//Move from block to item

				double space = ((AbstractGlassware) stack.getItem()).getCapacity() - endAmount;
				if(space <= 0){
					return stack;
				}
				double temp = getTemp() + 273D;//In kelvin
				boolean changed = false;

				HashSet<Integer> validIds = new HashSet<Integer>(4);
				double totalValid = 0;

				for(int i = 0; i < AlchemyCore.REAGENT_COUNT; i++){
					ReagentStack r = contents[i];
					if(r != null){
						validIds.add(i);
						totalValid += r.getAmount();
					}
				}

				totalValid = Math.min(1D, space / totalValid);

				for(int i : validIds){
					ReagentStack r = contents[i];
					double moved = r.getAmount() * totalValid;
					if(moved <= 0D){
						continue;
					}
					amount -= moved;
					changed = true;
					space -= moved;
					double heatTrans = moved * temp;
					if(r.increaseAmount(-moved) <= 0){
						contents[i] = null;
					}
					endAmount += moved;
					heat -= heatTrans;
					endHeat += heatTrans;
					if(reag[i] == null){
						reag[i] = new ReagentStack(AlchemyCore.REAGENTS[i], moved);
					}else{
						reag[i].increaseAmount(moved);
					}
				}

				if(changed){
					dirtyReag = true;
					markDirty();
					((AbstractGlassware) stack.getItem()).setReagents(stack, reag, endHeat, endAmount);
				}
				return stack;

			}else{
				//Move from item to block
				double space = CAPACITY - amount;
				if(space <= 0){
					return stack;
				}
				double callerTemp = other_phial.getMiddle() / other_phial.getRight();//In kelvin
				boolean changed;

				HashSet<Integer> validIds = new HashSet<Integer>(4);
				double totalValid = 0;

				for(int i = 0; i < AlchemyCore.REAGENT_COUNT; i++){
					ReagentStack r = reag[i];
					if(r != null){
						validIds.add(i);
						totalValid += r.getAmount();
					}
				}

				totalValid = Math.min(1D, space / totalValid);

				changed = !validIds.isEmpty();

				for(int i : validIds){
					ReagentStack r = reag[i];
					double moved = r.getAmount() * totalValid;
					if(moved <= 0D){
						continue;
					}
					amount += moved;
					space -= moved;
					double heatTrans = moved * callerTemp;
					if(r.increaseAmount(-moved) <= 0){
						reag[i] = null;
					}
					endAmount -= moved;
					heat += heatTrans;
					endHeat -= heatTrans;
					if(contents[i] == null){
						contents[i] = new ReagentStack(AlchemyCore.REAGENTS[i], moved);
					}else{
						contents[i].increaseAmount(moved);
					}
				}

				if(changed){
					dirtyReag = true;
					markDirty();
					((AbstractGlassware) stack.getItem()).setReagents(stack, reag, endHeat, endAmount);
				}
				return stack;
			}

		}else{
			IReagent toAdd = AlchemyCore.ITEM_TO_REAGENT.get(stack.getItem());
			if(toAdd != null){
				ReagentStack toAddStack = toAdd.getReagentFromStack(stack);
				if(toAddStack != null && CAPACITY - amount >= toAddStack.getAmount()){
					if(contents[toAdd.getIndex()] == null){
						contents[toAdd.getIndex()] = toAddStack;
					}else{
						contents[toAdd.getIndex()].increaseAmount(toAddStack.getAmount());
					}
					heat += Math.max(0, Math.min(toAdd.getMeltingPoint() + 273D, EnergyConverters.convertBiomeTemp(world.getBiomeForCoordsBody(pos).getTemperature(pos)) + 273D)) * toAddStack.getAmount();
					markDirty();
					dirtyReag = true;
					stack.shrink(1);
					return stack;
				}
			}
		}

		return stack;
	}

	@Override
	public ReagentStack[] getReagants(){
		return contents;
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
	public double getReactionCapacity(){
		return CAPACITY * 1.5D;
	}

	private boolean broken = false;

	@Override
	public void destroyChamber(){
		if(!broken){
			broken = true;
			double temp = getTemp();
			IBlockState state = world.getBlockState(pos);
			world.setBlockState(pos, Blocks.AIR.getDefaultState());
			SoundType sound = state.getBlock().getSoundType(state, world, pos, null);
			world.playSound(null, pos, sound.getBreakSound(), SoundCategory.BLOCKS, sound.getVolume(), sound.getPitch());
			for(ReagentStack r : contents){
				if(r != null){
					r.getType().onRelease(world, pos, r.getAmount(), temp, r.getPhase(temp), contents);
				}
			}
		}
	}

	private void performReaction(){
		boolean[] solvents = new boolean[EnumSolventType.values().length];

		for(int i = 0; i < AlchemyCore.REAGENT_COUNT; i++){
			ReagentStack reag = contents[i];
			if(reag != null){
				IReagent type = reag.getType();
				solvents[EnumSolventType.AQUA_REGIA.ordinal()] |= i == 11;//Aqua regia is a special case where it works no matter the phase, but ONLY works at all if a polar solvent is present. 

				if(type.getMeltingPoint() <= correctTemp() && type.getBoilingPoint() > correctTemp() && type.solventType() != null){
					solvents[type.solventType().ordinal()] = true;
				}
			}
		}

		solvents[EnumSolventType.AQUA_REGIA.ordinal()] &= solvents[EnumSolventType.POLAR.ordinal()];

		for(IReaction react : AlchemyCore.REACTIONS){
			if(react.performReaction(this, solvents)){
				solvents = correctReag();
				if(solvents == null){
					return;
				}
				break;
			}
		}
	}


	@Override
	public void addVisualEffect(EnumParticleTypes particleType, double speedX, double speedY, double speedZ, int... particleArgs){

	}

	private double correctTemp(){
		//Shares heat between internal cable & contents
		cableTemp = amount <= 0 ? cableTemp : ((273D + cableTemp + EnergyConverters.ALCHEMY_TEMP_CONVERSION * heat) / (EnergyConverters.ALCHEMY_TEMP_CONVERSION * amount + 1D)) - 273D;
		//cableTemp = amount <= 0 ? cableTemp : (cableTemp + EnergyConverters.ALCHEMY_TEMP_CONVERSION * amount * ((heat / amount) - 273D)) / (EnergyConverters.ALCHEMY_TEMP_CONVERSION * amount + 1D);
		heat = (cableTemp + 273D) * amount;
		return cableTemp;
	}

	@Nullable
	private boolean[] correctReag(){
		dirtyReag = false;
		amount = 0;
		for(ReagentStack r : contents){
			if(r != null){
				amount += r.getAmount();
			}
		}
		if(amount == 0){
			return null;
		}

		double endTemp = correctTemp();

		boolean[] solvents = new boolean[EnumSolventType.values().length];

		for(int i = 0; i < AlchemyCore.REAGENT_COUNT; i++){
			ReagentStack reag = contents[i];
			if(reag != null){
				if(reag.getAmount() >= AlchemyCore.MIN_QUANTITY){
					IReagent type = reag.getType();
					solvents[EnumSolventType.AQUA_REGIA.ordinal()] |= i == 11;//Aqua regia is a special case where it works no matter the phase, but ONLY works at all if a polar solvent is present. 

					if(type.getMeltingPoint() <= endTemp && type.getBoilingPoint() > endTemp && type.solventType() != null){
						solvents[type.solventType().ordinal()] = true;
					}
				}else{
					heat -= (endTemp + 273D) * reag.getAmount();
					contents[i] = null;
				}
			}
		}

		solvents[EnumSolventType.AQUA_REGIA.ordinal()] &= solvents[EnumSolventType.POLAR.ordinal()];
		boolean[] blankSolvents = new boolean[EnumSolventType.values().length];
		double ambientTemp = EnergyConverters.convertBiomeTemp(world.getBiomeForCoordsBody(pos).getTemperature(pos));

		
		ReagentStack[] movedContents = new ReagentStack[AlchemyCore.REAGENT_COUNT];
		
		for(int i = 0; i < contents.length; i++){
			ReagentStack reag = contents[i];
			if(reag == null){
				continue;
			}
			reag.updatePhase(endTemp);


			//Movement of upwards-flowing phases from alembic to phial
			if(reag.getPhase(endTemp).flowsUp()){
				contents[i] = null;
				heat -= (endTemp + 273D) * reag.getAmount();
				amount -= reag.getAmount();

				reag.updatePhase(ambientTemp);
				
				movedContents[reag.getType().getIndex()] = reag;
				markDirty();
			}
		}
		

		EnumFacing dir = world.getBlockState(pos).getValue(Properties.HORIZONTAL_FACING);
		TileEntity te = world.getTileEntity(pos.offset(dir));
		if(te instanceof GlasswareHolderTileEntity){
			GlasswareHolderTileEntity gh = (GlasswareHolderTileEntity) te;
			
			IChemicalHandler handler = gh.getCapability(Capabilities.CHEMICAL_HANDLER_CAPABILITY, EnumFacing.UP);
			if(handler != null){
				handler.insertReagents(movedContents, EnumFacing.UP, null);
			}
		}

		for(ReagentStack reag : movedContents){
			if(reag == null){
				continue;
			}
			Color c = reag.getType().getColor(reag.getPhase(ambientTemp));
			if(reag.getPhase(ambientTemp).flowsDown()){
				((WorldServer) world).spawnParticle(ModParticles.COLOR_LIQUID, false, pos.getX() + 0.5D + dir.getFrontOffsetX(), pos.getY() + 1.1D, pos.getZ() + 0.5D + dir.getFrontOffsetZ(), 0, 0, (Math.random() * 0.05D) - 0.1D, 0, 1F, c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha());

			}else{
				((WorldServer) world).spawnParticle(ModParticles.COLOR_GAS, false, pos.getX() + 0.5D + dir.getFrontOffsetX(), pos.getY() + 1.1D, pos.getZ() + 0.5D + dir.getFrontOffsetZ(), 0, 0, (Math.random() * -0.05D) + 0.1D, 0, 1F, c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha());
			}
		}
		return solvents;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		cableTemp = nbt.getDouble("temp");
		init = nbt.getBoolean("init");

		for(int i = 0; i < AlchemyCore.REAGENT_COUNT; i++){
			contents[i] = nbt.hasKey(i + "_am") ? new ReagentStack(AlchemyCore.REAGENTS[i], nbt.getDouble(i + "_am")) : null;
		}
		nbt.setDouble("heat", heat);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		nbt.setDouble("temp", cableTemp);
		nbt.setBoolean("init", init);
		heat = nbt.getDouble("heat");
		for(int i = 0; i < AlchemyCore.REAGENT_COUNT; i++){
			if(contents[i] != null){
				nbt.setDouble(i + "_am", contents[i].getAmount());
			}
		}
		dirtyReag = true;

		return nbt;
	}

	private final HeatHandler heatHandler = new HeatHandler();

	@Override
	public boolean hasCapability(Capability<?> cap, EnumFacing side){
		if((side == null || side == EnumFacing.DOWN) && cap == Capabilities.HEAT_HANDLER_CAPABILITY){
			return true;
		}
		return super.hasCapability(cap, side);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> cap, EnumFacing side){
		if((side == null || side == EnumFacing.DOWN) && cap == Capabilities.HEAT_HANDLER_CAPABILITY){
			return (T) heatHandler;
		}
		return super.getCapability(cap, side);
	}

	private class HeatHandler implements IHeatHandler{

		private void init(){
			if(!init){
				init = true;
				cableTemp = EnergyConverters.convertBiomeTemp(world.getBiomeForCoordsBody(pos).getTemperature(pos));
			}
		}

		@Override
		public double getTemp(){
			init();
			return cableTemp;
		}

		@Override
		public void setTemp(double tempIn){
			init = true;
			cableTemp = tempIn;
			//Shares heat between internal cable & contents
			if(amount != 0){
				cableTemp = (cableTemp + EnergyConverters.ALCHEMY_TEMP_CONVERSION * amount * ((heat / amount) - 273D)) / (EnergyConverters.ALCHEMY_TEMP_CONVERSION * amount + 1D);		
				heat = (cableTemp + 273D) * amount;
				dirtyReag = true;
			}
			markDirty();
		}

		@Override
		public void addHeat(double tempChange){
			init();
			cableTemp += tempChange;
			//Shares heat between internal cable & contents
			if(amount != 0){
				cableTemp = (cableTemp + EnergyConverters.ALCHEMY_TEMP_CONVERSION * amount * ((heat / amount) - 273D)) / (EnergyConverters.ALCHEMY_TEMP_CONVERSION * amount + 1D);		
				heat = (cableTemp + 273D) * amount;
				dirtyReag = true;
			}
			markDirty();
		}
	}
}
