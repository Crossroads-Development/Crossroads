package com.Da_Technomancer.crossroads.blocks.rotary;

import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.api.CRProperties;
import com.Da_Technomancer.crossroads.api.MiscUtil;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.essentials.api.ConfigUtil;
import com.Da_Technomancer.essentials.api.ITickableTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
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
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.List;

public class SteamTurbine extends BaseEntityBlock{

	private static final VoxelShape SHAPE = Shapes.or(box(2, 0, 2, 14, 16, 14), box(0, 5, 5, 16, 11, 11), box(5, 5, 0, 11, 11, 16));

	public SteamTurbine(){
		super(CRBlocks.getMetalProperty());
		String name = "steam_turbine";
		CRBlocks.queueForRegister(name, this);
		registerDefaultState(defaultBlockState().setValue(CRProperties.ACTIVE, false));
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state){
		return new SteamTurbineTileEntity(pos, state);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder){
		builder.add(CRProperties.ACTIVE);//Used for rendering
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> type){
		return ITickableTileEntity.createTicker(type, SteamTurbineTileEntity.TYPE);
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
	public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player playerIn, InteractionHand hand, BlockHitResult hit){
		if(!worldIn.isClientSide && worldIn.getBlockEntity(pos) instanceof SteamTurbineTileEntity rte){
			if(ConfigUtil.isWrench(playerIn.getItemInHand(hand))){
				int mode = rte.cycleMode();
				MiscUtil.displayMessage(playerIn, Component.translatable("tt.crossroads.steam_turbine.setting", SteamTurbineTileEntity.TIERS[mode]));
			}else{
				NetworkHooks.openScreen((ServerPlayer) playerIn, rte, rte::encodeBuf);
			}
		}
		return InteractionResult.SUCCESS;
	}

//	@Override
//	@OnlyIn(Dist.CLIENT)
//	public BlockRenderLayer getRenderLayer(){
//		return BlockRenderLayer.CUTOUT;
//	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable BlockGetter world, List<Component> tooltip, TooltipFlag advanced){
		tooltip.add(Component.translatable("tt.crossroads.steam_turbine.desc"));
		tooltip.add(Component.translatable("tt.crossroads.steam_turbine.tier", 100 * (double) CRConfig.steamWorth.get() / 1000 * CRConfig.jouleWorth.get(), 100, SteamTurbineTileEntity.TIERS[SteamTurbineTileEntity.TIERS.length - 1]));
		tooltip.add(Component.translatable("tt.crossroads.boilerplate.inertia", SteamTurbineTileEntity.INERTIA));

	}
}
