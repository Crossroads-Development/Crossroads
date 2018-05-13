package com.Da_Technomancer.crossroads.tileentities.alchemy;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.EnergyConverters;
import com.Da_Technomancer.crossroads.API.MiscOp;
import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.API.alchemy.*;
import com.Da_Technomancer.crossroads.API.heat.IHeatHandler;
import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import com.Da_Technomancer.crossroads.items.ModItems;
import com.Da_Technomancer.crossroads.items.alchemy.AbstractGlassware;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import org.apache.commons.lang3.tuple.Triple;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;

public class GlasswareHolderTileEntity extends AlchemyReactorTE{

	private boolean occupied = false;
	/**
	 * Meaningless if !occupied. If true, florence flask, else phial. 
	 */
	private boolean florence = false;
	private double cableTemp = 0;
	private boolean init = false;

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState){
		return oldState.getBlock() != newState.getBlock();
	}

	@Override
	public void addInfo(ArrayList<String> chat, EntityPlayer player, @Nullable EnumFacing side){
		if(occupied && amount > 0){
			chat.add("Temp: " + MiscOp.betterRound(florence ? cableTemp : (heat / amount) - 273D, 3) + "°C");
		}
		if(amount == 0){
			chat.add("No reagents");
		}
		for(ReagentStack reag : contents){
			if(reag != null){
				chat.add(reag.toString());
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

		super.update();
	}

	@Override
	protected double transferCapacity(){
		return occupied ? florence ? ModItems.florenceFlask.getCapacity() : ModItems.phial.getCapacity() : 0;
	}

	@Override
	public void destroyChamber(){
		double temp = getTemp();
		IBlockState state = world.getBlockState(pos);
		world.setBlockState(pos, state.withProperty(Properties.ACTIVE, false).withProperty(Properties.CRYSTAL, false).withProperty(Properties.CONTAINER_TYPE, false));
		world.playSound(null, pos, SoundType.GLASS.getBreakSound(), SoundCategory.BLOCKS, SoundType.GLASS.getVolume(), SoundType.GLASS.getPitch());
		occupied = false;
		florence = false;
		this.heat = 0;
		this.contents = new ReagentStack[AlchemyCore.REAGENT_COUNT];
		dirtyReag = true;
		for(ReagentStack r : contents){
			if(r != null){
				r.getType().onRelease(world, pos, r.getAmount(), temp, r.getPhase(temp), contents);
			}
		}
	}

	public void onBlockDestroyed(IBlockState state){
		if(occupied){
			AbstractGlassware glasswareType = florence ? ModItems.florenceFlask : ModItems.phial;
			ItemStack flask = new ItemStack(glasswareType, 1, glass ? 0 : 1);
			glasswareType.setReagents(flask, contents, heat, amount);
			this.heat = 0;
			this.contents = new ReagentStack[AlchemyCore.REAGENT_COUNT];
			dirtyReag = true;
			occupied = false;
			markDirty();
			InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), flask);
		}
	}

	/**
	 * Normal behavior: 
	 * Shift click with empty hand: Remove phial
	 * Normal click with empty hand: Try to remove solid reagent
	 * Normal click with phial: Add phial to stand, or merge phial contents
	 * Normal click with non-phial item: Try to add solid reagent
	 */
	@Nonnull
	@Override
	public ItemStack rightClickWithItem(ItemStack stack, boolean sneaking){
		IBlockState state = world.getBlockState(pos);

		if(occupied){
			if(stack.isEmpty() && sneaking){
				AbstractGlassware glasswareType = florence ? ModItems.florenceFlask : ModItems.phial;
				world.setBlockState(pos, state.withProperty(Properties.ACTIVE, false).withProperty(Properties.CRYSTAL, false).withProperty(Properties.CONTAINER_TYPE, false));
				ItemStack flask = new ItemStack(glasswareType, 1, glass ? 0 : 1);
				occupied = false;
				glasswareType.setReagents(flask, contents, heat, amount);
				this.heat = 0;
				this.contents = new ReagentStack[AlchemyCore.REAGENT_COUNT];
				dirtyReag = true;
				markDirty();
				return flask;
			}
		}else if(stack.getItem() == ModItems.phial || stack.getItem() == ModItems.florenceFlask){
			//Add item into TE
			Triple<ReagentStack[], Double, Double> phialCont = ((AbstractGlassware) stack.getItem()).getReagants(stack);
			this.heat = phialCont.getMiddle();
			this.contents = phialCont.getLeft();
			glass = stack.getMetadata() == 0;
			dirtyReag = true;
			markDirty();
			occupied = true;
			florence = stack.getItem() == ModItems.florenceFlask;
			world.setBlockState(pos, state.withProperty(Properties.ACTIVE, true).withProperty(Properties.CRYSTAL, !glass).withProperty(Properties.CONTAINER_TYPE, florence));
			return ItemStack.EMPTY;
		}

		return super.rightClickWithItem(stack, sneaking);
	}

	@Override
	protected Vec3d getParticlePos(){
		return new Vec3d(pos).addVector(0.5D, 0.25D, 0.5D);
	}

	@Override
	protected double correctTemp(){
		if(florence){
			//Shares heat between internal cable & contents
			cableTemp = amount <= 0 ? cableTemp : ((273D + cableTemp + EnergyConverters.ALCHEMY_TEMP_CONVERSION * heat) / (EnergyConverters.ALCHEMY_TEMP_CONVERSION * amount + 1D)) - 273D;
			heat = (cableTemp + 273D) * amount;
			return cableTemp;
		}
		return super.correctTemp();
	}

	@Override
	protected void performTransfer(){
		IBlockState state = world.getBlockState(pos);
		if(state.getBlock() == ModBlocks.glasswareHolder && state.getValue(Properties.ACTIVE)){
			EnumFacing side = EnumFacing.UP;
			TileEntity te = world.getTileEntity(pos.offset(side));
			if(amount <= 0 || te == null || !te.hasCapability(Capabilities.CHEMICAL_HANDLER_CAPABILITY, side.getOpposite())){
				return;
			}

			IChemicalHandler otherHandler = te.getCapability(Capabilities.CHEMICAL_HANDLER_CAPABILITY, side.getOpposite());
			EnumContainerType cont = otherHandler.getChannel(side.getOpposite());
			if(cont != EnumContainerType.NONE && ((cont == EnumContainerType.GLASS) != glass)){
				return;
			}

			if(amount != 0){
				if(otherHandler.insertReagents(contents, side.getOpposite(), handler, state.getValue(Properties.REDSTONE_BOOL))){
					correctReag();
					markDirty();
				}
			}
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		cableTemp = nbt.getDouble("temp");
		init = nbt.getBoolean("init");
		occupied = nbt.getBoolean("occupied");
		florence = occupied && nbt.getBoolean("florence");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		nbt.setDouble("temp", cableTemp);
		nbt.setBoolean("init", init);
		nbt.setBoolean("occupied", occupied);
		nbt.setBoolean("florence", florence);
		return nbt;
	}

	@Override
	protected EnumTransferMode[] getModes(){
		EnumTransferMode[] modes = {EnumTransferMode.NONE, EnumTransferMode.NONE, EnumTransferMode.NONE, EnumTransferMode.NONE, EnumTransferMode.NONE, EnumTransferMode.NONE};
		if(occupied){
			modes[1] = EnumTransferMode.BOTH;
		}
		return modes;
	}

	private final HeatHandler heatHandler = new HeatHandler();

	@Override
	public boolean hasCapability(Capability<?> cap, EnumFacing side){
		if((side == null || side == EnumFacing.UP) && cap == Capabilities.CHEMICAL_HANDLER_CAPABILITY && occupied){
			return true;
		}
		if((side == null || side == EnumFacing.DOWN) && cap == Capabilities.HEAT_HANDLER_CAPABILITY){
			IBlockState state = world.getBlockState(pos);
			if(state.getValue(Properties.CONTAINER_TYPE)){
				return true;
			}
		}
		return super.hasCapability(cap, side);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> cap, EnumFacing side){
		if((side == null || side == EnumFacing.UP) && cap == Capabilities.CHEMICAL_HANDLER_CAPABILITY && occupied){
			return (T) handler;
		}
		if((side == null || side == EnumFacing.DOWN) && cap == Capabilities.HEAT_HANDLER_CAPABILITY){
			IBlockState state = world.getBlockState(pos);
			if(state.getValue(Properties.CONTAINER_TYPE)){
				return (T) heatHandler;
			}
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
