package com.Da_Technomancer.crossroads.blocks.alchemy;

import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import com.Da_Technomancer.crossroads.items.ModItems;
import com.Da_Technomancer.crossroads.tileentities.alchemy.FlowLimiterTileEntity;
import com.Da_Technomancer.essentials.EssentialsConfig;
import com.Da_Technomancer.essentials.blocks.EssentialsProperties;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.tuple.Pair;

public class FlowLimiter extends BlockContainer{


	private static final AxisAlignedBB BB_X = new AxisAlignedBB(0, .25D, .25D, 1, .75D, .75D);
	private static final AxisAlignedBB BB_Y = new AxisAlignedBB(.25D, 0, .25D, .75D, 1, .75D);
	private static final AxisAlignedBB BB_Z = new AxisAlignedBB(.25D, .25D, 0, .75D, .75D, 1);

	public FlowLimiter(){
		super(Material.GLASS);
		String name = "flow_limiter";
		setTranslationKey(name);
		setRegistryName(name);
		setHardness(.5F);
		setCreativeTab(ModItems.TAB_CROSSROADS);
		setSoundType(SoundType.GLASS);
		ModBlocks.toRegister.add(this);
		Item item = new ItemBlock(this){
			@Override
			public String getTranslationKey(ItemStack stack){
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
		return new FlowLimiterTileEntity((meta & 1) == 0);
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state){
		return EnumBlockRenderType.MODEL;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getRenderLayer(){
		return BlockRenderLayer.TRANSLUCENT;
	}

	@Override
	public int getMetaFromState(IBlockState state){
		return (state.getValue(Properties.CRYSTAL) ? 1 : 0) + (state.getValue(EssentialsProperties.FACING).getIndex() << 1);
	}

	@Override
	public IBlockState getStateFromMeta(int meta){
		return getDefaultState().withProperty(Properties.CRYSTAL, (meta & 1) == 1).withProperty(EssentialsProperties.FACING, EnumFacing.byIndex(meta >> 1));
	}
	
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ){
		if(EssentialsConfig.isWrench(playerIn.getHeldItem(hand), worldIn.isRemote)){
			if(!worldIn.isRemote){
				if(playerIn.isSneaking()){
					TileEntity te = worldIn.getTileEntity(pos);
					
					if(te instanceof FlowLimiterTileEntity){
						((FlowLimiterTileEntity) te).cycleLimit((EntityPlayerMP) playerIn);
					}
				}else{
					worldIn.setBlockState(pos, state.cycleProperty(EssentialsProperties.FACING));
				}
			}
			return true;
		}
		return false;
	}

	@Override
	public int damageDropped(IBlockState state){
		return state.getValue(Properties.CRYSTAL) ? 1 : 0;
	}

	@Override
	protected BlockStateContainer createBlockState(){
		return new BlockStateContainer(this, Properties.CRYSTAL, EssentialsProperties.FACING);
	}

	@Override
	public boolean isOpaqueCube(IBlockState state){
		return false;
	}
	
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos){
		Axis axis = state.getValue(EssentialsProperties.FACING).getAxis();
		return axis == Axis.X ? BB_X : axis == Axis.Y ? BB_Y : BB_Z;
	}

	@Override
	public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing blockFaceClickedOn, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer){
		return getStateFromMeta(meta).withProperty(EssentialsProperties.FACING, (placer == null) ? EnumFacing.NORTH : EnumFacing.getDirectionFromEntityLiving(pos, placer));
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

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face){
		return BlockFaceShape.UNDEFINED;
	}
}
