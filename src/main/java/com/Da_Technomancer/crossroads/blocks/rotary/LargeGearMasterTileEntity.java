package com.Da_Technomancer.crossroads.blocks.rotary;

import com.Da_Technomancer.crossroads.api.CRCapabilities;
import com.Da_Technomancer.crossroads.api.CRMaterialLibrary;
import com.Da_Technomancer.crossroads.api.CRProperties;
import com.Da_Technomancer.crossroads.api.MathUtil;
import com.Da_Technomancer.crossroads.api.packets.CRPackets;
import com.Da_Technomancer.crossroads.api.rotary.*;
import com.Da_Technomancer.crossroads.api.templates.IInfoTE;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.blocks.CRTileEntity;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.Da_Technomancer.essentials.api.ITickableTileEntity;
import com.Da_Technomancer.essentials.api.packets.ILongReceiver;
import com.Da_Technomancer.essentials.api.packets.SendLongToTE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;
import java.util.ArrayList;

public class LargeGearMasterTileEntity extends BlockEntity implements ILongReceiver, ITickableTileEntity, IInfoTE, IAxleCapable{

	public static final BlockEntityType<LargeGearMasterTileEntity> TYPE = CRTileEntity.createType(LargeGearMasterTileEntity::new, CRBlocks.largeGearMaster);

	private CRMaterialLibrary.GearMaterial type;
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


	private final IAxleHandler axleHandler = new AxleHandler();

	public LargeGearMasterTileEntity(BlockPos pos, BlockState state){
		super(TYPE, pos, state);
	}

	public Direction getFacing(){
		if(facing == null){
			BlockState state = getBlockState();
			if(state.getBlock() != CRBlocks.largeGearMaster){
				return Direction.NORTH;
			}
			facing = state.getValue(CRProperties.FACING);
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

	public void initSetup(CRMaterialLibrary.GearMaterial typ){
		type = typ;
		if(!level.isClientSide){
			newTE = true;
		}

		inertia = type == null ? 0 : MathUtil.preciseRound(type.getDensity() * 1.125D * 9D / 8D, 2);//1.125 because r*r/2 so 1.5*1.5/2
	}

	public CRMaterialLibrary.GearMaterial getMember(){
		//The first material is returned instead of null to prevent edge case crashes.
		return type == null ? CRMaterialLibrary.getDefaultMaterial() : type;
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
			CRPackets.sendPacketAround(level, worldPosition, new SendLongToTE((byte) 1, type == null ? -1 : type.serialize(), worldPosition));
		}
	}

	@Override
	public void loadAdditional(CompoundTag nbt, HolderLookup.Provider registries){
		super.loadAdditional(nbt, registries);

		energy = nbt.getDouble("[1]mot");
		// member
		type = CRMaterialLibrary.findMaterial(nbt.getString("type"));
		inertia = type == null ? 0 : MathUtil.preciseRound(type.getDensity() * 1.125D * 9D / 8D, 3);
		//1.125 because r*r/2 so 1.5*1.5/2

		angleW[0] = nbt.getFloat("angle");
		angleW[1] = nbt.getFloat("cl_w");
	}

	@Override
	protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider pRegistries){
		super.saveAdditional(nbt, pRegistries);

		// motionData
		nbt.putDouble("[1]mot", energy);

		// member
		if(type != null){
			nbt.putString("type", type.getId());
		}

		nbt.putBoolean("new", true);
		nbt.putFloat("angle", angleW[0]);
		nbt.putFloat("cl_w", angleW[1]);
	}

	@Override
	public CompoundTag getUpdateTag(HolderLookup.Provider pRegistries){
		CompoundTag nbt = super.getUpdateTag(pRegistries);
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
			type = CRMaterialLibrary.GearMaterial.deserialize((int) message);
		}else if(identifier == 2){
			renderOffset = message == 1;
		}
	}

	@Override
	@Nullable
	public IAxleHandler getAxleHandler(Direction dir){
		if(dir == null || dir.getAxis() == getFacing().getAxis()){
			return axleHandler;
		}
		return null;
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
					BlockPos adjPos = worldPosition.relative(facing, 2);
					ICogHandler cogHandler;
					if((cogHandler = level.getCapability(CRCapabilities.COG_CAPABILITY, adjPos, side)) != null){
						cogHandler.connect(masterIn, key, -rotRatio, 1.5D, facing.getOpposite(), renderOffset);
					}else if((cogHandler = level.getCapability(CRCapabilities.COG_CAPABILITY, adjPos, side.getOpposite())) != null){
						//Check for large gears
						cogHandler.connect(masterIn, key, RotaryUtil.getDirSign(side, facing) * rotRatio, 1.5D, side, renderOffset);
					}

					// Diagonal gears
					BlockPos diagPos = worldPosition.relative(facing, 2).relative(side);
					if((cogHandler = level.getCapability(CRCapabilities.COG_CAPABILITY, diagPos, facing.getOpposite())) != null && RotaryUtil.canConnectThrough(level, worldPosition.relative(facing, 2), facing.getOpposite(), side)){
						cogHandler.connect(masterIn, key, -RotaryUtil.getDirSign(side, facing) * rotRatio, 1.5D, side.getOpposite(), renderOffset);
					}

					//Underside gears
					BlockPos undersidePos = worldPosition.relative(facing, 1).relative(side);
					if((cogHandler = level.getCapability(CRCapabilities.COG_CAPABILITY, undersidePos, facing)) != null){
						cogHandler.connect(masterIn, key, -RotaryUtil.getDirSign(side, facing) * rotRatioIn, 1.5D, side.getOpposite(), renderOffset);
					}
				}
			}

			for(Direction.AxisDirection dir : Direction.AxisDirection.values()){
				Direction axleDir = dir == Direction.AxisDirection.POSITIVE ? getFacing() : getFacing().getOpposite();
				BlockPos connectPos = worldPosition.relative(axleDir);

				IAxisHandler axisHandler;
				if((axisHandler = level.getCapability(CRCapabilities.AXIS_CAPABILITY, connectPos, axleDir.getOpposite())) != null){
					axisHandler.trigger(masterIn, key);
					}
				IAxleHandler axleHandler;
				if((axleHandler = level.getCapability(CRCapabilities.AXLE_CAPABILITY, connectPos, axleDir.getOpposite())) != null){
					axleHandler.propagate(masterIn, key, rotRatio, 0, renderOffset);
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
