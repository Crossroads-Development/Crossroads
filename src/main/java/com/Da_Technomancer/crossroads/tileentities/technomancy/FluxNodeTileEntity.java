package com.Da_Technomancer.crossroads.tileentities.technomancy;

import com.Da_Technomancer.crossroads.API.IInfoTE;
import com.Da_Technomancer.crossroads.API.packets.IIntReceiver;
import com.Da_Technomancer.crossroads.API.packets.ModPackets;
import com.Da_Technomancer.crossroads.API.packets.SendIntToClient;
import com.Da_Technomancer.crossroads.API.rotary.IAxleHandler;
import com.Da_Technomancer.crossroads.API.technomancy.FluxUtil;
import com.Da_Technomancer.crossroads.API.technomancy.IFluxHandler;
import com.Da_Technomancer.crossroads.API.templates.ILinkTE;
import com.Da_Technomancer.crossroads.tileentities.rotary.MasterAxisTileEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import javax.annotation.Nullable;
import java.util.ArrayList;

public class FluxNodeTileEntity extends MasterAxisTileEntity implements ILinkTE, IInfoTE, IFluxHandler, IIntReceiver{

	private boolean disabled = false;
	private int flux = 0;
	protected ArrayList<BlockPos> links = new ArrayList<>(getMaxLinks());
	private int clientFlux;

	private void syncFlux(){
		if(Math.abs(clientFlux - flux) >= 8){
			clientFlux = flux;
			ModPackets.network.sendToAllAround(new SendIntToClient(0, clientFlux, pos), new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
		}
	}

	@Override
	public void update(){
		super.update();

		if(!world.isRemote){
			syncFlux();
			if(flux == 0 || world.getTotalWorldTime() % FluxUtil.FLUX_TIME != 0){
				return;
			}

			IFluxHandler[] targets = new IFluxHandler[getMaxLinks()];
			int targetCount = 0;

			for(int i = 0; i < links.size(); i++){
				BlockPos endPos = pos.add(links.get(i));
				TileEntity te = world.getTileEntity(endPos);
				if(te instanceof IFluxHandler && ((IFluxHandler) te).isFluxReceiver() && !((IFluxHandler) te).canReceiveFlux()){
					targets[i] = (IFluxHandler) te;
					targetCount++;
				}
			}

			int movedFlux = 0;

			for(int i = 0; i < targetCount; i++){
				targets[i].addFlux(flux / targetCount);
				movedFlux += flux / targetCount;
				FluxUtil.renderFlux(world, pos, ((TileEntity) targets[i]).getPos(), flux / targetCount);
			}

			flux -= movedFlux;
			if(movedFlux != 0){
				markDirty();
			}
		}
	}

	@Override
	protected void runCalc(){
		double baseSpeed = flux;

		for(IAxleHandler gear : rotaryMembers){
			if(gear.getMoInertia() > 0){
				baseSpeed = 0;
				break;
			}
		}

		for(IAxleHandler gear : rotaryMembers){

			// set w
			gear.getMotionData()[0] = gear.getRotationRatio() * baseSpeed;
			// set energy
			gear.getMotionData()[1] = 0;
			sumEnergy += (double) 0;
			// set power
			gear.getMotionData()[2] = -gear.getMotionData()[3] * 20;
			// set lastE
			gear.getMotionData()[3] = 0;

			gear.markChanged();
		}

		runAngleCalc();
	}

	public void updateRedstone(boolean redstoneIn){
		disabled = redstoneIn;
		markDirty();
	}

	@Override
	public boolean canReceiveFlux(){
		return !disabled;
	}

	@Override
	public int getCapacity(){
		return 64;
	}

	@Override
	public int getFlux(){
		return flux;
	}

	@Override
	public boolean isFluxEmitter(){
		return true;
	}

	@Override
	public boolean isFluxReceiver(){
		return true;
	}

	@Override
	public int getMaxLinks(){
		return 4;
	}

	@Override
	public int addFlux(int fluxIn){
		flux += fluxIn;
		markDirty();
		if(flux > getCapacity()){
			world.destroyBlock(pos, false);
			world.createExplosion(null, pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F, 0, false);
			FluxUtil.fluxEvent(world, pos, 64);
		}
		return flux;
	}

	@Override
	public void addInfo(ArrayList<String> chat, EntityPlayer player, @Nullable EnumFacing side, float hitX, float hitY, float hitZ){
		for(BlockPos link : links){
			chat.add("Linked Position: X=" + (pos.getX() + link.getX()) + " Y=" + (pos.getY() + link.getY()) + " Z=" + (pos.getZ() + link.getZ()));
		}
		chat.add(flux + "/" + getCapacity() + " Flux");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		nbt.setInteger("flux", flux);
		nbt.setBoolean("disabled", disabled);
		for(int i = 0; i < links.size(); i++){
			nbt.setLong("link" + i, links.get(i).toLong());
		}

		return nbt;
	}

	@Override
	public NBTTagCompound getUpdateTag(){
		NBTTagCompound nbt = super.getUpdateTag();
		nbt.setInteger("flux", flux);
		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		flux = nbt.getInteger("flux");
		clientFlux = flux;
		disabled = nbt.getBoolean("disabled");
		for(int i = 0; i < getMaxLinks(); i++){
			if(nbt.hasKey("link" + i)){
				links.add(BlockPos.fromLong(nbt.getLong("link" + i)));
			}
		}
	}

	@Override
	public TileEntity getTE(){
		return this;
	}

	@Override
	public boolean canLink(ILinkTE otherTE){
		return isFluxEmitter() && otherTE instanceof IFluxHandler && ((IFluxHandler) otherTE).isFluxReceiver();
	}

	@Override
	public ArrayList<BlockPos> getLinks(){
		return links;
	}

	@Override
	public void receiveInt(int identifier, int message, @Nullable EntityPlayerMP sendingPlayer){
		if(identifier == 0){
			clientFlux = message;
		}
	}
}
