package com.Da_Technomancer.crossroads.blocks.alchemy;

import com.Da_Technomancer.crossroads.blocks.CrossroadsBlocks;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.Da_Technomancer.crossroads.tileentities.alchemy.FlowLimiterTileEntity;
import com.Da_Technomancer.essentials.EssentialsConfig;
import com.Da_Technomancer.essentials.blocks.EssentialsProperties;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.block.BlockRenderType;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class FlowLimiter extends ContainerBlock{


	private static final AxisAlignedBB BB_X = new AxisAlignedBB(0, .25D, .25D, 1, .75D, .75D);
	private static final AxisAlignedBB BB_Y = new AxisAlignedBB(.25D, 0, .25D, .75D, 1, .75D);
	private static final AxisAlignedBB BB_Z = new AxisAlignedBB(.25D, .25D, 0, .75D, .75D, 1);

	private final boolean crystal;

	public FlowLimiter(boolean crystal){
		super(Material.GLASS);
		this.crystal = crystal;
		String name = (crystal ? "crystal_" : "") + "flow_limiter";
		setTranslationKey(name);
		setRegistryName(name);
		setHardness(.5F);
		setCreativeTab(CRItems.TAB_CROSSROADS);
		setSoundType(SoundType.GLASS);
		CrossroadsBlocks.toRegister.add(this);
		CrossroadsBlocks.blockAddQue(this);
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn){
		return new FlowLimiterTileEntity(!crystal);
	}

	@Override
	public BlockRenderType getRenderType(BlockState state){
		return BlockRenderType.MODEL;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public BlockRenderLayer getRenderLayer(){
		return BlockRenderLayer.TRANSLUCENT;
	}

	@Override
	public int getMetaFromState(BlockState state){
		return state.get(EssentialsProperties.FACING).getIndex();
	}

	@Override
	public BlockState getStateFromMeta(int meta){
		return getDefaultState().with(EssentialsProperties.FACING, Direction.byIndex(meta));
	}
	
	@Override
	public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity playerIn, Hand hand, BlockRayTraceResult hit){
		if(EssentialsConfig.isWrench(playerIn.getHeldItem(hand), worldIn.isRemote)){
			if(!worldIn.isRemote){
				if(playerIn.isSneaking()){
					TileEntity te = worldIn.getTileEntity(pos);
					
					if(te instanceof FlowLimiterTileEntity){
						((FlowLimiterTileEntity) te).cycleLimit((ServerPlayerEntity) playerIn);
					}
				}else{
					worldIn.setBlockState(pos, state.cycle(EssentialsProperties.FACING));
				}
			}
			return true;
		}
		return false;
	}

	@Override
	protected BlockStateContainer createBlockState(){
		return new BlockStateContainer(this, EssentialsProperties.FACING);
	}

	@Override
	public boolean isOpaqueCube(BlockState state){
		return false;
	}
	
	@Override
	public AxisAlignedBB getBoundingBox(BlockState state, IBlockAccess source, BlockPos pos){
		Axis axis = state.get(EssentialsProperties.FACING).getAxis();
		return axis == Axis.X ? BB_X : axis == Axis.Y ? BB_Y : BB_Z;
	}

	@Override
	public BlockState getStateForPlacement(World worldIn, BlockPos pos, Direction blockFaceClickedOn, BlockRayTraceResult hit, int meta, LivingEntity placer){
		return getStateFromMeta(meta).with(EssentialsProperties.FACING, (placer == null) ? Direction.NORTH : Direction.getDirectionFromEntityLiving(pos, placer));
	}
	
	@Override
	public boolean isFullCube(BlockState state){
		return false;
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, BlockState state, BlockPos pos, Direction face){
		return BlockFaceShape.UNDEFINED;
	}
}
