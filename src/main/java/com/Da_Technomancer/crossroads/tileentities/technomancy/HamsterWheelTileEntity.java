package com.Da_Technomancer.crossroads.tileentities.technomancy;

import com.Da_Technomancer.crossroads.API.CRProperties;
import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.rotary.IAxleHandler;
import com.Da_Technomancer.crossroads.API.rotary.RotaryUtil;
import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.Crossroads;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(Crossroads.MODID)
public class HamsterWheelTileEntity extends TileEntity implements ITickableTileEntity{

	@ObjectHolder("hamster_wheel")
	public static TileEntityType<HamsterWheelTileEntity> type = null;

	public float angle = 0;
	public float nextAngle = 0;

	public HamsterWheelTileEntity(){
		super(type);
	}

	@Override
	public void tick(){
		Direction facing = getBlockState().get(CRProperties.HORIZ_FACING);
		TileEntity te = world.getTileEntity(pos.offset(facing));
		LazyOptional<IAxleHandler> axleOpt;
		if(te != null && (axleOpt = te.getCapability(Capabilities.AXLE_CAPABILITY, facing.getOpposite())).isPresent()){
			IAxleHandler axle = axleOpt.orElseThrow(NullPointerException::new);
			if(world.isRemote){
				angle = axle.getAngle(0);
				nextAngle = axle.getAngle(1F);
				return;
			}
			axle.addEnergy(CRConfig.lodestoneTurbinePower.get() * RotaryUtil.getCCWSign(facing), true);
		}else if(world.isRemote){
			nextAngle = angle;
		}
	}
}
