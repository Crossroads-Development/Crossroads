package com.Da_Technomancer.crossroads.blocks.rotary;

import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import com.Da_Technomancer.crossroads.gui.GuiHandler;
import com.Da_Technomancer.crossroads.items.ModItems;
import com.Da_Technomancer.crossroads.tileentities.rotary.MillstoneTileEntity;
import com.Da_Technomancer.crossroads.tileentities.rotary.StampMillTileEntity;
import com.Da_Technomancer.essentials.EssentialsConfig;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class StampMill extends BlockContainer{

	public StampMill(){
		super(Material.IRON);
		String name = "stamp_mill";
		setUnlocalizedName(name);
		setRegistryName(name);
		setCreativeTab(ModItems.TAB_CROSSROADS);
		setHardness(3);
		ModBlocks.toRegister.add(this);
		ModBlocks.blockAddQue(this);
		setDefaultState(getDefaultState().withProperty(Properties.TEXTURE_4, 0));
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ){
		if(EssentialsConfig.isWrench(playerIn.getHeldItem(hand), worldIn.isRemote)){
			if(!worldIn.isRemote){
				worldIn.setBlockState(pos, state.cycleProperty(Properties.HORIZ_FACING));
			}
			return true;
		}

		if(!worldIn.isRemote){
			playerIn.openGui(Main.instance, GuiHandler.STAMP_MILL_GUI, worldIn, pos.getX(), pos.getY(), pos.getZ());
		}
		return true;
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta){
		return new MillstoneTileEntity();
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state){
		return EnumBlockRenderType.MODEL;
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState blockstate){
		InventoryHelper.dropInventoryItems(world, pos, (StampMillTileEntity) world.getTileEntity(pos));
		super.breakBlock(world, pos, blockstate);
	}

	@Override
	public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing blockFaceClickedOn, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer){
		return getDefaultState().withProperty(Properties.HORIZ_FACING, placer == null ? EnumFacing.EAST : placer.getHorizontalFacing().getOpposite());
	}

	@Override
	protected BlockStateContainer createBlockState(){
		return new BlockStateContainer(this, Properties.HORIZ_FACING, Properties.TEXTURE_4);
	}

	@Override
	public IBlockState getStateFromMeta(int meta){
		return getDefaultState().withProperty(Properties.HORIZ_FACING, EnumFacing.getHorizontal(meta & 3)).withProperty(Properties.TEXTURE_4, meta >> 2);
	}

	@Override
	public int getMetaFromState(IBlockState state){
		return state.getValue(Properties.HORIZ_FACING).getHorizontalIndex() + (state.getValue(Properties.TEXTURE_4) << 2);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag advanced){
		tooltip.add("I: 200");//TODO
		tooltip.add("Consumes: Up to 10J/t while running");
		tooltip.add("Reaches peak speed above 10 rad/s");
	}
}
