package com.Da_Technomancer.crossroads.blocks.witchcraft;

import com.Da_Technomancer.crossroads.API.CRProperties;
import com.Da_Technomancer.crossroads.API.CircuitUtil;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.tileentities.witchcraft.HydroponicsTroughTileEntity;
import com.Da_Technomancer.essentials.blocks.redstone.IReadable;
import com.Da_Technomancer.essentials.blocks.redstone.RedstoneUtil;
import net.minecraft.block.*;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class HydroponicsTrough extends ContainerBlock implements IReadable, IGrowable{

	private static final VoxelShape SHAPE = VoxelShapes.join(box(0, 0, 0, 16, 12, 16), box(2, 2, 2, 14, 16, 14), IBooleanFunction.ONLY_FIRST);
	private static final VoxelShape SHAPE_OPEN = VoxelShapes.join(box(0, 0, 0, 16, 12, 16), box(2, 0, 2, 14, 16, 14), IBooleanFunction.ONLY_FIRST);

	public HydroponicsTrough(){
		super(CRBlocks.getMetalProperty().randomTicks());
		String name = "hydroponics_trough";
		setRegistryName(name);
		CRBlocks.toRegister.add(this);
		CRBlocks.blockAddQue(this);
		registerDefaultState(defaultBlockState().setValue(CRProperties.FULLNESS, 0).setValue(CRProperties.SOLID_FULLNESS, 0));
	}

	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> container){
		container.add(CRProperties.FULLNESS, CRProperties.SOLID_FULLNESS);
	}

	@Override
	public TileEntity newBlockEntity(IBlockReader worldIn){
		return new HydroponicsTroughTileEntity();
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context){
		return state.getValue(CRProperties.FULLNESS) == 3 ? SHAPE_OPEN : SHAPE;
	}

	@Override
	public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity playerIn, Hand hand, BlockRayTraceResult hit){
		TileEntity te;
		if(!worldIn.isClientSide && (te = worldIn.getBlockEntity(pos)) instanceof INamedContainerProvider){
			NetworkHooks.openGui((ServerPlayerEntity) playerIn, (INamedContainerProvider) te, pos);
		}
		return ActionResultType.SUCCESS;
	}

	@Override
	public void onRemove(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving){
		if(newState.getBlock() != this){
			InventoryHelper.dropContents(world, pos, (IInventory) world.getBlockEntity(pos));
		}
		super.onRemove(state, world, pos, newState, isMoving);
	}

	@Override
	public BlockRenderType getRenderShape(BlockState state){
		return BlockRenderType.MODEL;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, @Nullable IBlockReader world, List<ITextComponent> tooltip, ITooltipFlag advanced){
		tooltip.add(new TranslationTextComponent("tt.crossroads.hydroponic_trough.desc"));
		tooltip.add(new TranslationTextComponent("tt.crossroads.hydroponic_trough.output"));
		tooltip.add(new TranslationTextComponent("tt.crossroads.hydroponic_trough.drain", HydroponicsTroughTileEntity.SOLUTION_DRAIN));
		tooltip.add(new TranslationTextComponent("tt.crossroads.hydroponic_trough.circuit"));
	}

	@Override
	public boolean hasAnalogOutputSignal(BlockState p_149740_1_){
		return true;
	}

	@Override
	public int getAnalogOutputSignal(BlockState state, World world, BlockPos pos){
		return RedstoneUtil.clampToVanilla(read(world, pos, state));
	}

	@Override
	public float read(World world, BlockPos pos, BlockState state){
		TileEntity te = world.getBlockEntity(pos);
		if(te instanceof IInventory){
			return CircuitUtil.getRedstoneFromSlots((IInventory) te, 1, 2, 3, 4);
		}else{
			return 0;
		}
	}

	@Override
	public boolean isValidBonemealTarget(IBlockReader world, BlockPos pos, BlockState state, boolean p_176473_4_){
		TileEntity te = world.getBlockEntity(pos);
		if(te instanceof HydroponicsTroughTileEntity){
			return ((HydroponicsTroughTileEntity) te).canBonemeal();
		}
		return false;
	}

	@Override
	public boolean isBonemealSuccess(World world, Random rand, BlockPos pos, BlockState state){
		return true;
	}

	@Override
	public void performBonemeal(ServerWorld world, Random rand, BlockPos pos, BlockState state){
		int triggers = MathHelper.nextInt(rand, 2, 5);
		for(int i = 0; i < triggers; i++){
			randomTick(state, world, pos, world.getRandom());
		}
	}

	@Override
	public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random rand){
		TileEntity te = world.getBlockEntity(pos);
		if(te instanceof HydroponicsTroughTileEntity){
			((HydroponicsTroughTileEntity) te).performGrowth();
		}
	}
}
