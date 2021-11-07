package com.Da_Technomancer.crossroads.tileentities.rotary;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.IInfoTE;
import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.API.packets.CRPackets;
import com.Da_Technomancer.crossroads.API.rotary.IAxisHandler;
import com.Da_Technomancer.crossroads.API.rotary.IAxleHandler;
import com.Da_Technomancer.crossroads.API.rotary.ICogHandler;
import com.Da_Technomancer.crossroads.API.rotary.RotaryUtil;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.Da_Technomancer.crossroads.items.itemSets.GearFactory;
import com.Da_Technomancer.essentials.blocks.ESProperties;
import com.Da_Technomancer.essentials.packets.ILongReceiver;
import com.Da_Technomancer.essentials.packets.SendLongToClient;
import com.Da_Technomancer.essentials.tileentities.ITickableTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nullable;
import java.util.ArrayList;

@ObjectHolder(Crossroads.MODID)
public class LargeGearMasterTileEntity extends BlockEntity implements ILongReceiver, ITickableTileEntity, IInfoTE{

	@ObjectHolder("large_gear_master")
	public static BlockEntityType<LargeGearMasterTileEntity> TYPE = null;

	private GearFactory.GearMaterial type;
	private boolean newTE = false;//Used when placing the gear, to signify that the type data needs to be sent to clients. Sending immediately after placement can cause a packet race condition if the packet arrives before the TE exists
	private double energy = 0;
	private double inertia = 0;
	private boolean borken = false;//Any PR which changes the spelling on this line will be rejected
	private boolean renderOffset = false;
	/**
	 * 0: angle, 1: clientW
	 */
	private final float[] angleW = new float[2];
	private Direction facing = null;

	public LargeGearMasterTileEntity(BlockPos pos, BlockState state){
		super(TYPE, pos, state);
	}
	
	public Direction getFacing(){
		if(facing == null){
			BlockState state = getBlockState();
			if(state.getBlock() != CRBlocks.largeGearMaster){
				return Direction.NORTH;
			}
			facing = state.getValue(ESProperties.FACING);
		}
		return facing;
	}

	public boolean isRenderedOffset(){
		return renderOffset;
	}

	@Override
	public void addInfo(ArrayList<Component> chat, Player player, BlockHitResult hit){
		RotaryUtil.addRotaryInfo(chat, axleHandler, false);
	}

	public void initSetup(GearFactory.GearMaterial typ){
		type = typ;
		if(!level.isClientSide){
			newTE = true;
		}

		inertia = type == null ? 0 : MiscUtil.preciseRound(type.getDensity() * 1.125D * 9D / 8D, 2);//1.125 because r*r/2 so 1.5*1.5/2
	}

	public GearFactory.GearMaterial getMember(){
		//The first material is returned instead of null to prevent edge case crashes.
		return type == null ? GearFactory.getDefaultMaterial() : type;
	}

	private static final AABB RENDER_BOX = new AABB(-1.5, -1.5, -1.5, 2.5, 2.5, 2.5);

	@Override
	public AABB getRenderBoundingBox(){
		return RENDER_BOX.move(worldPosition);
	}

