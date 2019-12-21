package com.Da_Technomancer.crossroads.blocks.alchemy;

import com.Da_Technomancer.crossroads.API.CRProperties;
import com.Da_Technomancer.crossroads.API.alchemy.AtmosChargeSavedData;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.blocks.CrossroadsBlocks;
import com.Da_Technomancer.crossroads.tileentities.alchemy.AtmosChargerTileEntity;
import com.Da_Technomancer.essentials.EssentialsConfig;
import com.Da_Technomancer.essentials.blocks.redstone.IReadable;
import com.Da_Technomancer.essentials.blocks.redstone.RedstoneUtil;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
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
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class AtmosCharger extends ContainerBlock implements IReadable{

	public AtmosCharger(){
		super(Properties.create(Material.IRON).hardnessAndResistance(0.5F).sound(SoundType.METAL));
		String name = "atmos_charger";
		setRegistryName(name);
		CrossroadsBlocks.toRegister.add(this);
		CrossroadsBlocks.blockAddQue(this);
	}

	@Nullable
	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn){
		return new AtmosChargerTileEntity();
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder){
		builder.add(CRProperties.ACTIVE);
	}

	@Override
	public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity playerIn, Hand hand, BlockRayTraceResult hit){
		if(EssentialsConfig.isWrench(playerIn.getHeldItem(hand))){
			if(!worldIn.isRemote){
				worldIn.setBlockState(pos, state.cycle(CRProperties.ACTIVE));
				playerIn.sendMessage(new TranslationTextComponent(state.get(CRProperties.ACTIVE) ? "tt.crossroads.atmos_charger.charging" : "tt.crossroads.atmos_charger.draining"));
				TileEntity te = worldIn.getTileEntity(pos);
				if(te instanceof AtmosChargerTileEntity){
					((AtmosChargerTileEntity) te).resetCache();
				}
			}
			return true;
		}

		return false;
	}

	@Override
	public BlockRenderType getRenderType(BlockState state){
		return BlockRenderType.MODEL;
	}

	@Override
	public boolean hasComparatorInputOverride(BlockState state){
		return true;
	}

	@Override
	public int getComparatorInputOverride(BlockState blockState, World worldIn, BlockPos pos){
		return RedstoneUtil.clampToVanilla(read(worldIn, pos, blockState));
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, @Nullable IBlockReader world, List<ITextComponent> tooltip, ITooltipFlag advanced){
		tooltip.add(new TranslationTextComponent("tt.crossroads.atmos_charger.desc"));
		tooltip.add(new TranslationTextComponent("tt.crossroads.atmos_charger.safety"));
		tooltip.add(new TranslationTextComponent("tt.crossroads.atmos_charger.structure"));
		tooltip.add(new TranslationTextComponent("tt.crossroads.atmos_charger.circuit"));
	}

	@Override
	public float read(World world, BlockPos blockPos, BlockState blockState){
		if(world instanceof ServerWorld){
			return 100F * (float) AtmosChargeSavedData.getCharge((ServerWorld) world) / (float) AtmosChargeSavedData.getCapacity();
		}else{
			Crossroads.logger.warn("Atmos Charger read on client side! Report to mod author");
			return 0;
		}
	}
}
