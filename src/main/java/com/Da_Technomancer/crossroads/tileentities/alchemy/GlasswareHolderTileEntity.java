package com.Da_Technomancer.crossroads.tileentities.alchemy;

import com.Da_Technomancer.crossroads.API.CRProperties;
import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.alchemy.*;
import com.Da_Technomancer.crossroads.API.heat.HeatUtil;
import com.Da_Technomancer.crossroads.API.heat.IHeatHandler;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.blocks.alchemy.GlasswareHolder;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.Da_Technomancer.crossroads.items.alchemy.AbstractGlassware;
import com.Da_Technomancer.essentials.blocks.ESProperties;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Explosion;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nonnull;

@ObjectHolder(Crossroads.MODID)
public class GlasswareHolderTileEntity extends AlchemyReactorTE{

	@ObjectHolder("glassware_holder")
	private static TileEntityType<GlasswareHolderTileEntity> type = null;

	protected AbstractGlassware.GlasswareTypes glassType = null;

	public GlasswareHolderTileEntity(){
		this(type);
	}

	protected GlasswareHolderTileEntity(TileEntityType<? extends GlasswareHolderTileEntity> type){
		super(type);
	}

	private AbstractGlassware.GlasswareTypes heldType(){
		if(glassType == null){
			BlockState state = getBlockState();
			if(state.hasProperty(CRProperties.CONTAINER_TYPE)){
				glassType = state.get(CRProperties.CONTAINER_TYPE);
				return glassType;
			}

			return AbstractGlassware.GlasswareTypes.NONE;
		}
		return glassType;
	}

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
		return heldType().capacity;
	}


	private ItemStack getStoredItem(BlockState state){
		AbstractGlassware.GlasswareTypes heldType = state.get(CRProperties.CONTAINER_TYPE);
		if(heldType == AbstractGlassware.GlasswareTypes.NONE){
			return ItemStack.EMPTY;
		}
		boolean crystal = state.get(CRProperties.CRYSTAL);
		ItemStack out;
		//This feels like a really bad way of doing this
		switch(heldType){
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

	@Override
	protected void destroyCarrier(float strength){
		BlockState state = getBlockState();
		world.setBlockState(pos, state.with(CRProperties.CRYSTAL, false).with(CRProperties.CONTAINER_TYPE, AbstractGlassware.GlasswareTypes.NONE));
		world.playSound(null, pos, SoundType.GLASS.getBreakSound(), SoundCategory.BLOCKS, SoundType.GLASS.getVolume(), SoundType.GLASS.getPitch());
		glassType = null;
		dirtyReag = true;
		AlchemyUtil.releaseChemical(world, pos, contents);
		contents = new ReagentMap();
		if(strength > 0){
			world.createExplosion(null, pos.getX(), pos.getY(), pos.getZ(), strength, Explosion.Mode.BREAK);
		}
	}


	public void onBlockDestroyed(BlockState state){
		if(state.get(CRProperties.CONTAINER_TYPE) != AbstractGlassware.GlasswareTypes.NONE){
			ItemStack out = getStoredItem(state);
			this.contents = new ReagentMap();
			dirtyReag = true;
			glassType = AbstractGlassware.GlasswareTypes.NONE;
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
		BlockState state = getBlockState();

		if(heldType() != AbstractGlassware.GlasswareTypes.NONE){
			if(stack.isEmpty() && sneaking){
				ItemStack out = getStoredItem(getBlockState());
				glassType = null;
				this.contents.clear();
				dirtyReag = true;
				markDirty();
				world.setBlockState(pos, state.with(CRProperties.CRYSTAL, false).with(CRProperties.CONTAINER_TYPE, AbstractGlassware.GlasswareTypes.NONE));
				//Invalidate the heat capability, as if we went from florence -> non florence, we stopped allowing cable connections
				heatOpt.invalidate();
				heatOpt = LazyOptional.of(HeatHandler::new);
				return out;
			}
			return super.rightClickWithItem(stack, sneaking, player, hand);
		}else if(stack.getItem() instanceof AbstractGlassware){
			//Add item into TE
			this.contents = ((AbstractGlassware) stack.getItem()).getReagants(stack);
			glass = !((AbstractGlassware) stack.getItem()).isCrystal();
			dirtyReag = true;
			markDirty();
			glassType = null;
			world.setBlockState(pos, state.with(CRProperties.CRYSTAL, !glass).with(CRProperties.CONTAINER_TYPE, ((AbstractGlassware) stack.getItem()).containerType()));
			if(contents.getTotalQty() > 0){
				//Force cable temperature to bottle temperature, prevents averaging with previous temperature
				cableTemp = contents.getTempC();
			}
			return ItemStack.EMPTY;
		}

		return stack;
	}

	@Override
	protected Vector3d getParticlePos(){
		BlockState state = getBlockState();
		if(state.getBlock() == CRBlocks.glasswareHolder && state.get(CRProperties.CONTAINER_TYPE) == AbstractGlassware.GlasswareTypes.FLORENCE){
			return Vector3d.copy(pos).add(0.5D, 0.7D, 0.5D);
		}else{
			return Vector3d.copy(pos).add(0.5D, 0.25D, 0.5D);
		}
	}

	@Override
	protected double correctTemp(){
		if(heldType().connectToCable){
			return super.correctTemp();
		}
		return contents.getTempC();
	}

	@Override
	protected void performTransfer(){
		BlockState state = world.getBlockState(pos);
		if(state.getBlock() instanceof GlasswareHolder && heldType() != AbstractGlassware.GlasswareTypes.NONE){
			Direction side = Direction.UP;
			TileEntity te = world.getTileEntity(pos.offset(side));
			LazyOptional<IChemicalHandler> otherOpt;
			if(contents.getTotalQty() == 0 || te == null || !(otherOpt = te.getCapability(Capabilities.CHEMICAL_CAPABILITY, side.getOpposite())).isPresent()){
				return;
			}

			IChemicalHandler otherHandler = otherOpt.orElseThrow(NullPointerException::new);
			EnumContainerType cont = otherHandler.getChannel(side.getOpposite());
			if(cont != EnumContainerType.NONE && ((cont == EnumContainerType.GLASS) != glass)){
				return;
			}

			if(otherHandler.insertReagents(contents, side.getOpposite(), handler, state.hasProperty(ESProperties.REDSTONE_BOOL) && state.get(ESProperties.REDSTONE_BOOL))){
				correctReag();
				markDirty();
			}
		}
	}

	@Override
	protected EnumTransferMode[] getModes(){
		EnumTransferMode[] modes = {EnumTransferMode.NONE, EnumTransferMode.NONE, EnumTransferMode.NONE, EnumTransferMode.NONE, EnumTransferMode.NONE, EnumTransferMode.NONE};
		if(heldType() != AbstractGlassware.GlasswareTypes.NONE){
			modes[1] = EnumTransferMode.BOTH;
		}
		return modes;
	}

	@Override
	public void remove(){
		super.remove();
		heatOpt.invalidate();
	}

	private LazyOptional<IHeatHandler> heatOpt = LazyOptional.of(HeatHandler::new);

	@SuppressWarnings("unchecked")
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side){
		if((side == null || side == Direction.UP) && cap == Capabilities.CHEMICAL_CAPABILITY && heldType() != AbstractGlassware.GlasswareTypes.NONE){
			return (LazyOptional<T>) chemOpt;
		}
		if((side == null || side == Direction.DOWN) && cap == Capabilities.HEAT_CAPABILITY && heldType().connectToCable){
			return (LazyOptional<T>) heatOpt;
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
