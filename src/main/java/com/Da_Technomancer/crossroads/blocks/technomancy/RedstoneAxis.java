package com.Da_Technomancer.crossroads.blocks.technomancy;

import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.tileentities.technomancy.RedstoneAxisTileEntity;
import com.Da_Technomancer.essentials.EssentialsConfig;
import com.Da_Technomancer.essentials.blocks.EssentialsProperties;
import com.Da_Technomancer.essentials.blocks.redstone.RedstoneUtil;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class RedstoneAxis extends ContainerBlock{
	
	public RedstoneAxis(){
		super(Properties.create(Material.IRON).sound(SoundType.METAL).hardnessAndResistance(3));
		String name = "redstone_axis";
		setRegistryName(name);
		CRBlocks.toRegister.add(this);
		CRBlocks.blockAddQue(this);
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable IBlockReader reader, List<ITextComponent> tooltip, ITooltipFlag flag){
		tooltip.add(new TranslationTextComponent("tt.crossroads.redstone_axis.desc"));
		tooltip.add(new TranslationTextComponent("tt.crossroads.redstone_axis.power"));
		tooltip.add(new TranslationTextComponent("tt.crossroads.redstone_axis.circuit"));
	}

	@Override
	public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving){
		TileEntity te = world.getTileEntity(pos);
		if(te instanceof RedstoneAxisTileEntity){
			((RedstoneAxisTileEntity) te).disconnect();
		}
		super.onReplaced(state, world, pos, newState, isMoving);
	}
	
	@Override
	public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity playerIn, Hand hand, BlockRayTraceResult hit){
		if(EssentialsConfig.isWrench(playerIn.getHeldItem(hand))){
			TileEntity te = worldIn.getTileEntity(pos);
			if(te instanceof RedstoneAxisTileEntity){
				((RedstoneAxisTileEntity) te).disconnect();
			}
			if(!worldIn.isRemote){
				worldIn.setBlockState(pos, state.cycle(EssentialsProperties.FACING));
			}
			return true;
		}
		return false;
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder){
		builder.add(EssentialsProperties.FACING);
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context){
		return getDefaultState().with(EssentialsProperties.FACING, context.getNearestLookingDirection());
	}

	@Override
	public boolean canConnectRedstone(BlockState state, IBlockReader world, BlockPos pos, @Nullable Direction side){
		return side != null && side.getAxis() != Direction.Axis.Y;
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn){
		return new RedstoneAxisTileEntity();

	}

	@Override
	public BlockRenderType getRenderType(BlockState state){
		return BlockRenderType.MODEL;
	}

	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving){
		TileEntity te = worldIn.getTileEntity(pos);

		if(te instanceof RedstoneAxisTileEntity){
			//Simple optimization- if the block update is just signal strength changing, we don't need to rebuild connections
			if(blockIn != Blocks.REDSTONE_WIRE && !(blockIn instanceof RedstoneDiodeBlock)){
				((RedstoneAxisTileEntity) te).buildConnections();
			}
			((RedstoneAxisTileEntity) te).setRedstone(RedstoneUtil.getRedstoneAtPos(worldIn, pos));
		}
	}
}
