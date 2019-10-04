package com.Da_Technomancer.crossroads.blocks.alchemy;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.blocks.CrossroadsBlocks;
import com.Da_Technomancer.crossroads.items.CrossroadsItems;
import com.Da_Technomancer.crossroads.tileentities.alchemy.ChargingStandTileEntity;
import net.minecraft.block.*;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.block.BlockRenderType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class ChargingStand extends ContainerBlock{

	public ChargingStand(){
		super(Material.IRON);
		String name = "charging_stand";
		setTranslationKey(name);
		setRegistryName(name);
		setHardness(2);
		setCreativeTab(CrossroadsItems.TAB_CROSSROADS);
		setSoundType(SoundType.METAL);
		CrossroadsBlocks.toRegister.add(this);
		CrossroadsBlocks.blockAddQue(this);
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn){
		return new ChargingStandTileEntity();
	}

	@Override
	public BlockRenderType getRenderType(BlockState state){
		return BlockRenderType.MODEL;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public BlockRenderLayer getRenderLayer(){
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public boolean isOpaqueCube(BlockState state){
		return false;
	}

	@Override
	public boolean isFullCube(BlockState state){
		return false;
	}

	@Override
	public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving){
		TileEntity te = world.getTileEntity(pos);
		if(te instanceof ChargingStandTileEntity){
			((ChargingStandTileEntity) te).onBlockDestroyed(blockstate);
		}
		super.onReplaced(state, world, pos, newState, isMoving);
	}

	@Override
	public int getMetaFromState(BlockState state){
		return (state.get(Properties.CRYSTAL) ? 1 : 0) + (state.get(Properties.ACTIVE) ? 2 : 0) + (state.get(Properties.CONTAINER_TYPE) ? 4 : 0);
	}

	@Override
	public BlockState getStateFromMeta(int meta){
		return getDefaultState().with(Properties.CRYSTAL, (meta & 1) == 1).with(Properties.ACTIVE, (meta & 2) == 2).with(Properties.CONTAINER_TYPE, (meta & 4) == 4);
	}

	@Override
	protected BlockStateContainer createBlockState(){
		return new BlockStateContainer(this, Properties.CRYSTAL, Properties.ACTIVE, Properties.CONTAINER_TYPE);
	}

	@Override
	public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity playerIn, Hand hand, BlockRayTraceResult hit){
		if(!worldIn.isRemote){
			TileEntity te = worldIn.getTileEntity(pos);
			if(te instanceof ChargingStandTileEntity){
				playerIn.setHeldItem(hand, ((ChargingStandTileEntity) te).rightClickWithItem(playerIn.getHeldItem(hand), playerIn.isSneaking(), playerIn, hand));
			}
		}
		return true;
	}

	@OnlyIn(Dist.CLIENT)
	public void initModel(){
		StateMapperBase glasswareMapper = new StateMapperBase(){
			@Override
			protected ModelResourceLocation getModelResourceLocation(BlockState state){
				if(state.get(Properties.CONTAINER_TYPE)){
					return new ModelResourceLocation(Crossroads.MODID + ":charging_stand_florence", getPropertyString(state.getProperties()));
				}else{
					return new ModelResourceLocation(Crossroads.MODID + ":charging_stand_phial", getPropertyString(state.getProperties()));
				}
			}
		};
		ModelLoader.setCustomStateMapper(this, glasswareMapper);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, @Nullable IBlockReader world, List<ITextComponent> tooltip, ITooltipFlag advanced){
		tooltip.add("Consumes: -10FE/t");
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, BlockState state, BlockPos pos, Direction face){
		return face.getAxis() == Direction.Axis.Y ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
	}
}
