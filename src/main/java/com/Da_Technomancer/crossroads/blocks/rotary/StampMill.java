package com.Da_Technomancer.crossroads.blocks.rotary;

import com.Da_Technomancer.crossroads.API.CRProperties;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.tileentities.rotary.StampMillTileEntity;
import com.Da_Technomancer.essentials.ESConfig;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.List;

public class StampMill extends ContainerBlock{

	private static final VoxelShape[] SHAPES = new VoxelShape[2];

	static{
		VoxelShape base = makeCuboidShape(0, 0, 0, 16, 4, 16);
		SHAPES[0] = VoxelShapes.or(base, makeCuboidShape(0, 4, 3, 1, 16, 11), makeCuboidShape(15, 4, 3, 16, 16, 11));
		SHAPES[1] = VoxelShapes.or(base, makeCuboidShape(3, 4, 0, 11, 16, 1), makeCuboidShape(3, 4, 15, 11, 16, 16));
	}

	public StampMill(){
		super(Properties.create(Material.WOOD).hardnessAndResistance(1).sound(SoundType.METAL));
		String name = "stamp_mill";
		setRegistryName(name);
		CRBlocks.toRegister.add(this);
		CRBlocks.blockAddQue(this);
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context){
		return SHAPES[state.get(CRProperties.HORIZ_AXIS) == Direction.Axis.X ? 0 : 1];
	}

	@Override
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity playerIn, Hand hand, BlockRayTraceResult hit){
		TileEntity te;
		if(ESConfig.isWrench(playerIn.getHeldItem(hand))){
			if(!worldIn.isRemote){
				worldIn.setBlockState(pos, state.cycle(CRProperties.HORIZ_AXIS));
				BlockState upState = worldIn.getBlockState(pos.up());
				if(upState.getBlock() instanceof StampMillTop){
					worldIn.setBlockState(pos.up(), upState.cycle(CRProperties.HORIZ_AXIS));
				}
			}
		}else if(!worldIn.isRemote && (te = worldIn.getTileEntity(pos)) instanceof INamedContainerProvider){
			NetworkHooks.openGui((ServerPlayerEntity) playerIn, (INamedContainerProvider) te, pos);
		}
		return ActionResultType.SUCCESS;
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn){
		return new StampMillTileEntity();
	}

	@Override
	public BlockRenderType getRenderType(BlockState state){
		return BlockRenderType.MODEL;
	}

	@Override
	public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving){
		InventoryHelper.dropInventoryItems(world, pos, (StampMillTileEntity) world.getTileEntity(pos));
		super.onReplaced(state, world, pos, newState, isMoving);
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context){
		return getDefaultState().with(CRProperties.HORIZ_AXIS, context.getPlacementHorizontalFacing().rotateY().getAxis());
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder){
		builder.add(CRProperties.HORIZ_AXIS);
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, @Nullable IBlockReader world, List<ITextComponent> tooltip, ITooltipFlag advanced){
		tooltip.add(new TranslationTextComponent("tt.crossroads.stamp_mill.desc", StampMillTileEntity.REQUIRED / StampMillTileEntity.TIME_LIMIT / StampMillTileEntity.PROGRESS_PER_RADIAN * 20));
		tooltip.add(new TranslationTextComponent("tt.crossroads.stamp_mill.power", StampMillTileEntity.PROGRESS_PER_RADIAN));
		tooltip.add(new TranslationTextComponent("tt.crossroads.boilerplate.inertia", StampMillTileEntity.INERTIA));
	}

	@Override
	public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos){
		return super.isValidPosition(state, worldIn, pos) && worldIn.getBlockState(pos.up()).isAir(worldIn, pos.up());
	}

	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving){
		if(!worldIn.isRemote && !(worldIn.getBlockState(pos.offset(Direction.UP)).getBlock() instanceof StampMillTop)){
			worldIn.destroyBlock(pos, true);
		}
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack){
		world.setBlockState(pos.offset(Direction.UP), CRBlocks.stampMillTop.getDefaultState().with(CRProperties.HORIZ_AXIS, state.get(CRProperties.HORIZ_AXIS)));
	}
}
