package com.Da_Technomancer.crossroads.items.itemSets;

import com.Da_Technomancer.crossroads.API.rotary.GearTypes;
import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import com.Da_Technomancer.crossroads.items.ModItems;
import com.Da_Technomancer.crossroads.items.crafting.ModCrafting;
import com.Da_Technomancer.crossroads.tileentities.rotary.mechanisms.MechanismTileEntity;
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

public class Axle extends Item{

	private final GearTypes type;

	public Axle(GearTypes typeIn){
		String name = "axle_" + typeIn.toString().toLowerCase();
		setUnlocalizedName(name);
		setRegistryName(name);
		setCreativeTab(ModItems.TAB_GEAR);
		type = typeIn;
		ModItems.toRegister.add(this);
		ModItems.itemAddQue(this);
		ModCrafting.toRegisterOreDict.add(Pair.of(this, new String[] {"stick" + typeIn.toString()}));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag advanced){
		tooltip.add("I: " + type.getDensity() / 32_000D);
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ){
		if(worldIn.isRemote){
			return EnumActionResult.SUCCESS;
		}

		//Attempt to add this axle to a pre-existing mechanism
		TileEntity te = worldIn.getTileEntity(pos);
		if(te instanceof MechanismTileEntity){
			MechanismTileEntity mte = (MechanismTileEntity) te;
			if(mte.members[6] == null){
				mte.setMechanism(6, MechanismTileEntity.MECHANISMS.get(1), type, side.getAxis(), false);
				if(!playerIn.capabilities.isCreativeMode){
					playerIn.getHeldItem(hand).shrink(1);
				}
				return EnumActionResult.SUCCESS;
			}
		}

		//Check if offsetting by one would land us in another mechanism
		te = worldIn.getTileEntity(pos.offset(side));
		if(te instanceof MechanismTileEntity){
			MechanismTileEntity mte = (MechanismTileEntity) te;
			if(mte.members[6] == null){
				mte.setMechanism(6, MechanismTileEntity.MECHANISMS.get(1), type, side.getAxis(), false);
				if(!playerIn.capabilities.isCreativeMode){
					playerIn.getHeldItem(hand).shrink(1);
				}
			}
			return EnumActionResult.SUCCESS;
		}

		//Make a new mechanism block
		if(worldIn.getBlockState(pos.offset(side)).getBlock().isReplaceable(worldIn, pos.offset(side))){
			if(!playerIn.capabilities.isCreativeMode){
				playerIn.getHeldItem(hand).shrink(1);
			}

			worldIn.setBlockState(pos.offset(side), ModBlocks.sextupleGear.getDefaultState(), 3);
			te = worldIn.getTileEntity(pos.offset(side));
			((MechanismTileEntity) te).setMechanism(6, MechanismTileEntity.MECHANISMS.get(1), type, side.getAxis(), true);
		}

		return EnumActionResult.SUCCESS;
	}
}
