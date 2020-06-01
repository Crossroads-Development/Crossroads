package com.Da_Technomancer.crossroads.blocks.alchemy;

import com.Da_Technomancer.crossroads.API.CRProperties;
import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.alchemy.EnumContainerType;
import com.Da_Technomancer.crossroads.API.alchemy.IChemicalHandler;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.tileentities.alchemy.ReagentPumpTileEntity;
import com.Da_Technomancer.essentials.ESConfig;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;

public class ReagentPump extends ContainerBlock{

	private static final double SIZE = 5D;
	private static final double CORE_SIZE = 4D;
	
	protected static final VoxelShape[] SHAPES = new VoxelShape[16];
	static{
		final double sizeN = 16D - SIZE;
		//There are 16 (2^4) possible shapes for this block
		VoxelShape core = makeCuboidShape(CORE_SIZE, 0, CORE_SIZE, 16 - CORE_SIZE, 16, 16 - CORE_SIZE);
		VoxelShape[] pieces = new VoxelShape[4];
		pieces[0] = makeCuboidShape(SIZE, SIZE, 0, sizeN, sizeN, SIZE);
		pieces[1] = makeCuboidShape(SIZE, SIZE, 16, sizeN, sizeN, sizeN);
		pieces[2] = makeCuboidShape(0, SIZE, SIZE, SIZE, sizeN, sizeN);
		pieces[3] = makeCuboidShape(16, SIZE, SIZE, sizeN, sizeN, sizeN);
		for(int i = 0; i < 16; i++){
			VoxelShape comp = core;
			for(int j = 0; j < 4; j++){
				if((i & (1 << j)) != 0){
					comp = VoxelShapes.or(comp, pieces[j]);
				}
			}
			SHAPES[i] = comp;
		}
	}

	private final boolean crystal;

	public ReagentPump(boolean crystal){
		super(Properties.create(Material.GLASS).sound(SoundType.GLASS).hardnessAndResistance(0.5F));
		this.crystal = crystal;
		String name = (crystal ? "crystal_" : "") + "reagent_pump";
		setRegistryName(name);
		CRBlocks.toRegister.add(this);
		CRBlocks.blockAddQue(this);
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn){
		return new ReagentPumpTileEntity(!crystal);
	}

	@Override
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity playerIn, Hand hand, BlockRayTraceResult hit){
		if(ESConfig.isWrench(playerIn.getHeldItem(hand))){
			if(!worldIn.isRemote){
				worldIn.setBlockState(pos, state.cycle(CRProperties.ACTIVE));
			}
			return ActionResultType.SUCCESS;
		}
		return ActionResultType.PASS;
	}

	@Override
	public BlockRenderType getRenderType(BlockState state){
		return BlockRenderType.MODEL;
	}

//	@Override
//	@OnlyIn(Dist.CLIENT)
//	public BlockRenderLayer getRenderLayer(){
//		return BlockRenderLayer.TRANSLUCENT;
//	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder){
		builder.add(CRProperties.ACTIVE, CRProperties.HAS_MATCH_SIDES[2], CRProperties.HAS_MATCH_SIDES[3], CRProperties.HAS_MATCH_SIDES[4], CRProperties.HAS_MATCH_SIDES[5]);
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context){
		int index = 0;
		for(int i = 2; i < 5; i++){
			index |= state.get(CRProperties.HAS_MATCH_SIDES[i]) ? 1 << i - 2 : 0;
		}
		return SHAPES[index];
	}


	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context){
		boolean[] connect = new boolean[6];
		EnumContainerType contType = crystal ? EnumContainerType.CRYSTAL : EnumContainerType.GLASS;
		for(int i = 2; i < 6; i++){
			TileEntity te = context.getWorld().getTileEntity(context.getPos().offset(Direction.byIndex(i)));
			LazyOptional<IChemicalHandler> otherOpt;
			if(te != null && (otherOpt = te.getCapability(Capabilities.CHEMICAL_CAPABILITY, Direction.byIndex(i).getOpposite())).isPresent() && otherOpt.orElseThrow(NullPointerException::new).getChannel(Direction.byIndex(i)).connectsWith(contType)){
				connect[i] = true;
			}
		}
		return getDefaultState().with(CRProperties.HAS_MATCH_SIDES[2], connect[2]).with(CRProperties.HAS_MATCH_SIDES[3], connect[3]).with(CRProperties.HAS_MATCH_SIDES[4], connect[4]).with(CRProperties.HAS_MATCH_SIDES[5], connect[5]);
	}

	@Override
	public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos pos, BlockPos facingPos){
		TileEntity te = worldIn.getTileEntity(facingPos);
		TileEntity thisTE = worldIn.getTileEntity(pos);
		LazyOptional<IChemicalHandler> otherOpt;
		boolean connect = thisTE instanceof ReagentPumpTileEntity && te != null && (otherOpt = te.getCapability(Capabilities.CHEMICAL_CAPABILITY, facing.getOpposite())).isPresent() && otherOpt.orElseThrow(NullPointerException::new).getChannel(facing).connectsWith(crystal ? EnumContainerType.CRYSTAL : EnumContainerType.GLASS);
		if(facing.getAxis() != Direction.Axis.Y){
			BooleanProperty prop = CRProperties.HAS_MATCH_SIDES[facing.getIndex()];
			return stateIn.with(prop, connect);
		}else{
			return stateIn;
		}
	}
}
