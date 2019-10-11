package com.Da_Technomancer.crossroads.blocks.rotary;

import com.Da_Technomancer.crossroads.blocks.CrossroadsBlocks;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.Da_Technomancer.crossroads.tileentities.rotary.RotaryDrillTileEntity;
import com.Da_Technomancer.essentials.EssentialsConfig;
import com.Da_Technomancer.essentials.blocks.EssentialsProperties;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class RotaryDrill extends ContainerBlock{

	private static final AxisAlignedBB X = new AxisAlignedBB(0, .375, .375, 1, .625, .625);
	private static final AxisAlignedBB Y = new AxisAlignedBB(.375, 0, .375, .625, 1, .625);
	private static final AxisAlignedBB Z = new AxisAlignedBB(.375, .375, 0, .625, .625, 1);

	private final boolean golden;

	public RotaryDrill(boolean golden){
		super(Material.IRON);
		this.golden = golden;
		String name = "rotary_drill" + (golden ? "_gold" : "");
		setTranslationKey(name);
		setRegistryName(name);
		setCreativeTab(CRItems.TAB_CROSSROADS);
		setHardness(3);
		setSoundType(SoundType.METAL);
		CrossroadsBlocks.toRegister.add(this);
		CrossroadsBlocks.blockAddQue(this);
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn){
		return new RotaryDrillTileEntity(golden);
	}

	@Override
	public BlockState getStateForPlacement(World worldIn, BlockPos pos, Direction side, BlockRayTraceResult hit, int meta, LivingEntity placer){
		return getDefaultState().with(EssentialsProperties.FACING, side.getOpposite());
	}
	
	@Override
	public AxisAlignedBB getBoundingBox(BlockState state, IBlockAccess source, BlockPos pos){
		return state.get(EssentialsProperties.FACING).getAxis() == Axis.X ? X : state.get(EssentialsProperties.FACING).getAxis() == Axis.Z ? Z : Y;
	}
	
	@Override
	public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity playerIn, Hand hand, BlockRayTraceResult hit){
		if(EssentialsConfig.isWrench(playerIn.getHeldItem(hand), worldIn.isRemote)){
			if(!worldIn.isRemote){
				worldIn.setBlockState(pos, state.cycle(EssentialsProperties.FACING));
			}
			return true;
		}
		return false;
	}

	@Override
	public void addCollisionBoxToList(BlockState state, World worldIn, BlockPos pos, AxisAlignedBB mask, List<AxisAlignedBB> list, Entity collidingEntity, boolean somethingOrOtherProbably){
		addCollisionBoxToList(pos, mask, list, state.get(EssentialsProperties.FACING).getAxis() == Axis.X ? X : state.get(EssentialsProperties.FACING).getAxis() == Axis.Z ? Z : Y);
	}

	@Override
	public boolean isOpaqueCube(BlockState state){
		return false;
	}

	@Override
	public boolean isSideSolid(BlockState state, IBlockAccess worldIn, BlockPos pos, Direction side){
		return side.getOpposite() == state.get(EssentialsProperties.FACING);
	}

	@Override
	public boolean isFullCube(BlockState state){
		return false;
	}

	@Override
	protected BlockStateContainer createBlockState(){
		return new BlockStateContainer(this, EssentialsProperties.FACING);
	}

	@Override
	public BlockState getStateFromMeta(int meta){
		Direction facing = Direction.byIndex(meta);
		return getDefaultState().with(EssentialsProperties.FACING, facing);
	}

	@Override
	public int getMetaFromState(BlockState state){
		return state.get(EssentialsProperties.FACING).getIndex();
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, BlockState state, BlockPos pos, Direction face){
		return face == state.get(EssentialsProperties.FACING).getOpposite() ? BlockFaceShape.CENTER : BlockFaceShape.UNDEFINED;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, @Nullable IBlockReader world, List<ITextComponent> tooltip, ITooltipFlag advanced){
		tooltip.add(golden ? "I: 100" : "I: 50");
		tooltip.add("Consumes: " + RotaryDrillTileEntity.ENERGY_USE + "J/t");
	}
}
