package com.Da_Technomancer.crossroads.tileentities.rotary;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.API.effects.PlaceEffect;
import com.Da_Technomancer.crossroads.API.templates.ModuleTE;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.blocks.rotary.RotaryDrill;
import com.Da_Technomancer.essentials.blocks.ESProperties;
import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.registries.ObjectHolder;

import java.util.List;

@ObjectHolder(Crossroads.MODID)
public class RotaryDrillTileEntity extends ModuleTE{

	@ObjectHolder("rotary_drill")
	public static BlockEntityType<RotaryDrillTileEntity> TYPE = null;

	private static final DamageSource DRILL = new DamageSource("drill");

	public RotaryDrillTileEntity(BlockPos pos, BlockState state){
		super(TYPE, pos, state);
	}

	private int ticksExisted = 0;
	public static final double ENERGY_USE_IRON = 3D;
	public static final double ENERGY_USE_GOLD = 5D;
	private static final double SPEED_PER_HARDNESS = .2D;
	private static final float DAMAGE_PER_SPEED = 5F;
	public static final double[] INERTIA = {50, 100};

	public boolean isGolden(){
		return getBlockState().getBlock() == CRBlocks.rotaryDrillGold;
	}

	@Override
	protected boolean useRotary(){
		return true;
	}

	@Override
	protected double getMoInertia(){
		return INERTIA[isGolden() ? 1 : 0];
	}

	private Direction getFacing(){
		BlockState state = getBlockState();
		if(state.getBlock() instanceof RotaryDrill){
			return state.getValue(ESProperties.FACING);
		}
		setRemoved();
		return Direction.UP;
	}

	@Override
	public void setBlockState(BlockState stateIn){
		super.setBlockState(stateIn);
		axleOpt.invalidate();
		axleOpt = LazyOptional.of(() -> axleHandler);
	}

	@Override
	public void serverTick(){
		super.serverTick();

		double powerDrain = isGolden() ? ENERGY_USE_GOLD : ENERGY_USE_IRON;
		if(Math.abs(energy) >= powerDrain && Math.abs(axleHandler.getSpeed()) >= 0.05D){
			axleHandler.addEnergy(-powerDrain, false);
			if(++ticksExisted % 2 == 0){//Activate once every redstone tick
				Direction facing = getFacing();
				BlockPos targetPos = worldPosition.relative(facing);
				BlockState targetState = level.getBlockState(targetPos);
				if(!targetState.isAir()){
					float hardness = targetState.getDestroySpeed(level, targetPos);
					if(hardness >= 0 && Math.abs(axleHandler.getSpeed()) >= hardness * SPEED_PER_HARDNESS){
						FakePlayer fakePlayer = PlaceEffect.getBlockFakePlayer((ServerLevel) level);
						ItemStack tool = new ItemStack(Items.IRON_PICKAXE);//This shouldn't make a difference as we call the drops method directly, but some blocks may add a tool requirement in the loot table
						level.destroyBlock(targetPos, false);//Don't drop items; we do that separately on the next line
						//Make sure to call through this method, as it is often overriden with extra effects
						//By calling directly, we shortcut any tool requirement that isn't explicitly in the loot table
						targetState.getBlock().playerDestroy(level, fakePlayer, targetPos, targetState, null, tool);

//						boolean isSnow = targetState.getBlock() == Blocks.SNOW;
//						//Snow layers have an unusual loot table that requires it to be broken by an entity holding a shovel
//						//As we want snow layers to be able to drop items with a drill, we special case it
//						world.destroyBlock(targetPos, !isSnow);
//						if(isSnow){
//							Block.spawnAsEntity(world, targetPos, new ItemStack(Items.SNOWBALL, targetState.get(SnowBlock.LAYERS)));
//						}
					}
				}

				List<LivingEntity> ents = level.getEntitiesOfClass(LivingEntity.class, new AABB(worldPosition.relative(facing)), EntitySelector.ENTITY_STILL_ALIVE);
				for(LivingEntity ent : ents){
					ent.hurt(isGolden() ? new EntityDamageSource("drill", FakePlayerFactory.get((ServerLevel) level, new GameProfile(null, "drill_player_" + MiscUtil.getDimensionName(level)))) : DRILL, (float) Math.abs(axleHandler.getSpeed()) * DAMAGE_PER_SPEED);
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side){
		if(cap == Capabilities.AXLE_CAPABILITY && (side == null || side == getFacing().getOpposite())){
			return (LazyOptional<T>) axleOpt;
		}
		return super.getCapability(cap, side);
	}
}
