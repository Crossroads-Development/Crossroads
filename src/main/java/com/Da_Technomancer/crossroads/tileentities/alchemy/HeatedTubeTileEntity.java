package com.Da_Technomancer.crossroads.tileentities.alchemy;

import java.awt.Color;
import java.util.ArrayList;

import javax.annotation.Nullable;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.EnergyConverters;
import com.Da_Technomancer.crossroads.API.IInfoDevice;
import com.Da_Technomancer.crossroads.API.IInfoTE;
import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.API.alchemy.AlchemyCore;
import com.Da_Technomancer.crossroads.API.alchemy.AlchemyHelper;
import com.Da_Technomancer.crossroads.API.alchemy.EnumContainerType;
import com.Da_Technomancer.crossroads.API.alchemy.EnumMatterPhase;
import com.Da_Technomancer.crossroads.API.alchemy.EnumTransferMode;
import com.Da_Technomancer.crossroads.API.alchemy.IChemicalHandler;
import com.Da_Technomancer.crossroads.API.alchemy.IReagent;
import com.Da_Technomancer.crossroads.API.alchemy.ReagentStack;
import com.Da_Technomancer.crossroads.API.alchemy.SolventType;
import com.Da_Technomancer.crossroads.API.heat.IHeatHandler;
import com.Da_Technomancer.crossroads.API.technomancy.EnumGoggleLenses;
import com.Da_Technomancer.crossroads.items.ModItems;
import com.Da_Technomancer.crossroads.particles.ModParticles;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.capabilities.Capability;

