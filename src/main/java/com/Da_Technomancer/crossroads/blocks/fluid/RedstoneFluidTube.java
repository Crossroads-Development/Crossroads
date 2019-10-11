package com.Da_Technomancer.crossroads.blocks.fluid;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.blocks.CrossroadsBlocks;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.Da_Technomancer.crossroads.render.bakedModel.AdvConduitBakedModel;
import com.Da_Technomancer.crossroads.render.bakedModel.IAdvConduitModel;
import com.Da_Technomancer.crossroads.tileentities.fluid.RedstoneFluidTubeTileEntity;
import com.Da_Technomancer.essentials.EssentialsConfig;
import com.Da_Technomancer.essentials.blocks.BlockUtil;
import com.Da_Technomancer.essentials.blocks.EssentialsProperties;
import net.minecraft.block.*;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.block.BlockRenderType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
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

public class RedstoneFluidTube extends ContainerBlock implements IAdvConduitModel{

	private static final double SIZE = .3125D;
	private static final AxisAlignedBB BB = new AxisAlignedBB(SIZE, SIZE, SIZE, 1 - SIZE, 1 - SIZE, 1 - SIZE);
	private static final AxisAlignedBB DOWN = new AxisAlignedBB(SIZE, 0, SIZE, 1 - SIZE, SIZE, 1 - SIZE);
	private static final AxisAlignedBB UP = new AxisAlignedBB(SIZE, 1, SIZE, 1 - SIZE, 1 - SIZE, 1 - SIZE);
	private static final AxisAlignedBB NORTH = new AxisAlignedBB(SIZE, SIZE, 0, 1 - SIZE, 1 - SIZE, SIZE);
	private static final AxisAlignedBB SOUTH = new AxisAlignedBB(SIZE, SIZE, 1, 1 - SIZE, 1 - SIZE, 1 - SIZE);
	private static final AxisAlignedBB WEST = new AxisAlignedBB(0, SIZE, SIZE, SIZE, 1 - SIZE, 1 - SIZE);
	private static final AxisAlignedBB EAST = new AxisAlignedBB(1, SIZE, SIZE, 1 - SIZE, 1 - SIZE, 1 - SIZE);

	public RedstoneFluidTube(){
		super(Material.IRON);
		String name = "redstone_fluid_tube";
		setTranslationKey(name);
		setRegistryName(name);
		setCreativeTab(CRItems.TAB_CROSSROADS);
		setHardness(2);
		setSoundType(SoundType.METAL);
		CrossroadsBlocks.toRegister.add(this);
		CrossroadsBlocks.blockAddQue(this);
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, BlockState state, BlockPos pos, Direction face){
		return BlockFaceShape.UNDEFINED;
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn){
		return new RedstoneFluidTubeTileEntity();
	}

	@Override
	public BlockRenderType getRenderType(BlockState state){
		return BlockRenderType.MODEL;
	}

