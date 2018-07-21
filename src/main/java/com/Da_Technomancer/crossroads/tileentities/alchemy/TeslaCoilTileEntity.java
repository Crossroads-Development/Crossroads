package com.Da_Technomancer.crossroads.tileentities.alchemy;

import com.Da_Technomancer.crossroads.API.IInfoTE;
import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.API.alchemy.LooseArcRenderable;
import com.Da_Technomancer.crossroads.API.packets.ModPackets;
import com.Da_Technomancer.crossroads.API.packets.SendLooseArcToClient;
import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import com.Da_Technomancer.crossroads.items.ModItems;
import com.Da_Technomancer.crossroads.items.alchemy.LeydenJar;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class TeslaCoilTileEntity extends TileEntity implements IInfoTE, ITickable{

	@Override
	public void addInfo(ArrayList<String> chat, EntityPlayer player, EnumFacing side){
		for(int i = 0; i < 3; i++){
			if(linked[i] != null){
				chat.add("Linked Position: X=" + (pos.getX() + linked[i].getX()) + " Y=" + (pos.getY() + linked[i].getY()) + " Z=" + (pos.getZ() + linked[i].getZ()));
			}
		}
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState){
		return oldState.getBlock() != newState.getBlock();
	}

	public static final int[] COLOR_CODES = {new Color(128, 0, 255, 128).getRGB(), new Color(64, 0, 255, 128).getRGB(), new Color(100, 0, 255, 128).getRGB()};
	private static final int[] ATTACK_COLOR_CODES = {new Color(255, 32, 0, 128).getRGB(), new Color(255, 0, 32, 128).getRGB(), new Color(255, 32, 32, 128).getRGB()};

	private static final int JOLT_CONSERVED = 950;
	private int stored = 0;
	private Boolean hasJar = null;
	private static final int JOLT_AMOUNT = 1000;
	private static final int CAPACITY = 2000;
	public static final int RANGE = 8;

	//Relative to this TileEntity's position
	public BlockPos[] linked = new BlockPos[3];
	public boolean redstone = false;

	@Override
	public void update(){
		if(world.isRemote){
			return;
		}
		if(hasJar == null){
			IBlockState state = world.getBlockState(pos);
			if(state.getBlock() != ModBlocks.teslaCoil){
				invalidate();
				return;
			}
			hasJar = world.getBlockState(pos).getValue(Properties.ACTIVE);
		}

		if(!redstone && world.getTotalWorldTime() % 10 == 0 && stored >= JOLT_AMOUNT){
			IBlockState state = world.getBlockState(pos);
			if(state.getBlock() != ModBlocks.teslaCoil){
				invalidate();
				return;
			}

			if(state.getValue(Properties.LIGHT)){
				List<EntityLivingBase> ents = world.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(pos.getX() - RANGE, pos.getY() - RANGE, pos.getZ() - RANGE, pos.getX() + RANGE, pos.getY() + RANGE, pos.getZ() + RANGE), EntitySelectors.IS_ALIVE);

				if(!ents.isEmpty()){
					EntityLivingBase ent = ents.get(world.rand.nextInt(ents.size()));
					stored -= JOLT_AMOUNT;
					markDirty();

					ModPackets.network.sendToAllAround(new SendLooseArcToClient(new LooseArcRenderable(pos.getX() + 0.5F, pos.getY() + 2F, pos.getZ() + 0.5F, (float) ent.posX, (float) ent.posY, (float) ent.posZ, 5, 0.6F, 1F, ATTACK_COLOR_CODES[(int) (world.getTotalWorldTime() % 3)])), new TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
					world.playSound(null, pos.getX() + 0.5F, pos.getY() + 2F, pos.getZ() + 0.5F, SoundEvents.BLOCK_REDSTONE_TORCH_BURNOUT, SoundCategory.BLOCKS, 0.1F, 0F);
					//Sound may need tweaking

					ent.onStruckByLightning(new EntityLightningBolt(world, ent.posX, ent.posY, ent.posZ, true));
					return;
				}
			}


			for(BlockPos linkPos : linked){
				if(linkPos != null){
					BlockPos actualPos = linkPos.add(pos);
					TileEntity te = world.getTileEntity(actualPos);
					if(te instanceof TeslaCoilTileEntity){
						TeslaCoilTileEntity tcTe = (TeslaCoilTileEntity) te;
						if(tcTe.handlerIn.getMaxEnergyStored() - tcTe.stored > JOLT_CONSERVED){
							tcTe.stored += JOLT_CONSERVED;
							tcTe.markDirty();
							stored -= JOLT_AMOUNT;
							markDirty();

							ModPackets.network.sendToAllAround(new SendLooseArcToClient(new LooseArcRenderable(pos.getX() + 0.5F, pos.getY() + 2F, pos.getZ() + 0.5F, actualPos.getX() + 0.5F, actualPos.getY() + 2F, actualPos.getZ() + 0.5F, 5, 0.6F, 1F, COLOR_CODES[(int) (world.getTotalWorldTime() % 3)])), new TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
							world.playSound(null, pos.getX() + 0.5F, pos.getY() + 2F, pos.getZ() + 0.5F, SoundEvents.BLOCK_REDSTONE_TORCH_BURNOUT, SoundCategory.BLOCKS, 0.1F, 0F);
							//Sound may need tweaking
							break;
						}
					}
				}
			}
		}

		if(!redstone && stored > 0){
			EnumFacing facing = world.getBlockState(pos).getValue(Properties.HORIZONTAL_FACING);
			TileEntity te = world.getTileEntity(pos.offset(facing));
			if(te != null && te.hasCapability(CapabilityEnergy.ENERGY, facing.getOpposite())){
				IEnergyStorage storage = te.getCapability(CapabilityEnergy.ENERGY, facing.getOpposite());
				int moved = storage.receiveEnergy(stored, false);
				if(moved > 0){
					stored -= moved;
					markDirty();
				}
			}
		}
	}

	public void addJar(ItemStack stack){
		stored = Math.min(stored + LeydenJar.getCharge(stack), CAPACITY + LeydenJar.MAX_CHARGE);
		hasJar = true;
		markDirty();
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		nbt.setInteger("stored", stored);
		for(int i = 0; i < 3; i++){
			if(linked[i] != null){
				nbt.setLong("link" + i, linked[i].toLong());
			}
		}
		nbt.setBoolean("reds", redstone);
		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		stored = nbt.getInteger("stored");
		for(int i = 0; i < 3; i++){
			if(nbt.hasKey("link" + i)){
				linked[i] = BlockPos.fromLong(nbt.getLong("link" + i));
			}
		}
		redstone = nbt.getBoolean("reds");
	}

	@Nonnull
	public ItemStack removeJar(){
		ItemStack out = new ItemStack(ModItems.leydenJar, 1);
		LeydenJar.setCharge(out, Math.min(stored, LeydenJar.MAX_CHARGE));
		stored -= Math.min(stored, LeydenJar.MAX_CHARGE);
		hasJar = false;
		markDirty();
		return out;
	}

	private final EnergyHandlerIn handlerIn = new EnergyHandlerIn();
	private final EnergyHandlerOut handlerOut = new EnergyHandlerOut();

	public boolean hasCapability(Capability<?> cap, EnumFacing side){
		if(cap == CapabilityEnergy.ENERGY && (side == null || side.getAxis() != Axis.Y)){
			return true;
		}
		return super.hasCapability(cap, side);
	}

	@SuppressWarnings("unchecked")
	public <T> T getCapability(Capability<T> cap, EnumFacing side){
		if(cap == CapabilityEnergy.ENERGY && (side == null || side.getAxis() != Axis.Y)){
			return (T) (side == world.getBlockState(pos).getValue(Properties.HORIZONTAL_FACING) ? handlerOut : handlerIn);
		}
		return super.getCapability(cap, side);
	}

	private class EnergyHandlerIn implements IEnergyStorage{

		@Override
		public int receiveEnergy(int maxReceive, boolean simulate){
			int toInsert = Math.min(maxReceive, getMaxEnergyStored() - stored);

			if(!simulate){
				stored += toInsert;
				markDirty();
			}
			return toInsert;
		}

		@Override
		public int extractEnergy(int maxExtract, boolean simulate){
			return 0;
		}

		@Override
		public int getEnergyStored(){
			return stored;
		}

		@Override
		public int getMaxEnergyStored(){
			return hasJar == Boolean.TRUE ? CAPACITY + LeydenJar.MAX_CHARGE : CAPACITY;
		}

		@Override
		public boolean canExtract(){
			return false;
		}

		@Override
		public boolean canReceive(){
			return true;
		}
	}

	private class EnergyHandlerOut implements IEnergyStorage{

		@Override
		public int receiveEnergy(int maxReceive, boolean simulate){
			return 0;
		}

		@Override
		public int extractEnergy(int maxExtract, boolean simulate){
			int toExtract = Math.min(stored, maxExtract);
			if(!simulate){
				stored -= toExtract;
				markDirty();
			}
			return toExtract;
		}

		@Override
		public int getEnergyStored(){
			return stored;
		}

		@Override
		public int getMaxEnergyStored(){
			return hasJar == Boolean.TRUE ? CAPACITY + LeydenJar.MAX_CHARGE : CAPACITY;
		}

		@Override
		public boolean canExtract(){
			return true;
		}

		@Override
		public boolean canReceive(){
			return false;
		}
	}
}
