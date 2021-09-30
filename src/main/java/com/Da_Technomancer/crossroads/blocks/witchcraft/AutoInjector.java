package com.Da_Technomancer.crossroads.blocks.witchcraft;

import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.tileentities.witchcraft.AutoInjectorTileEntity;
import com.Da_Technomancer.essentials.ESConfig;
import com.Da_Technomancer.essentials.blocks.ESProperties;
import com.Da_Technomancer.essentials.blocks.redstone.IReadable;
import com.Da_Technomancer.essentials.blocks.redstone.RedstoneUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.List;

public class AutoInjector extends ContainerBlock implements IReadable{

	private static final VoxelShape[] SHAPES = new VoxelShape[6];

	static{
		SHAPES[0] = VoxelShapes.or(box(0, 7, 0, 16, 16, 16), box(7.5, 0, 7.5, 8.5, 7, 8.5));
		SHAPES[1] = VoxelShapes.or(box(0, 0, 0, 16, 9, 16), box(7.5, 9, 7.5, 8.5, 16, 8.5));
		SHAPES[2] = VoxelShapes.or(box(0, 0, 7, 16, 16, 16), box(7.5, 7.5, 0, 8.5, 8.5, 7));
		SHAPES[3] = VoxelShapes.or(box(0, 0, 0, 16, 16, 9), box(7.5, 7.5, 9, 8.5, 8.5, 16));
		SHAPES[4] = VoxelShapes.or(box(7, 0, 0, 16, 16, 16), box(0, 7.5, 7.5, 7, 8.5, 8.5));
		SHAPES[5] = VoxelShapes.or(box(0, 0, 0, 9, 16, 16), box(9, 7.5, 7.5, 16, 8.5, 8.5));
	}

	public AutoInjector(){
		super(CRBlocks.getMetalProperty());
		String name = "auto_injector";
		setRegistryName(name);
		CRBlocks.toRegister.add(this);
		CRBlocks.blockAddQue(this);
	}

	@Override
	public TileEntity newBlockEntity(IBlockReader worldIn){
		return new AutoInjectorTileEntity();
	}

	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder){
		builder.add(ESProperties.FACING);
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context){
		return SHAPES[state.getValue(ESProperties.FACING).get3DDataValue()];
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context){
		return defaultBlockState().setValue(ESProperties.FACING, context.getNearestLookingDirection().getOpposite());
	}

	@Override
	public ActionResultType use(BlockState state, World worldIn, BlockPos pos, PlayerEntity playerIn, Hand hand, BlockRayTraceResult hit){
		TileEntity te;
		if(ESConfig.isWrench(playerIn.getItemInHand(hand))){
			if(playerIn.isShiftKeyDown()){
				//Sneak clicking- change setting
				if(!worldIn.isClientSide){
					te = worldIn.getBlockEntity(pos);
					if(te instanceof AutoInjectorTileEntity){
						int duration = ((AutoInjectorTileEntity) te).increaseSetting() / 20;//Converted from ticks to seconds
						MiscUtil.chatMessage(playerIn, new TranslationTextComponent("tt.crossroads.auto_injector.duration_setting_new", duration));
					}
				}
			}else{
				//Rotate this machine
				worldIn.setBlockAndUpdate(pos, state.cycle(ESProperties.FACING));
			}
		}else if(!worldIn.isClientSide && (te = worldIn.getBlockEntity(pos)) instanceof INamedContainerProvider){
			//Open the UI
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
		tooltip.add(new TranslationTextComponent("tt.crossroads.auto_injector.desc", CRConfig.injectionEfficiency.get()));
		tooltip.add(new TranslationTextComponent("tt.crossroads.auto_injector.dose"));
		tooltip.add(new TranslationTextComponent("tt.crossroads.auto_injector.redstone"));
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
		if(te instanceof AutoInjectorTileEntity){
			return ((AutoInjectorTileEntity) te).getDuration();
		}else{
			return 0;
		}
	}
}
