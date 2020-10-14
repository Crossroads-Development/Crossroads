package com.Da_Technomancer.crossroads.blocks.alchemy;

import com.Da_Technomancer.crossroads.API.CRProperties;
import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.tileentities.alchemy.ReagentFilterTileEntity;
import com.Da_Technomancer.essentials.ESConfig;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.List;

public class ReagentFilter extends ContainerBlock{

	private static final VoxelShape SHAPE = makeCuboidShape(2, 2, 2, 14, 14, 14);
	private final boolean crystal;

	public ReagentFilter(boolean crystal){
		super(CRBlocks.GLASS_PROPERTY);
		this.crystal = crystal;
		String name = (crystal ? "crystal_" : "") + "reagent_filter";
		setRegistryName(name);
		CRBlocks.toRegister.add(this);
		CRBlocks.blockAddQue(this);
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder){
		builder.add(CRProperties.HORIZ_FACING);
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context){
		return getDefaultState().with(CRProperties.HORIZ_FACING, context.getPlacementHorizontalFacing().getOpposite());
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn){
		return new ReagentFilterTileEntity(crystal);
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context){
		return SHAPE;
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn){
		tooltip.add(new TranslationTextComponent("tt.crossroads.reagent_filter.desc"));
		tooltip.add(new TranslationTextComponent("tt.crossroads.reagent_filter.filter"));
		tooltip.add(new TranslationTextComponent("tt.crossroads.reagent_filter.quip").setStyle(MiscUtil.TT_QUIP));
	}

//	@Override
//	public BlockRenderLayer getRenderLayer(){
//		return BlockRenderLayer.CUTOUT;
//	}

	@Override
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity playerIn, Hand hand, BlockRayTraceResult hit){
		if(!worldIn.isRemote){
			TileEntity te = worldIn.getTileEntity(pos);
			if(ESConfig.isWrench(playerIn.getHeldItem(hand))){
				worldIn.setBlockState(pos, state.func_235896_a_(CRProperties.HORIZ_FACING));
				if(te instanceof ReagentFilterTileEntity){
					((ReagentFilterTileEntity) te).clearCache();
				}
			}else{
				if(te instanceof INamedContainerProvider){
					NetworkHooks.openGui((ServerPlayerEntity) playerIn, (INamedContainerProvider) te, pos);
				}
			}
		}
		return ActionResultType.SUCCESS;
	}

	@Override
	public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving){
		TileEntity te = worldIn.getTileEntity(pos);
		if(te instanceof ReagentFilterTileEntity){
			InventoryHelper.dropInventoryItems(worldIn, pos, (ReagentFilterTileEntity) te);
		}
		super.onReplaced(state, worldIn, pos, newState, isMoving);
	}

	@Override
	public BlockRenderType getRenderType(BlockState state){
		return BlockRenderType.MODEL;
	}
}
