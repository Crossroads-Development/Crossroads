package com.Da_Technomancer.crossroads.tileentities.rotary;

import com.Da_Technomancer.crossroads.API.CRProperties;
import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.packets.CRPackets;
import com.Da_Technomancer.crossroads.API.templates.ModuleTE;
import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.essentials.packets.SendLongToClient;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@ObjectHolder(Crossroads.MODID)
public class WindTurbineTileEntity extends ModuleTE{

	@ObjectHolder("wind_turbine")
	public static BlockEntityType<WindTurbineTileEntity> type = null;

	public static final double MAX_SPEED = 2D;
	public static final double INERTIA = 200;
	public static final double LOW_POWER = 5D;
	public static final double HIGH_POWER = 25D;
	private static final AABB RENDER_BOX = new AABB(-1, -1, -1, 2, 2, 2);

	//Undocumented 'easter egg'. This person takes way more damage from windmills
	//Don't ask.
	private static final String murderEasterEgg = "dinidini";

	private boolean newlyPlaced = true;
	private boolean running = false;
	private AABB targetBB = null;
	public int[] bladeColors = new int[4];
	private int lastColoredBlade = 3;//Index of the last blade dyed

	public WindTurbineTileEntity(BlockPos pos, BlockState state){
		super(type, pos, state);
	}

	public WindTurbineTileEntity(boolean newlyPlaced){
		this();
		this.newlyPlaced = newlyPlaced;
	}

	protected Direction getFacing(){
		BlockState state = getBlockState();
		if(state.getBlock() != CRBlocks.windTurbine){
			setRemoved();
			return Direction.NORTH;
		}
		return state.getValue(CRProperties.HORIZ_FACING);
	}

	private AABB getTargetBB(){
		if(targetBB == null){
			Direction dir = getFacing();
			Direction planeDir = dir.getClockWise();
			if(planeDir.getAxisDirection() == Direction.AxisDirection.NEGATIVE){
				planeDir = planeDir.getOpposite();
			}
			BlockPos center = worldPosition.relative(dir);
			if(dir.getAxisDirection() == Direction.AxisDirection.POSITIVE){
				targetBB = new AABB(center.relative(planeDir, -2).relative(Direction.DOWN, 2), center.relative(planeDir, 3).relative(Direction.UP, 3).relative(dir));
			}else{
				targetBB = new AABB(center.relative(planeDir, -2).relative(Direction.DOWN, 2), center.relative(planeDir, 3).relative(Direction.UP, 3).relative(dir, -1));
			}
		}

		return targetBB;
	}

	public void dyeBlade(ItemStack dye){
		DyeColor newColor = DyeColor.getColor(dye);
		if(newColor != null){
			lastColoredBlade++;
			lastColoredBlade %= bladeColors.length;
			if(newColor.getId() != bladeColors[lastColoredBlade]){
				bladeColors[lastColoredBlade] = newColor.getId();

				//Send the blade colors to clients
				long message = 0;
				for(int i = 0; i < bladeColors.length; i++){
					message |= bladeColors[i] << i * 4;
				}
				CRPackets.sendPacketAround(level, worldPosition, new SendLongToClient(5, message, worldPosition));

				setChanged();
			}
		}
	}

	@Override
	public void clearCache(){
		super.clearCache();
		axleOpt.invalidate();
		axleOpt = LazyOptional.of(() -> axleHandler);
		newlyPlaced = true;
		targetBB = null;
	}

	@Override
	protected boolean useRotary(){
		return true;
	}

	@Override
	public void addInfo(ArrayList<Component> chat, Player player, BlockHitResult hit){
		chat.add(new TranslatableComponent("tt.crossroads.wind_turbine.weather", CRConfig.formatVal(getPowerOutput())));
		super.addInfo(chat, player, hit);
	}

	private static final long PERIOD = 18000;//Period of this cycle, in ticks; 15 minutes

