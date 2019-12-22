package com.Da_Technomancer.crossroads.tileentities.alchemy;

import com.Da_Technomancer.crossroads.API.CRProperties;
import com.Da_Technomancer.crossroads.API.alchemy.AlchemyReactorTE;
import com.Da_Technomancer.crossroads.API.alchemy.AlchemyUtil;
import com.Da_Technomancer.crossroads.API.alchemy.EnumTransferMode;
import com.Da_Technomancer.crossroads.API.alchemy.ReagentMap;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.Da_Technomancer.crossroads.items.alchemy.AbstractGlassware;
import com.Da_Technomancer.crossroads.render.RenderUtil;
import com.Da_Technomancer.crossroads.tileentities.electric.TeslaCoilTopTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.Explosion;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nonnull;

public class ChargingStandTileEntity extends AlchemyReactorTE{

	private static final int ENERGY_CAPACITY = 100;
	public static final int DRAIN = 10;

	private AbstractGlassware.GlasswareTypes type = null;
	private int fe = 0;

	private AbstractGlassware.GlasswareTypes heldType(){
		if(type == null){
			BlockState state = world.getBlockState(pos);
			if(state.has(CRProperties.CONTAINER_TYPE)){
				type = state.get(CRProperties.CONTAINER_TYPE);
				return type;
			}

			return AbstractGlassware.GlasswareTypes.NONE;
		}
		return type;
	}

	@Override
	public void tick(){
		if(world.isRemote){
			return;
		}
		if(fe > 0){
			fe = Math.max(0, fe - DRAIN);
			if(world.getGameTime() % 10 == 0){
				RenderUtil.addArc(world, pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F, pos.getX() + world.rand.nextFloat(), pos.getY() + world.rand.nextFloat(), pos.getZ() + world.rand.nextFloat(), 1, 0F, TeslaCoilTopTileEntity.COLOR_CODES[(int) (world.getGameTime() % 3)]);
			}
		}
		super.tick();
	}

	@Override
	public boolean isCharged(){
		return fe > 0 || super.isCharged();
	}

	@Override
	protected int transferCapacity(){
		return heldType().capacity;
	}

	@Override
	public void destroyChamber(float strength){
		BlockState state = world.getBlockState(pos);
		world.setBlockState(pos, state.with(CRProperties.CRYSTAL, false).with(CRProperties.CONTAINER_TYPE, AbstractGlassware.GlasswareTypes.NONE));
		world.playSound(null, pos, SoundType.GLASS.getBreakSound(), SoundCategory.BLOCKS, SoundType.GLASS.getVolume(), SoundType.GLASS.getPitch());
		type = null;
		dirtyReag = true;
		AlchemyUtil.releaseChemical(world, pos, contents);
		contents = new ReagentMap();
		if(strength > 0){
			world.createExplosion(null, pos.getX(), pos.getY(), pos.getZ(), strength, Explosion.Mode.BREAK);
		}
	}

	public void onBlockDestroyed(BlockState state){
		if(heldType() != AbstractGlassware.GlasswareTypes.NONE){
			ItemStack out = getStoredItem();
			this.contents = new ReagentMap();
			dirtyReag = true;
			type = AbstractGlassware.GlasswareTypes.NONE;
			markDirty();
			InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), out);
		}
	}

	private ItemStack getStoredItem(){
		if(heldType() == AbstractGlassware.GlasswareTypes.NONE){
			return ItemStack.EMPTY;
		}
		boolean crystal = world.getBlockState(pos).get(CRProperties.CRYSTAL);
		ItemStack out;
		//This feels like a really bad way of doing this
		switch(heldType()){
			case PHIAL:
				out = crystal ? new ItemStack(CRItems.phialCrystal, 1) : new ItemStack(CRItems.phialGlass, 1);
				break;
			case FLORENCE:
				out =  crystal ? new ItemStack(CRItems.florenceFlaskCrystal, 1) : new ItemStack(CRItems.florenceFlaskGlass, 1);
				break;
			case SHELL:
				out =  crystal ? new ItemStack(CRItems.shellCrystal, 1) : new ItemStack(CRItems.shellGlass, 1);
				break;
			default:
				return ItemStack.EMPTY;
		}

		((AbstractGlassware) (out.getItem())).setReagents(out, contents);
		return out;
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
	public ItemStack rightClickWithItem(ItemStack stack, boolean sneaking, PlayerEntity player, Hand hand){
		BlockState state = world.getBlockState(pos);

		if(heldType() != AbstractGlassware.GlasswareTypes.NONE){
			if(stack.isEmpty() && sneaking){
				ItemStack out = getStoredItem();
				world.setBlockState(pos, state.with(CRProperties.CRYSTAL, false).with(CRProperties.CONTAINER_TYPE, AbstractGlassware.GlasswareTypes.NONE));
				type = null;
				this.contents.clear();
				dirtyReag = true;
				markDirty();
				return out;
			}
			super.rightClickWithItem(stack, sneaking, player, hand);
		}else if(stack.getItem() instanceof AbstractGlassware){
			//Add item into TE
			this.contents = ((AbstractGlassware) stack.getItem()).getReagants(stack);
			glass = !((AbstractGlassware) stack.getItem()).isCrystal();
			dirtyReag = true;
			markDirty();
			world.setBlockState(pos, state.with(CRProperties.CRYSTAL, !glass).with(CRProperties.CONTAINER_TYPE, ((AbstractGlassware) stack.getItem()).containerType()));
			type = null;
			return ItemStack.EMPTY;
		}

		return stack;
	}

	@Override
	public void read(CompoundNBT nbt){
		super.read(nbt);
		fe = nbt.getInt("fe");
	}

	@Override
	public CompoundNBT write(CompoundNBT nbt){
		super.write(nbt);
		nbt.putInt("fe", fe);
		return nbt;
	}

	@Override
	protected EnumTransferMode[] getModes(){
		return new EnumTransferMode[] {EnumTransferMode.NONE, EnumTransferMode.NONE, EnumTransferMode.NONE, EnumTransferMode.NONE, EnumTransferMode.NONE, EnumTransferMode.NONE};
	}

	@Override
	public void remove(){
		super.remove();
		elecOpt.invalidate();
	}

	private final LazyOptional<IEnergyStorage> elecOpt = LazyOptional.of(ElecHandler::new);

	@SuppressWarnings("unchecked")
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side){
		if(cap == CapabilityEnergy.ENERGY){
			return (LazyOptional<T>) elecOpt;
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
