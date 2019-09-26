package com.Da_Technomancer.crossroads.blocks.alchemy;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.blocks.CrossroadsBlocks;
import com.Da_Technomancer.crossroads.items.CrossroadsItems;
import com.Da_Technomancer.crossroads.render.bakedModel.AdvConduitBakedModel;
import com.Da_Technomancer.crossroads.render.bakedModel.IAdvConduitModel;
import com.Da_Technomancer.crossroads.tileentities.alchemy.AlchemicalTubeTileEntity;
import com.Da_Technomancer.essentials.EssentialsConfig;
import com.Da_Technomancer.essentials.blocks.BlockUtil;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.SoundType;
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
import net.minecraft.util.*;
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

public class AlchemicalTube extends ContainerBlock implements IAdvConduitModel{

	private static final double SIZE = 5D / 16D;
	private static final AxisAlignedBB BB = new AxisAlignedBB(SIZE, SIZE, SIZE, 1 - SIZE, 1 - SIZE, 1 - SIZE);
	private static final AxisAlignedBB DOWN = new AxisAlignedBB(SIZE, 0, SIZE, 1 - SIZE, SIZE, 1 - SIZE);
	private static final AxisAlignedBB UP = new AxisAlignedBB(SIZE, 1, SIZE, 1 - SIZE, 1 - SIZE, 1 - SIZE);
	private static final AxisAlignedBB NORTH = new AxisAlignedBB(SIZE, SIZE, 0, 1 - SIZE, 1 - SIZE, SIZE);
	private static final AxisAlignedBB SOUTH = new AxisAlignedBB(SIZE, SIZE, 1, 1 - SIZE, 1 - SIZE, 1 - SIZE);
	private static final AxisAlignedBB WEST = new AxisAlignedBB(0, SIZE, SIZE, SIZE, 1 - SIZE, 1 - SIZE);
	private static final AxisAlignedBB EAST = new AxisAlignedBB(1, SIZE, SIZE, 1 - SIZE, 1 - SIZE, 1 - SIZE);

	private final boolean crystal;

	public AlchemicalTube(boolean crystal){
		super(Material.GLASS);
		this.crystal = crystal;
		String name = (crystal ? "crystal_" : "") + "alch_tube";
		setTranslationKey(name);
		setRegistryName(name);
		setHardness(.5F);
		setCreativeTab(CrossroadsItems.TAB_CROSSROADS);
		setSoundType(SoundType.GLASS);
		CrossroadsBlocks.toRegister.add(this);
		CrossroadsBlocks.blockAddQue(this);
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn){
		return new AlchemicalTubeTileEntity(!crystal);
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
				if(te instanceof AlchemicalTubeTileEntity){
					Integer[] conMode = ((AlchemicalTubeTileEntity) te).getConnectMode(false);

					switch(conMode[face]){
						case 0:
							conMode[face] = 1;
							((AlchemicalTubeTileEntity) te).markSideChanged(face);
							break;
						case 1:
							conMode[face] = 2;
							((AlchemicalTubeTileEntity) te).markSideChanged(face);
							break;
						case 2:
							conMode[face] = 0;
							((AlchemicalTubeTileEntity) te).markSideChanged(face);
							break;
					}
				}
			}
			return true;
		}
		return false;
	}

	private static final ResourceLocation GLASS_CAP = new ResourceLocation(Crossroads.MODID, "blocks/alch_tube/glass_tube_cap");
	private static final ResourceLocation GLASS_OUT = new ResourceLocation(Crossroads.MODID, "blocks/alch_tube/glass_tube_out");
	private static final ResourceLocation GLASS_IN = new ResourceLocation(Crossroads.MODID, "blocks/alch_tube/glass_tube_in");
	private static final ResourceLocation CRYST_CAP = new ResourceLocation(Crossroads.MODID, "blocks/alch_tube/cryst_tube_cap");
	private static final ResourceLocation CRYST_OUT = new ResourceLocation(Crossroads.MODID, "blocks/alch_tube/cryst_tube_out");
	private static final ResourceLocation CRYST_IN = new ResourceLocation(Crossroads.MODID, "blocks/alch_tube/cryst_tube_in");

	@Override
	public ResourceLocation getTexture(BlockState state, int mode){
		return crystal ? mode == 0 ? CRYST_CAP : mode == 1 ? CRYST_OUT : CRYST_IN : mode == 0 ? GLASS_CAP : mode == 1 ? GLASS_OUT : GLASS_IN;
	}

	@Override
	public double getSize(){
		return SIZE;
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

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack){
		world.markBlockRangeForRenderUpdate(pos.add(-1, -1, -1), pos.add(1, 1, 1));
	}

	@Override
	protected BlockStateContainer createBlockState(){
		return new ExtendedBlockState(this, new IProperty[] {}, new IUnlistedProperty[] {Properties.CONNECT_MODE});
	}

	@Override
	public BlockState getExtendedState(BlockState state, IBlockAccess world, BlockPos pos){
		IExtendedBlockState extendedBlockState = (IExtendedBlockState) state;
		TileEntity te = world.getTileEntity(pos);
		return extendedBlockState.with(Properties.CONNECT_MODE, te instanceof AlchemicalTubeTileEntity ? ((AlchemicalTubeTileEntity) te).getConnectMode(true) : new Integer[] {0, 0, 0, 0, 0, 0});
	}

	@Override
	public boolean isOpaqueCube(BlockState state){
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
		list.add(BB);
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
		if(connectMode[0] != 0){
			addCollisionBoxToList(pos, mask, list, DOWN);
		}
		if(connectMode[1] != 0){
			addCollisionBoxToList(pos, mask, list, UP);
		}
		if(connectMode[2] != 0){
			addCollisionBoxToList(pos, mask, list, NORTH);
		}
		if(connectMode[3] != 0){
			addCollisionBoxToList(pos, mask, list, SOUTH);
		}
		if(connectMode[4] != 0){
			addCollisionBoxToList(pos, mask, list, WEST);
		}
		if(connectMode[5] != 0){
			addCollisionBoxToList(pos, mask, list, EAST);
		}
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
