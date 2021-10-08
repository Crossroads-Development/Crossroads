package com.Da_Technomancer.crossroads.blocks.technomancy;

import com.Da_Technomancer.crossroads.API.CircuitUtil;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.tileentities.technomancy.SequenceBoxTileEntity;
import com.Da_Technomancer.essentials.blocks.redstone.IWireConnect;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.List;

public class SequenceBox extends BaseEntityBlock implements IWireConnect{

	private static final VoxelShape SHAPE = box(0, 0, 0, 16, 8, 16);

	public SequenceBox(){
		super(CRBlocks.getMetalProperty());
		setRegistryName("sequence_box");
		CRBlocks.toRegister.add(this);
		CRBlocks.blockAddQue(this);
	}

	@Override
	public RenderShape getRenderShape(BlockState state){
		return RenderShape.MODEL;
	}

	@Override
	public boolean canConnect(Direction direction, BlockState blockState){
		return true;
	}

	@Override
	public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
		super.neighborChanged(state, worldIn, pos, blockIn, fromPos, isMoving);
		BlockEntity te = worldIn.getBlockEntity(pos);
		if(!worldIn.isClientSide && te instanceof SequenceBoxTileEntity){
			((SequenceBoxTileEntity) te).worldUpdate(blockIn);
		}
	}

	@Override
	public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		BlockEntity te = worldIn.getBlockEntity(pos);
		if (te instanceof SequenceBoxTileEntity) {
			CircuitUtil.updateFromWorld(((SequenceBoxTileEntity) te).circHandler, this);
		}
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockGetter worldIn){
		return new SequenceBoxTileEntity();
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable BlockGetter worldIn, List<Component> tooltip, TooltipFlag flagIn){
		tooltip.add(new TranslatableComponent("tt.crossroads.sequence_box.desc"));
		tooltip.add(new TranslatableComponent("tt.crossroads.sequence_box.trigger"));
	}

	@Override
	public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit){
		BlockEntity te;
		if(!worldIn.isClientSide && (te = worldIn.getBlockEntity(pos)) instanceof SequenceBoxTileEntity){
			SequenceBoxTileEntity cte = (SequenceBoxTileEntity) te;
			NetworkHooks.openGui((ServerPlayer) player, cte, cte::encodeBuf);
		}

		return InteractionResult.SUCCESS;
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context){
		return SHAPE;
	}
}
