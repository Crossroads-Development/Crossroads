package com.Da_Technomancer.crossroads.blocks.technomancy;

import com.Da_Technomancer.crossroads.API.packets.ModPackets;
import com.Da_Technomancer.crossroads.API.packets.SendDoubleArrayToClient;
import com.Da_Technomancer.crossroads.API.packets.SendIntToClient;
import com.Da_Technomancer.crossroads.API.redstone.RedstoneUtil;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.blocks.CrossroadsBlocks;
import com.Da_Technomancer.crossroads.gui.GuiHandler;
import com.Da_Technomancer.crossroads.items.CrossroadsItems;
import com.Da_Technomancer.crossroads.tileentities.technomancy.RedstoneRegistryTileEntity;
import com.Da_Technomancer.essentials.blocks.EssentialsProperties;
import net.minecraft.block.*;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.block.BlockRenderType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class RedstoneRegistry extends ContainerBlock{
	
	public RedstoneRegistry(){
		super(Material.IRON);
		String name = "redstone_registry";
		setTranslationKey(name);
		setRegistryName(name);
		setCreativeTab(CrossroadsItems.TAB_CROSSROADS);
		setHardness(3);
		CrossroadsBlocks.toRegister.add(this);
		CrossroadsBlocks.blockAddQue(this);
	}
	
	@Override
	public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity playerIn, Hand hand, BlockRayTraceResult hit){
		if(!worldIn.isRemote){
			ModPackets.network.sendTo(new SendDoubleArrayToClient("output", ((RedstoneRegistryTileEntity) worldIn.getTileEntity(pos)).getOutput(), pos), (ServerPlayerEntity) playerIn);
			ModPackets.network.sendTo(new SendIntToClient((byte) 0, ((RedstoneRegistryTileEntity) worldIn.getTileEntity(pos)).getIndex(), pos), (ServerPlayerEntity) playerIn);
			playerIn.openGui(Crossroads.instance, GuiHandler.REDSTONE_REGISTRY_GUI, worldIn, pos.getX(), pos.getY(), pos.getZ());
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
	public TileEntity createNewTileEntity(IBlockReader worldIn){
		return new RedstoneRegistryTileEntity();
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack){
		neighborChanged(state, world, pos, this, pos);
	}

	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos){
		double power = RedstoneUtil.getPowerAtPos(worldIn, pos);
		if(power > 0){
			if(!state.get(EssentialsProperties.REDSTONE_BOOL)){
				worldIn.setBlockState(pos, state.with(EssentialsProperties.REDSTONE_BOOL, true));
				((RedstoneRegistryTileEntity) worldIn.getTileEntity(pos)).activate(power);
			}
		}else{
			if(state.get(EssentialsProperties.REDSTONE_BOOL)){
				worldIn.setBlockState(pos, state.with(EssentialsProperties.REDSTONE_BOOL, false));
			}
		}
	}
	
	@Override
	public BlockState getStateForPlacement(World worldIn, BlockPos pos, Direction blockFaceClickedOn, BlockRayTraceResult hit, int meta, LivingEntity placer){
		return getDefaultState().with(EssentialsProperties.REDSTONE_BOOL, false);
	}

	@Override
	protected BlockStateContainer createBlockState(){
		return new BlockStateContainer(this, EssentialsProperties.REDSTONE_BOOL);
	}

	@Override
	public BlockState getStateFromMeta(int meta){
		return getDefaultState().with(EssentialsProperties.REDSTONE_BOOL, meta == 1);
	}

	@Override
	public int getMetaFromState(BlockState state){
		return state.get(EssentialsProperties.REDSTONE_BOOL) ? 1 : 0;
	}
	
	@Override
	public BlockRenderType getRenderType(BlockState state){
		return BlockRenderType.MODEL;
	}
}
