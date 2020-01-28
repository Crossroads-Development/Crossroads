package com.Da_Technomancer.crossroads.blocks.technomancy;

import com.Da_Technomancer.crossroads.API.FlexibleGameProfile;
import com.Da_Technomancer.crossroads.API.technomancy.FluxUtil;
import com.Da_Technomancer.crossroads.API.templates.ILinkTE;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.Da_Technomancer.crossroads.tileentities.technomancy.GatewayFrameTileEntity;
import com.Da_Technomancer.essentials.EssentialsConfig;
import com.Da_Technomancer.essentials.blocks.EssentialsProperties;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;

import javax.annotation.Nullable;
import java.util.List;

public class GatewayFrame extends ContainerBlock{

	public GatewayFrame(){
		super(Properties.create(Material.IRON).hardnessAndResistance(3).sound(SoundType.METAL));
		String name = "gateway_frame";
		setRegistryName(name);
		CRBlocks.toRegister.add(this);
		CRBlocks.blockAddQue(this);
	}

	@Override
	public BlockState getStateForPlacement(World worldIn, BlockPos pos, Direction blockFaceClickedOn, BlockRayTraceResult hit, int meta, LivingEntity placer){
		Direction facing = (placer == null) ? Direction.NORTH : Direction.getDirectionFromEntityLiving(pos, placer);
		if(facing == Direction.UP){
			facing = Direction.DOWN;
		}
		return getDefaultState().with(EssentialsProperties.FACING, placer instanceof FakePlayer ? Direction.NORTH : facing);
	}

	@Override
	public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity playerIn, Hand hand, BlockRayTraceResult hit){
		ItemStack heldItem = playerIn.getHeldItem(hand);
		if(FluxUtil.handleFluxLinking(worldIn, pos, heldItem, playerIn)){
			return true;
		}else if(EssentialsConfig.isWrench(heldItem)){
			worldIn.setBlockState(pos, state.cycle(EssentialsProperties.FACING));//TODO check
			TileEntity te = worldIn.getTileEntity(pos);
			if(!worldIn.isRemote && te instanceof GatewayFrameTileEntity){
				((GatewayFrameTileEntity) te).resetCache();
			}
			return true;
		}
		return false;
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack){
		if(!worldIn.isRemote){
			TileEntity te = worldIn.getTileEntity(pos);
			if(te instanceof GatewayFrameTileEntity){
				((GatewayFrameTileEntity) te).setOwner(!(placer instanceof PlayerEntity) ? null : new FlexibleGameProfile(((PlayerEntity) placer).getGameProfile()));
			}
		}
	}

	@Override
	protected BlockStateContainer createBlockState(){
		return new BlockStateContainer(this, EssentialsProperties.FACING);
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn){
		return new GatewayFrameTileEntity();

	}

	@Override
	public BlockRenderType getRenderType(BlockState state){
		return BlockRenderType.MODEL;
	}
	
	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn){
		tooltip.add("Opens a portal through space and between dimensions");
		tooltip.add("Uses a beam to maintain the portal");
		tooltip.add("Potential: Overworld; Energy: Nether; Void: The End");
		tooltip.add(String.format("Produces %1$.3f%% entropy/tick while running, and %2$.3f%% entropy for every entity teleported", EntropySavedData.getPercentage(GatewayFrameTileEntity.FLUX_MAINTAIN), EntropySavedData.getPercentage(GatewayFrameTileEntity.FLUX_TRANSPORT)));
	}
}
