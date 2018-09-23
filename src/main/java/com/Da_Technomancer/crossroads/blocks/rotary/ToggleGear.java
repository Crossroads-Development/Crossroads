package com.Da_Technomancer.crossroads.blocks.rotary;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.MiscOp;
import com.Da_Technomancer.crossroads.API.rotary.GearTypes;
import com.Da_Technomancer.crossroads.CommonProxy;
import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import com.Da_Technomancer.crossroads.items.ModItems;
import com.Da_Technomancer.crossroads.tileentities.rotary.ToggleGearTileEntity;
import com.Da_Technomancer.essentials.blocks.EssentialsProperties;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class ToggleGear extends BlockContainer{

	private static final AxisAlignedBB DOWN = new AxisAlignedBB(0D, 0D, 0D, 1D, .125D, 1D);
	private static final AxisAlignedBB UP = new AxisAlignedBB(0D, .5625D, 0D, 1D, .625D, 1D);
	private final GearTypes type;
	private static final ModelResourceLocation LOCAT = new ModelResourceLocation(Main.MODID + ":gear_base_toggle", "inventory");

	public ToggleGear(GearTypes type){
		super(Material.IRON);
		this.type = type;
		String name = "toggle_gear_" + type.toString().toLowerCase();
		setUnlocalizedName(name);
		setRegistryName(name);
		setCreativeTab(ModItems.TAB_GEAR);
		setHardness(3);
		setSoundType(SoundType.METAL);
		ModBlocks.toRegister.add(this);
		ModBlocks.blockAddQue(this, 0, LOCAT);
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack){
		neighborChanged(state, worldIn, pos, null, null);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag advanced){
		tooltip.add("I: " + MiscOp.betterRound(type.getDensity() / 8D, 2) * .125D);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta){
		return new ToggleGearTileEntity(type);
	}

	@Override
	public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing blockFaceClickedOn, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer){
		CommonProxy.masterKey++;
		return getDefaultState().withProperty(EssentialsProperties.REDSTONE_BOOL, worldIn.isBlockPowered(pos));
	}

	@Override
	public boolean hasComparatorInputOverride(IBlockState state){
		return true;
	}

	@Override
	public int getComparatorInputOverride(IBlockState blockState, World worldIn, BlockPos pos){
		TileEntity te = worldIn.getTileEntity(pos);
		if(!te.hasCapability(Capabilities.AXLE_HANDLER_CAPABILITY, EnumFacing.DOWN)){
			return 0;
		}
		double holder = Math.pow(te.getCapability(Capabilities.AXLE_HANDLER_CAPABILITY, EnumFacing.DOWN).getMotionData()[0], 2) / 2D;
		holder *= 15D;
		holder = Math.min(15, holder);

		return (int) holder;
	}

	@Override
	protected BlockStateContainer createBlockState(){
		return new BlockStateContainer(this, EssentialsProperties.REDSTONE_BOOL);
	}

	@Override
	public IBlockState getStateFromMeta(int meta){
		return getDefaultState().withProperty(EssentialsProperties.REDSTONE_BOOL, meta == 1);
	}

	@Override
	public int getMetaFromState(IBlockState state){
		return state.getValue(EssentialsProperties.REDSTONE_BOOL) ? 1 : 0;
	}


	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos){
		return state.getValue(EssentialsProperties.REDSTONE_BOOL) ? DOWN : UP;
	}

	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos prevPos){
		if(worldIn.isRemote){
			return;
		}
		if(worldIn.isBlockPowered(pos)){
			if(!state.getValue(EssentialsProperties.REDSTONE_BOOL)){
				worldIn.setBlockState(pos, state.withProperty(EssentialsProperties.REDSTONE_BOOL, true));
				worldIn.playSound(null, pos, SoundEvents.BLOCK_LEVER_CLICK, SoundCategory.BLOCKS, .3F, .6F);
				worldIn.updateComparatorOutputLevel(pos, this);
			}
		}else if(state.getValue(EssentialsProperties.REDSTONE_BOOL)){
			worldIn.setBlockState(pos, state.withProperty(EssentialsProperties.REDSTONE_BOOL, false));
			worldIn.playSound(null, pos, SoundEvents.BLOCK_LEVER_CLICK, SoundCategory.BLOCKS, .3F, .5F);
			worldIn.updateComparatorOutputLevel(pos, this);
		}
		CommonProxy.masterKey++;
	}

	@Override
	public boolean isFullCube(IBlockState state){
		return false;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state){
		return false;
	}
}