public class HeatedTubeTileEntity extends TileEntity implements ITickable, IInfoTE{

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState){
		return oldState.getBlock() != newState.getBlock();
	}

	private static final double TEMP_CONVERSION = .025D;

	private boolean glass;
	private final ReagentStack[] contents = new ReagentStack[AlchemyCore.REAGENT_COUNT];
	private double heat = 0;
	private double amount = 0;
	private boolean dirtyReag = false;
	private double cableTemp = 0;
	private boolean init = false;

	/**
	 * @param chat Add info to this list, 1 line per entry. 
	 * @param device The device type calling this method. 
	 * @param player The player using the info device.
	 * @param side The viewed EnumFacing (only used by goggles).
	 */
	public void addInfo(ArrayList<String> chat, IInfoDevice device, EntityPlayer player, @Nullable EnumFacing side){
		if(device == ModItems.omnimeter || device == EnumGoggleLenses.DIAMOND){
			chat.add("Temp: " + cableTemp + "°C");
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

	public HeatedTubeTileEntity(){
		super();
	}

	public HeatedTubeTileEntity(boolean glass){
		super();
		this.glass = glass;
	}

	private void correctReag(){
		amount = 0;
		for(ReagentStack r : contents){
			if(r != null){
				amount += r.getAmount();
			}
		}
		if(amount == 0){
			return;
		}

		//Shares heat between internal cable & contents
		cableTemp = (cableTemp + TEMP_CONVERSION * amount * ((heat / amount) - 273D)) / (TEMP_CONVERSION * amount + 1D);		
		heat = (cableTemp + 273D) * amount;

		boolean hasPolar = false;
		boolean hasNonPolar = false;
		boolean hasAquaRegia = false;//Aqua regia is a special case where it works no matter the phase, but ONLY works at all if a polar solvent is present. 

		for(int i = 0; i < AlchemyCore.REAGENT_COUNT; i++){
			ReagentStack reag = contents[i];
			if(reag != null){
				if(reag.getAmount() >= AlchemyHelper.MIN_QUANTITY){
					IReagent type = reag.getType();
					hasAquaRegia |= i == 11;

					if(type.getMeltingPoint() <= cableTemp && type.getBoilingPoint() > cableTemp){
						SolventType solv = type.solventType();
						hasPolar |= solv == SolventType.POLAR || solv == SolventType.MIXED_POLAR;
						hasNonPolar |= solv == SolventType.NON_POLAR || solv == SolventType.MIXED_POLAR;
						hasAquaRegia |= solv == SolventType.AQUA_REGIA;
					}
				}else{
					heat -= (cableTemp + 273D) * reag.getAmount();
					contents[i] = null;
				}
			}
		}

		hasAquaRegia &= hasPolar;

		for(int i = 0; i < contents.length; i++){
			ReagentStack reag = contents[i];
			if(reag == null){
				continue;
			}
			reag.updatePhase(cableTemp, hasPolar, hasNonPolar, hasAquaRegia);
		}
	}

	@Override
	public void update(){
		if(world.isRemote){
			return;
		}
		if(!init){
			cableTemp = EnergyConverters.BIOME_TEMP_MULT * world.getBiomeForCoordsBody(pos).getTemperature(pos);
			init = true;
		}
		if(dirtyReag){
			correctReag();
			dirtyReag = false;
		}

		if(world.getTotalWorldTime() % AlchemyCore.ALCHEMY_TIME == 0){
			double temp = handler.getTemp();
			WorldServer server = (WorldServer) world;
			float liqAmount = 0;
			float[] liqCol = new float[4];
			float gasAmount = 0;
			float[] gasCol = new float[4];
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
						default:
							break;
					}
				}
			}
			if(liqAmount > 0){
				server.spawnParticle(ModParticles.COLOR_LIQUID, false, (float) pos.getX() + .5F, (float) pos.getY() + .5F, (float) pos.getZ() + .5F, 0, (Math.random() * 2D - 1D) * 0.02D, (Math.random() - 1D) * 0.02D, (Math.random() * 2D - 1D) * 0.02D, 1F, new int[] {(int) (liqCol[0] / liqAmount), (int) (liqCol[1] / liqAmount), (int) (liqCol[2] / liqAmount), (int) (liqCol[3] / liqAmount)});
			}
			if(gasAmount > 0){
				server.spawnParticle(ModParticles.COLOR_GAS, false, (float) pos.getX() + .5F, (float) pos.getY() + .5F, (float) pos.getZ() + .5F, 0, (Math.random() * 2D - 1D) * 0.015D, Math.random() * 0.015D, (Math.random() * 2D - 1D) * 0.015D, 1F, new int[] {(int) (gasCol[0] / gasAmount), (int) (gasCol[1] / gasAmount), (int) (gasCol[2] / gasAmount), (int) (gasCol[3] / gasAmount)});
			}


			EnumFacing side = world.getBlockState(pos).getValue(Properties.HORIZONTAL_FACING);
			TileEntity te = world.getTileEntity(pos.offset(side));
			if(amount <= 0 || te == null || !te.hasCapability(Capabilities.CHEMICAL_HANDLER_CAPABILITY, side.getOpposite())){
				return;
			}

			IChemicalHandler otherHandler = te.getCapability(Capabilities.CHEMICAL_HANDLER_CAPABILITY, side.getOpposite());
			EnumContainerType cont = otherHandler.getChannel(side.getOpposite());
			if(cont != EnumContainerType.NONE && (cont == EnumContainerType.GLASS ? !glass : glass)){
				return;
			}

			if(amount != 0){
				if(otherHandler.insertReagents(contents, side.getOpposite(), handler)){
					correctReag();
					markDirty();
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
		cableTemp = nbt.getDouble("temp");
		init = nbt.getBoolean("init");
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
		nbt.setDouble("temp", cableTemp);
		nbt.setBoolean("init", init);
		return nbt;
	}

	@Override
	public boolean hasCapability(Capability<?> cap, EnumFacing side){
		if(cap == Capabilities.CHEMICAL_HANDLER_CAPABILITY && (side == null || side.getAxis() == world.getBlockState(pos).getValue(Properties.HORIZONTAL_FACING).getAxis())){
			return true;
		}
		if(cap == Capabilities.HEAT_HANDLER_CAPABILITY && (side == null || side.getAxis() == EnumFacing.Axis.Y)){
			return true;
		}
		return super.hasCapability(cap, side);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> cap, EnumFacing side){
		if(cap == Capabilities.CHEMICAL_HANDLER_CAPABILITY && (side == null || side.getAxis() == world.getBlockState(pos).getValue(Properties.HORIZONTAL_FACING).getAxis())){
			return (T) handler;
		}
		if(cap == Capabilities.HEAT_HANDLER_CAPABILITY && (side == null || side.getAxis() == EnumFacing.Axis.Y)){
			return (T) heatHandler;
		}
		return super.getCapability(cap, side);
	}

	private final HeatHandler heatHandler = new HeatHandler();
	private final AlchHandler handler = new AlchHandler();

	private class HeatHandler implements IHeatHandler{

		private void init(){
			if(!init){
				init = true;
				cableTemp = EnergyConverters.BIOME_TEMP_MULT * world.getBiomeForCoordsBody(pos).getTemperature(pos);
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
				cableTemp = (cableTemp + TEMP_CONVERSION * amount * ((heat / amount) - 273D)) / (TEMP_CONVERSION * amount + 1D);		
				heat = (cableTemp + 273D) * amount;
			}
			markDirty();
		}

		@Override
		public void addHeat(double tempChange){
			init();
			cableTemp += tempChange;
			//Shares heat between internal cable & contents
			if(amount != 0){
				cableTemp = (cableTemp + TEMP_CONVERSION * amount * ((heat / amount) - 273D)) / (TEMP_CONVERSION * amount + 1D);		
				heat = (cableTemp + 273D) * amount;
			}
			markDirty();
		}
	}

	private class AlchHandler implements IChemicalHandler{

		@Override
		public EnumTransferMode getMode(EnumFacing side){
			return side == world.getBlockState(pos).getValue(Properties.HORIZONTAL_FACING) ? EnumTransferMode.OUTPUT : EnumTransferMode.INPUT;
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
		public boolean insertReagents(ReagentStack[] reag, EnumFacing side, IChemicalHandler caller){
			if(getMode(side) == EnumTransferMode.INPUT){
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