	public void breakGroup(Direction side, boolean drop){
		if(borken){
			return;
		}
		borken = true;
		for(int i = -1; i < 2; ++i){
			for(int j = -1; j < 2; ++j){
				level.setBlockAndUpdate(worldPosition.relative(side.getAxis() == Axis.X ? Direction.UP : Direction.EAST, i).relative(side.getAxis() == Axis.Z ? Direction.UP : Direction.NORTH, j), Blocks.AIR.defaultBlockState());
			}
		}
		if(drop){
			level.addFreshEntity(new ItemEntity(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), CRItems.largeGear.withMaterial(type, 1)));
		}
	}

	@Override
	public void clientTick(){
		ITickableTileEntity.super.clientTick();
		angleW[0] += angleW[1] * 9D / Math.PI;
	}

	@Override
	public void serverTick(){
		ITickableTileEntity.super.serverTick();
		if(newTE){
			newTE = false;
			//This is newly placed. Lazy-load send (lazy send? lazy network?) the type data to any clients.
			//This is unnecessary for the client that placed this, but needed in MP for other clients
			CRPackets.sendPacketAround(level, worldPosition, new SendLongToClient((byte) 1, type == null ? -1 : type.serialize(), worldPosition));
		}
	}

	@Override
	public void load(CompoundTag nbt){
		super.load(nbt);

		energy = nbt.getDouble("[1]mot");
		// member
		type = GearFactory.findMaterial(nbt.getString("type"));
		inertia = type == null ? 0 : MiscUtil.preciseRound(type.getDensity() * 1.125D * 9D / 8D, 3);
		//1.125 because r*r/2 so 1.5*1.5/2

		angleW[0] = nbt.getFloat("angle");
		angleW[1] = nbt.getFloat("cl_w");
	}

	@Override
	public CompoundTag save(CompoundTag nbt){
		super.save(nbt);

		// motionData
		nbt.putDouble("[1]mot", energy);

		// member
		if(type != null){
			nbt.putString("type", type.getId());
		}

		nbt.putBoolean("new", true);
		nbt.putFloat("angle", angleW[0]);
		nbt.putFloat("cl_w", angleW[1]);
		return nbt;
	}

	@Override
	public CompoundTag getUpdateTag(){
		CompoundTag nbt = super.getUpdateTag();
		if(type != null){
			nbt.putString("type", type.getId());
		}
		nbt.putBoolean("new", true);
		nbt.putFloat("angle", angleW[0]);
		nbt.putFloat("cl_w", angleW[1]);
		return nbt;
	}

	@Override
	public void receiveLong(byte identifier, long message, @Nullable ServerPlayer sendingPlayer){
		if(identifier == 0){
			float angle = Float.intBitsToFloat((int) (message & 0xFFFFFFFFL));
			angleW[0] = Math.abs(angle - angleW[0]) > 5F ? angle : angleW[0];
			angleW[1] = Float.intBitsToFloat((int) (message >>> 32L));
		}else if(identifier == 1){
			type = GearFactory.GearMaterial.deserialize((int) message);
		}else if(identifier == 2){
			renderOffset = message == 1;
		}
	}

	@Override
	public void setRemoved(){
		super.setRemoved();
		mainOpt.invalidate();
	}

	private final IAxleHandler axleHandler = new AxleHandler();
	private final LazyOptional<IAxleHandler> mainOpt = LazyOptional.of(() -> axleHandler);

	@SuppressWarnings("unchecked")
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing){
		if(capability == Capabilities.AXLE_CAPABILITY && (facing == null || facing.getAxis() == getFacing().getAxis())){
			return (LazyOptional<T>) mainOpt;
		}
		return super.getCapability(capability, facing);
	}

	private class AxleHandler implements IAxleHandler{

		private byte updateKey;
		private double rotRatio;
		private IAxisHandler axis;

		@Override
		public double getEnergy(){
			return energy;
		}

		@Override
		public void setEnergy(double newEnergy){
			energy = newEnergy;
			setChanged();
		}

		@Override
		public double getSpeed(){
			return axis == null ? 0 : axis.getBaseSpeed() * rotRatio;
		}

		@Override
		public void propagate(IAxisHandler masterIn, byte key, double rotRatioIn, double lastRadius, boolean renderOffset){
			if(type == null){
				return;
			}

			if(lastRadius != 0){
				rotRatioIn *= lastRadius / 1.5D;
			}

			//If true, this has already been checked.
			if(key == updateKey){
				//If true, there is rotation conflict.
				if(rotRatio != rotRatioIn){
					masterIn.lock();
				}
				return;
			}

			if(masterIn.addToList(this)){
				return;
			}

			axis = masterIn;

			rotRatio = rotRatioIn;
			LargeGearMasterTileEntity.this.renderOffset = renderOffset;

			updateKey = key;

			Direction side = getFacing();
			
			for(int i = 0; i < 6; i++){
				if(i != side.get3DDataValue() && i != side.getOpposite().get3DDataValue()){
					Direction facing = Direction.from3DDataValue(i);
					// Adjacent gears
					BlockEntity adjTE = level.getBlockEntity(worldPosition.relative(facing, 2));
					if(adjTE != null){
						LazyOptional<ICogHandler> cogOpt;
						if((cogOpt = adjTE.getCapability(Capabilities.COG_CAPABILITY, side)).isPresent()){
							cogOpt.orElseThrow(NullPointerException::new).connect(masterIn, key, -rotRatio, 1.5D, facing.getOpposite(), renderOffset);
						}else if((cogOpt = adjTE.getCapability(Capabilities.COG_CAPABILITY, facing.getOpposite())).isPresent()){
							//Check for large gears
							cogOpt.orElseThrow(NullPointerException::new).connect(masterIn, key, RotaryUtil.getDirSign(side, facing) * rotRatio, 1.5D, side, renderOffset);
						}
					}

					// Diagonal gears
					BlockEntity diagTE = level.getBlockEntity(worldPosition.relative(facing, 2).relative(side));
					LazyOptional<ICogHandler> cogOpt;
					if(diagTE != null && (cogOpt = diagTE.getCapability(Capabilities.COG_CAPABILITY, facing.getOpposite())).isPresent() && RotaryUtil.canConnectThrough(level, worldPosition.relative(facing, 2), facing.getOpposite(), side)){
						cogOpt.orElseThrow(NullPointerException::new).connect(masterIn, key, -RotaryUtil.getDirSign(side, facing) * rotRatio, 1.5D, side.getOpposite(), renderOffset);
					}

					//Underside gears
					BlockEntity undersideTE = level.getBlockEntity(worldPosition.relative(facing, 1).relative(side));
					if(undersideTE != null && (cogOpt = undersideTE.getCapability(Capabilities.COG_CAPABILITY, facing)).isPresent()){
						cogOpt.orElseThrow(NullPointerException::new).connect(masterIn, key, -RotaryUtil.getDirSign(side, facing) * rotRatioIn, 1.5D, side.getOpposite(), renderOffset);
					}
				}
			}

			for(Direction.AxisDirection dir : Direction.AxisDirection.values()){
				Direction axleDir = dir == Direction.AxisDirection.POSITIVE ? getFacing() : getFacing().getOpposite();
				BlockEntity connectTE = level.getBlockEntity(worldPosition.relative(axleDir));

				if(connectTE != null){
					LazyOptional<IAxisHandler> axisOpt;
					if((axisOpt = connectTE.getCapability(Capabilities.AXIS_CAPABILITY, axleDir.getOpposite())).isPresent()){
						axisOpt.orElseThrow(NullPointerException::new).trigger(masterIn, key);
					}
					LazyOptional<IAxleHandler> axleOpt;
					if((axleOpt = connectTE.getCapability(Capabilities.AXLE_CAPABILITY, axleDir.getOpposite())).isPresent()){
						axleOpt.orElseThrow(NullPointerException::new).propagate(masterIn, key, rotRatio, 0, renderOffset);
					}
				}
			}
		}

		@Override
		public void disconnect(){
			axis = null;
		}

		@Override
		public double getMoInertia(){
			return inertia;
		}

		@Override
		public float getAngle(float partialTicks){
			return axis == null ? 0 : axis.getAngle(rotRatio, partialTicks, renderOffset, 7.5F);
		}

		@Override
		public double getRotationRatio(){
			return rotRatio;
		}
	}
}
