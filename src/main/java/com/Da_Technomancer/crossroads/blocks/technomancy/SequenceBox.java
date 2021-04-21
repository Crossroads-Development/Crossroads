package com.Da_Technomancer.crossroads.blocks.technomancy;

import com.Da_Technomancer.crossroads.API.CircuitUtil;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.tileentities.technomancy.SequenceBoxTileEntity;
import com.Da_Technomancer.essentials.blocks.redstone.IWireConnect;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
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

public class SequenceBox extends ContainerBlock implements IWireConnect{

	private static final VoxelShape SHAPE = box(0, 0, 0, 16, 8, 16);

	public SequenceBox(){
		super(CRBlocks.getMetalProperty());
		setRegistryName("sequence_box");
		CRBlocks.toRegister.add(this);
		CRBlocks.blockAddQue(this);
	}

	@Override
	public BlockRenderType getRenderShape(BlockState state){
		return BlockRenderType.MODEL;
	}

	@Override
	public boolean canConnect(Direction direction, BlockState blockState){
		return true;
	}

	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
		super.neighborChanged(state, worldIn, pos, blockIn, fromPos, isMoving);
		TileEntity te = worldIn.getBlockEntity(pos);
		if(!worldIn.isClientSide && te instanceof SequenceBoxTileEntity){
			((SequenceBoxTileEntity) te).worldUpdate(blockIn);
		}
	}

	@Override
	public void setPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		TileEntity te = worldIn.getBlockEntity(pos);
		if (te instanceof SequenceBoxTileEntity) {
			CircuitUtil.updateFromWorld(((SequenceBoxTileEntity) te).circHandler, this);
		}
	}

	@Nullable
	@Override
	public TileEntity newBlockEntity(IBlockReader worldIn){
		return new SequenceBoxTileEntity();
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn){
		tooltip.add(new TranslationTextComponent("tt.crossroads.sequence_box.desc"));
		tooltip.add(new TranslationTextComponent("tt.crossroads.sequence_box.trigger"));
	}

	@Override
	public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit){
		TileEntity te;
		if(!worldIn.isClientSide && (te = worldIn.getBlockEntity(pos)) instanceof SequenceBoxTileEntity){
			SequenceBoxTileEntity cte = (SequenceBoxTileEntity) te;
			NetworkHooks.openGui((ServerPlayerEntity) player, cte, cte::encodeBuf);
		}

		return ActionResultType.SUCCESS;
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context){
		return SHAPE;
	}
}
