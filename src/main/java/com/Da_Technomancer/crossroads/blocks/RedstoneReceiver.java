package com.Da_Technomancer.crossroads.blocks;

import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.items.ModItems;
import com.Da_Technomancer.crossroads.tileentities.RedstoneReceiverTileEntity;
import com.Da_Technomancer.essentials.EssentialsConfig;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class RedstoneReceiver extends BlockContainer{

	protected RedstoneReceiver(){
		super(Material.ROCK);
		String name = "redstone_receiver";
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
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ){
		ItemStack heldItem = playerIn.getHeldItem(hand);
		TileEntity te = worldIn.getTileEntity(pos);
		if(EssentialsConfig.isWrench(heldItem, false) && te instanceof RedstoneReceiverTileEntity){
			if(!worldIn.isRemote){
				((RedstoneReceiverTileEntity) te).wrench(heldItem, playerIn);
			}
			return true;
		}else if(heldItem.getItem() == Items.DYE && te instanceof RedstoneReceiverTileEntity){
			if(!worldIn.isRemote){
				((RedstoneReceiverTileEntity) te).dye(EnumDyeColor.byDyeDamage(heldItem.getMetadata()));
			}
			return true;
		}
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag advanced){
		tooltip.add("Receives redstone signals wirelessly from a nearby linked Redstone Transmitter");
		tooltip.add("Link to a Transmitter with a wrench. Use it on a Transmitter first then a Receiver");
		tooltip.add("Can be color coded with dyes");
	}

	@Nullable
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta){
		return new RedstoneReceiverTileEntity();
	}

	@Override
	public int getWeakPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side){
		TileEntity te = blockAccess.getTileEntity(pos);
		if(te instanceof RedstoneReceiverTileEntity){
			return (int) Math.round(((RedstoneReceiverTileEntity) te).getStrength());
		}

		return super.getWeakPower(blockState, blockAccess, pos, side);
	}

	@Override
	public boolean canProvidePower(IBlockState state){
		return true;
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
