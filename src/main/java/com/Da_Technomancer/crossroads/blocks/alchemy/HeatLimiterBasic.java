package com.Da_Technomancer.crossroads.blocks.alchemy;

import com.Da_Technomancer.crossroads.API.CRProperties;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.tileentities.alchemy.HeatLimiterBasicTileEntity;
import com.Da_Technomancer.essentials.EssentialsConfig;
import com.Da_Technomancer.essentials.blocks.EssentialsProperties;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.List;

public class HeatLimiterBasic extends ContainerBlock{

	public HeatLimiterBasic(){
		super(Properties.create(Material.IRON).hardnessAndResistance(0.5F).sound(SoundType.STONE));
		String name = "heat_limiter_basic";
		setRegistryName(name);
		CRBlocks.toRegister.add(this);
		CRBlocks.blockAddQue(this);
		setDefaultState(getDefaultState().with(CRProperties.ACTIVE, false));
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
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder){
		builder.add(CRProperties.ACTIVE, EssentialsProperties.FACING);
	}

	@Override
	public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity playerIn, Hand hand, BlockRayTraceResult hit){
		if(!worldIn.isRemote){
			TileEntity te;
			if(EssentialsConfig.isWrench(playerIn.getHeldItem(hand))){
				if(playerIn.isSneaking()){
					worldIn.setBlockState(pos, state.cycle(CRProperties.ACTIVE));
				}else{
					worldIn.setBlockState(pos, state.cycle(EssentialsProperties.FACING));
				}
			}else if((te = worldIn.getTileEntity(pos)) instanceof HeatLimiterBasicTileEntity){
				NetworkHooks.openGui((ServerPlayerEntity) playerIn, (INamedContainerProvider) te, buf -> {buf.writeFloat(((HeatLimiterBasicTileEntity) te).setting); buf.writeString(((HeatLimiterBasicTileEntity) te).expression); buf.writeBlockPos(pos);});
			}
		}
		return true;
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context){
		return getDefaultState().with(EssentialsProperties.FACING, context.getNearestLookingDirection());
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn){
		tooltip.add(new TranslationTextComponent("tt.crossroads.heat_limiter.desc_cable"));
		tooltip.add(new TranslationTextComponent("tt.crossroads.heat_limiter.desc_purpose"));
		tooltip.add(new TranslationTextComponent("tt.crossroads.heat_limiter.desc_mode"));
		tooltip.add(new TranslationTextComponent("tt.crossroads.heat_limiter.desc_ui"));
	}
}
