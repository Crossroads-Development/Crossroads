package com.Da_Technomancer.crossroads.items.itemSets;

import com.Da_Technomancer.crossroads.API.rotary.RotaryUtil;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.blocks.CrossroadsBlocks;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.Da_Technomancer.crossroads.items.crafting.ModCrafting;
import com.Da_Technomancer.crossroads.tileentities.rotary.mechanisms.MechanismTileEntity;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.List;

public class Axle extends Item{

	private final GearFactory.GearMaterial type;
	private static final ModelResourceLocation LOCAT = new ModelResourceLocation(Crossroads.MODID + ":axle", "inventory");

	public Axle(GearFactory.GearMaterial typeIn){
		String name = "axle_" + typeIn.toString().toLowerCase();
		setTranslationKey("axle_metal");
		setRegistryName(name);
		setCreativeTab(CRItems.TAB_GEAR);
		type = typeIn;
		CRItems.toRegister.add(this);
		CRItems.itemAddQue(this);
		CRItems.toClientRegister.put(Pair.of(this, 0), LOCAT);
		ModCrafting.toRegisterOreDict.add(Pair.of(this, new String[] {"stick" + type.toString()}));
	}

	@Override
	public String getItemStackDisplayName(ItemStack stack){
		return String.format(super.getItemStackDisplayName(stack), type.getName());
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, @Nullable IBlockReader world, List<ITextComponent> tooltip, ITooltipFlag advanced){
		tooltip.add("I: " + type.getDensity() / 32_000D);
	}

	@Override
	public ActionResultType onItemUse(PlayerEntity playerIn, World worldIn, BlockPos pos, Hand hand, Direction side, BlockRayTraceResult hit){
		if(worldIn.isRemote){
			return ActionResultType.SUCCESS;
		}

		//Attempt to add this axle to a pre-existing mechanism
		TileEntity te = worldIn.getTileEntity(pos);
		if(te instanceof MechanismTileEntity){
			MechanismTileEntity mte = (MechanismTileEntity) te;
			if(mte.members[6] == null){
				RotaryUtil.increaseMasterKey(true);
				mte.setMechanism(6, MechanismTileEntity.MECHANISMS.get(1), type, side.getAxis(), false);
				if(!playerIn.capabilities.isCreativeMode){
					playerIn.getHeldItem(hand).shrink(1);
				}
				return ActionResultType.SUCCESS;
			}
		}

		//Check if offsetting by one would land us in another mechanism
		te = worldIn.getTileEntity(pos.offset(side));
		if(te instanceof MechanismTileEntity){
			MechanismTileEntity mte = (MechanismTileEntity) te;
			if(mte.members[6] == null){
				RotaryUtil.increaseMasterKey(true);
				mte.setMechanism(6, MechanismTileEntity.MECHANISMS.get(1), type, side.getAxis(), false);
				if(!playerIn.capabilities.isCreativeMode){
					playerIn.getHeldItem(hand).shrink(1);
				}
			}
			return ActionResultType.SUCCESS;
		}

		//Make a new mechanism block
		if(worldIn.getBlockState(pos.offset(side)).getBlock().isReplaceable(worldIn, pos.offset(side))){
			if(!playerIn.capabilities.isCreativeMode){
				playerIn.getHeldItem(hand).shrink(1);
			}

			worldIn.setBlockState(pos.offset(side), CrossroadsBlocks.sextupleGear.getDefaultState(), 3);
			te = worldIn.getTileEntity(pos.offset(side));
			if(te instanceof MechanismTileEntity){
				RotaryUtil.increaseMasterKey(true);
				((MechanismTileEntity) te).setMechanism(6, MechanismTileEntity.MECHANISMS.get(1), type, side.getAxis(), true);
			}
		}

		return ActionResultType.SUCCESS;
	}
}
