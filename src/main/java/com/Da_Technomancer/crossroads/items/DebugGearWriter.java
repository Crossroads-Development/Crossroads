package com.Da_Technomancer.crossroads.items;

import com.Da_Technomancer.crossroads.API.Capabilities;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class DebugGearWriter extends Item{
	
	public DebugGearWriter(){
		setUnlocalizedName("debugGearWriter");
		setRegistryName("debugGearWriter");
	    GameRegistry.register(this);
	    this.setCreativeTab(ModItems.tabCrossroads);
	}

	protected double getEfficiency(){
		return 200;
	}

	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ){
		if(worldIn.getTileEntity(pos) != null && worldIn.getTileEntity(pos).hasCapability(Capabilities.ROTARY_HANDLER_CAPABILITY, side.getOpposite())){
			if(playerIn.isSneaking()){
				worldIn.getTileEntity(pos).getCapability(Capabilities.ROTARY_HANDLER_CAPABILITY, side.getOpposite()).addEnergy(-getEfficiency(), true, true);
				return EnumActionResult.SUCCESS;
			}else{
				worldIn.getTileEntity(pos).getCapability(Capabilities.ROTARY_HANDLER_CAPABILITY, side.getOpposite()).addEnergy(getEfficiency(), true, true);
				return EnumActionResult.SUCCESS;
			}
		}
		return EnumActionResult.PASS;
	}

}