	private double getPowerOutput(){
		long worldTime = level.getGameTime();
		double triangleWave = ((double) worldTime / PERIOD) % 1D;//triangle wave with range [0, 1), period PERIOD
		if(triangleWave > 0.5){
			return 2D * (triangleWave - 0.5D) * (HIGH_POWER - LOW_POWER) + LOW_POWER;//LOW_POWER to HIGH_POWER, avg (HIGH_POWER - LOW_POWER) / 2
		}else{
			return -2D * triangleWave * (HIGH_POWER - LOW_POWER) - LOW_POWER;//-LOW_POWER to -HIGH_POWER, avg -(HIGH_POWER - LOW_POWER) / 2
		}
	}

	public float getRedstoneOutput(){
		return (float) getPowerOutput();
	}

	@Override
	public void tick(){
		super.tick();

		if(!level.isClientSide){
			//Every 30 seconds check whether the placement requirements are valid, and cache the result
			if(newlyPlaced || level.getGameTime() % 600 == 0){
				newlyPlaced = false;
				running = false;
				Direction facing = getFacing();
				BlockPos offsetPos = worldPosition.relative(facing);
				if(level.canSeeSkyFromBelowWater(offsetPos)){
					running = true;
					outer:
					for(int i = -2; i <= 2; i++){
						for(int j = -2; j <= 2; j++){
							BlockPos checkPos = offsetPos.offset(facing.getStepZ() * i, j, facing.getStepX() * i);
							BlockState checkState = level.getBlockState(checkPos);
							if(!checkState.getBlock().isAir(checkState, level, checkPos)){
								running = false;
								break outer;
							}
						}
					}
				}

				setChanged();
			}

			//Damage entities in the blades while spinning at high speed
			if(Math.abs(axleHandler.getSpeed()) >= 1.5D){
				List<LivingEntity> ents = level.getEntitiesOfClass(LivingEntity.class, getTargetBB(), EntitySelector.LIVING_ENTITY_STILL_ALIVE);
				for(LivingEntity ent : ents){
					if(ent instanceof Player && murderEasterEgg.equals(((Player) ent).getGameProfile().getName())){
						ent.hurt(DamageSource.FLY_INTO_WALL, 100);//This seems fair
					}else{
						ent.hurt(DamageSource.FLY_INTO_WALL, 1);
					}
				}
			}

			if(running && axleHandler.axis != null){
				double power = getPowerOutput();
				if(axleHandler.getSpeed() * Math.signum(power) < MAX_SPEED){//Stop producing power above MAX_SPEED
					axleHandler.addEnergy(power, true);
				}

				setChanged();
			}
		}
	}

	@Override
	public void load(CompoundTag nbt){
		super.load(nbt);
		running = nbt.getBoolean("running");
		for(int i = 0; i < 4; i++){
			bladeColors[i] = nbt.getByte("blade_col_" + i);
		}
	}

	@Override
	public CompoundTag save(CompoundTag nbt){
		super.save(nbt);
		nbt.putBoolean("running", running);
		for(int i = 0; i < 4; i++){
			nbt.putByte("blade_col_" + i, (byte) bladeColors[i]);
		}
		return nbt;
	}

	@Override
	public CompoundTag getUpdateTag(){
		CompoundTag nbt = super.getUpdateTag();
		for(int i = 0; i < 4; i++){
			nbt.putByte("blade_col_" + i, (byte) bladeColors[i]);
		}
		return nbt;
	}

	@Override
	public void receiveLong(byte identifier, long message, @Nullable ServerPlayer sendingPlayer){
		super.receiveLong(identifier, message, sendingPlayer);
		if(identifier == 5){
			for(int i = 0; i < bladeColors.length; i++){
				bladeColors[i] = (int) ((message >> (i * 4)) & 0xF);
			}
		}
	}

	@Override
	public AABB getRenderBoundingBox(){
		return RENDER_BOX.move(worldPosition);
	}

	@Override
	public double getMoInertia(){
		return INERTIA;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing){
		if(capability == Capabilities.AXLE_CAPABILITY && (facing == null || facing == getFacing().getOpposite())){
			return (LazyOptional<T>) axleOpt;
		}
		return super.getCapability(capability, facing);
	}
}
