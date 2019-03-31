package com.Da_Technomancer.crossroads.tileentities.alchemy;

import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.API.alchemy.AlchemyReactorTE;
import com.Da_Technomancer.crossroads.API.alchemy.AlchemyUtil;
import com.Da_Technomancer.crossroads.API.alchemy.EnumTransferMode;
import com.Da_Technomancer.crossroads.API.alchemy.ReagentMap;
import com.Da_Technomancer.crossroads.items.ModItems;
import com.Da_Technomancer.crossroads.items.alchemy.AbstractGlassware;
import com.Da_Technomancer.crossroads.items.alchemy.FlorenceFlask;
import com.Da_Technomancer.crossroads.items.alchemy.Phial;
import com.Da_Technomancer.crossroads.render.RenderUtil;
import com.Da_Technomancer.crossroads.tileentities.electric.TeslaCoilTopTileEntity;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nonnull;

public class ChargingStandTileEntity extends AlchemyReactorTE{

	private boolean occupied = false;
	/**
	 * Meaningless if !occupied. If true, florence flask, else phial.
	 */
	private boolean florence = false;
	private int fe = 0;
	private static final int ENERGY_CAPACITY = 100;

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState){
		return oldState.getBlock() != newState.getBlock();
	}

	@Override
	public void update(){
		if(world.isRemote){
			return;
		}
		if(fe > 0){
			fe = Math.max(0, fe - 10);
			if(world.getTotalWorldTime() % 10 == 0){
				RenderUtil.addArc(world.provider.getDimension(), pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F, pos.getX() + world.rand.nextFloat(), pos.getY() + world.rand.nextFloat(), pos.getZ() + world.rand.nextFloat(), 1, 0F, TeslaCoilTopTileEntity.COLOR_CODES[(int) (world.getTotalWorldTime() % 3)]);
			}
		}
		super.update();
	}

	@Override
	public boolean isCharged(){
		return fe > 0;
	}

	@Override
	protected int transferCapacity(){
		return occupied ? florence ? ModItems.florenceFlaskGlass.getCapacity() : ModItems.phialGlass.getCapacity() : 0;
	}

	@Override
	public void destroyChamber(float strength){
		IBlockState state = world.getBlockState(pos);
		world.setBlockState(pos, state.withProperty(Properties.ACTIVE, false).withProperty(Properties.CRYSTAL, false).withProperty(Properties.CONTAINER_TYPE, false));
		world.playSound(null, pos, SoundType.GLASS.getBreakSound(), SoundCategory.BLOCKS, SoundType.GLASS.getVolume(), SoundType.GLASS.getPitch());
		occupied = false;
		florence = false;
		dirtyReag = true;
		AlchemyUtil.releaseChemical(world, pos, contents);
		contents = new ReagentMap();
		if(strength > 0){
			world.createExplosion(null, pos.getX(), pos.getY(), pos.getZ(), strength, true);
		}
	}

	public void onBlockDestroyed(IBlockState state){
		if(occupied){
			ItemStack out = getStoredItem();
			this.contents = new ReagentMap();
			dirtyReag = true;
			occupied = false;
			markDirty();
			InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), out);
		}
	}

	private ItemStack getStoredItem(){
		AbstractGlassware glasswareType = florence ? glass ? ModItems.florenceFlaskGlass : ModItems.florenceFlaskCrystal : glass ? ModItems.phialGlass : ModItems.phialCrystal;
		ItemStack flask = new ItemStack(glasswareType, 1);
		glasswareType.setReagents(flask, contents);
		return flask;
	}

	/**
	 * Normal behavior: 
	 * Shift click with empty hand: Remove phial
	 * Normal click with empty hand: Try to remove solid reagent
	 * Normal click with phial: Add phial to stand, or merge phial contents
	 * Normal click with non-phial item: Try to add solid reagent
	 */
	@Nonnull
	@Override
	public ItemStack rightClickWithItem(ItemStack stack, boolean sneaking, EntityPlayer player, EnumHand hand){
		IBlockState state = world.getBlockState(pos);

		if(occupied){
			if(stack.isEmpty() && sneaking){
				ItemStack out = getStoredItem();
				world.setBlockState(pos, state.withProperty(Properties.ACTIVE, false).withProperty(Properties.CRYSTAL, false).withProperty(Properties.CONTAINER_TYPE, false));
				occupied = false;
				this.contents.clear();
				dirtyReag = true;
				markDirty();
				return out;
			}

			super.rightClickWithItem(stack, sneaking, player, hand);
		}else if(stack.getItem() instanceof Phial || stack.getItem() instanceof FlorenceFlask){
			//Add item into TE
			this.contents = ((AbstractGlassware) stack.getItem()).getReagants(stack);
			glass = !((AbstractGlassware) stack.getItem()).isCrystal();
			dirtyReag = true;
			markDirty();
			occupied = true;
			florence = stack.getItem() instanceof FlorenceFlask;
			world.setBlockState(pos, state.withProperty(Properties.ACTIVE, true).withProperty(Properties.CRYSTAL, !glass).withProperty(Properties.CONTAINER_TYPE, florence));
			return ItemStack.EMPTY;
		}

		return stack;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		occupied = nbt.getBoolean("occupied");
		florence = occupied && nbt.getBoolean("florence");
		fe = nbt.getInteger("fe");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		nbt.setBoolean("occupied", occupied);
		nbt.setBoolean("florence", florence);
		nbt.setInteger("fe", fe);
		return nbt;
	}

	@Override
	protected EnumTransferMode[] getModes(){
		return new EnumTransferMode[] {EnumTransferMode.NONE, EnumTransferMode.NONE, EnumTransferMode.NONE, EnumTransferMode.NONE, EnumTransferMode.NONE, EnumTransferMode.NONE};
	}

	private final ElecHandler elecHandler = new ElecHandler();

	@Override
	public boolean hasCapability(Capability<?> cap, EnumFacing side){
		if(cap == CapabilityEnergy.ENERGY){
			return true;
		}
		return super.hasCapability(cap, side);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> cap, EnumFacing side){
		if(cap == CapabilityEnergy.ENERGY){
			return (T) elecHandler;
		}
		return super.getCapability(cap, side);
	}

	private class ElecHandler implements IEnergyStorage{

		@Override
		public int receiveEnergy(int maxReceive, boolean simulate){
			int toMove = Math.min(ENERGY_CAPACITY - fe, maxReceive);

			if(!simulate && toMove > 0){
				fe += toMove;
				markDirty();
			}

			return toMove;
		}

		@Override
		public int extractEnergy(int maxExtract, boolean simulate){
			return 0;
		}

		@Override
		public int getEnergyStored(){
			return fe;
		}

		@Override
		public int getMaxEnergyStored(){
			return ENERGY_CAPACITY;
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
}
