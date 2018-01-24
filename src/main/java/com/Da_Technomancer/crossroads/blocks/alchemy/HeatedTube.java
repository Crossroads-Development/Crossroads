package com.Da_Technomancer.crossroads.blocks.alchemy;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Pair;

import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.ModConfig;
import com.Da_Technomancer.crossroads.API.MiscOp;
import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import com.Da_Technomancer.crossroads.items.ModItems;
import com.Da_Technomancer.crossroads.tileentities.alchemy.HeatedTubeTileEntity;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class HeatedTube extends BlockContainer{


	private static final AxisAlignedBB BB_X = new AxisAlignedBB(0, .25D, .25D, 1, .75D, .75D);
	private static final AxisAlignedBB BB_Z = new AxisAlignedBB(.25D, .25D, 0, .75D, .75D, 1);
	private static final AxisAlignedBB BB_VERT = new AxisAlignedBB(.25D, 0, .25D, .75D, 1, .75D);

	public HeatedTube(){
		super(Material.GLASS);
		String name = "heated_tube";
		setUnlocalizedName(name);
		setRegistryName(name);
		setHardness(.5F);
		setCreativeTab(ModItems.tabCrossroads);
		setSoundType(SoundType.GLASS);
		ModBlocks.toRegister.add(this);
		Item item = new ItemBlock(this){
			@Override
			public String getUnlocalizedName(ItemStack stack){
				return stack.getMetadata() == 1 ? "tile." + name + "_cryst" : "tile." + name + "_glass";
			}
			
			@Override
			public int getMetadata(int damage){
				return damage;
			}
		}.setMaxDamage(0).setHasSubtypes(true);
		item.setRegistryName(name);
		ModItems.toRegister.add(item);
		ModItems.toClientRegister.put(Pair.of(item, 0), new ModelResourceLocation(Main.MODID + ':' + name + "_glass", "inventory"));
		ModItems.toClientRegister.put(Pair.of(item, 1), new ModelResourceLocation(Main.MODID + ':' + name + "_cryst", "inventory"));
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta){
		return new HeatedTubeTileEntity((meta & 1) == 0);
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state){
		return EnumBlockRenderType.MODEL;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer(){
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public int getMetaFromState(IBlockState state){
		return (state.getValue(Properties.LIGHT) ? 1 : 0) + state.getValue(Properties.HORIZONTAL_FACING).getHorizontalIndex() << 1;
	}

	@Override
	public IBlockState getStateFromMeta(int meta){
		return getDefaultState().withProperty(Properties.LIGHT, (meta & 1) == 1).withProperty(Properties.HORIZONTAL_FACING, EnumFacing.getHorizontal(meta >> 1));
	}
	
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ){
		if(ModConfig.isWrench(playerIn.getHeldItem(hand), worldIn.isRemote)){
			if(!worldIn.isRemote){
				worldIn.setBlockState(pos, state.cycleProperty(Properties.HORIZONTAL_FACING));
			}
			return true;
		}
		return false;
	}

	@Override
	public int damageDropped(IBlockState state){
		return state.getValue(Properties.LIGHT) ? 1 : 0;
	}

	@Override
	protected BlockStateContainer createBlockState(){
		//On this device, light is being re-used. True means crystal, false means glass. 
		return new BlockStateContainer(this, new IProperty[] {Properties.LIGHT, Properties.HORIZONTAL_FACING});
	}

	@Override
	public boolean isOpaqueCube(IBlockState state){
		return false;
	}
	
	@Override
	public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB mask, List<AxisAlignedBB> list, Entity collidingEntity, boolean pleaseDontBeRelevantToAnythingOrIWillBeSad){
		addCollisionBoxToList(pos, mask, list, BB_VERT);
		
		if(state.getValue(Properties.HORIZONTAL_FACING).getAxis() == EnumFacing.Axis.X){
			addCollisionBoxToList(pos, mask, list, BB_X);
		}else{
			addCollisionBoxToList(pos, mask, list, BB_Z);
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World source, BlockPos pos){
		ArrayList<AxisAlignedBB> list = new ArrayList<AxisAlignedBB>();
		if(state.getValue(Properties.HORIZONTAL_FACING).getAxis() == EnumFacing.Axis.X){
			list.add(BB_X);
		}else{
			list.add(BB_Z);
		}
		
		EntityPlayer play = Minecraft.getMinecraft().player;
		float reDist = Minecraft.getMinecraft().playerController.getBlockReachDistance();
		Vec3d start = play.getPositionEyes(0F).subtract((double)pos.getX(), (double)pos.getY(), (double)pos.getZ());
		Vec3d end = start.addVector(play.getLook(0F).x * reDist, play.getLook(0F).y * reDist, play.getLook(0F).z * reDist);
		AxisAlignedBB out = MiscOp.rayTraceMulti(list, start, end);
		return (out == null ? BB_VERT : out).offset(pos);
	}

	@Override
	@Nullable
	public RayTraceResult collisionRayTrace(IBlockState state, World worldIn, BlockPos pos, Vec3d start, Vec3d end){
		ArrayList<AxisAlignedBB> list = new ArrayList<AxisAlignedBB>();
		list.add(BB_VERT);
		if(state.getValue(Properties.HORIZONTAL_FACING).getAxis() == EnumFacing.Axis.X){
			list.add(BB_X);
		}else{
			list.add(BB_Z);
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
	public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing blockFaceClickedOn, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer){
		return getStateFromMeta(meta).withProperty(Properties.HORIZONTAL_FACING, (placer == null) ? EnumFacing.NORTH : placer.getHorizontalFacing().getOpposite());
	}
	
	@Override
	public boolean isFullCube(IBlockState state){
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list){
		list.add(new ItemStack(this, 1, 0));
		list.add(new ItemStack(this, 1, 1));
	}
}
