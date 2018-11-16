package com.Da_Technomancer.crossroads.items.itemSets;

import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.API.rotary.EnumGearType;
import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import com.Da_Technomancer.crossroads.items.ModItems;
import com.Da_Technomancer.crossroads.tileentities.rotary.mechanisms.MechanismTileEntity;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
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

public class ToggleGear extends Item{

	private final EnumGearType type;
	private final boolean inverted;
	private static final ModelResourceLocation LOCAT = new ModelResourceLocation(Main.MODID + ":gear_toggle", "inventory");
	private static final ModelResourceLocation LOCAT_INV = new ModelResourceLocation(Main.MODID + ":gear_toggle_inv", "inventory");

	public ToggleGear(EnumGearType typeIn, boolean inverted){
		String name = "gear_toggle_" + (inverted ? "inverted_" : "") + typeIn.toString().toLowerCase();
		setTranslationKey(name);
		setRegistryName(name);
		setCreativeTab(ModItems.TAB_GEAR);
		type = typeIn;
		this.inverted = inverted;
		ModItems.toRegister.add(this);
		ModItems.toClientRegister.put(Pair.of(this, 0), inverted ? LOCAT_INV : LOCAT);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag advanced){
		tooltip.add("I: " + MiscUtil.betterRound(type.getDensity() / 8, 2) * .125);
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ){
		if(worldIn.isRemote){
			return EnumActionResult.SUCCESS;
		}

		TileEntity te = worldIn.getTileEntity(pos.offset(side));
		if(te instanceof MechanismTileEntity && worldIn.isSideSolid(pos, side)){
			MechanismTileEntity mte = (MechanismTileEntity) te;
			if(mte.members[side.getOpposite().getIndex()] != null){
				return EnumActionResult.SUCCESS;
			}
			if(!playerIn.capabilities.isCreativeMode){
				playerIn.getHeldItem(hand).shrink(1);
			}

			mte.setMechanism(side.getOpposite().getIndex(), MechanismTileEntity.MECHANISMS.get(inverted ? 5 : 4), type, null, false);
		}else if(worldIn.getBlockState(pos.offset(side)).getBlock().isReplaceable(worldIn, pos.offset(side)) && worldIn.isSideSolid(pos, side)){
			if(!playerIn.capabilities.isCreativeMode){
				playerIn.getHeldItem(hand).shrink(1);
			}

			worldIn.setBlockState(pos.offset(side), ModBlocks.sextupleGear.getDefaultState(), 3);
			te = worldIn.getTileEntity(pos.offset(side));
			((MechanismTileEntity) te).setMechanism(side.getOpposite().getIndex(), MechanismTileEntity.MECHANISMS.get(inverted ? 5 : 4), type, null, true);
		}

		return EnumActionResult.SUCCESS;
	}
}
