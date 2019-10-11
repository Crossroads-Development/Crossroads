package com.Da_Technomancer.crossroads.blocks.alchemy;

import com.Da_Technomancer.crossroads.blocks.CrossroadsBlocks;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.Da_Technomancer.crossroads.render.bakedModel.ReagentPumpBakedModel;
import com.Da_Technomancer.crossroads.tileentities.alchemy.ReagentPumpTileEntity;
import com.Da_Technomancer.essentials.EssentialsConfig;
import com.Da_Technomancer.essentials.blocks.BlockUtil;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.block.BlockRenderType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ReagentPump extends ContainerBlock{

	private final boolean crystal;

	public ReagentPump(boolean crystal){
		super(Material.GLASS);
		this.crystal = crystal;
		String name = (crystal ? "crystal_" : "") + "reagent_pump";
		setTranslationKey(name);
		setRegistryName(name);
		setHardness(.5F);
		setCreativeTab(CRItems.TAB_CROSSROADS);
		setSoundType(SoundType.GLASS);
		CrossroadsBlocks.toRegister.add(this);
		CrossroadsBlocks.blockAddQue(this, true);
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn){
		return new ReagentPumpTileEntity(!crystal);
	}

	@Override
	public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity playerIn, Hand hand, BlockRayTraceResult hit){
		if(EssentialsConfig.isWrench(playerIn.getHeldItem(hand), worldIn.isRemote)){
			if(!worldIn.isRemote){
				worldIn.setBlockState(pos, state.cycle(Properties.ACTIVE));
			}
			return true;
		}
		return false;
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
		return state.get(Properties.ACTIVE) ? 1 : 0;
	}

	@Override
	public BlockState getStateFromMeta(int meta){
		return getDefaultState().with(Properties.ACTIVE, (meta & 1) == 1);
	}

	@Override
	protected BlockStateContainer createBlockState(){
		return new ExtendedBlockState(this, new IProperty[] {Properties.ACTIVE}, new IUnlistedProperty[] {Properties.CONNECT});
	}

	@Override
	public boolean isOpaqueCube(BlockState state){
		return false;
	}

	@Override
	public boolean isFullCube(BlockState state){
		return false;
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, BlockState state, BlockPos pos, Direction face){
		if(state instanceof IExtendedBlockState){
			return (state.get(Properties.ACTIVE) ? face == Direction.UP : face == Direction.DOWN) || ((IExtendedBlockState) state).get(Properties.CONNECT)[face.getIndex()] ? BlockFaceShape.CENTER_SMALL : BlockFaceShape.UNDEFINED;
		}
		return BlockFaceShape.CENTER_SMALL;
	}

	@OnlyIn(Dist.CLIENT)
	public void initModel(){
		StateMapperBase ignoreState = new StateMapperBase(){
			@Override
			protected ModelResourceLocation getModelResourceLocation(BlockState IBlockState){
				return ReagentPumpBakedModel.BAKED_MODEL;
			}
		};
		ModelLoader.setCustomStateMapper(this, ignoreState);
	}

	@Override
	public BlockState getExtendedState(BlockState state, IBlockAccess world, BlockPos pos){
		IExtendedBlockState extendedBlockState = (IExtendedBlockState) state;
		TileEntity te = world.getTileEntity(pos);

		extendedBlockState = extendedBlockState.with(Properties.CONNECT, te instanceof ReagentPumpTileEntity ? ((ReagentPumpTileEntity) te).getMatches() : new Boolean[] {false, false, false, false, false, false});

		return extendedBlockState;
	}

	private static final double SIZE = 5D / 16D;
	private static final double CORE_SIZE = 4D / 16D;
	private static final AxisAlignedBB BB = new AxisAlignedBB(CORE_SIZE, CORE_SIZE, CORE_SIZE, 1 - CORE_SIZE, 1 - CORE_SIZE, 1 - CORE_SIZE);
	private static final AxisAlignedBB DOWN = new AxisAlignedBB(SIZE, 0, SIZE, 1 - SIZE, CORE_SIZE, 1 - SIZE);
	private static final AxisAlignedBB UP = new AxisAlignedBB(SIZE, 1, SIZE, 1 - SIZE, 1 - CORE_SIZE, 1 - SIZE);
	private static final AxisAlignedBB NORTH = new AxisAlignedBB(SIZE, SIZE, 0, 1 - SIZE, 1 - SIZE, CORE_SIZE);
	private static final AxisAlignedBB SOUTH = new AxisAlignedBB(SIZE, SIZE, 1, 1 - SIZE, 1 - SIZE, 1 - CORE_SIZE);
	private static final AxisAlignedBB WEST = new AxisAlignedBB(0, SIZE, SIZE, CORE_SIZE, 1 - SIZE, 1 - SIZE);
	private static final AxisAlignedBB EAST = new AxisAlignedBB(1, SIZE, SIZE, 1 - CORE_SIZE, 1 - SIZE, 1 - SIZE);

	@Override
	public void addCollisionBoxToList(BlockState state, World worldIn, BlockPos pos, AxisAlignedBB mask, List<AxisAlignedBB> list, Entity collidingEntity, boolean pleaseDontBeRelevantToAnythingOrIWillBeSad){
		addCollisionBoxToList(pos, mask, list, BB);
		IExtendedBlockState exState = (IExtendedBlockState) getExtendedState(state, worldIn, pos);

		if(exState.get(Properties.CONNECT)[0]){
			addCollisionBoxToList(pos, mask, list, DOWN);
		}
		if(exState.get(Properties.CONNECT)[1]){
			addCollisionBoxToList(pos, mask, list, UP);
		}
		if(exState.get(Properties.CONNECT)[2]){
			addCollisionBoxToList(pos, mask, list, NORTH);
		}
		if(exState.get(Properties.CONNECT)[3]){
			addCollisionBoxToList(pos, mask, list, SOUTH);
		}
		if(exState.get(Properties.CONNECT)[4]){
			addCollisionBoxToList(pos, mask, list, WEST);
		}
		if(exState.get(Properties.CONNECT)[5]){
			addCollisionBoxToList(pos, mask, list, EAST);
		}
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public AxisAlignedBB getSelectedBoundingBox(BlockState state, World source, BlockPos pos){
		IExtendedBlockState exState = (IExtendedBlockState) getExtendedState(state, source, pos);
		ArrayList<AxisAlignedBB> list = new ArrayList<>();
		if(exState.get(Properties.CONNECT)[0]){
			list.add(DOWN);
		}
		if(exState.get(Properties.CONNECT)[1]){
			list.add(UP);
		}
		if(exState.get(Properties.CONNECT)[2]){
			list.add(NORTH);
		}
		if(exState.get(Properties.CONNECT)[3]){
			list.add(SOUTH);
		}
		if(exState.get(Properties.CONNECT)[4]){
			list.add(WEST);
		}
		if(exState.get(Properties.CONNECT)[5]){
			list.add(EAST);
		}
		PlayerEntity play = Minecraft.getInstance().player;
		float reDist = Minecraft.getInstance().playerController.getBlockReachDistance();
		Vec3d start = play.getEyePosition(0F).subtract((double)pos.getX(), (double)pos.getY(), (double)pos.getZ());
		Vec3d end = start.add(play.getLook(0F).x * reDist, play.getLook(0F).y * reDist, play.getLook(0F).z * reDist);
		AxisAlignedBB out = BlockUtil.selectionRaytrace(list, start, end);
		return (out == null ? BB : out).offset(pos);
	}

	@Override
	@Nullable
	public RayTraceResult collisionRayTrace(BlockState state, World worldIn, BlockPos pos, Vec3d start, Vec3d end){
		IExtendedBlockState exState = (IExtendedBlockState) getExtendedState(state, worldIn, pos);
		ArrayList<AxisAlignedBB> list = new ArrayList<AxisAlignedBB>();
		list.add(BB);
		if(exState.get(Properties.CONNECT)[0]){
			list.add(DOWN);
		}
		if(exState.get(Properties.CONNECT)[1]){
			list.add(UP);
		}
		if(exState.get(Properties.CONNECT)[2]){
			list.add(NORTH);
		}
		if(exState.get(Properties.CONNECT)[3]){
			list.add(SOUTH);
		}
		if(exState.get(Properties.CONNECT)[4]){
			list.add(WEST);
		}
		if(exState.get(Properties.CONNECT)[5]){
			list.add(EAST);
		}

		start = start.subtract(pos.getX(), pos.getY(), pos.getZ());
		end = end.subtract(pos.getX(), pos.getY(), pos.getZ());
		AxisAlignedBB out = BlockUtil.selectionRaytrace(list, start, end);
		if(out == null){
			return null;
		}else{
			RayTraceResult untransformed = out.calculateIntercept(start, end);
			return new RayTraceResult(untransformed.hitVec.add((double)pos.getX(), (double)pos.getY(), (double)pos.getZ()), untransformed.sideHit, pos);
		}
	}
}
