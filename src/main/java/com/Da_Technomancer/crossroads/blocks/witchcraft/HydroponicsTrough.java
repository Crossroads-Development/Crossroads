package com.Da_Technomancer.crossroads.blocks.witchcraft;

import com.Da_Technomancer.crossroads.API.CRProperties;
import com.Da_Technomancer.crossroads.API.CircuitUtil;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.tileentities.witchcraft.HydroponicsTroughTileEntity;
import com.Da_Technomancer.essentials.blocks.redstone.IReadable;
import com.Da_Technomancer.essentials.blocks.redstone.RedstoneUtil;
import net.minecraft.block.*;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;

public class HydroponicsTrough extends BaseEntityBlock implements IReadable, BonemealableBlock{

	private static final VoxelShape SHAPE = Shapes.join(box(0, 0, 0, 16, 12, 16), box(2, 2, 2, 14, 16, 14), BooleanOp.ONLY_FIRST);
	private static final VoxelShape SHAPE_OPEN = Shapes.join(box(0, 0, 0, 16, 12, 16), box(2, 0, 2, 14, 16, 14), BooleanOp.ONLY_FIRST);

	public HydroponicsTrough(){
		super(CRBlocks.getMetalProperty().randomTicks());
		String name = "hydroponics_trough";
		setRegistryName(name);
		CRBlocks.toRegister.add(this);
		CRBlocks.blockAddQue(this);
		registerDefaultState(defaultBlockState().setValue(CRProperties.FULLNESS, 0).setValue(CRProperties.SOLID_FULLNESS, 0));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> container){
		container.add(CRProperties.FULLNESS, CRProperties.SOLID_FULLNESS);
	}

	@Override
	public BlockEntity newBlockEntity(BlockGetter worldIn){
		return new HydroponicsTroughTileEntity();
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context){
		return state.getValue(CRProperties.FULLNESS) == 3 ? SHAPE_OPEN : SHAPE;
	}

	@Override
	public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player playerIn, InteractionHand hand, BlockHitResult hit){
		BlockEntity te;
		if(!worldIn.isClientSide && (te = worldIn.getBlockEntity(pos)) instanceof MenuProvider){
			NetworkHooks.openGui((ServerPlayer) playerIn, (MenuProvider) te, pos);
		}
		return InteractionResult.SUCCESS;
	}

	@Override
	public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving){
		if(newState.getBlock() != this){
			Containers.dropContents(world, pos, (Container) world.getBlockEntity(pos));
		}
		super.onRemove(state, world, pos, newState, isMoving);
	}

	@Override
	public RenderShape getRenderShape(BlockState state){
		return RenderShape.MODEL;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, @Nullable BlockGetter world, List<Component> tooltip, TooltipFlag advanced){
		tooltip.add(new TranslatableComponent("tt.crossroads.hydroponic_trough.desc"));
		tooltip.add(new TranslatableComponent("tt.crossroads.hydroponic_trough.output"));
		tooltip.add(new TranslatableComponent("tt.crossroads.hydroponic_trough.drain", HydroponicsTroughTileEntity.SOLUTION_DRAIN));
		tooltip.add(new TranslatableComponent("tt.crossroads.hydroponic_trough.circuit"));
	}

	@Override
	public boolean hasAnalogOutputSignal(BlockState p_149740_1_){
		return true;
	}

	@Override
	public int getAnalogOutputSignal(BlockState state, Level world, BlockPos pos){
		return RedstoneUtil.clampToVanilla(read(world, pos, state));
	}

	@Override
	public float read(Level world, BlockPos pos, BlockState state){
		BlockEntity te = world.getBlockEntity(pos);
		if(te instanceof Container){
			return CircuitUtil.getRedstoneFromSlots((Container) te, 1, 2, 3, 4);
		}else{
			return 0;
		}
	}

	@Override
	public boolean isValidBonemealTarget(BlockGetter world, BlockPos pos, BlockState state, boolean p_176473_4_){
		BlockEntity te = world.getBlockEntity(pos);
		if(te instanceof HydroponicsTroughTileEntity){
			return ((HydroponicsTroughTileEntity) te).canBonemeal();
		}
		return false;
	}

	@Override
	public boolean isBonemealSuccess(Level world, Random rand, BlockPos pos, BlockState state){
		return true;
	}

	@Override
	public void performBonemeal(ServerLevel world, Random rand, BlockPos pos, BlockState state){
		int triggers = Mth.nextInt(rand, 2, 5);
		for(int i = 0; i < triggers; i++){
			randomTick(state, world, pos, world.getRandom());
		}
	}

	@Override
	public void randomTick(BlockState state, ServerLevel world, BlockPos pos, Random rand){
		BlockEntity te = world.getBlockEntity(pos);
		if(te instanceof HydroponicsTroughTileEntity){
			((HydroponicsTroughTileEntity) te).performGrowth();
		}
	}
}
