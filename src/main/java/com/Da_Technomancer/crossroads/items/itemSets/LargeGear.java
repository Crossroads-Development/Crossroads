package com.Da_Technomancer.crossroads.items.itemSets;

import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.API.rotary.GearTypes;
import com.Da_Technomancer.crossroads.CommonProxy;
import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import com.Da_Technomancer.crossroads.items.ModItems;
import com.Da_Technomancer.crossroads.tileentities.rotary.LargeGearMasterTileEntity;
import com.Da_Technomancer.crossroads.tileentities.rotary.LargeGearSlaveTileEntity;
import com.Da_Technomancer.essentials.blocks.EssentialsProperties;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.List;

public class LargeGear extends Item{

	private final GearTypes type;
	public static final ModelResourceLocation LOCAT = new ModelResourceLocation(Main.MODID + ":gear_base_large", "inventory");

	public LargeGear(GearTypes typeIn){
		String name = "large_gear_" + typeIn.toString().toLowerCase();
		setUnlocalizedName(name);
		setRegistryName(name);
		type = typeIn;
		setCreativeTab(ModItems.TAB_GEAR);
		ModItems.toRegister.add(this);
		ModItems.toClientRegister.put(Pair.of(this, 0), LOCAT);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag advanced){
		tooltip.add("I: " + MiscUtil.betterRound(9D * type.getDensity() / 8D, 2) * 1.125D);
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ){
		pos = pos.offset(side);

		BlockPos[] spaces = new BlockPos[9];

		for(int i = -1; i < 2; i++){
			for(int j = -1; j < 2; j++){
				spaces[i * 3 + j + 4] = new BlockPos(side.getFrontOffsetX() == 0 ? i : 0, side.getFrontOffsetY() == 0 ? j : 0, side.getFrontOffsetX() == 0 ? side.getFrontOffsetY() == 0 ? 0 : j : i);
			}
		}

		for(BlockPos cPos : spaces){
			if(!worldIn.getBlockState(pos.add(cPos)).getBlock().isReplaceable(worldIn, pos.add(cPos))){
				return EnumActionResult.FAIL;
			}
		}

		if(!playerIn.capabilities.isCreativeMode){
			playerIn.getHeldItem(hand).shrink(1);
		}

		for(BlockPos cPos : spaces){
			if(cPos.distanceSq(BlockPos.ORIGIN) == 0){
				worldIn.setBlockState(pos, ModBlocks.largeGearMaster.getDefaultState().withProperty(EssentialsProperties.FACING, side.getOpposite()), 3);
				((LargeGearMasterTileEntity) worldIn.getTileEntity(pos)).initSetup(type);
			}else{
				worldIn.setBlockState(pos.add(cPos), ModBlocks.largeGearSlave.getDefaultState().withProperty(EssentialsProperties.FACING, side.getOpposite()), 3);
				((LargeGearSlaveTileEntity) worldIn.getTileEntity(pos.add(cPos))).setInitial(BlockPos.ORIGIN.subtract(cPos));
			}
		}
		++CommonProxy.masterKey;

		return EnumActionResult.SUCCESS;
	}
}
