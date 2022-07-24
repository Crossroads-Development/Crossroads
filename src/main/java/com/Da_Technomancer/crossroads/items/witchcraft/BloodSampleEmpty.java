package com.Da_Technomancer.crossroads.items.witchcraft;

import com.Da_Technomancer.crossroads.items.CRItems;
import com.Da_Technomancer.essentials.api.BlockUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nullable;
import java.util.List;

public class BloodSampleEmpty extends Item{

	public BloodSampleEmpty(){
		super(new Item.Properties().stacksTo(1).tab(CRItems.TAB_CROSSROADS));
		String name = "blood_sample_empty";
		CRItems.toRegister.put(name, this);

		DefaultDispenseItemBehavior dispenseBehavior = new OptionalDispenseItemBehavior(){
			@Override
			protected ItemStack execute(BlockSource world, ItemStack stack) {
				ServerLevel level = world.getLevel();
				BlockPos blockpos = world.getPos().relative(world.getBlockState().getValue(DispenserBlock.FACING));
				ItemStack drawn;
				List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, new AABB(blockpos), EntitySelector.NO_SPECTATORS);
				if(entities.size() > 0){
					drawn = Syringe.drawBlood(stack, entities.get(level.random.nextInt(entities.size())), null);
					setSuccess(!BlockUtil.sameItem(drawn, stack));
				}else{
					drawn = stack;
					setSuccess(false);
				}
				return drawn;
			}
		};
		DispenserBlock.registerBehavior(this, dispenseBehavior);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag){
		tooltip.add(Component.translatable("tt.crossroads.blood_sample_empty.desc"));
	}
}
