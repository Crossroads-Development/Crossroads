package com.Da_Technomancer.crossroads.blocks.technomancy;

import com.Da_Technomancer.crossroads.API.packets.ModPackets;
import com.Da_Technomancer.crossroads.API.packets.SendDoubleArrayToClient;
import com.Da_Technomancer.crossroads.API.packets.SendIntToClient;
import com.Da_Technomancer.crossroads.API.redstone.RedstoneUtil;
import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import com.Da_Technomancer.crossroads.gui.GuiHandler;
import com.Da_Technomancer.crossroads.items.ModItems;
import com.Da_Technomancer.crossroads.tileentities.technomancy.RedstoneRegistryTileEntity;
import com.Da_Technomancer.essentials.blocks.EssentialsProperties;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class RedstoneRegistry extends BlockContainer{
	
	public RedstoneRegistry(){
		super(Material.IRON);
		String name = "redstone_registry";
		setTranslationKey(name);
		setRegistryName(name);
		setCreativeTab(ModItems.TAB_CROSSROADS);
		setHardness(3);
		ModBlocks.toRegister.add(this);
		ModBlocks.blockAddQue(this);
	}
	
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ){
		if(!worldIn.isRemote){
			ModPackets.network.sendTo(new SendDoubleArrayToClient("output", ((RedstoneRegistryTileEntity) worldIn.getTileEntity(pos)).getOutput(), pos), (EntityPlayerMP) playerIn);
			ModPackets.network.sendTo(new SendIntToClient(0, ((RedstoneRegistryTileEntity) worldIn.getTileEntity(pos)).getIndex(), pos), (EntityPlayerMP) playerIn);
			playerIn.openGui(Main.instance, GuiHandler.REDSTONE_REGISTRY_GUI, worldIn, pos.getX(), pos.getY(), pos.getZ());
		}
		return true;
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn){
		tooltip.add("Stores a list of redstone outputs, and cycles between them on a pulse");
	}

//	@Override
//	public boolean canProvidePower(IBlockState state){
//		return true;
//	}

//	@Override
//	public int getWeakPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side){
//		TileEntity te = blockAccess.getTileEntity(pos);
//		return Math.min(15, (int) Math.round(((RedstoneRegistryTileEntity) te).getOutput()[((RedstoneRegistryTileEntity) te).getIndex()]));
//	}
	
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta){
		return new RedstoneRegistryTileEntity();
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack){
		neighborChanged(state, world, pos, this, pos);
	}

	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos){
		double power = RedstoneUtil.getPowerAtPos(worldIn, pos);
		if(power > 0){
			if(!state.getValue(EssentialsProperties.REDSTONE_BOOL)){
				worldIn.setBlockState(pos, state.withProperty(EssentialsProperties.REDSTONE_BOOL, true));
				((RedstoneRegistryTileEntity) worldIn.getTileEntity(pos)).activate(power);
			}
		}else{
			if(state.getValue(EssentialsProperties.REDSTONE_BOOL)){
				worldIn.setBlockState(pos, state.withProperty(EssentialsProperties.REDSTONE_BOOL, false));
			}
		}
	}
	
	@Override
	public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing blockFaceClickedOn, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer){
		return getDefaultState().withProperty(EssentialsProperties.REDSTONE_BOOL, false);
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
	public EnumBlockRenderType getRenderType(IBlockState state){
		return EnumBlockRenderType.MODEL;
	}
}
