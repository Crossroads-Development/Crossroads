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
				double ratioToMove = Math.max(0, Math.min(1D, (CAPACITY - amount) / phialAmount));
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
			IReagent typeProduced = AlchemyCore.ITEM_TO_REAGENT.get(stack.getItem());
			if(typeProduced != null){
				double amountProduced = typeProduced.getReagentFromStack(stack).getAmount();
				if(amountProduced <= CAPACITY - amount){
					amount += amountProduced;
					heat += Math.max(0, Math.min(typeProduced.getMeltingPoint() + 273D, EnergyConverters.convertBiomeTemp(world.getBiomeForCoordsBody(pos).getTemperature(pos)) + 273D)) * amountProduced;
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
		for(IReaction react : AlchemyCore.REACTIONS){
			if(react.performReaction(this)){
				correctReag();
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

	private boolean correctReag(){
		dirtyReag = false;
		amount = 0;
		for(ReagentStack r : contents){
			if(r != null){
				amount += r.getAmount();
			}
		}
		if(amount == 0){
			return false;
		}

		double endTemp = correctTemp();

		for(int i = 0; i < AlchemyCore.REAGENT_COUNT; i++){
			ReagentStack reag = contents[i];
			if(reag != null && reag.getAmount() < AlchemyCore.MIN_QUANTITY){
				heat -= (endTemp + 273D) * reag.getAmount();
				contents[i] = null;
			}
		}

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
		return true;
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
