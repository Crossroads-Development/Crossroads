package com.Da_Technomancer.crossroads.tileentities.rotary;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.templates.ModuleTE;
import com.Da_Technomancer.crossroads.blocks.rotary.RotaryDrill;
import com.Da_Technomancer.essentials.blocks.EssentialsProperties;
import com.mojang.authlib.GameProfile;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.FakePlayerFactory;

import java.util.List;

public class RotaryDrillTileEntity extends ModuleTE{

	private static final DamageSource DRILL = new DamageSource("drill");

	public RotaryDrillTileEntity(){
		super();
	}

	public RotaryDrillTileEntity(boolean golden){
		super();
		this.golden = golden;
	}

	private int ticksExisted = 0;
	private boolean golden;
	public static final double ENERGY_USE = 2D;
	private static final double SPEED_PER_HARDNESS = .2D;

	public boolean isGolden(){
		return golden;
	}

	@Override
	protected boolean useRotary(){
		return true;
	}

	@Override
	protected AxleHandler createAxleHandler(){
		return new AngleAxleHandler();
	}

	@Override
	protected double getMoInertia(){
		return golden ? 100 : 50;
	}

	@Override
	public void update(){
		super.update();

		if(world.isRemote){
			return;
		}

		if(!(world.getBlockState(pos).getBlock() instanceof RotaryDrill)){
			invalidate();
			return;
		}

		if(Math.abs(motData[1]) >= ENERGY_USE){
			axleHandler.addEnergy(-ENERGY_USE, false, false);
			if(++ticksExisted % 10 == 0){
				EnumFacing facing = world.getBlockState(pos).getValue(EssentialsProperties.FACING);
				if(world.getBlockState(pos.offset(facing)).getBlock().canCollideCheck(world.getBlockState(pos.offset(facing)), false)){
					float hardness = world.getBlockState(pos.offset(facing)).getBlockHardness(world, pos.offset(facing));
					if(hardness >= 0 && Math.abs(motData[0]) >= hardness * SPEED_PER_HARDNESS){
						world.destroyBlock(pos.offset(facing), true);
					}
				}else{
					List<EntityLivingBase> ents = world.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(pos.offset(facing)), EntitySelectors.IS_ALIVE);
					for(EntityLivingBase ent : ents){
						ent.attackEntityFrom(golden ? new EntityDamageSource("drill", FakePlayerFactory.get((WorldServer) world, new GameProfile(null, "drill_player_" + world.provider.getDimension()))) : DRILL, (float) Math.abs(motData[0] / SPEED_PER_HARDNESS));
					}
				}
			}
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		nbt.setBoolean("gold", golden);
		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		golden = nbt.getBoolean("gold");
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> cap, EnumFacing side){
		if(cap == Capabilities.AXLE_HANDLER_CAPABILITY && (side == null || side == world.getBlockState(pos).getValue(EssentialsProperties.FACING).getOpposite())){
			return (T) axleHandler;
		}
		return super.getCapability(cap, side);
	}
}
