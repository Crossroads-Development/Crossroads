package com.Da_Technomancer.crossroads.blocks.alchemy;

import com.Da_Technomancer.crossroads.API.CRProperties;
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
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.block.BlockRenderType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.*;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
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
		super(Properties.create(Material.GLASS).sound(SoundType.GLASS).hardnessAndResistance(0.5F));
		this.crystal = crystal;
		String name = (crystal ? "crystal_" : "") + "reagent_pump";
		setRegistryName(name);
		CrossroadsBlocks.toRegister.add(this);
		CrossroadsBlocks.blockAddQue(this);
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn){
		return new ReagentPumpTileEntity(!crystal);
	}

	@Override
	public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity playerIn, Hand hand, BlockRayTraceResult hit){
		if(EssentialsConfig.isWrench(playerIn.getHeldItem(hand))){
			if(!worldIn.isRemote){
				worldIn.setBlockState(pos, state.cycle(CRProperties.ACTIVE));
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
	protected BlockStateContainer createBlockState(){
		return new ExtendedBlockState(this, new IProperty[] {Properties.ACTIVE}, new IUnlistedProperty[] {Properties.CONNECT});
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
