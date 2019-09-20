package com.Da_Technomancer.crossroads.tileentities.technomancy;

import com.Da_Technomancer.crossroads.API.beams.BeamManager;
import com.Da_Technomancer.crossroads.API.beams.IBeamHandler;
import com.Da_Technomancer.crossroads.API.beams.BeamUnit;
import com.Da_Technomancer.crossroads.API.packets.IIntReceiver;
import com.Da_Technomancer.crossroads.API.packets.IStringReceiver;
import com.Da_Technomancer.crossroads.API.technomancy.IPrototypePort;
import com.Da_Technomancer.crossroads.API.technomancy.PrototypeInfo;
import com.Da_Technomancer.crossroads.API.technomancy.PrototypePortTypes;
import com.Da_Technomancer.crossroads.dimensions.PrototypeWorldSavedData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class PrototypePortTileEntity extends TileEntity implements IIntReceiver, IStringReceiver, IPrototypePort{

	private Direction side = Direction.DOWN;
	private PrototypePortTypes type = PrototypePortTypes.HEAT;
	private boolean active;
	public String desc = "";
	private int index = -1;

	@Override
	public CompoundNBT write(CompoundNBT nbt){
		super.write(nbt);
		nbt.putString("side", side.name());
		nbt.putString("type", type.name());
		nbt.putBoolean("act", active);
		nbt.putInt("index", index);
		nbt.putString("desc", desc);
		return nbt;
	}

	@Override
	public void read(CompoundNBT nbt){
		super.read(nbt);
		side = nbt.contains("side") ? Direction.valueOf(nbt.getString("side")) : Direction.DOWN;
		type = nbt.contains("type") ? PrototypePortTypes.valueOf(nbt.getString("type")) : PrototypePortTypes.HEAT;
		active = nbt.getBoolean("act");
		index = nbt.getInt("index");
		desc = nbt.getString("desc");
	}

	@Override
	public CompoundNBT getUpdateTag(){
		CompoundNBT nbt = super.getUpdateTag();
		nbt.putString("side", side.name());
		nbt.putString("type", type.name());
		nbt.putString("desc", desc);
		return nbt;
	}

	public boolean isUsableByPlayer(PlayerEntity player){
		return world.getTileEntity(pos) == this && player.getDistanceSq(pos.add(0.5, 0.5, 0.5)) <= 64;
	}

	@Override
	public PrototypePortTypes getType(){
		return type;
	}

	public void setType(PrototypePortTypes type){
		this.type = type;
	}

	@Override
	public Direction getSide(){
		return side;
	}

	public void setSide(Direction side){
		this.side = side;
	}

	@Override
	public String getDesc(){
		return desc;
	}

	@Override
	public void makeActive(){
		active = true;
	}

	@Override
	public boolean isActive(){
		return active;
	}

	@Override
	public void receiveInt(byte identifier, int message, ServerPlayerEntity player){
		if(identifier == 0){
			side = Direction.byIndex(message & 7);
			type = PrototypePortTypes.values()[message >> 3];
			world.markBlockRangeForRenderUpdate(pos, pos);
			markDirty();
		}
	}

	@Override
	public void receiveString(String context, String message, @Nullable ServerPlayerEntity sender){
		if(context.equals("desc") && !message.equals(desc)){
			desc = message;
			markDirty();
		}
	}

	@Override
	public boolean hasCapability(Capability<?> cap, Direction side){
		if(!world.isRemote && index != -1 && active && type.getCapability() == cap && type.exposeInternal() && side == this.side){
			PrototypeInfo info = PrototypeWorldSavedData.get(false).prototypes.get(index);
			if(info != null && info.owner != null && info.owner.get() != null && info.owner.get().hasCap(cap, this.side)){
				return true;
			}
		}
		return super.hasCapability(cap, side);
	}

	@Override
	public <T> T getCapability(Capability<T> cap, Direction side){
		if(!world.isRemote && index != -1 && active && type.getCapability() == cap && type.exposeInternal() && side == this.side){
			PrototypeInfo info = PrototypeWorldSavedData.get(false).prototypes.get(index);
			if(info != null && info.owner != null && info.owner.get() != null && info.owner.get().hasCap(cap, this.side)){
				return info.owner.get().getCap(cap, this.side);
			}
		}
		return super.getCapability(cap, side);
	}

	@Override
	public boolean hasCapPrototype(Capability<?> cap){
		if(active && type.getCapability() == cap){
			if(type == PrototypePortTypes.MAGIC_IN){
				return true;
			}
			TileEntity te = world.getTileEntity(pos.offset(side));
			return te != null && te.hasCapability(cap, side.getOpposite());
		}
		return false;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T getCapPrototype(Capability<T> cap){
		if(active && type.getCapability() == cap){
			if(type == PrototypePortTypes.MAGIC_IN){
				if(magHandler == null){
					magHandler = new MagHandler();
				}
				return (T) magHandler;
			}
			return world.getTileEntity(pos.offset(side)).getCapability(cap, side.getOpposite());
		}
		return null;
	}

	@Override
	public int getIndex(){
		return index;
	}

	@Override
	public void setIndex(int index){
		this.index = index;
	}

	private MagHandler magHandler = null;

	private class MagHandler implements IBeamHandler{

		private final BeamManager beam = new BeamManager(side, pos);

		@Override
		public void setMagic(@Nullable BeamUnit mag){
			beam.emit(mag, world);
		}
	}
}
