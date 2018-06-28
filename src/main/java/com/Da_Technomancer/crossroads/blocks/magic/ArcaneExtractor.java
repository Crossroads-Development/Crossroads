package com.Da_Technomancer.crossroads.blocks.magic;

import java.util.List;

import javax.annotation.Nullable;

import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.API.magic.BeamRenderTE;
import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import com.Da_Technomancer.crossroads.items.ModItems;
import com.Da_Technomancer.crossroads.tileentities.magic.ArcaneExtractorTileEntity;

import com.Da_Technomancer.essentials.EssentialsConfig;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ArcaneExtractor extends BlockContainer{

	public ArcaneExtractor(){
		super(Material.ROCK);
		String name = "arcane_extractor";
		setUnlocalizedName(name);
		setRegistryName(name);
		setCreativeTab(ModItems.TAB_CROSSROADS);
		setHardness(3);
		ModBlocks.toRegister.add(this);
		ModBlocks.blockAddQue(this);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta){
		return new ArcaneExtractorTileEntity();
	}
	
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ){
		if(EssentialsConfig.isWrench(playerIn.getHeldItem(hand), worldIn.isRemote)){
			if(!worldIn.isRemote){
				TileEntity te = worldIn.getTileEntity(pos);
				worldIn.setBlockState(pos, state.withProperty(Properties.FACING, state.getValue(Properties.FACING).rotateY()));
				if(te instanceof BeamRenderTE){
					((BeamRenderTE) te).resetBeamer();
				}
			}
			return true;
		}
		return false;
	}
	
	@Override
	public EnumBlockRenderType getRenderType(IBlockState state){
		return EnumBlockRenderType.MODEL;
	}
	
	@Override
	public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing blockFaceClickedOn, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer){
		return getDefaultState().withProperty(Properties.FACING, (placer == null) ? EnumFacing.NORTH : placer.getHorizontalFacing().getOpposite());
	}
	
	@Override
	protected BlockStateContainer createBlockState(){
		return new BlockStateContainer(this, Properties.FACING);
	}
	
	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state){
		TileEntity te = worldIn.getTileEntity(pos);
		if(te instanceof BeamRenderTE){
			((BeamRenderTE) te).refresh();
		}
		super.breakBlock(worldIn, pos, state);
	}

	@Override
	public IBlockState getStateFromMeta(int meta){
		return getDefaultState().withProperty(Properties.FACING, EnumFacing.getFront(meta == 0 || meta == 1 ? 2 : meta));
	}

	@Override
	public int getMetaFromState(IBlockState state){
		return state.getValue(Properties.FACING).getIndex();
	}
	
	@Override
	public boolean isOpaqueCube(IBlockState state){
		return false;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag advanced){
		tooltip.add("Who knew charcoal has magical powers?");
	}
}
