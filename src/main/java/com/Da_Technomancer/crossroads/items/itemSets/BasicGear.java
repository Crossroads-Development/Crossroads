package com.Da_Technomancer.crossroads.items.itemSets;

import java.util.List;

import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Pair;

import com.Da_Technomancer.crossroads.CommonProxy;
import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.MiscOp;
import com.Da_Technomancer.crossroads.API.rotary.GearTypes;
import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import com.Da_Technomancer.crossroads.items.ModItems;
import com.Da_Technomancer.crossroads.items.crafting.ModCrafting;
import com.Da_Technomancer.crossroads.tileentities.rotary.SidedGearHolderTileEntity;

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

public class BasicGear extends Item{

	private final GearTypes type;
	public static final ModelResourceLocation LOCAT = new ModelResourceLocation(Main.MODID + ":gear_base", "inventory");

	public BasicGear(GearTypes typeIn){
		String name = "gear_" + typeIn.toString().toLowerCase();
		setUnlocalizedName(name);
		setRegistryName(name);
		setCreativeTab(ModItems.TAG_GEAR);
		type = typeIn;
		ModItems.toRegister.add(this);
		ModItems.toClientRegister.put(Pair.of(this, 0), LOCAT);
		ModCrafting.toRegisterOreDict.add(Pair.of(this, new String[] {"gear" + typeIn.toString()}));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag advanced){
		tooltip.add("I: " + MiscOp.betterRound(type.getDensity() / 8, 2) * .125);
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ){
		if(worldIn.isRemote){
			return EnumActionResult.SUCCESS;
		}

		TileEntity te = worldIn.getTileEntity(pos.offset(side));
		if(te instanceof SidedGearHolderTileEntity && !te.hasCapability(Capabilities.AXLE_HANDLER_CAPABILITY, side.getOpposite()) && worldIn.isSideSolid(pos, side)){
			if(!playerIn.capabilities.isCreativeMode){
				playerIn.getHeldItem(hand).shrink(1);
			}

			((SidedGearHolderTileEntity) te).setMembers(type, side.getOpposite().getIndex(), false);
			CommonProxy.masterKey++;
		}else if(worldIn.getBlockState(pos.offset(side)).getBlock().isReplaceable(worldIn, pos.offset(side)) && worldIn.isSideSolid(pos, side)){
			if(!playerIn.capabilities.isCreativeMode){
				playerIn.getHeldItem(hand).shrink(1);
			}

			worldIn.setBlockState(pos.offset(side), ModBlocks.sextupleGear.getDefaultState(), 3);
			te = worldIn.getTileEntity(pos.offset(side));
			((SidedGearHolderTileEntity) te).setMembers(type, side.getOpposite().getIndex(), true);
			CommonProxy.masterKey++;
		}

		return EnumActionResult.SUCCESS;
	}
}
