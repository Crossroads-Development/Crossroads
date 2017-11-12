package com.Da_Technomancer.crossroads.tileentities.alchemy;

import java.awt.Color;
import java.util.ArrayList;

import javax.annotation.Nullable;

import com.Da_Technomancer.crossroads.API.Capabilities;
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
import com.Da_Technomancer.crossroads.API.packets.ModPackets;
import com.Da_Technomancer.crossroads.API.packets.SendChatToClient;
import com.Da_Technomancer.crossroads.API.technomancy.EnumGoggleLenses;
import com.Da_Technomancer.crossroads.items.ModItems;
import com.Da_Technomancer.crossroads.particles.ModParticles;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.capabilities.Capability;

public class FlowLimiterTileEntity extends TileEntity implements ITickable, IInfoTE{

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState){
		return oldState.getBlock() != newState.getBlock();
	}
	
	private static final double[] LIMITS = new double[] {0.5D, 1, 2, 4};

	private boolean glass;
	private final ReagentStack[] contents = new ReagentStack[AlchemyCore.REAGENT_COUNT];
	private double heat = 0;
	private double amount = 0;
	private boolean dirtyReag = false;
	private int limitIndex = 0;

	/**
	 * @param chat Add info to this list, 1 line per entry. 
	 * @param device The device type calling this method. 
	 * @param player The player using the info device.
	 * @param side The viewed EnumFacing (only used by goggles).
	 */
	public void addInfo(ArrayList<String> chat, IInfoDevice device, EntityPlayer player, @Nullable EnumFacing side){
		if(device == ModItems.omnimeter || device == EnumGoggleLenses.DIAMOND){
			if(amount != 0){
				chat.add("Temp: " + handler.getTemp() + "°C");
			}else{
				chat.add("No reagents");
			}
			for(ReagentStack reag : contents){
				if(reag != null){
					chat.add(reag.toString());
				}
			}
		}
	}

	public FlowLimiterTileEntity(){
		super();
	}

	public FlowLimiterTileEntity(boolean glass){
		super();
		this.glass = glass;
	}

	public void cycleLimit(EntityPlayerMP player){
		limitIndex += 1;
		limitIndex %= LIMITS.length;
		markDirty();
		ModPackets.network.sendTo(new SendChatToClient("Reagent movement limit configured to: " + LIMITS[limitIndex], 25856), player);//CHAT_ID chosen at random
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
		double endTemp = (heat / amount) - 273D;

		boolean hasPolar = false;
		boolean hasNonPolar = false;
		boolean hasAquaRegia = false;//Aqua regia is a special case where it works no matter the phase, but ONLY works at all if a polar solvent is present. 

		for(int i = 0; i < AlchemyCore.REAGENT_COUNT; i++){
			ReagentStack reag = contents[i];
			if(reag != null){
				if(reag.getAmount() >= AlchemyHelper.MIN_QUANTITY){
					IReagent type = reag.getType();
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
			ReagentStack reag = contents[i];
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
				server.spawnParticle(ModParticles.COLOR_LIQUID, false, (float) pos.getX() + .5F, (float) pos.getY() + .5F, (float) pos.getZ() + .5F, 0, (float) liqCol[0] / (255F * liqAmount), (float) liqCol[1] / (255F * liqAmount), (float) liqCol[2] / (255F * liqAmount), 1F, new int[] {((int) ((float) liqCol[3] / liqAmount))});
			}
			if(gasAmount > 0){
				server.spawnParticle(ModParticles.COLOR_GAS, false, (float) pos.getX() + .5F, (float) pos.getY() + .5F, (float) pos.getZ() + .5F, 0, (float) gasCol[0] / (255F * gasAmount), (float) gasCol[1] / (255F * gasAmount), (float) gasCol[2] / (255F * gasAmount), 1F, new int[] {((int) ((float) gasCol[3] / gasAmount))});
			}


			EnumFacing side = world.getBlockState(pos).getValue(Properties.FACING);
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
				double limit = LIMITS[limitIndex];
				ReagentStack[] transReag = new ReagentStack[AlchemyCore.REAGENT_COUNT];
				for(int i = 0; i < AlchemyCore.REAGENT_COUNT; i++){
					if(contents[i] != null){
						double transLimit = Math.min(contents[i].getAmount(), limit - otherHandler.getContent(i));
						transReag[i] = new ReagentStack(AlchemyCore.REAGENTS[i], transLimit);
						if(contents[i].increaseAmount(-transLimit) <= 0){
							contents[i] = null;
						}
					}
				}

				boolean changed = otherHandler.insertReagents(transReag, side.getOpposite(), handler);
				for(int i = 0; i < AlchemyCore.REAGENT_COUNT; i++){
					if(transReag[i] != null){
						if(contents[i] == null){
							contents[i] = transReag[i];
						}else{
							contents[i].increaseAmount(transReag[i].getAmount());
						}
					}
				}

				if(changed){
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
		limitIndex = Math.min(nbt.getInteger("limit"), LIMITS.length - 1);
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
		nbt.setInteger("limit", limitIndex);
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
		if(cap == Capabilities.CHEMICAL_HANDLER_CAPABILITY && (side == null || side.getAxis() == world.getBlockState(pos).getValue(Properties.FACING).getAxis())){
			return true;
		}
		return super.hasCapability(cap, side);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> cap, EnumFacing side){
		if(cap == Capabilities.CHEMICAL_HANDLER_CAPABILITY && (side == null || side.getAxis() == world.getBlockState(pos).getValue(Properties.FACING).getAxis())){
			return (T) handler;
		}
		return super.getCapability(cap, side);
	}

	private final AlchHandler handler = new AlchHandler();

	private class AlchHandler implements IChemicalHandler{

		@Override
		public EnumTransferMode getMode(EnumFacing side){
			return side == world.getBlockState(pos).getValue(Properties.FACING) ? EnumTransferMode.OUTPUT : EnumTransferMode.INPUT;
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
