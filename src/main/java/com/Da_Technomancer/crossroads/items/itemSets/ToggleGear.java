package com.Da_Technomancer.crossroads.items.itemSets;

import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.API.rotary.RotaryUtil;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.blocks.CrossroadsBlocks;
import com.Da_Technomancer.crossroads.items.CRItems;
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
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.List;

public class ToggleGear extends Item{

	private final GearFactory.GearMaterial type;
	private final boolean inverted;
	private static final ModelResourceLocation LOCAT = new ModelResourceLocation(Crossroads.MODID + ":gear_toggle", "inventory");
	private static final ModelResourceLocation LOCAT_INV = new ModelResourceLocation(Crossroads.MODID + ":gear_toggle_inv", "inventory");

	public ToggleGear(GearFactory.GearMaterial typeIn, boolean inverted){
		String name = "gear_toggle_" + (inverted ? "inverted_" : "") + typeIn.toString().toLowerCase();
		setTranslationKey(inverted ? "gear_toggle_inverted_metal" : "gear_toggle_metal");
		setRegistryName(name);
		setCreativeTab(CRItems.TAB_GEAR);
		type = typeIn;
		this.inverted = inverted;
		CRItems.toRegister.add(this);
		CRItems.toClientRegister.put(Pair.of(this, 0), inverted ? LOCAT_INV : LOCAT);
	}

	@Override
	public String getItemStackDisplayName(ItemStack stack){
		return String.format(super.getItemStackDisplayName(stack), type.getName());
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, @Nullable IBlockReader world, List<ITextComponent> tooltip, ITooltipFlag advanced){
		tooltip.add("I: " + MiscUtil.betterRound(type.getDensity() / 8, 2) * .125);
	}

	@Override
	public ActionResultType onItemUse(PlayerEntity playerIn, World worldIn, BlockPos pos, Hand hand, Direction side, BlockRayTraceResult hit){
		if(worldIn.isRemote){
			return ActionResultType.SUCCESS;
		}

		TileEntity te = worldIn.getTileEntity(pos.offset(side));
		if(te instanceof MechanismTileEntity && RotaryUtil.solidToGears(worldIn, pos, side)){
			MechanismTileEntity mte = (MechanismTileEntity) te;
			if(mte.members[side.getOpposite().getIndex()] != null){
				return ActionResultType.SUCCESS;
			}
			if(!playerIn.capabilities.isCreativeMode){
				playerIn.getHeldItem(hand).shrink(1);
			}

			RotaryUtil.increaseMasterKey(true);
			mte.setMechanism(side.getOpposite().getIndex(), MechanismTileEntity.MECHANISMS.get(inverted ? 5 : 4), type, null, false);
		}else if(worldIn.getBlockState(pos.offset(side)).getBlock().isReplaceable(worldIn, pos.offset(side)) && RotaryUtil.solidToGears(worldIn, pos, side)){
			if(!playerIn.capabilities.isCreativeMode){
				playerIn.getHeldItem(hand).shrink(1);
			}

			worldIn.setBlockState(pos.offset(side), CrossroadsBlocks.sextupleGear.getDefaultState(), 3);
			te = worldIn.getTileEntity(pos.offset(side));
			if(te instanceof MechanismTileEntity){
				RotaryUtil.increaseMasterKey(true);
				((MechanismTileEntity) te).setMechanism(side.getOpposite().getIndex(), MechanismTileEntity.MECHANISMS.get(inverted ? 5 : 4), type, null, true);
			}
		}

		return ActionResultType.SUCCESS;
	}
}
