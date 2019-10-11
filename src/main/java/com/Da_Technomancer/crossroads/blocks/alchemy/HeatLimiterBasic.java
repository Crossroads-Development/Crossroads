package com.Da_Technomancer.crossroads.blocks.alchemy;

import com.Da_Technomancer.crossroads.API.packets.CrossroadsPackets;
import com.Da_Technomancer.crossroads.API.packets.SendDoubleToClient;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.blocks.CrossroadsBlocks;
import com.Da_Technomancer.crossroads.gui.GuiHandler;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.Da_Technomancer.crossroads.tileentities.alchemy.HeatLimiterBasicTileEntity;
import com.Da_Technomancer.essentials.EssentialsConfig;
import com.Da_Technomancer.essentials.blocks.EssentialsProperties;
import net.minecraft.block.*;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.BlockState;
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

public class HeatLimiterBasic extends ContainerBlock{

	public HeatLimiterBasic(){
		super(Material.IRON);
		String name = "heat_limiter_basic";
		setTranslationKey(name);
		setRegistryName(name);
		setHardness(.5F);
		setCreativeTab(CRItems.TAB_CROSSROADS);
		setSoundType(SoundType.METAL);
		CrossroadsBlocks.toRegister.add(this);
		CrossroadsBlocks.blockAddQue(this);
		setDefaultState(getDefaultState().with(Properties.ACTIVE, false));
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn){
		return new HeatLimiterBasicTileEntity();
	}

	@Override
	public BlockRenderType getRenderType(BlockState state){
		return BlockRenderType.MODEL;
	}

	@Override
	public int getMetaFromState(BlockState state){
		return state.get(EssentialsProperties.FACING).getIndex() | (state.get(Properties.ACTIVE) ? 8 : 0);
	}

	@Override
	public BlockState getStateFromMeta(int meta){
		return getDefaultState().with(EssentialsProperties.FACING, Direction.byIndex(meta & 7)).with(Properties.ACTIVE, (meta & 8) != 0);
	}

	@Override
	public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity playerIn, Hand hand, BlockRayTraceResult hit){
		if(!worldIn.isRemote){
			if(EssentialsConfig.isWrench(playerIn.getHeldItem(hand), false)){
				if(playerIn.isSneaking()){
					worldIn.setBlockState(pos, state.cycle(Properties.ACTIVE));
				}else{
					worldIn.setBlockState(pos, state.cycle(EssentialsProperties.FACING));
				}
			}else{
				CrossroadsPackets.network.sendTo(new SendDoubleToClient("new_setting", ((HeatLimiterBasicTileEntity) worldIn.getTileEntity(pos)).getSetting(), pos), (ServerPlayerEntity) playerIn);
				playerIn.openGui(Crossroads.instance, GuiHandler.HEAT_LIMITER_BASIC_GUI, worldIn, pos.getX(), pos.getY(), pos.getZ());
			}
		}
		return true;
	}

	@Override
	protected BlockStateContainer createBlockState(){
		return new BlockStateContainer(this, EssentialsProperties.FACING, Properties.ACTIVE);
	}

	@Override
	public BlockState getStateForPlacement(World worldIn, BlockPos pos, Direction blockFaceClickedOn, BlockRayTraceResult hit, int meta, LivingEntity placer){
		return getDefaultState().with(EssentialsProperties.FACING, (placer == null) ? Direction.NORTH : Direction.getDirectionFromEntityLiving(pos, placer));
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn){
		tooltip.add("Connects two heat cables");
		tooltip.add("Only allows heat to flow until the front reaches the target temperature");
		tooltip.add("In red mode, stops when hotter than target; In blue mode, stops when colder than target");
		tooltip.add("Target temperature in Kelvin set in UI");
	}
}
