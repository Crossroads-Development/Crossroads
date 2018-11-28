package com.Da_Technomancer.crossroads.blocks;

import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.ModConfig;
import com.Da_Technomancer.crossroads.items.ModItems;
import com.Da_Technomancer.crossroads.tileentities.RedstoneReceiverTileEntity;
import com.Da_Technomancer.crossroads.tileentities.RedstoneTransmitterTileEntity;
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
import net.minecraft.util.text.TextComponentString;
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
		if(!worldIn.isRemote){
			ItemStack heldItem = playerIn.getHeldItem(hand);
			if(EssentialsConfig.isWrench(heldItem, false)){
				if(heldItem.hasTagCompound() && heldItem.getTagCompound().hasKey("c_link")){
					BlockPos prev = BlockPos.fromLong(heldItem.getTagCompound().getLong("c_link"));
					TileEntity te = worldIn.getTileEntity(prev);
					if(te instanceof RedstoneTransmitterTileEntity){
						int range = ModConfig.getConfigInt(ModConfig.redstoneTransmitterRange, false);
						if(prev.distanceSq(pos) <= range * range){
							((RedstoneTransmitterTileEntity) te).link(pos);
							playerIn.sendMessage(new TextComponentString("Linked transmitter at " + prev + " to send to " + pos + "."));
							heldItem.getTagCompound().removeTag("c_link");
						}else{
							playerIn.sendMessage(new TextComponentString("Out of range; Canceling linking."));
							heldItem.getTagCompound().removeTag("c_link");
						}
					}else{
						playerIn.sendMessage(new TextComponentString("Invalid pair; Canceling linking."));
						heldItem.getTagCompound().removeTag("c_link");
					}
				}
			}else if(heldItem.getItem() == Items.DYE){
				TileEntity te = worldIn.getTileEntity(pos);
				if(te instanceof RedstoneReceiverTileEntity){
					((RedstoneReceiverTileEntity) te).dye(EnumDyeColor.byDyeDamage(heldItem.getMetadata()));
				}
			}
		}
		return true;
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
