package com.Da_Technomancer.crossroads.tileentities.alchemy;

import java.awt.Color;
import java.util.ArrayList;

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
import com.Da_Technomancer.crossroads.API.packets.IIntReceiver;
import com.Da_Technomancer.crossroads.API.packets.ModPackets;
import com.Da_Technomancer.crossroads.API.packets.SendIntToClient;
import com.Da_Technomancer.crossroads.API.technomancy.EnumGoggleLenses;
import com.Da_Technomancer.crossroads.items.ModItems;
import com.Da_Technomancer.crossroads.particles.ModParticles;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;

public class AlchemicalTubeTileEntity extends TileEntity implements ITickable, IIntReceiver, IInfoTE{

	private final Integer[] connectMode = {0, 0, 0, 0, 0, 0};
	private boolean glass;
	private final Reagent[] contents = new Reagent[AlchemyCore.REAGENT_COUNT];
	private double heat = 0;
	private double amount = 0;
	private boolean dirtyReag = false;

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
			for(Reagent reag : contents){
				if(reag != null){
					chat.add(reag.toString());
				}
			}
		}
	}

	public AlchemicalTubeTileEntity(){
		super();
	}

	public AlchemicalTubeTileEntity(boolean glass){
		super();
		this.glass = glass;
	}

	public Integer[] getConnectMode(boolean forRender){
		return forRender ? new Integer[] {Math.max(0, connectMode[0]), Math.max(0, connectMode[1]), Math.max(0, connectMode[2]), Math.max(0, connectMode[3]), Math.max(0, connectMode[4]), Math.max(0, connectMode[5])} : connectMode;
	}

	public void markSideChanged(int index){
		markDirty();
		ModPackets.network.sendToAllAround(new SendIntToClient(index, connectMode[index], pos), new TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
	}

	@Override
	public void receiveInt(int identifier, int message, @Nullable EntityPlayerMP sender){
		if(identifier < 6){
			connectMode[identifier] = message;
			world.markBlockRangeForRenderUpdate(pos, pos);
		}
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
			for(int i = 0; i < 6; i++){
				EnumFacing side = EnumFacing.getFront(i);
				TileEntity te = null;
				if(connectMode[i] != -1){
					te = world.getTileEntity(pos.offset(side));
					if(te == null){
						if(connectMode[i] != 0){
							connectMode[i] = 0;
							markSideChanged(i);
						}
						continue;
					}
					if(!te.hasCapability(Capabilities.CHEMICAL_HANDLER_CAPABILITY, side.getOpposite())){
						if(connectMode[i] != 0){
							connectMode[i] = 0;
							markSideChanged(i);
						}
						continue;
					}

					IChemicalHandler otherHandler = te.getCapability(Capabilities.CHEMICAL_HANDLER_CAPABILITY, side.getOpposite());
					EnumContainerType cont = otherHandler.getChannel(side.getOpposite());
					if(cont != EnumContainerType.NONE && (cont == EnumContainerType.GLASS ? !glass : glass)){
						if(connectMode[i] != 0){
							connectMode[i] = 0;
							markSideChanged(i);
						}
						continue;
					}

					if(connectMode[i] == 0){
						connectMode[i] = 1;
						markSideChanged(i);
						continue;
					}else if(amount != 0 && connectMode[i] == 1){
						if(otherHandler.insertReagents(contents, side.getOpposite(), handler)){
							correctReag();
							markDirty();
						}
					}
				}
			}

			double temp = handler.getTemp();
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
		for(int i = 0; i < 6; i++){
			connectMode[i] = nbt.getInteger("mode_" + i);
		}
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
		for(int i = 0; i < 6; i++){
			nbt.setInteger("mode_" + i, connectMode[i]);
		}
		for(int i = 0; i < AlchemyCore.REAGENT_COUNT; i++){
			if(contents[i] != null){
				nbt.setDouble(i + "_am", contents[i].getAmount());
			}
		}
		nbt.setDouble("heat", heat);
		return nbt;
	}

	@Override
	public NBTTagCompound getUpdateTag(){
		NBTTagCompound out = super.getUpdateTag();
		for(int i = 0; i < 6; i++){
			out.setInteger("mode_" + i, connectMode[i]);
		}
		return out;
	}

	@Override
	public boolean hasCapability(Capability<?> cap, EnumFacing side){
		if(cap == Capabilities.CHEMICAL_HANDLER_CAPABILITY && (side == null || connectMode[side.getIndex()] != -1)){
			return true;
		}
		return super.hasCapability(cap, side);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> cap, EnumFacing side){
		if(cap == Capabilities.CHEMICAL_HANDLER_CAPABILITY && (side == null || connectMode[side.getIndex()] != -1)){
			return (T) handler;
		}
		return super.getCapability(cap, side);
	}

	private final AlchHandler handler = new AlchHandler();

	private class AlchHandler implements IChemicalHandler{

		@Override
		public EnumTransferMode getMode(EnumFacing side){
			return connectMode[side.getIndex()] <= 0 ? EnumTransferMode.NONE : connectMode[side.getIndex()] == 1 ? EnumTransferMode.OUTPUT : EnumTransferMode.INPUT;
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
