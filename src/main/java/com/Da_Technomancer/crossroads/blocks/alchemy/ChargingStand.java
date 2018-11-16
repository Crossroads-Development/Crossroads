package com.Da_Technomancer.crossroads.blocks.alchemy;

import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import com.Da_Technomancer.crossroads.items.ModItems;
import com.Da_Technomancer.crossroads.tileentities.alchemy.ChargingStandTileEntity;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class ChargingStand extends BlockContainer{

	public ChargingStand(){
		super(Material.IRON);
		String name = "charging_stand";
		setTranslationKey(name);
		setRegistryName(name);
		setHardness(2);
		setCreativeTab(ModItems.TAB_CROSSROADS);
		setSoundType(SoundType.METAL);
		ModBlocks.toRegister.add(this);
		ModBlocks.blockAddQue(this);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta){
		return new ChargingStandTileEntity();
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state){
		return EnumBlockRenderType.MODEL;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getRenderLayer(){
		return BlockRenderLayer.CUTOUT;
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
	public void breakBlock(World world, BlockPos pos, IBlockState blockstate){
		TileEntity te = world.getTileEntity(pos);
		if(te instanceof ChargingStandTileEntity){
			((ChargingStandTileEntity) te).onBlockDestroyed(blockstate);
		}
		super.breakBlock(world, pos, blockstate);
	}

	@Override
	public int getMetaFromState(IBlockState state){
		return (state.getValue(Properties.CRYSTAL) ? 1 : 0) + (state.getValue(Properties.ACTIVE) ? 2 : 0) + (state.getValue(Properties.CONTAINER_TYPE) ? 4 : 0);
	}

	@Override
	public IBlockState getStateFromMeta(int meta){
		return getDefaultState().withProperty(Properties.CRYSTAL, (meta & 1) == 1).withProperty(Properties.ACTIVE, (meta & 2) == 2).withProperty(Properties.CONTAINER_TYPE, (meta & 4) == 4);
	}

	@Override
	protected BlockStateContainer createBlockState(){
		return new BlockStateContainer(this, Properties.CRYSTAL, Properties.ACTIVE, Properties.CONTAINER_TYPE);
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ){
		if(!worldIn.isRemote){
			TileEntity te = worldIn.getTileEntity(pos);
			if(te instanceof ChargingStandTileEntity){
				playerIn.setHeldItem(hand, ((ChargingStandTileEntity) te).rightClickWithItem(playerIn.getHeldItem(hand), playerIn.isSneaking()));
			}
		}
		return true;
	}

	@SideOnly(Side.CLIENT)
	public void initModel(){
		StateMapperBase glasswareMapper = new StateMapperBase(){
			@Override
			protected ModelResourceLocation getModelResourceLocation(IBlockState state){
				if(state.getValue(Properties.CONTAINER_TYPE)){
					return new ModelResourceLocation(Main.MODID + ":charging_stand_florence", getPropertyString(state.getProperties()));
				}else{
					return new ModelResourceLocation(Main.MODID + ":charging_stand_phial", getPropertyString(state.getProperties()));
				}
			}
		};
		ModelLoader.setCustomStateMapper(this, glasswareMapper);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag advanced){
		tooltip.add("Consumes: -10FE/t");
	}
}