	@Override
	public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity playerIn, Hand hand, BlockRayTraceResult hit){
		if(EssentialsConfig.isWrench(playerIn.getHeldItem(hand), worldIn.isRemote)){
			if(!worldIn.isRemote){
				int face;
				if(hitY < SIZE){
					face = 0;//Down
				}else if(hitY > 1F - (float) SIZE){
					face = 1;//Up
				}else if(hitX < (float) SIZE){
					face = 4;//West
				}else if(hitX > 1F - (float) SIZE){
					face = 5;//East
				}else if(hitZ < (float) SIZE){
					face = 2;//North
				}else if(hitZ > 1F - (float) SIZE){
					face = 3;//South
				}else{
					face = side.getIndex();
				}
				TileEntity te = worldIn.getTileEntity(pos);
				if(te instanceof RedstoneFluidTubeTileEntity){
					Integer[] conMode = ((RedstoneFluidTubeTileEntity) te).getConnectMode(false);

					conMode[face] = (conMode[face] + 1) % 4;
					((RedstoneFluidTubeTileEntity) te).markSideChanged(face);
				}
			}
			return true;
		}
		return false;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public AxisAlignedBB getSelectedBoundingBox(BlockState state, World source, BlockPos pos){
		IExtendedBlockState exState = (IExtendedBlockState) getExtendedState(state, source, pos);
		ArrayList<AxisAlignedBB> list = new ArrayList<AxisAlignedBB>();
		Integer[] connectMode = exState.get(Properties.CONNECT_MODE);
		if(connectMode[0] != 0){
			list.add(DOWN);
		}
		if(connectMode[1] != 0){
			list.add(UP);
		}
		if(connectMode[2] != 0){
			list.add(NORTH);
		}
		if(connectMode[3] != 0){
			list.add(SOUTH);
		}
		if(connectMode[4] != 0){
			list.add(WEST);
		}
		if(connectMode[5] != 0){
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
		Integer[] connectMode = exState.get(Properties.CONNECT_MODE);
		list.add(BB);
		if(connectMode[0]  != 0){
			list.add(DOWN);
		}
		if(connectMode[1]  != 0){
			list.add(UP);
		}
		if(connectMode[2]  != 0){
			list.add(NORTH);
		}
		if(connectMode[3]  != 0){
			list.add(SOUTH);
		}
		if(connectMode[4]  != 0){
			list.add(WEST);
		}
		if(connectMode[5]  != 0){
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

	@Override
	public void addCollisionBoxToList(BlockState state, World worldIn, BlockPos pos, AxisAlignedBB mask, List<AxisAlignedBB> list, Entity collidingEntity, boolean nobodyKnows){
		addCollisionBoxToList(pos, mask, list, BB);
		IExtendedBlockState exState = (IExtendedBlockState) getExtendedState(state, worldIn, pos);
		Integer[] connectMode = exState.get(Properties.CONNECT_MODE);

		if(connectMode[0]  != 0){
			addCollisionBoxToList(pos, mask, list, DOWN);
		}
		if(connectMode[1]  != 0){
			addCollisionBoxToList(pos, mask, list, UP);
		}
		if(connectMode[2]  != 0){
			addCollisionBoxToList(pos, mask, list, NORTH);
		}
		if(connectMode[3]  != 0){
			addCollisionBoxToList(pos, mask, list, SOUTH);
		}
		if(connectMode[4]  != 0){
			addCollisionBoxToList(pos, mask, list, WEST);
		}
		if(connectMode[5]  != 0){
			addCollisionBoxToList(pos, mask, list, EAST);
		}
	}

	@OnlyIn(Dist.CLIENT)
	public void initModel(){
		StateMapperBase ignoreState = new StateMapperBase(){
			@Override
			protected ModelResourceLocation getModelResourceLocation(BlockState IBlockState){
				return AdvConduitBakedModel.BAKED_MODEL;
			}
		};
		ModelLoader.setCustomStateMapper(this, ignoreState);
	}

	private static final ResourceLocation CAP = new ResourceLocation(Crossroads.MODID, "blocks/block_bronze");
	private static final ResourceLocation OUT = new ResourceLocation(Crossroads.MODID, "blocks/fluid_tube_out");
	private static final ResourceLocation IN = new ResourceLocation(Crossroads.MODID, "blocks/fluid_tube_in");

	@Override
	public ResourceLocation getTexture(BlockState state, int mode){
		return mode == 0 || mode == 1 ? CAP : mode == 2 ? OUT : IN;
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack){
		world.markBlockRangeForRenderUpdate(pos.add(-1, -1, -1), pos.add(1, 1, 1));
		neighborChanged(state, world, pos, null, null);
	}
	
	@Override
	public boolean isFullCube(BlockState state){
		return false;
	}

	@Override
	protected BlockStateContainer createBlockState(){
		return new ExtendedBlockState(this, new IProperty[] {EssentialsProperties.REDSTONE_BOOL}, new IUnlistedProperty[] {Properties.CONNECT_MODE});
	}
	
	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos){
		if(worldIn.isBlockPowered(pos)){
			if(!state.get(EssentialsProperties.REDSTONE_BOOL)){
				worldIn.setBlockState(pos, state.with(EssentialsProperties.REDSTONE_BOOL, true));
			}
		}else{
			if(state.get(EssentialsProperties.REDSTONE_BOOL)){
				worldIn.setBlockState(pos, state.with(EssentialsProperties.REDSTONE_BOOL, false));
			}
		}
	}

	@Override
	public BlockState getStateFromMeta(int meta){
		return this.getDefaultState().with(EssentialsProperties.REDSTONE_BOOL, meta == 1);
	}

	@Override
	public int getMetaFromState(BlockState state){
		return state.get(EssentialsProperties.REDSTONE_BOOL) ? 1 : 0;
	}

	@Override
	public BlockState getExtendedState(BlockState state, IBlockAccess world, BlockPos pos){
		IExtendedBlockState extendedBlockState = (IExtendedBlockState) state;
		TileEntity te = world.getTileEntity(pos);
		return extendedBlockState.with(Properties.CONNECT_MODE, te instanceof RedstoneFluidTubeTileEntity ? ((RedstoneFluidTubeTileEntity) te).getConnectMode(true) : new Integer[] {0, 0, 0, 0, 0, 0});

	}

	@Override
	public boolean isOpaqueCube(BlockState state){
		return false;
	}

	@Override
	public double getSize(){
		return SIZE;
	}
}
