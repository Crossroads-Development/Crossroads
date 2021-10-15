package com.Da_Technomancer.crossroads.blocks.technomancy;

import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.API.technomancy.FluxUtil;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.tileentities.technomancy.BeaconHarnessTileEntity;
import com.Da_Technomancer.essentials.tileentities.ITickableTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
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
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fmllegacy.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.List;

public class BeaconHarness extends BaseEntityBlock{

	private static final VoxelShape SHAPE = Shapes.or(box(0, 15, 0, 16, 16, 16), box(2, 1, 2, 14, 15, 14));

	public BeaconHarness(){
		super(CRBlocks.getGlassProperty().lightLevel(state -> 15));
		setRegistryName("beacon_harness");
		CRBlocks.toRegister.add(this);
		CRBlocks.blockAddQue(this);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state){
		return new BeaconHarnessTileEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> type){
		return ITickableTileEntity.createTicker(type, BeaconHarnessTileEntity.TYPE);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context){
		return SHAPE;
	}

	@Override
	public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving){
		BlockEntity te;
		if(worldIn.hasNeighborSignal(pos) && (te = worldIn.getBlockEntity(pos)) instanceof BeaconHarnessTileEntity){
			((BeaconHarnessTileEntity) te).trigger();
		}
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, @Nullable BlockGetter world, List<Component> tooltip, TooltipFlag advanced){
		tooltip.add(new TranslatableComponent("tt.crossroads.beacon_harness.desc"));
		tooltip.add(new TranslatableComponent("tt.crossroads.beacon_harness.buffer"));
		tooltip.add(new TranslatableComponent("tt.crossroads.beacon_harness.flux", BeaconHarnessTileEntity.FLUX_GEN));
		tooltip.add(new TranslatableComponent("tt.crossroads.beacon_harness.quip").setStyle(MiscUtil.TT_QUIP));
	}

	@Override
	public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player playerIn, InteractionHand hand, BlockHitResult hit){
		ItemStack heldItem = playerIn.getItemInHand(hand);
		if(FluxUtil.handleFluxLinking(worldIn, pos, playerIn.getItemInHand(hand), playerIn).shouldSwing()){
			return InteractionResult.SUCCESS;
		}else if(!worldIn.isClientSide){
			BlockEntity te = worldIn.getBlockEntity(pos);
			if(te instanceof MenuProvider){
				NetworkHooks.openGui((ServerPlayer) playerIn, (MenuProvider) te, pos);
			}
		}
		return InteractionResult.PASS;
	}

	@Override
	public RenderShape getRenderShape(BlockState state){
		return RenderShape.MODEL;
	}
}
