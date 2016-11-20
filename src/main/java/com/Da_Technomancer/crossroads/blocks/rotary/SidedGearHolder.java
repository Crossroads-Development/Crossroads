package com.Da_Technomancer.crossroads.blocks.rotary;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import com.Da_Technomancer.crossroads.CommonProxy;
import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.MiscOp;
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
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SidedGearHolder extends BlockContainer{

	private static final AxisAlignedBB BB = new AxisAlignedBB(.25D, .25D, .25D, .75D, .75D, .75D);
	private static final AxisAlignedBB NORTH = new AxisAlignedBB(0D, 0D, 0D, 1D, 1D, .125D);
	private static final AxisAlignedBB SOUTH = new AxisAlignedBB(0D, 0D, .875D, 1D, 1D, 1D);
	private static final AxisAlignedBB EAST = new AxisAlignedBB(.875D, 0D, 0D, 1D, 1D, 1D);
	private static final AxisAlignedBB WEST = new AxisAlignedBB(0D, 0D, 0D, .125D, 1D, 1D);
	private static final AxisAlignedBB UP = new AxisAlignedBB(0D, .875D, 0D, 1D, 1D, 1D);
	private static final AxisAlignedBB DOWN = new AxisAlignedBB(0D, 0D, 0D, 1D, .125D, 1D);

	public SidedGearHolder(){
		super(Material.IRON);
		setUnlocalizedName("sidedGearHolder");
		setRegistryName("sidedGearHolder");
		GameRegistry.register(this);
		this.setHardness(1);
		setSoundType(SoundType.METAL);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta){
		return new SidedGearHolderTileEntity();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player){
		return null;
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
		ArrayList<AxisAlignedBB> list = new ArrayList<AxisAlignedBB>();
		if(te.hasCapability(Capabilities.ROTARY_HANDLER_CAPABILITY, EnumFacing.DOWN)){
			list.add(DOWN);
		}
		if(te.hasCapability(Capabilities.ROTARY_HANDLER_CAPABILITY, EnumFacing.UP)){
			list.add(UP);
		}
		if(te.hasCapability(Capabilities.ROTARY_HANDLER_CAPABILITY, EnumFacing.NORTH)){
			list.add(NORTH);
		}
		if(te.hasCapability(Capabilities.ROTARY_HANDLER_CAPABILITY, EnumFacing.SOUTH)){
			list.add(SOUTH);
		}
		if(te.hasCapability(Capabilities.ROTARY_HANDLER_CAPABILITY, EnumFacing.WEST)){
			list.add(WEST);
		}
		if(te.hasCapability(Capabilities.ROTARY_HANDLER_CAPABILITY, EnumFacing.EAST)){
			list.add(EAST);
		}
		if(list.isEmpty()){
			return BB;
		}
		EntityPlayer play = Minecraft.getMinecraft().thePlayer;
		float reDist = Minecraft.getMinecraft().playerController.getBlockReachDistance();
		Vec3d start = play.getPositionEyes(0F).subtract((double)pos.getX(), (double)pos.getY(), (double)pos.getZ());
		Vec3d end = start.addVector(play.getLook(0F).xCoord * reDist, play.getLook(0F).yCoord * reDist, play.getLook(0F).zCoord * reDist);
		AxisAlignedBB out = MiscOp.rayTraceMulti(list, start, end);
		return (out == null ? list.get(0) : out).offset(pos);
	}

	@Override
	@Nullable
	public RayTraceResult collisionRayTrace(IBlockState state, World worldIn, BlockPos pos, Vec3d start, Vec3d end){
		TileEntity te = worldIn.getTileEntity(pos);
		ArrayList<AxisAlignedBB> list = new ArrayList<AxisAlignedBB>();
		if(te.hasCapability(Capabilities.ROTARY_HANDLER_CAPABILITY, EnumFacing.DOWN)){
			list.add(DOWN);
		}
		if(te.hasCapability(Capabilities.ROTARY_HANDLER_CAPABILITY, EnumFacing.UP)){
			list.add(UP);
		}
		if(te.hasCapability(Capabilities.ROTARY_HANDLER_CAPABILITY, EnumFacing.NORTH)){
			list.add(NORTH);
		}
		if(te.hasCapability(Capabilities.ROTARY_HANDLER_CAPABILITY, EnumFacing.SOUTH)){
			list.add(SOUTH);
		}
		if(te.hasCapability(Capabilities.ROTARY_HANDLER_CAPABILITY, EnumFacing.WEST)){
			list.add(WEST);
		}
		if(te.hasCapability(Capabilities.ROTARY_HANDLER_CAPABILITY, EnumFacing.EAST)){
			list.add(EAST);
		}
		if(list.isEmpty()){
			list.add(BB);
		}
		start = start.subtract(pos.getX(), pos.getY(), pos.getZ());
		end = end.subtract(pos.getX(), pos.getY(), pos.getZ());
		AxisAlignedBB out = MiscOp.rayTraceMulti(list, start, end);
		if(out == null){
			return null;
		}else{
			RayTraceResult untransformed = out.calculateIntercept(start, end);
			return new RayTraceResult(untransformed.hitVec.addVector((double)pos.getX(), (double)pos.getY(), (double)pos.getZ()), untransformed.sideHit, pos);
		}
	}

	@Override
	public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB mask, List<AxisAlignedBB> list, Entity collidingEntity){

		if(worldIn.getTileEntity(pos).hasCapability(Capabilities.ROTARY_HANDLER_CAPABILITY, EnumFacing.DOWN)){
			addCollisionBoxToList(pos, mask, list, DOWN);
		}
		if(worldIn.getTileEntity(pos).hasCapability(Capabilities.ROTARY_HANDLER_CAPABILITY, EnumFacing.UP)){
			addCollisionBoxToList(pos, mask, list, UP);
		}
		if(worldIn.getTileEntity(pos).hasCapability(Capabilities.ROTARY_HANDLER_CAPABILITY, EnumFacing.NORTH)){
			addCollisionBoxToList(pos, mask, list, NORTH);
		}
		if(worldIn.getTileEntity(pos).hasCapability(Capabilities.ROTARY_HANDLER_CAPABILITY, EnumFacing.SOUTH)){
			addCollisionBoxToList(pos, mask, list, SOUTH);
		}
		if(worldIn.getTileEntity(pos).hasCapability(Capabilities.ROTARY_HANDLER_CAPABILITY, EnumFacing.WEST)){
			addCollisionBoxToList(pos, mask, list, WEST);
		}
		if(worldIn.getTileEntity(pos).hasCapability(Capabilities.ROTARY_HANDLER_CAPABILITY, EnumFacing.EAST)){
			addCollisionBoxToList(pos, mask, list, EAST);
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
	public boolean isBlockSolid(IBlockAccess worldIn, BlockPos pos, EnumFacing side){
		return false;
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state){
		return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
	}

	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn){
		if(worldIn.isRemote){
			return;
		}

		boolean destroy = false;
		for(EnumFacing side : EnumFacing.VALUES){
			if(worldIn.getTileEntity(pos).hasCapability(Capabilities.ROTARY_HANDLER_CAPABILITY, side) && worldIn.getTileEntity(pos).getCapability(Capabilities.ROTARY_HANDLER_CAPABILITY, side).getMember() != null && !worldIn.isSideSolid(pos.offset(side), side.getOpposite(), true)){
				destroy = true;
			}
		}
		if(destroy){
			dropItems(worldIn, pos, (SidedGearHolderTileEntity) worldIn.getTileEntity(pos));
			worldIn.destroyBlock(pos, false);
		}

		CommonProxy.masterKey++;
	}

	@Override
	public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity te, ItemStack stack){

		if(te instanceof SidedGearHolderTileEntity){
			dropItems(worldIn, pos, (SidedGearHolderTileEntity) te);
		}else{
			super.harvestBlock(worldIn, player, pos, state, te, stack);
		}
	}

	@Override
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune){
		List<ItemStack> drops = new ArrayList<ItemStack>();
		TileEntity te = world.getTileEntity(pos);
		for(EnumFacing checker : EnumFacing.values()){
			if(te.hasCapability(Capabilities.ROTARY_HANDLER_CAPABILITY, checker)){
				drops.add(new ItemStack(GearFactory.basicGears.get(te.getCapability(Capabilities.ROTARY_HANDLER_CAPABILITY, checker).getMember()), 1));
			}
		}
		return drops;
	}

	private void dropItems(World worldIn, BlockPos pos, SidedGearHolderTileEntity te){
		for(EnumFacing checker : EnumFacing.values()){
			if(te.hasCapability(Capabilities.ROTARY_HANDLER_CAPABILITY, checker)){
				spawnAsEntity(worldIn, pos, new ItemStack(GearFactory.basicGears.get(te.getCapability(Capabilities.ROTARY_HANDLER_CAPABILITY, checker).getMember()), 1));
			}
		}
	}
}
