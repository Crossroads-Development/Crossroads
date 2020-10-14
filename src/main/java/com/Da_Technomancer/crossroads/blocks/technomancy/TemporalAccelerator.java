package com.Da_Technomancer.crossroads.blocks.technomancy;

import com.Da_Technomancer.crossroads.API.CRProperties;
import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.API.technomancy.FluxUtil;
import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.tileentities.technomancy.TemporalAcceleratorTileEntity;
import com.Da_Technomancer.essentials.ESConfig;
import com.Da_Technomancer.essentials.blocks.ESProperties;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.*;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;

public class TemporalAccelerator extends ContainerBlock{

	private static final VoxelShape[] SHAPES = new VoxelShape[6];
	static{
		SHAPES[0] = VoxelShapes.or(makeCuboidShape(0, 0, 0, 16, 4, 16), makeCuboidShape(4, 4, 4, 12, 8, 12));
		SHAPES[1] = VoxelShapes.or(makeCuboidShape(0, 12, 0, 16, 16, 16), makeCuboidShape(4, 8, 4, 12, 12, 12));
		SHAPES[2] = VoxelShapes.or(makeCuboidShape(0, 0, 0, 16, 16, 4), makeCuboidShape(4, 4, 4, 12, 12, 8));
		SHAPES[3] = VoxelShapes.or(makeCuboidShape(0, 0, 12, 16, 16, 16), makeCuboidShape(4, 4, 8, 12, 12, 12));
		SHAPES[4] = VoxelShapes.or(makeCuboidShape(0, 0, 0, 4, 16, 16), makeCuboidShape(4, 4, 4, 8, 12, 12));
		SHAPES[5] = VoxelShapes.or(makeCuboidShape(12, 0, 0, 16, 16, 16), makeCuboidShape(8, 4, 4, 12, 12, 12));
	}

	public TemporalAccelerator(){
		super(CRBlocks.METAL_PROPERTY);
		String name = "temporal_accelerator";
		setRegistryName(name);
		CRBlocks.toRegister.add(this);
		CRBlocks.blockAddQue(this);
		setDefaultState(getDefaultState().with(CRProperties.ACCELERATOR_TARGET, Mode.BOTH));
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder){
		builder.add(ESProperties.FACING, CRProperties.ACCELERATOR_TARGET);
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context){
		return SHAPES[state.get(ESProperties.FACING).getIndex()];
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context){
		return getDefaultState().with(ESProperties.FACING, context.getNearestLookingDirection().getOpposite());
	}

	@Override
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity playerIn, Hand hand, BlockRayTraceResult hit){
		ItemStack held = playerIn.getHeldItem(hand);
		//Linking with a linking tool
		if(FluxUtil.handleFluxLinking(worldIn, pos, held, playerIn).isSuccess()){
			return ActionResultType.SUCCESS;
		}else if(ESConfig.isWrench(held)){
			if(playerIn.isSneaking()){
				//Sneak clicking- change mode
				TileEntity te = worldIn.getTileEntity(pos);
				state = state.func_235896_a_(CRProperties.ACCELERATOR_TARGET);
				worldIn.setBlockState(pos, state);
				if(te instanceof TemporalAcceleratorTileEntity){
					((TemporalAcceleratorTileEntity) te).resetCache();
				}
				if(worldIn.isRemote){
					Mode newMode = state.get(CRProperties.ACCELERATOR_TARGET);
					MiscUtil.chatMessage(playerIn, new TranslationTextComponent("tt.crossroads.time_accel.new_mode", MiscUtil.localize(newMode.getLocalizationName())));
					if(!CRConfig.teTimeAccel.get() && newMode.accelerateTileEntities){
						MiscUtil.chatMessage(playerIn, new TranslationTextComponent("tt.crossroads.time_accel.config").setStyle(Style.EMPTY.applyFormatting(TextFormatting.RED)));
					}
				}
			}else{
				//Rotate this machine
				TileEntity te = worldIn.getTileEntity(pos);
				worldIn.setBlockState(pos, state.func_235896_a_(ESProperties.FACING));
				if(te instanceof TemporalAcceleratorTileEntity){
					((TemporalAcceleratorTileEntity) te).resetCache();
				}
			}
			return ActionResultType.SUCCESS;
		}
		return ActionResultType.PASS;
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn){
		return new TemporalAcceleratorTileEntity();
	}

	@Override
	public BlockRenderType getRenderType(BlockState state){
		return BlockRenderType.MODEL;
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn){
		tooltip.add(new TranslationTextComponent("tt.crossroads.time_accel.desc", TemporalAcceleratorTileEntity.SIZE));
		tooltip.add(new TranslationTextComponent("tt.crossroads.time_accel.beam"));
		tooltip.add(new TranslationTextComponent("tt.crossroads.time_accel.wrench"));
		tooltip.add(new TranslationTextComponent("tt.crossroads.time_accel.flux"));
	}

	public enum Mode implements IStringSerializable{

		ENTITIES(true, false, false),
		BLOCKS(false, true, true),
		BOTH(true, true, true);

		public final boolean accelerateEntities;
		public final boolean accelerateTileEntities;
		public final boolean accelerateBlockTicks;

		Mode(boolean entity, boolean te, boolean blockTicks){
			accelerateEntities = entity;
			accelerateTileEntities = te;
			accelerateBlockTicks = blockTicks;
		}

		@Override
		public String toString(){
			return name().toLowerCase(Locale.US);
		}

		@Override
		public String getString(){
			return toString();
		}

		public String getLocalizationName(){
			return "tt.crossroads.time_accel.mode." + toString();
		}
	}
}
