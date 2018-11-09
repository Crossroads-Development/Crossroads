package com.Da_Technomancer.crossroads.blocks.alchemy;

import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import com.Da_Technomancer.crossroads.items.ModItems;
import com.Da_Technomancer.crossroads.tileentities.alchemy.DensusPlateTileEntity;
import com.Da_Technomancer.essentials.EssentialsConfig;
import com.Da_Technomancer.essentials.blocks.EssentialsProperties;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public class DensusPlate extends BlockContainer{

	private static final AxisAlignedBB BBNORTH = new AxisAlignedBB(0, 0, 0.5D, 1, 1, 1);
	private static final AxisAlignedBB BBSOUTH = new AxisAlignedBB(0, 0, 0, 1, 1, 0.5D);
	private static final AxisAlignedBB BBWEST = new AxisAlignedBB(0.5D, 0, 0, 1, 1, 1);
	private static final AxisAlignedBB BBEAST = new AxisAlignedBB(0, 0, 0, 0.5D, 1, 1);
	private static final AxisAlignedBB BBUP = new AxisAlignedBB(0, 0, 0, 1, 0.5D, 1);
	private static final AxisAlignedBB BBDOWN = new AxisAlignedBB(0, 0.5D, 0, 1, 1, 1);

	public DensusPlate(){
		//TODO flatten for 1.13

		super(Material.IRON);
		String name = "densus_plate";
		setUnlocalizedName(name);
		setRegistryName(name);
		setCreativeTab(ModItems.TAB_CROSSROADS);
		setHardness(3);
		Item item = new ItemBlock(this){
			@Override
			public String getUnlocalizedName(ItemStack stack){
				return stack.getMetadata() == 1 ? "tile.anti_densus_plate" : "tile.densus_plate";
			}
			
			@Override
			public int getMetadata(int damage){
				return damage;
			}
		}.setMaxDamage(0).setHasSubtypes(true);
		item.setRegistryName(name);
		ModBlocks.toRegister.add(this);
		ModItems.toRegister.add(item);
		ModItems.toClientRegister.put(Pair.of(item, 0), new ModelResourceLocation(Main.MODID + ":densus_plate", "inventory"));
		ModItems.toClientRegister.put(Pair.of(item, 1), new ModelResourceLocation(Main.MODID + ":anti_densus_plate", "inventory"));
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ){
		if(EssentialsConfig.isWrench(playerIn.getHeldItem(hand), worldIn.isRemote)){
			if(!worldIn.isRemote){
				worldIn.setBlockState(pos, state.cycleProperty(EssentialsProperties.FACING));
			}
			return true;
		}
		return false;
	}

	@Override
	public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing blockFaceClickedOn, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer){
		return getDefaultState().withProperty(EssentialsProperties.FACING, (placer == null) ? EnumFacing.NORTH : EnumFacing.getDirectionFromEntityLiving(pos, placer)).withProperty(Properties.CONTAINER_TYPE, (meta & 1) == 1);
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos){
		switch(state.getValue(EssentialsProperties.FACING)){
			case EAST:
				return BBEAST;
			case SOUTH:
				return BBSOUTH;
			case WEST:
				return BBWEST;
			case NORTH:
				return BBNORTH;
			case UP:
				return BBUP;
			default:
				return BBDOWN;
		}
	}

	@Override
	public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB mask, List<AxisAlignedBB> list, Entity collidingEntity, boolean thingamijiger){
		addCollisionBoxToList(pos, mask, list, getBoundingBox(state, worldIn, pos));
	}

	@Override
	public int damageDropped(IBlockState state){
		return state.getValue(Properties.CONTAINER_TYPE) ? 1 : 0;
	}

	@Override
	protected BlockStateContainer createBlockState(){
		return new BlockStateContainer(this, EssentialsProperties.FACING, Properties.CONTAINER_TYPE);
	}

	@Override
	public IBlockState getStateFromMeta(int meta){
		return getDefaultState().withProperty(EssentialsProperties.FACING, EnumFacing.getFront(meta >> 1)).withProperty(Properties.CONTAINER_TYPE, (meta & 1) == 1);
	}

	@Override
	public int getMetaFromState(IBlockState state){
		return (state.getValue(EssentialsProperties.FACING).getIndex() << 1) + (state.getValue(Properties.CONTAINER_TYPE) ? 1 : 0);
	}

	@Override
	public boolean isOpaqueCube(IBlockState state){
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state){
		return false;
	}
	
	@Override
	public EnumBlockRenderType getRenderType(IBlockState state){
		return EnumBlockRenderType.MODEL;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list){
		list.add(new ItemStack(this, 1, 0));
		list.add(new ItemStack(this, 1, 1));
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta){
		return new DensusPlateTileEntity();
	}
}
