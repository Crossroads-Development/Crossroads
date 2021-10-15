package com.Da_Technomancer.crossroads.blocks.technomancy;

import com.Da_Technomancer.crossroads.API.technomancy.FluxUtil;
import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.tileentities.technomancy.ChronoHarnessTileEntity;
import com.Da_Technomancer.essentials.blocks.ESProperties;
import com.Da_Technomancer.essentials.tileentities.ITickableTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
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
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class ChronoHarness extends BaseEntityBlock{

	private static final VoxelShape SHAPE = Shapes.or(box(0, 0, 0, 16, 2, 16), box(0, 14, 0, 16, 16, 16), box(4, 2, 4, 12, 14, 12));

	public ChronoHarness(){
		super(CRBlocks.getMetalProperty());
		String name = "chrono_harness";
		setRegistryName(name);
		CRBlocks.toRegister.add(this);
		CRBlocks.blockAddQue(this);
		registerDefaultState(defaultBlockState().setValue(ESProperties.REDSTONE_BOOL, false));
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state){
		return new ChronoHarnessTileEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> type){
		return ITickableTileEntity.createTicker(type, ChronoHarnessTileEntity.TYPE);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder){
		builder.add(ESProperties.REDSTONE_BOOL);
	}

	@Override
	public RenderShape getRenderShape(BlockState state){
		return RenderShape.MODEL;
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context){
		return SHAPE;
	}

	@Override
	public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving){
		if(worldIn.hasNeighborSignal(pos)){
			if(!state.getValue(ESProperties.REDSTONE_BOOL)){
				worldIn.setBlock(pos, state.setValue(ESProperties.REDSTONE_BOOL, true), 2);
			}
		}else if(state.getValue(ESProperties.REDSTONE_BOOL)){
			worldIn.setBlock(pos, state.setValue(ESProperties.REDSTONE_BOOL, false), 2);
		}
	}

	@Override
	public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack){
		neighborChanged(state, worldIn, pos, this, pos, false);
	}

	@Override
	public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player playerIn, InteractionHand hand, BlockHitResult hit){
		return FluxUtil.handleFluxLinking(worldIn, pos, playerIn.getItemInHand(hand), playerIn);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, @Nullable BlockGetter world, List<Component> tooltip, TooltipFlag advanced){
		tooltip.add(new TranslatableComponent("tt.crossroads.chrono_harness.desc"));
		tooltip.add(new TranslatableComponent("tt.crossroads.chrono_harness.power", 64 / FluxUtil.FLUX_TIME * CRConfig.fePerEntropy.get(), CRConfig.fePerEntropy.get()));
		tooltip.add(new TranslatableComponent("tt.crossroads.chrono_harness.reds"));
	}
}
