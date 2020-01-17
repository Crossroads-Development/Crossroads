package com.Da_Technomancer.crossroads.blocks.alchemy;

import com.Da_Technomancer.crossroads.API.CRProperties;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.tileentities.alchemy.ChargingStandTileEntity;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class ChargingStand extends ContainerBlock{

	private static final VoxelShape SHAPE = VoxelShapes.or(makeCuboidShape(0, 0, 0, 16, 2, 16), makeCuboidShape(0, 14, 0, 16, 16, 16), makeCuboidShape(5, 2, 0, 11, 14, 1), makeCuboidShape(5, 2, 15, 11, 14,16), makeCuboidShape(0, 2, 5, 1, 4, 11), makeCuboidShape(15, 2, 5, 16, 14, 11));

	public ChargingStand(){
		super(Properties.create(Material.IRON).sound(SoundType.METAL).hardnessAndResistance(2));
		String name = "charging_stand";
		setRegistryName(name);
		CRBlocks.toRegister.add(this);
		CRBlocks.blockAddQue(this);
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn){
		return new ChargingStandTileEntity();
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context){
		return SHAPE;
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
	public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving){
		TileEntity te = world.getTileEntity(pos);
		if(te instanceof ChargingStandTileEntity){
			((ChargingStandTileEntity) te).onBlockDestroyed(state);
		}
		super.onReplaced(state, world, pos, newState, isMoving);
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder){
		builder.add(CRProperties.CRYSTAL, CRProperties.CONTAINER_TYPE);
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

	//TODO redo model

	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, @Nullable IBlockReader world, List<ITextComponent> tooltip, ITooltipFlag advanced){
		tooltip.add(new TranslationTextComponent("tt.crossroads.charging_stand.desc"));
		tooltip.add(new TranslationTextComponent("tt.crossroads.charging_stand.power", ChargingStandTileEntity.DRAIN));
	}
}
