package com.Da_Technomancer.crossroads.blocks.fluid;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.blocks.CrossroadsBlocks;
import com.Da_Technomancer.crossroads.gui.GuiHandler;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.Da_Technomancer.crossroads.tileentities.fluid.WaterCentrifugeTileEntity;
import com.Da_Technomancer.essentials.EssentialsConfig;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.block.BlockRenderType;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class WaterCentrifuge extends ContainerBlock{
	
	public WaterCentrifuge(){
		super(Material.IRON);
		String name = "water_centrifuge";
		setTranslationKey(name);
		setRegistryName(name);
		setCreativeTab(CRItems.TAB_CROSSROADS);
		setHardness(3);
		setSoundType(SoundType.METAL);
		CrossroadsBlocks.toRegister.add(this);
		CrossroadsBlocks.blockAddQue(this);
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn){
		return new WaterCentrifugeTileEntity();
	}
	
	@Override
	public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity playerIn, Hand hand, BlockRayTraceResult hit){
		if(!worldIn.isRemote){
			if(EssentialsConfig.isWrench(playerIn.getHeldItem(hand), false)){
				worldIn.setBlockState(pos, state.cycle(Properties.HORIZ_AXIS));
			}else{
				playerIn.openGui(Crossroads.instance, GuiHandler.WATER_CENTRIFUGE_GUI, worldIn, pos.getX(), pos.getY(), pos.getZ());
			}
		}
		return true;
	}

	@Override
	public BlockRenderType getRenderType(BlockState state){
		return BlockRenderType.MODEL;
	}
	
	@Override
	public int getMetaFromState(BlockState state){
		return state.get(Properties.HORIZ_AXIS) == Axis.X ? 1 : 0;
	}
	
	@Override
	public BlockState getStateFromMeta(int meta){
		return getDefaultState().with(Properties.HORIZ_AXIS, meta == 1 ? Axis.X : Axis.Z);
	}
	
	@Override
	public BlockStateContainer createBlockState(){
		return new BlockStateContainer(this, Properties.HORIZ_AXIS);
	}
	
	@Override
	public BlockState getStateForPlacement(World worldIn, BlockPos pos, Direction blockFaceClickedOn, BlockRayTraceResult hit, int meta, LivingEntity placer){
		return getDefaultState().with(Properties.HORIZ_AXIS, placer == null ? Axis.X : placer.getHorizontalFacing().getAxis());
	}
	
	@Override
	public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving){
		InventoryHelper.dropInventoryItems(world, pos, (IInventory) world.getTileEntity(pos));
		super.onReplaced(state, world, pos, newState, isMoving);
	}

	@Override
	public boolean hasComparatorInputOverride(BlockState state){
		return true;
	}

	@Override
	public int getComparatorInputOverride(BlockState blockState, World worldIn, BlockPos pos){
		TileEntity te = worldIn.getTileEntity(pos);
		if(te instanceof WaterCentrifugeTileEntity && ((WaterCentrifugeTileEntity) te).isNeg()){
			return 1;
		}
		return 0;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, @Nullable IBlockReader world, List<ITextComponent> tooltip, ITooltipFlag advanced){
		tooltip.add("Converts water into distilled water and salt or dirty water into distilled water and byproduct");
		tooltip.add("The spin direction needs to keep changing to operate");
		tooltip.add("I: 115");
		tooltip.add("Produces LoL players");
	}
}
