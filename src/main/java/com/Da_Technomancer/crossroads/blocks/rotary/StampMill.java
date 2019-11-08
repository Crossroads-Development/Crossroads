package com.Da_Technomancer.crossroads.blocks.rotary;

import com.Da_Technomancer.crossroads.API.CRProperties;
import com.Da_Technomancer.crossroads.API.templates.ModuleTE;
import com.Da_Technomancer.crossroads.blocks.CrossroadsBlocks;
import com.Da_Technomancer.crossroads.tileentities.rotary.StampMillTileEntity;
import com.Da_Technomancer.essentials.EssentialsConfig;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
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

public class StampMill extends ContainerBlock{

	public StampMill(){
		super(Properties.create(Material.WOOD).hardnessAndResistance(1).sound(SoundType.METAL));
		String name = "stamp_mill";
		setRegistryName(name);
		CrossroadsBlocks.toRegister.add(this);
		CrossroadsBlocks.blockAddQue(this);
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context){
		//TODO
		return super.getShape(state, worldIn, pos, context);
	}

	@Override
	public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity playerIn, Hand hand, BlockRayTraceResult hit){
		TileEntity te;
		if(EssentialsConfig.isWrench(playerIn.getHeldItem(hand))){
			if(!worldIn.isRemote){
				worldIn.setBlockState(pos, state.cycle(CRProperties.HORIZ_AXIS));
				te = worldIn.getTileEntity(pos);
				if(te instanceof ModuleTE){
					((ModuleTE) te).rotate();
				}
			}
		}else if(!worldIn.isRemote && (te = worldIn.getTileEntity(pos)) instanceof INamedContainerProvider){
			NetworkHooks.openGui((ServerPlayerEntity) playerIn, (INamedContainerProvider) te, pos);
		}
		return true;
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn){
		return new StampMillTileEntity();
	}

	@Override
	public BlockRenderType getRenderType(BlockState state){
		return BlockRenderType.MODEL;
	}

	@Override
	public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving){
		InventoryHelper.dropInventoryItems(world, pos, (StampMillTileEntity) world.getTileEntity(pos));
		super.onReplaced(state, world, pos, newState, isMoving);
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context){
		return getDefaultState().with(CRProperties.HORIZ_AXIS, context.getPlacementHorizontalFacing().rotateY().getAxis());
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder){
		builder.add(CRProperties.HORIZ_AXIS);
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, @Nullable IBlockReader world, List<ITextComponent> tooltip, ITooltipFlag advanced){
		tooltip.add(new TranslationTextComponent("tt.crossroads.stamp_mill.desc", StampMillTileEntity.REQUIRED / StampMillTileEntity.TIME_LIMIT / StampMillTileEntity.PROGRESS_PER_RADIAN * 20));
		tooltip.add(new TranslationTextComponent("tt.crossroads.stamp_mill.power", StampMillTileEntity.PROGRESS_PER_RADIAN));
		tooltip.add(new TranslationTextComponent("tt.crossroads.boilerplate.inertia", StampMillTileEntity.INERTIA));
	}

	@Override
	public boolean canPlaceBlockAt(World worldIn, BlockPos pos){
		return super.canPlaceBlockAt(worldIn, pos) && super.canPlaceBlockAt(worldIn, pos.offset(Direction.UP));
	}

	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving){
		if(!worldIn.isRemote && !(worldIn.getBlockState(pos.offset(Direction.UP)).getBlock() instanceof StampMillTop)){
			worldIn.destroyBlock(pos, true);
		}
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack){
		world.setBlockState(pos.offset(Direction.UP), CrossroadsBlocks.stampMillTop.getDefaultState().with(CRProperties.HORIZ_AXIS, state.get(CRProperties.HORIZ_AXIS)));
	}
}
