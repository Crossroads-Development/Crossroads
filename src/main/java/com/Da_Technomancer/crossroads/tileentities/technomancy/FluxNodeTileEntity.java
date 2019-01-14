package com.Da_Technomancer.crossroads.tileentities.technomancy;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.IInfoTE;
import com.Da_Technomancer.crossroads.API.packets.IIntReceiver;
import com.Da_Technomancer.crossroads.API.packets.ModPackets;
import com.Da_Technomancer.crossroads.API.packets.SendIntToClient;
import com.Da_Technomancer.crossroads.API.rotary.AxisTypes;
import com.Da_Technomancer.crossroads.API.rotary.IAxisHandler;
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
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;

public class FluxNodeTileEntity extends MasterAxisTileEntity implements ILinkTE, IInfoTE, IFluxHandler, IIntReceiver{

	private int flux = 0;
	private int throughput = 0;
	private ArrayList<BlockPos> links = new ArrayList<>(getMaxLinks());
	private int clientThroughput;
	private float angle;

	private void syncFlux(){
		throughput = flux;
		if(clientThroughput == 0 ^ throughput == 0 || Math.abs(clientThroughput - throughput) >= 4){
			clientThroughput = throughput;
			ModPackets.network.sendToAllAround(new SendIntToClient(0, clientThroughput, pos), new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
		}
	}

	public float getRenderAngle(float partialTicks){
		return angle + partialTicks * (float) Math.toDegrees(clientThroughput) / 4F / 20F;
	}

	@Override
	public boolean canBeginLinking(){
		return true;
	}

	@Override
	public void update(){
		if(world.isRemote){
			angle += Math.toDegrees(clientThroughput) / 4F / 20F;
		}else if(world.getTotalWorldTime() % FluxUtil.FLUX_TIME == 0){
			syncFlux();

			if(flux != 0){
				int moved = FluxUtil.transFlux(world, pos, links, flux);
				if(moved != 0){
					flux -= moved;
					markDirty();
				}
			}
		}

		super.update();
	}

	@Override
	protected AxisTypes getType(){
		return AxisTypes.FIXED;
	}

	@Override
	protected void runCalc(){
		double baseSpeed = throughput / 4D;

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
			// set power
			gear.getMotionData()[2] = -gear.getMotionData()[3] * 20;
			// set lastE
			gear.getMotionData()[3] = 0;

			gear.markChanged();
		}

		runAngleCalc();
	}

	@Override
	public int canAccept(){
		return Math.max(0, (int) Math.round(getCapacity() - axleHandler.speed * 4D));
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
	public void receiveLong(byte identifier, long message, @Nullable EntityPlayerMP sendingPlayer){
		if(identifier == LINK_PACKET_ID){
			links.add(BlockPos.fromLong(message));
		}else if(identifier == CLEAR_PACKET_ID){
			links.clear();
		}
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
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		nbt.setInteger("flux", flux);
		nbt.setInteger("throughput", throughput);
		for(int i = 0; i < links.size(); i++){
			nbt.setLong("link" + i, links.get(i).toLong());
		}
		nbt.setDouble("spd", axleHandler.speed);

		return nbt;
	}

	@Override
	public NBTTagCompound getUpdateTag(){
		NBTTagCompound nbt = super.getUpdateTag();
		for(int i = 0; i < links.size(); i++){
			nbt.setLong("link" + i, links.get(i).toLong());
		}
		nbt.setInteger("throughput", throughput);
		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		flux = nbt.getInteger("flux");
		throughput = nbt.getInteger("throughput");
		clientThroughput = throughput;
		for(int i = 0; i < getMaxLinks(); i++){
			if(nbt.hasKey("link" + i)){
				links.add(BlockPos.fromLong(nbt.getLong("link" + i)));
			}
		}
		axleHandler.speed = nbt.getDouble("spd");
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
			clientThroughput = message;
		}
	}

	private final AxleHandler axleHandler = new AxleHandler();

	@Override
	public boolean hasCapability(Capability<?> cap, EnumFacing side){
		if(cap == Capabilities.AXLE_CAPABILITY && side == getFacing().getOpposite()){
			return true;
		}
		return super.hasCapability(cap, side);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T getCapability(Capability<T> cap, EnumFacing side){
		if(cap == Capabilities.AXLE_CAPABILITY && side == getFacing().getOpposite()){
			return (T) axleHandler;
		}
		return super.getCapability(cap, side);
	}

	private class AxleHandler implements IAxleHandler{

		private double speed = 0;
		private double rotRatio;
		public boolean connected;
		public byte updateKey;

		@Override
		public double[] getMotionData(){
			return new double[] {speed, 0, 0, 0};
		}

		@Override
		public void propogate(@Nonnull IAxisHandler masterIn, byte key, double rotRatioIn, double lastRadius, boolean renderOffset){
			//If true, this has already been checked.
			if(key == updateKey || masterIn.addToList(this)){
				return;
			}

			rotRatio = rotRatioIn == 0 ? 1 : rotRatioIn;
			updateKey = key;
			connected = true;
		}

		@Override
		public double getMoInertia(){
			return 0;
		}

		@Override
		public double getRotationRatio(){
			return rotRatio;
		}

		@Override
		public void markChanged(){
			//Not needed for this device
		}

		@Override
		public boolean shouldManageAngle(){
			return false;
		}

		@Override
		public void disconnect(){
			connected = false;
		}
	}
}
