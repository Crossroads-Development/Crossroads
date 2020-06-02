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
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nullable;
import java.util.ArrayList;

@ObjectHolder(Crossroads.MODID)
public class LargeGearMasterTileEntity extends TileEntity implements ILongReceiver, ITickableTileEntity, IInfoTE{

	@ObjectHolder("large_gear_master")
	public static TileEntityType<LargeGearMasterTileEntity> teType = null;

	private GearFactory.GearMaterial type;
	private boolean newTE = false;//Used when placing the gear, to signify that the type data needs to be sent to clients. Sending immediately after placement can cause a packet race condition if the packet arrives before the TE exists
	private double[] motionData = new double[4];
	private double inertia = 0;
	private boolean borken = false;//Any PR which changes the spelling on this line will be rejected
	private boolean renderOffset = false;
	/**
	 * 0: angle, 1: clientW
	 */
	private float[] angleW = new float[2];
	private Direction facing = null;

	public LargeGearMasterTileEntity(){
		super(teType);
	}
	
	public Direction getFacing(){
		if(facing == null){
			BlockState state = world.getBlockState(pos);
			if(state.getBlock() != CRBlocks.largeGearMaster){
				return Direction.NORTH;
			}
			facing = state.get(ESProperties.FACING);
		}
		return facing;
	}

	public boolean isRenderedOffset(){
		return renderOffset;
	}

	@Override
	public void addInfo(ArrayList<ITextComponent> chat, PlayerEntity player, BlockRayTraceResult hit){
		RotaryUtil.addRotaryInfo(chat, motionData, inertia, mainOpt.orElseGet(AxleHandler::new).getRotationRatio(), false);
	}

	public void initSetup(GearFactory.GearMaterial typ){
		type = typ;
		if(!world.isRemote){
			newTE = true;
		}

		inertia = type == null ? 0 : MiscUtil.preciseRound(type.getDensity() * 1.125D * 9D / 8D, 2);//1.125 because r*r/2 so 1.5*1.5/2
	}

	public GearFactory.GearMaterial getMember(){
		//The first material is returned instead of null to prevent edge case crashes.
		return type == null ? GearFactory.getDefaultMaterial() : type;
	}

	private static final AxisAlignedBB RENDER_BOX = new AxisAlignedBB(-1, -1, -1, 2, 2, 2);

	@Override
	public AxisAlignedBB getRenderBoundingBox(){
		return RENDER_BOX.offset(pos);
	}

	public void breakGroup(Direction side, boolean drop){
		if(borken){
			return;
		}
		borken = true;
		for(int i = -1; i < 2; ++i){
			for(int j = -1; j < 2; ++j){
				world.setBlockState(pos.offset(side.getAxis() == Axis.X ? Direction.UP : Direction.EAST, i).offset(side.getAxis() == Axis.Z ? Direction.UP : Direction.NORTH, j), Blocks.AIR.getDefaultState());
			}
		}
		if(drop){
			world.addEntity(new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), CRItems.largeGear.withMaterial(type, 1)));
		}
	}

	@Override
	public void tick(){
		if(world.isRemote){
			angleW[0] += angleW[1] * 9D / Math.PI;
		}else if(newTE){
			newTE = false;
			//This is newly placed. Lazy-load send (lazy send? lazy network?) the type data to any clients.
			//This is unnecessary for the client that placed this, but needed in MP for other clients
			CRPackets.sendPacketAround(world, pos, new SendLongToClient((byte) 1, type == null ? -1 : type.serialize(), pos));
		}
	}

	@Override
	public void read(CompoundNBT nbt){
		super.read(nbt);

		// motionData
		for(int j = 0; j < 4; j++){
			motionData[j] = nbt.getDouble("[" + j + "]mot");
		}
		// member
		type = GearFactory.findMaterial(nbt.getString("type"));
		inertia = type == null ? 0 : MiscUtil.preciseRound(type.getDensity() * 1.125D * 9D / 8D, 3);
		//1.125 because r*r/2 so 1.5*1.5/2

		angleW[0] = nbt.getFloat("angle");
		angleW[1] = nbt.getFloat("cl_w");
	}

	@Override
	public CompoundNBT write(CompoundNBT nbt){
		super.write(nbt);

		// motionData
		for(int j = 0; j < 3; j++){
			if(motionData[j] != 0)
				nbt.putDouble("[" + j + "]mot", motionData[j]);
		}

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
	public CompoundNBT getUpdateTag(){
		CompoundNBT nbt = super.getUpdateTag();
		if(type != null){
			nbt.putString("type", type.getId());
		}
		nbt.putBoolean("new", true);
		nbt.putFloat("angle", angleW[0]);
		nbt.putFloat("cl_w", angleW[1]);
		return nbt;
	}

	@Override
	public void receiveLong(byte identifier, long message, @Nullable ServerPlayerEntity sendingPlayer){
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
	public void remove(){
		super.remove();
		mainOpt.invalidate();
	}

	private final LazyOptional<IAxleHandler> mainOpt = LazyOptional.of(AxleHandler::new);

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
		public double[] getMotionData(){
			return motionData;
		}

		@Override
		public void propogate(IAxisHandler masterIn, byte key, double rotRatioIn, double lastRadius, boolean renderOffset){
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
				if(i != side.getIndex() && i != side.getOpposite().getIndex()){
					Direction facing = Direction.byIndex(i);
					// Adjacent gears
					TileEntity adjTE = world.getTileEntity(pos.offset(facing, 2));
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
					TileEntity diagTE = world.getTileEntity(pos.offset(facing, 2).offset(side));
					LazyOptional<ICogHandler> cogOpt;
					if(diagTE != null && (cogOpt = diagTE.getCapability(Capabilities.COG_CAPABILITY, facing.getOpposite())).isPresent() && RotaryUtil.canConnectThrough(world, pos.offset(facing, 2), facing.getOpposite(), side)){
						cogOpt.orElseThrow(NullPointerException::new).connect(masterIn, key, -RotaryUtil.getDirSign(side, facing) * rotRatio, 1.5D, side.getOpposite(), renderOffset);
					}

					//Underside gears
					TileEntity undersideTE = world.getTileEntity(pos.offset(facing, 1).offset(side));
					if(undersideTE != null && (cogOpt = undersideTE.getCapability(Capabilities.COG_CAPABILITY, facing)).isPresent()){
						cogOpt.orElseThrow(NullPointerException::new).connect(masterIn, key, -RotaryUtil.getDirSign(side, facing) * rotRatioIn, 1.5D, side.getOpposite(), renderOffset);
					}
				}
			}

			for(Direction.AxisDirection dir : Direction.AxisDirection.values()){
				Direction axleDir = dir == Direction.AxisDirection.POSITIVE ? getFacing() : getFacing().getOpposite();
				TileEntity connectTE = world.getTileEntity(pos.offset(axleDir));

				if(connectTE != null){
					LazyOptional<IAxisHandler> axisOpt;
					if((axisOpt = connectTE.getCapability(Capabilities.AXIS_CAPABILITY, axleDir.getOpposite())).isPresent()){
						axisOpt.orElseThrow(NullPointerException::new).trigger(masterIn, key);
					}
					LazyOptional<IAxleHandler> axleOpt;
					if((axleOpt = connectTE.getCapability(Capabilities.AXLE_CAPABILITY, axleDir.getOpposite())).isPresent()){
						axleOpt.orElseThrow(NullPointerException::new).propogate(masterIn, key, rotRatio, 0, renderOffset);
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

		@Override
		public void markChanged(){
			markDirty();
		}
	}
}
