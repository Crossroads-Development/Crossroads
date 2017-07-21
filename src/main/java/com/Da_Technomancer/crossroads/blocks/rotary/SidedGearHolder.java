package com.Da_Technomancer.crossroads.blocks.rotary;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import com.Da_Technomancer.crossroads.CommonProxy;
import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.MiscOp;
import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import com.Da_Technomancer.crossroads.items.itemSets.GearFactory;
import com.Da_Technomancer.crossroads.tileentities.rotary.SidedGearHolderTileEntity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SidedGearHolder extends BlockContainer{

	private static final ArrayList<AxisAlignedBB> BOUNDING_BOXES = new ArrayList<AxisAlignedBB>();

	static{
		BOUNDING_BOXES.add(new AxisAlignedBB(0D, 0D, 0D, 1D, .125D, 1D));//DOWN
		BOUNDING_BOXES.add(new AxisAlignedBB(0D, .875D, 0D, 1D, 1D, 1D));//UP
		BOUNDING_BOXES.add(new AxisAlignedBB(0D, 0D, 0D, 1D, 1D, .125D));//NORTH
		BOUNDING_BOXES.add(new AxisAlignedBB(0D, 0D, .875D, 1D, 1D, 1D));//SOUTH
		BOUNDING_BOXES.add(new AxisAlignedBB(0D, 0D, 0D, .125D, 1D, 1D));//WEST
		BOUNDING_BOXES.add(new AxisAlignedBB(.875D, 0D, 0D, 1D, 1D, 1D));//EAST
		BOUNDING_BOXES.add(new AxisAlignedBB(.3125D, .3125D, .3125D, .6875D, .6875D, .6875D));//Center
	}

	public SidedGearHolder(){
		super(Material.IRON);
		String name = "sided_gear_holder";
		setUnlocalizedName(name);
		setRegistryName(name);
		setHardness(1);
		setSoundType(SoundType.METAL);
		ModBlocks.toRegister.add(this);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta){
		return new SidedGearHolderTileEntity();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player){
		Item out = GearFactory.BASIC_GEARS.get(((SidedGearHolderTileEntity) world.getTileEntity(pos)).getMembers()[target.sideHit.getOpposite().getIndex()]);
		if(out == null){
			return ItemStack.EMPTY;
		}else{
			return new ItemStack(out, 1);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side){
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World worldIn, BlockPos pos){
		TileEntity te = worldIn.getTileEntity(pos);
		EntityPlayer play = Minecraft.getMinecraft().player;
		float reDist = Minecraft.getMinecraft().playerController.getBlockReachDistance();
		Vec3d start = play.getPositionEyes(0F).subtract((double) pos.getX(), (double) pos.getY(), (double) pos.getZ());
		Vec3d end = start.addVector(play.getLook(0F).x * reDist, play.getLook(0F).y * reDist, play.getLook(0F).z * reDist);
		AxisAlignedBB out = getAimedSide(te, start, end, true);
		return (out == null ? BOUNDING_BOXES.get(6) : out).offset(pos);
	}

	@Override
	@Nullable
	public RayTraceResult collisionRayTrace(IBlockState state, World worldIn, BlockPos pos, Vec3d start, Vec3d end){
		TileEntity te = worldIn.getTileEntity(pos);
		start = start.subtract(pos.getX(), pos.getY(), pos.getZ());
		end = end.subtract(pos.getX(), pos.getY(), pos.getZ());
		AxisAlignedBB out = getAimedSide(te, start, end, true);
		if(out == null){
			return null;
		}else{
			RayTraceResult untransformed = out.calculateIntercept(start, end);
			return new RayTraceResult(untransformed.hitVec.addVector((double) pos.getX(), (double) pos.getY(), (double) pos.getZ()), untransformed.sideHit, pos);
		}
	}

	@Override
	public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB mask, List<AxisAlignedBB> list, Entity collidingEntity, boolean nothingProbably){
		for(EnumFacing side : EnumFacing.values()){
			if(worldIn.getTileEntity(pos).hasCapability(Capabilities.AXLE_HANDLER_CAPABILITY, side)){
				addCollisionBoxToList(pos, mask, list, BOUNDING_BOXES.get(side.getIndex()));
			}
		}
	}

	@Override
	public boolean isFullCube(IBlockState state){
		return false;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state){
		return false;
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state){
		return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
	}

	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos){
		if(worldIn.isRemote){
			return;
		}

		SidedGearHolderTileEntity te = (SidedGearHolderTileEntity) worldIn.getTileEntity(pos);

		for(EnumFacing side : EnumFacing.VALUES){
			if(te.getMembers()[side.getIndex()] != null && !worldIn.isSideSolid(pos.offset(side), side.getOpposite(), false)){
				spawnAsEntity(worldIn, pos, new ItemStack(GearFactory.BASIC_GEARS.get(te.getMembers()[side.getIndex()]), 1));
				te.setMembers(null, side.getIndex(), false);
			}
		}
		if(te.getMembers()[0] == null && te.getMembers()[1] == null && te.getMembers()[2] == null && te.getMembers()[3] == null && te.getMembers()[4] == null && te.getMembers()[5] == null){
			worldIn.destroyBlock(pos, false);
		}

		CommonProxy.masterKey++;
	}

	@Override
	public boolean removedByPlayer(IBlockState state, World worldIn, BlockPos pos, EntityPlayer player, boolean canHarvest){
		TileEntity te = worldIn.getTileEntity(pos);
		if(te instanceof SidedGearHolderTileEntity){
			float reDist = player.isCreative() ? 5F : 4.5F;
			Vec3d start = new Vec3d(player.prevPosX, player.prevPosY + (double) player.getEyeHeight(), player.prevPosZ).subtract((double)pos.getX(), (double)pos.getY(), (double)pos.getZ());
			Vec3d end = start.addVector(player.getLook(0F).x * reDist, player.getLook(0F).y * reDist, player.getLook(0F).z * reDist);
			int out = BOUNDING_BOXES.indexOf(getAimedSide(te, start, end, true));

			if(out == -1){
				return false;
			}
			SidedGearHolderTileEntity gear = (SidedGearHolderTileEntity) te;
			if(out == 6){
				if(canHarvest){
					for(int i = 0; i < 6; i++){
						if(gear.getMembers()[i] != null){
							spawnAsEntity(worldIn, pos, new ItemStack(GearFactory.BASIC_GEARS.get(gear.getMembers()[i]), 1));
						}
					}
				}
				CommonProxy.masterKey++;
				worldIn.setBlockState(pos, Blocks.AIR.getDefaultState());
				return true;
			}else{
				if(canHarvest){
					spawnAsEntity(worldIn, pos, new ItemStack(GearFactory.BASIC_GEARS.get(gear.getMembers()[out]), 1));
				}
				gear.setMembers(null, out, false);
				CommonProxy.masterKey++;
				if(gear.getMembers()[0] == null && gear.getMembers()[1] == null && gear.getMembers()[2] == null && gear.getMembers()[3] == null && gear.getMembers()[4] == null && gear.getMembers()[5] == null){
					worldIn.setBlockState(pos, Blocks.AIR.getDefaultState());
					return true;
				}

				return false;
			}
		}else{
			return super.removedByPlayer(state, worldIn, pos, player, canHarvest);
		}
	}

	@Override
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune){
		List<ItemStack> drops = new ArrayList<ItemStack>();
		SidedGearHolderTileEntity te = (SidedGearHolderTileEntity) world.getTileEntity(pos);
		if(te != null){
			for(int i = 0; i < 6; i++){
				if(te.getMembers()[i] != null){
					drops.add(new ItemStack(GearFactory.BASIC_GEARS.get(te.getMembers()[i]), 1));
				}
			}
		}
		return drops;
	}

	/**
	 * 
	 * @param te
	 * @param start Start vector, subtract position first
	 * @param end End vector, subtract position first
	 * @param useCenter whether or not to include center when raytracing
	 * @return 
	 */
	private AxisAlignedBB getAimedSide(TileEntity te, Vec3d start, Vec3d end, boolean useCenter){
		ArrayList<AxisAlignedBB> list = new ArrayList<AxisAlignedBB>();
		for(int i = 0; i < 6; i++){
			if(te.hasCapability(Capabilities.AXLE_HANDLER_CAPABILITY, EnumFacing.getFront(i))){
				list.add(BOUNDING_BOXES.get(i));
			}
		}
		if(useCenter){
			list.add(BOUNDING_BOXES.get(6));
		}
		return MiscOp.rayTraceMulti(list, start, end);
	}
}
