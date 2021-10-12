package com.Da_Technomancer.crossroads.blocks.alchemy;

import com.Da_Technomancer.crossroads.API.CRProperties;
import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.tileentities.alchemy.ReagentFilterTileEntity;
import com.Da_Technomancer.essentials.ESConfig;
import com.Da_Technomancer.essentials.tileentities.ITickableTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.fmllegacy.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.List;

public class ReagentFilter extends BaseEntityBlock{

	private static final VoxelShape SHAPE = box(2, 0, 2, 14, 16, 14);
	private final boolean crystal;

	public ReagentFilter(boolean crystal){
		super(CRBlocks.getGlassProperty());
		this.crystal = crystal;
		String name = (crystal ? "crystal_" : "") + "reagent_filter";
		setRegistryName(name);
		CRBlocks.toRegister.add(this);
		CRBlocks.blockAddQue(this);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder){
		builder.add(CRProperties.HORIZ_FACING);
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context){
		return defaultBlockState().setValue(CRProperties.HORIZ_FACING, context.getHorizontalDirection().getOpposite());
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state){
		return new ReagentFilterTileEntity(pos, state, crystal);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> type){
		return ITickableTileEntity.createTicker(type, ReagentFilterTileEntity.TYPE);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context){
		return SHAPE;
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable BlockGetter worldIn, List<Component> tooltip, TooltipFlag flagIn){
		tooltip.add(new TranslatableComponent("tt.crossroads.reagent_filter.desc"));
		tooltip.add(new TranslatableComponent("tt.crossroads.reagent_filter.filter"));
		tooltip.add(new TranslatableComponent("tt.crossroads.reagent_filter.quip").setStyle(MiscUtil.TT_QUIP));
	}

//	@Override
//	public BlockRenderLayer getRenderLayer(){
//		return BlockRenderLayer.CUTOUT;
//	}

	@Override
	public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player playerIn, InteractionHand hand, BlockHitResult hit){
		if(!worldIn.isClientSide){
			BlockEntity te = worldIn.getBlockEntity(pos);
			if(ESConfig.isWrench(playerIn.getItemInHand(hand))){
				worldIn.setBlockAndUpdate(pos, state.cycle(CRProperties.HORIZ_FACING));
			}else{
				if(te instanceof MenuProvider){
					NetworkHooks.openGui((ServerPlayer) playerIn, (MenuProvider) te, pos);
				}
			}
		}
		return InteractionResult.SUCCESS;
	}

	@Override
	public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving){
		BlockEntity te = worldIn.getBlockEntity(pos);
		if(te instanceof ReagentFilterTileEntity){
			Containers.dropContents(worldIn, pos, (ReagentFilterTileEntity) te);
		}
		super.onRemove(state, worldIn, pos, newState, isMoving);
	}

	@Override
	public RenderShape getRenderShape(BlockState state){
		return RenderShape.MODEL;
	}
}
