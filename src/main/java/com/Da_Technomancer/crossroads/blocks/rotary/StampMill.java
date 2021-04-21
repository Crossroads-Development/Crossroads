package com.Da_Technomancer.crossroads.blocks.rotary;

import com.Da_Technomancer.crossroads.API.CRProperties;
import com.Da_Technomancer.crossroads.API.CircuitUtil;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.tileentities.rotary.StampMillTileEntity;
import com.Da_Technomancer.essentials.ESConfig;
import com.Da_Technomancer.essentials.blocks.redstone.IReadable;
import com.Da_Technomancer.essentials.blocks.redstone.RedstoneUtil;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
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

public class StampMill extends ContainerBlock implements IReadable{

	private static final VoxelShape[] SHAPES = new VoxelShape[2];

	static{
		VoxelShape base = box(0, 0, 0, 16, 4, 16);
		SHAPES[0] = VoxelShapes.or(base, box(0, 4, 3, 1, 16, 11), box(15, 4, 3, 16, 16, 11));
		SHAPES[1] = VoxelShapes.or(base, box(3, 4, 0, 11, 16, 1), box(3, 4, 15, 11, 16, 16));
	}

	public StampMill(){
		super(Properties.of(Material.WOOD).strength(1).sound(SoundType.METAL));
		String name = "stamp_mill";
		setRegistryName(name);
		CRBlocks.toRegister.add(this);
		CRBlocks.blockAddQue(this);
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context){
		return SHAPES[state.getValue(CRProperties.HORIZ_AXIS) == Direction.Axis.X ? 0 : 1];
	}

	@Override
	public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity playerIn, Hand hand, BlockRayTraceResult hit){
		TileEntity te;
		if(ESConfig.isWrench(playerIn.getItemInHand(hand))){
			if(!worldIn.isClientSide){
				worldIn.setBlockAndUpdate(pos, state.cycle(CRProperties.HORIZ_AXIS));
				BlockState upState = worldIn.getBlockState(pos.above());
				if(upState.getBlock() instanceof StampMillTop){
					worldIn.setBlockAndUpdate(pos.above(), upState.cycle(CRProperties.HORIZ_AXIS));
				}
			}
		}else if(!worldIn.isClientSide && (te = worldIn.getBlockEntity(pos)) instanceof INamedContainerProvider){
			NetworkHooks.openGui((ServerPlayerEntity) playerIn, (INamedContainerProvider) te, pos);
		}
		return ActionResultType.SUCCESS;
	}

	@Override
	public TileEntity newBlockEntity(IBlockReader worldIn){
		return new StampMillTileEntity();
	}

	@Override
	public BlockRenderType getRenderShape(BlockState state){
		return BlockRenderType.MODEL;
	}

	@Override
	public void onRemove(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving){
		InventoryHelper.dropContents(world, pos, (StampMillTileEntity) world.getBlockEntity(pos));
		super.onRemove(state, world, pos, newState, isMoving);
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context){
		return defaultBlockState().setValue(CRProperties.HORIZ_AXIS, context.getHorizontalDirection().getClockWise().getAxis());
	}

	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder){
		builder.add(CRProperties.HORIZ_AXIS);
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, @Nullable IBlockReader world, List<ITextComponent> tooltip, ITooltipFlag advanced){
		tooltip.add(new TranslationTextComponent("tt.crossroads.stamp_mill.desc", StampMillTileEntity.REQUIRED / StampMillTileEntity.TIME_LIMIT / StampMillTileEntity.PROGRESS_PER_RADIAN * 20));
		tooltip.add(new TranslationTextComponent("tt.crossroads.stamp_mill.power", StampMillTileEntity.PROGRESS_PER_RADIAN));
		tooltip.add(new TranslationTextComponent("tt.crossroads.boilerplate.inertia", StampMillTileEntity.INERTIA));
	}

	@Override
	public boolean canSurvive(BlockState state, IWorldReader worldIn, BlockPos pos){
		return super.canSurvive(state, worldIn, pos) && worldIn.getBlockState(pos.above()).isAir(worldIn, pos.above());
	}

	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving){
		if(!worldIn.isClientSide && !(worldIn.getBlockState(pos.relative(Direction.UP)).getBlock() instanceof StampMillTop)){
			worldIn.destroyBlock(pos, true);
		}
	}

	@Override
	public void setPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack){
		world.setBlockAndUpdate(pos.relative(Direction.UP), CRBlocks.stampMillTop.defaultBlockState().setValue(CRProperties.HORIZ_AXIS, state.getValue(CRProperties.HORIZ_AXIS)));
	}

	@Override
	public boolean hasAnalogOutputSignal(BlockState state){
		return true;
	}

	@Override
	public int getAnalogOutputSignal(BlockState state, World worldIn, BlockPos pos){
		return RedstoneUtil.clampToVanilla(read(worldIn, pos, state));
	}

	@Override
	public float read(World world, BlockPos pos, BlockState state){
		TileEntity te = world.getBlockEntity(pos);
		if(te instanceof IInventory){
			return CircuitUtil.getRedstoneFromSlots((IInventory) te, 0);
		}else{
			return 0;
		}
	}
}
