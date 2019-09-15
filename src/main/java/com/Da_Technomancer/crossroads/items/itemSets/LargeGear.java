package com.Da_Technomancer.crossroads.items.itemSets;

import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.API.rotary.RotaryUtil;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.blocks.CrossroadsBlocks;
import com.Da_Technomancer.crossroads.items.CrossroadsItems;
import com.Da_Technomancer.crossroads.tileentities.rotary.LargeGearMasterTileEntity;
import com.Da_Technomancer.crossroads.tileentities.rotary.LargeGearSlaveTileEntity;
import com.Da_Technomancer.essentials.blocks.EssentialsProperties;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.List;

public class LargeGear extends Item{

	private final GearFactory.GearMaterial type;
	public static final ModelResourceLocation LOCAT = new ModelResourceLocation(Crossroads.MODID + ":gear_base_large", "inventory");

	public LargeGear(GearFactory.GearMaterial typeIn){
		String name = "large_gear_" + typeIn.toString().toLowerCase();
		setTranslationKey("gear_large_metal");
		setRegistryName(name);
		type = typeIn;
		setCreativeTab(CrossroadsItems.TAB_GEAR);
		CrossroadsItems.toRegister.add(this);
		CrossroadsItems.toClientRegister.put(Pair.of(this, 0), LOCAT);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, @Nullable IBlockReader world, List<ITextComponent> tooltip, ITooltipFlag advanced){
		tooltip.add("I: " + MiscUtil.betterRound(9D * type.getDensity() / 8D, 2) * 1.125D);
	}

	@Override
	public String getItemStackDisplayName(ItemStack stack){
		return String.format(super.getItemStackDisplayName(stack), type.getName());
	}

	@Override
	public ActionResultType onItemUse(PlayerEntity playerIn, World worldIn, BlockPos pos, Hand hand, Direction side, BlockRayTraceResult hit){
		pos = pos.offset(side);

		BlockPos[] spaces = new BlockPos[9];

		for(int i = -1; i < 2; i++){
			for(int j = -1; j < 2; j++){
				spaces[i * 3 + j + 4] = new BlockPos(side.getXOffset() == 0 ? i : 0, side.getYOffset() == 0 ? j : 0, side.getXOffset() == 0 ? side.getYOffset() == 0 ? 0 : j : i);
			}
		}

		for(BlockPos cPos : spaces){
			if(!worldIn.getBlockState(pos.add(cPos)).getBlock().isReplaceable(worldIn, pos.add(cPos))){
				return ActionResultType.FAIL;
			}
		}

		if(!playerIn.capabilities.isCreativeMode){
			playerIn.getHeldItem(hand).shrink(1);
		}

		for(BlockPos cPos : spaces){
			if(cPos.distanceSq(BlockPos.ORIGIN) == 0){
				worldIn.setBlockState(pos, CrossroadsBlocks.largeGearMaster.getDefaultState().with(EssentialsProperties.FACING, side.getOpposite()), 3);
				((LargeGearMasterTileEntity) worldIn.getTileEntity(pos)).initSetup(type);
			}else{
				worldIn.setBlockState(pos.add(cPos), CrossroadsBlocks.largeGearSlave.getDefaultState().with(EssentialsProperties.FACING, side.getOpposite()), 3);
				((LargeGearSlaveTileEntity) worldIn.getTileEntity(pos.add(cPos))).setInitial(BlockPos.ORIGIN.subtract(cPos));
			}
		}
		RotaryUtil.increaseMasterKey(false);

		return ActionResultType.SUCCESS;
	}
}
