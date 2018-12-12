package com.Da_Technomancer.crossroads.blocks;

import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.API.redstone.RedstoneUtil;
import com.Da_Technomancer.crossroads.items.ModItems;
import com.Da_Technomancer.crossroads.tileentities.RedstoneTransmitterTileEntity;
import com.Da_Technomancer.essentials.EssentialsConfig;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockRedstoneDiode;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
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
import java.util.Random;

public class RedstoneTransmitter extends BlockContainer{

	protected RedstoneTransmitter(){
		super(Material.ROCK);
		String name = "redstone_transmitter";
		setTranslationKey(name);
		setRegistryName(name);
		setCreativeTab(ModItems.TAB_CROSSROADS);
		setHardness(.5F);
		setSoundType(SoundType.STONE);
		ModBlocks.toRegister.add(this);
		ModBlocks.blockAddQue(this);
		setDefaultState(getDefaultState().withProperty(Properties.COLOR, EnumDyeColor.WHITE));
	}

	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos){
		if(!worldIn.isBlockTickPending(pos, this)){
			int i = -1;

			if(BlockRedstoneDiode.isDiode(worldIn.getBlockState(fromPos))){
				i = -3;
			}
			worldIn.updateBlockTick(pos, this, 2, i);
		}
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack){
		neighborChanged(state, worldIn, pos, this, pos);
	}

	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand){
		TileEntity rawTE = worldIn.getTileEntity(pos);
		if(!(rawTE instanceof RedstoneTransmitterTileEntity)){
			return;
		}
		((RedstoneTransmitterTileEntity) rawTE).setOutput(RedstoneUtil.getPowerAtPos(worldIn, pos));
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ){
		ItemStack heldItem = playerIn.getHeldItem(hand);
		TileEntity te = worldIn.getTileEntity(pos);
		if(EssentialsConfig.isWrench(heldItem, false) && te instanceof RedstoneTransmitterTileEntity){
			if(!worldIn.isRemote){
				((RedstoneTransmitterTileEntity) te).wrench(heldItem, playerIn);
			}
			return true;
		}else if(heldItem.getItem() == Items.DYE && te instanceof RedstoneTransmitterTileEntity){
			if(!worldIn.isRemote){
				((RedstoneTransmitterTileEntity) te).dye(EnumDyeColor.byDyeDamage(heldItem.getMetadata()));
			}
			return true;
		}
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag advanced){
		tooltip.add("Transmits redstone signals wirelessly to nearby linked Redstone Receivers");
		tooltip.add("Link to Receivers with a wrench. Use it on a Transmitter first then a Receiver");
		tooltip.add("Can be color coded with dyes");
	}

	@Nullable
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta){
		return new RedstoneTransmitterTileEntity();
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state){
		return EnumBlockRenderType.MODEL;
	}

	@Override
	protected BlockStateContainer createBlockState(){
		return new BlockStateContainer(this, Properties.COLOR);
	}

	@Override
	public int getMetaFromState(IBlockState state){
		return state.getValue(Properties.COLOR).getMetadata();
	}

	@Override
	public IBlockState getStateFromMeta(int meta){
		return getDefaultState().withProperty(Properties.COLOR, EnumDyeColor.byMetadata(meta));
	}
}
