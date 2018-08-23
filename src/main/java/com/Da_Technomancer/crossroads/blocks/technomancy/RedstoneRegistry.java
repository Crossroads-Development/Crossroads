package com.Da_Technomancer.crossroads.blocks.technomancy;

import com.Da_Technomancer.crossroads.API.packets.ModPackets;
import com.Da_Technomancer.crossroads.API.packets.SendDoubleArrayToClient;
import com.Da_Technomancer.crossroads.API.packets.SendIntToClient;
import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import com.Da_Technomancer.crossroads.blocks.Ratiator;
import com.Da_Technomancer.crossroads.gui.GuiHandler;
import com.Da_Technomancer.crossroads.items.ModItems;
import com.Da_Technomancer.crossroads.tileentities.technomancy.RedstoneRegistryTileEntity;
import com.Da_Technomancer.essentials.blocks.EssentialsProperties;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class RedstoneRegistry extends BlockContainer{
	
	public RedstoneRegistry(){
		super(Material.IRON);
		String name = "redstone_registry";
		setUnlocalizedName(name);
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
	public boolean hasComparatorInputOverride(IBlockState state){
		return true;
	}

	@Override
	public int getComparatorInputOverride(IBlockState blockState, World worldIn, BlockPos pos){
		TileEntity te = worldIn.getTileEntity(pos);
		return Math.min(15, (int) Math.round(((RedstoneRegistryTileEntity) te).getOutput()[((RedstoneRegistryTileEntity) te).getIndex()]));
	}
	
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta){
		return new RedstoneRegistryTileEntity();
	}
	
	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos){
		double power = 0;
		for(EnumFacing side : EnumFacing.values()){
			power = Math.max(power, Ratiator.getPowerOnSide(worldIn, pos, side, false));
		}
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
		return new BlockStateContainer(this, new IProperty[] {EssentialsProperties.REDSTONE_BOOL});
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
