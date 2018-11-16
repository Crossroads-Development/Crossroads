package com.Da_Technomancer.crossroads.blocks.fluid;

import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import com.Da_Technomancer.crossroads.render.bakedModel.AdvConduitBakedModel;
import com.Da_Technomancer.crossroads.render.bakedModel.IAdvConduitModel;
import com.Da_Technomancer.crossroads.items.ModItems;
import com.Da_Technomancer.crossroads.tileentities.fluid.FluidTubeTileEntity;
import com.Da_Technomancer.essentials.EssentialsConfig;
import com.Da_Technomancer.essentials.blocks.BlockUtil;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
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

public class FluidTube extends BlockContainer implements IAdvConduitModel{

	private static final double SIZE = .3125D;
	private static final AxisAlignedBB BB = new AxisAlignedBB(SIZE, SIZE, SIZE, 1 - SIZE, 1 - SIZE, 1 - SIZE);
	private static final AxisAlignedBB DOWN = new AxisAlignedBB(SIZE, 0, SIZE, 1 - SIZE, SIZE, 1 - SIZE);
	private static final AxisAlignedBB UP = new AxisAlignedBB(SIZE, 1, SIZE, 1 - SIZE, 1 - SIZE, 1 - SIZE);
	private static final AxisAlignedBB NORTH = new AxisAlignedBB(SIZE, SIZE, 0, 1 - SIZE, 1 - SIZE, SIZE);
	private static final AxisAlignedBB SOUTH = new AxisAlignedBB(SIZE, SIZE, 1, 1 - SIZE, 1 - SIZE, 1 - SIZE);
	private static final AxisAlignedBB WEST = new AxisAlignedBB(0, SIZE, SIZE, SIZE, 1 - SIZE, 1 - SIZE);
	private static final AxisAlignedBB EAST = new AxisAlignedBB(1, SIZE, SIZE, 1 - SIZE, 1 - SIZE, 1 - SIZE);
	
	public FluidTube(){
		super(Material.IRON);
		String name = "fluid_tube";
		setTranslationKey(name);
		setRegistryName(name);
		setCreativeTab(ModItems.TAB_CROSSROADS);
		setHardness(3);
		setSoundType(SoundType.METAL);
		ModBlocks.toRegister.add(this);
		ModBlocks.blockAddQue(this);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta){
		return new FluidTubeTileEntity();
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state){
		return EnumBlockRenderType.MODEL;
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ){
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
				if(te instanceof FluidTubeTileEntity){
					Integer[] conMode = ((FluidTubeTileEntity) te).getConnectMode(false);

					conMode[face] = (conMode[face] + 1) % 4;
					((FluidTubeTileEntity) te).markSideChanged(face);
				}
			}
			return true;
		}
		return false;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World source, BlockPos pos){
		IExtendedBlockState exState = (IExtendedBlockState) getExtendedState(state, source, pos);
		ArrayList<AxisAlignedBB> list = new ArrayList<AxisAlignedBB>();
		Integer[] connectMode = exState.getValue(Properties.CONNECT_MODE);
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
		EntityPlayer play = Minecraft.getMinecraft().player;
		float reDist = Minecraft.getMinecraft().playerController.getBlockReachDistance();
		Vec3d start = play.getPositionEyes(0F).subtract((double)pos.getX(), (double)pos.getY(), (double)pos.getZ());
		Vec3d end = start.add(play.getLook(0F).x * reDist, play.getLook(0F).y * reDist, play.getLook(0F).z * reDist);
		AxisAlignedBB out = BlockUtil.selectionRaytrace(list, start, end);
		return (out == null ? BB : out).offset(pos);
	}

	@Override
	@Nullable
	public RayTraceResult collisionRayTrace(IBlockState state, World worldIn, BlockPos pos, Vec3d start, Vec3d end){
		IExtendedBlockState exState = (IExtendedBlockState) getExtendedState(state, worldIn, pos);
		ArrayList<AxisAlignedBB> list = new ArrayList<AxisAlignedBB>();
		Integer[] connectMode = exState.getValue(Properties.CONNECT_MODE);
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
	public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB mask, List<AxisAlignedBB> list, Entity collidingEntity, boolean nobodyKnows){
		addCollisionBoxToList(pos, mask, list, BB);
		IExtendedBlockState exState = (IExtendedBlockState) getExtendedState(state, worldIn, pos);
		Integer[] connectMode = exState.getValue(Properties.CONNECT_MODE);

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

	@SideOnly(Side.CLIENT)
	public void initModel(){
		StateMapperBase ignoreState = new StateMapperBase(){
			@Override
			protected ModelResourceLocation getModelResourceLocation(IBlockState IBlockState){
				return AdvConduitBakedModel.BAKED_MODEL;
			}
		};
		ModelLoader.setCustomStateMapper(this, ignoreState);
	}

	private static final ResourceLocation CAP = new ResourceLocation(Main.MODID, "blocks/block_bronze");
	private static final ResourceLocation OUT = new ResourceLocation(Main.MODID, "blocks/fluid_tube_out");
	private static final ResourceLocation IN = new ResourceLocation(Main.MODID, "blocks/fluid_tube_in");

	@Override
	public ResourceLocation getTexture(IBlockState state, int mode){
		return mode == 0 || mode == 1 ? CAP : mode == 2 ? OUT : IN;
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack){
		world.markBlockRangeForRenderUpdate(pos.add(-1, -1, -1), pos.add(1, 1, 1));
	}
	
	@Override
	public boolean isFullCube(IBlockState state){
		return false;
	}

	@Override
	protected BlockStateContainer createBlockState(){
		return new ExtendedBlockState(this, new IProperty[0], new IUnlistedProperty[] {Properties.CONNECT_MODE});
	}

	@Override
	public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos){
		IExtendedBlockState extendedBlockState = (IExtendedBlockState) state;
		TileEntity te = world.getTileEntity(pos);
		return extendedBlockState.withProperty(Properties.CONNECT_MODE, te instanceof FluidTubeTileEntity ? ((FluidTubeTileEntity) te).getConnectMode(true) : new Integer[] {0, 0, 0, 0, 0, 0});

	}

	@Override
	public boolean isOpaqueCube(IBlockState state){
		return false;
	}

	@Override
	public double getSize(){
		return SIZE;
	}
}
