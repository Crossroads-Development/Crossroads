package com.Da_Technomancer.crossroads.tileentities.alchemy;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.CRProperties;
import com.Da_Technomancer.crossroads.API.alchemy.*;
import com.Da_Technomancer.crossroads.API.heat.HeatUtil;
import com.Da_Technomancer.crossroads.API.heat.IHeatHandler;
import com.Da_Technomancer.crossroads.blocks.CrossroadsBlocks;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.Da_Technomancer.crossroads.items.alchemy.AbstractGlassware;
import com.Da_Technomancer.crossroads.items.alchemy.FlorenceFlask;
import com.Da_Technomancer.crossroads.items.alchemy.Phial;
import com.Da_Technomancer.essentials.blocks.EssentialsProperties;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nonnull;

public class GlasswareHolderTileEntity extends AlchemyReactorTE{

	private boolean occupied = false;
	/**
	 * Meaningless if !occupied. If true, florence flask, else phial. 
	 */
	private boolean florence = false;

	@Override
	protected boolean useCableHeat(){
		return true;
	}

	@Override
	protected void initHeat(){
		if(!init){
			init = true;
			cableTemp = HeatUtil.convertBiomeTemp(world, pos);
		}
	}

	@Override
	protected int transferCapacity(){
		return occupied ? florence ? CRItems.florenceFlaskGlass.getCapacity() : CRItems.phialGlass.getCapacity() : 0;
	}

	private ItemStack getStoredItem(){
		AbstractGlassware glasswareType = florence ? glass ? CRItems.florenceFlaskGlass : CRItems.florenceFlaskCrystal : glass ? CRItems.phialGlass : CRItems.phialCrystal;
		ItemStack flask = new ItemStack(glasswareType, 1);
		glasswareType.setReagents(flask, contents);
		return flask;
	}

	@Override
	public void destroyChamber(float strength){
		BlockState state = world.getBlockState(pos);
		world.setBlockState(pos, state.with(CRProperties.ACTIVE, false).with(CRProperties.CRYSTAL, false).with(CRProperties.CONTAINER_TYPE, false));
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

	public void onBlockDestroyed(BlockState state){
		if(occupied){
			ItemStack out = getStoredItem();
			this.contents = new ReagentMap();
			dirtyReag = true;
			occupied = false;
			markDirty();
			InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), out);
		}
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

		if(occupied){
			if(stack.isEmpty() && sneaking){
				ItemStack flask = getStoredItem();
				world.setBlockState(pos, state.with(CRProperties.ACTIVE, false).with(CRProperties.CRYSTAL, false).with(CRProperties.CONTAINER_TYPE, false));
				occupied = false;
				this.contents.clear();
				dirtyReag = true;
				markDirty();
				return flask;
			}
		}else if(stack.getItem()instanceof Phial || stack.getItem() instanceof FlorenceFlask){
			//Add item into TE
			this.contents = ((AbstractGlassware) stack.getItem()).getReagants(stack);
			glass = !((AbstractGlassware) stack.getItem()).isCrystal();
			dirtyReag = true;
			markDirty();
			occupied = true;
			florence = stack.getItem() instanceof FlorenceFlask;
			if(florence && contents.getTotalQty() != 0){
				cableTemp = contents.getTempC();
			}
			world.setBlockState(pos, state.with(CRProperties.ACTIVE, true).with(CRProperties.CRYSTAL, !glass).with(CRProperties.CONTAINER_TYPE, florence));
			return ItemStack.EMPTY;
		}

		return super.rightClickWithItem(stack, sneaking, player, hand);
	}

	@Override
	protected Vec3d getParticlePos(){
		return new Vec3d(pos).add(0.5D, 0.25D, 0.5D);
	}

	@Override
	protected double correctTemp(){
		if(florence){
			return super.correctTemp();
		}
		return contents.getTempC();
	}

	@Override
	protected void performTransfer(){
		BlockState state = world.getBlockState(pos);
		if(state.getBlock() == CrossroadsBlocks.glasswareHolder && state.get(CRProperties.ACTIVE)){
			Direction side = Direction.UP;
			TileEntity te = world.getTileEntity(pos.offset(side));
			if(contents.getTotalQty() == 0 || te == null || !te.hasCapability(Capabilities.CHEMICAL_CAPABILITY, side.getOpposite())){
				return;
			}

			IChemicalHandler otherHandler = te.getCapability(Capabilities.CHEMICAL_CAPABILITY, side.getOpposite());
			EnumContainerType cont = otherHandler.getChannel(side.getOpposite());
			if(cont != EnumContainerType.NONE && ((cont == EnumContainerType.GLASS) != glass)){
				return;
			}

			if(otherHandler.insertReagents(contents, side.getOpposite(), handler, state.get(EssentialsProperties.REDSTONE_BOOL))){
				correctReag();
				markDirty();
			}
		}
	}

	@Override
	public void read(CompoundNBT nbt){
		super.read(nbt);
		occupied = nbt.getBoolean("occupied");
		florence = occupied && nbt.getBoolean("florence");
	}

	@Override
	public CompoundNBT write(CompoundNBT nbt){
		super.write(nbt);
		nbt.putBoolean("occupied", occupied);
		nbt.putBoolean("florence", florence);
		return nbt;
	}

	@Override
	protected EnumTransferMode[] getModes(){
		EnumTransferMode[] modes = {EnumTransferMode.NONE, EnumTransferMode.NONE, EnumTransferMode.NONE, EnumTransferMode.NONE, EnumTransferMode.NONE, EnumTransferMode.NONE};
		if(occupied){
			modes[1] = EnumTransferMode.BOTH;
		}
		return modes;
	}

	private final HeatHandler heatHandler = new HeatHandler();

	@SuppressWarnings("unchecked")
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side){
		if((side == null || side == Direction.UP) && cap == Capabilities.CHEMICAL_CAPABILITY && occupied){
			return (T) handler;
		}
		if((side == null || side == Direction.DOWN) && cap == Capabilities.HEAT_CAPABILITY){
			BlockState state = world.getBlockState(pos);
			if(state.get(CRProperties.CONTAINER_TYPE)){
				return (T) heatHandler;
			}
		}
		return super.getCapability(cap, side);
	}

	private class HeatHandler implements IHeatHandler{

		@Override
		public double getTemp(){
			initHeat();
			return cableTemp;
		}

		@Override
		public void setTemp(double tempIn){
			init = true;
			cableTemp = tempIn;
			//Shares heat between internal cable & contents
			dirtyReag = true;
			markDirty();
		}

		@Override
		public void addHeat(double tempChange){
			initHeat();
			cableTemp += tempChange;
			//Shares heat between internal cable & contents
			dirtyReag = true;
			markDirty();
		}
	}
}
