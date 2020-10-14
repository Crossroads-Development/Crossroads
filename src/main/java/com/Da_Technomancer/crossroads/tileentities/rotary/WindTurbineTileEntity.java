package com.Da_Technomancer.crossroads.tileentities.rotary;

import com.Da_Technomancer.crossroads.API.CRProperties;
import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.packets.CRPackets;
import com.Da_Technomancer.crossroads.API.templates.ModuleTE;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.essentials.packets.SendLongToClient;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@ObjectHolder(Crossroads.MODID)
public class WindTurbineTileEntity extends ModuleTE{

	@ObjectHolder("wind_turbine")
	public static TileEntityType<WindTurbineTileEntity> type = null;

	public static final double MAX_SPEED = 2D;
	public static final double INERTIA = 200;
	public static final double POWER_PER_LEVEL = 10D;
	private static final AxisAlignedBB RENDER_BOX = new AxisAlignedBB(-1, -1, -1, 2, 2, 2);

	//Undocumented 'easter egg'. This person takes way more damage from windmills
	//Don't ask.
	private static final String murderEasterEgg = "dinidini";

	private boolean newlyPlaced = true;
	private int level = 1;
	private boolean running = false;
	private AxisAlignedBB targetBB = null;
	public int[] bladeColors = new int[4];
	private int lastColoredBlade = 3;//Index of the last blade dyed

	public WindTurbineTileEntity(){
		super(type);
	}

	public WindTurbineTileEntity(boolean newlyPlaced){
		this();
		this.newlyPlaced = newlyPlaced;
	}

	protected Direction getFacing(){
		BlockState state = getBlockState();
		if(state.getBlock() != CRBlocks.windTurbine){
			remove();
			return Direction.NORTH;
		}
		return state.get(CRProperties.HORIZ_FACING);
	}

	private AxisAlignedBB getTargetBB(){
		if(targetBB == null){
			Direction dir = getFacing();
			Direction planeDir = dir.rotateY();
			if(planeDir.getAxisDirection() == Direction.AxisDirection.NEGATIVE){
				planeDir = planeDir.getOpposite();
			}
			BlockPos center = pos.offset(dir);
			if(dir.getAxisDirection() == Direction.AxisDirection.POSITIVE){
				targetBB = new AxisAlignedBB(center.offset(planeDir, -2).offset(Direction.DOWN, 2), center.offset(planeDir, 3).offset(Direction.UP, 3).offset(dir));
			}else{
				targetBB = new AxisAlignedBB(center.offset(planeDir, -2).offset(Direction.DOWN, 2), center.offset(planeDir, 3).offset(Direction.UP, 3).offset(dir, -1));
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
				CRPackets.sendPacketAround(world, pos, new SendLongToClient(5, message, pos));

				markDirty();
			}
		}
	}

	@Override
	public void updateContainingBlockInfo(){
		super.updateContainingBlockInfo();
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
	protected AxleHandler createAxleHandler(){
		return new AngleAxleHandler();
	}

	@Override
	public void addInfo(ArrayList<ITextComponent> chat, PlayerEntity player, BlockRayTraceResult hit){
		chat.add(new TranslationTextComponent("tt.crossroads.wind_turbine.weather", POWER_PER_LEVEL * (double) level));
		super.addInfo(chat, player, hit);
	}

	public float getRedstoneOutput(){
		return (float) (level * POWER_PER_LEVEL);
	}

	@Override
	public void tick(){
		super.tick();

		if(!world.isRemote){
			//Every 30 seconds check whether the placement requirements are valid, and cache the result
			if(newlyPlaced || world.getGameTime() % 600 == 0){
				newlyPlaced = false;
				running = false;
				Direction facing = getFacing();
				BlockPos offsetPos = pos.offset(facing);
				if(world.canBlockSeeSky(offsetPos)){
					running = true;
					outer:
					for(int i = -2; i <= 2; i++){
						for(int j = -2; j <= 2; j++){
							BlockPos checkPos = offsetPos.add(facing.getZOffset() * i, j, facing.getXOffset() * i);
							BlockState checkState = world.getBlockState(checkPos);
							if(!checkState.getBlock().isAir(checkState, world, checkPos)){
								running = false;
								break outer;
							}
						}
					}
				}

				markDirty();
			}

			//Damage entities in the blades while spinning at high speed
			if(Math.abs(motData[0]) >= 1.5D){
				List<LivingEntity> ents = world.getEntitiesWithinAABB(LivingEntity.class, getTargetBB(), EntityPredicates.IS_LIVING_ALIVE);
				for(LivingEntity ent : ents){
					if(ent instanceof PlayerEntity && murderEasterEgg.equals(((PlayerEntity) ent).getGameProfile().getName())){
						ent.attackEntityFrom(DamageSource.FLY_INTO_WALL, 100);//This seems fair
					}else{
						ent.attackEntityFrom(DamageSource.FLY_INTO_WALL, 1);
					}
				}
			}

			if(running && axleHandler.axis != null){
				if(world.getGameTime() % 10 == 0 && world.rand.nextInt(240) == 0){
					//Randomize output
					level = (world.rand.nextInt(2) + 1) * (world.rand.nextBoolean() ? -1 : 1);//Gen a random number from -2 to 2, other than 0
				}

				if(motData[0] * Math.signum(level) < MAX_SPEED){
					motData[1] += (double) level * POWER_PER_LEVEL;
				}

				markDirty();
			}
		}
	}

	@Override
	public void read(BlockState state, CompoundNBT nbt){
		super.read(state, nbt);
		level = nbt.getInt("level");
		running = nbt.getBoolean("running");
		for(int i = 0; i < 4; i++){
			bladeColors[i] = nbt.getByte("blade_col_" + i);
		}
	}

	@Override
	public CompoundNBT write(CompoundNBT nbt){
		super.write(nbt);
		nbt.putInt("level", level);
		nbt.putBoolean("running", running);
		for(int i = 0; i < 4; i++){
			nbt.putByte("blade_col_" + i, (byte) bladeColors[i]);
		}
		return nbt;
	}

	@Override
	public CompoundNBT getUpdateTag(){
		CompoundNBT nbt = super.getUpdateTag();
		for(int i = 0; i < 4; i++){
			nbt.putByte("blade_col_" + i, (byte) bladeColors[i]);
		}
		return nbt;
	}

	@Override
	public void receiveLong(byte identifier, long message, @Nullable ServerPlayerEntity sendingPlayer){
		super.receiveLong(identifier, message, sendingPlayer);
		if(identifier == 5){
			for(int i = 0; i < bladeColors.length; i++){
				bladeColors[i] = (int) ((message >> (i * 4)) & 0xF);
			}
		}
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox(){
		return RENDER_BOX.offset(pos);
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
