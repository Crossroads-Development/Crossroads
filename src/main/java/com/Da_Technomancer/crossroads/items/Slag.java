package com.Da_Technomancer.crossroads.items;

import com.Da_Technomancer.crossroads.items.crafting.ModCrafting;
import net.minecraft.block.DispenserBlock;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IDispenseItemBehavior;
import net.minecraft.dispenser.OptionalDispenseBehavior;
import net.minecraft.item.BoneMealItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Pair;

public class Slag extends Item{

	private static final IDispenseItemBehavior SLAG_DISPENSER_BEHAVIOR = new OptionalDispenseBehavior(){
		@Override
		protected ItemStack dispenseStack(IBlockSource source, ItemStack stack){
			World world = source.getWorld();
			BlockPos blockpos = source.getBlockPos().offset(source.getBlockState().get(DispenserBlock.FACING));

			//We currently use the deprecated method because this is what vanilla dispensers currently use
			if(BoneMealItem.applyBonemeal(stack, world, blockpos)){
				if(!world.isRemote){
					world.playEvent(2005, blockpos, 0);
				}
				successful = true;
			}else{
				successful = false;
			}
			return stack;
		}
	};

	protected Slag(){
		super(CRItems.itemProp);
		String name = "slag";
		setRegistryName(name);
		CRItems.toRegister.add(this);

		DispenserBlock.registerDispenseBehavior(this, SLAG_DISPENSER_BEHAVIOR);
		ModCrafting.toRegisterOreDict.add(Pair.of(this, new String[] {"itemSlag"}));
	}

	@Override
	public ActionResultType onItemUse(ItemUseContext context){
		if(BoneMealItem.applyBonemeal(context.getItem(), context.getWorld(), context.getPos(), context.getPlayer())){
			if(!context.getWorld().isRemote){
				context.getWorld().playEvent(2005, context.getPos(), 0);
			}
			return ActionResultType.SUCCESS;
		}
		return ActionResultType.FAIL;
	}
}
