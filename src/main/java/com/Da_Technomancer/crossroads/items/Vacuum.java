package com.Da_Technomancer.crossroads.items;

import com.Da_Technomancer.crossroads.API.MiscUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class Vacuum extends Item{

	private static final int RANGE = 5;
	private static final double ANGLE = Math.cos(Math.PI / 4F);//Pre-calc cosine for speed

	protected Vacuum(){
		super(new Properties().tab(CRItems.TAB_CROSSROADS).stacksTo(1).defaultDurability(2400));
		String name = "vacuum";
		setRegistryName(name);
		CRItems.toRegister.add(this);
	}

	@Override
	public Rarity getRarity(ItemStack stack){
		return CRItems.BOBO_RARITY;
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn){
		tooltip.add(new TranslatableComponent("tt.crossroads.vacuum.quip").setStyle(MiscUtil.TT_QUIP));
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand hand){
		ArrayList<Entity> entities = (ArrayList<Entity>) worldIn.getEntitiesOfClass(Entity.class, new AABB(playerIn.getX(), playerIn.getY(), playerIn.getZ(), playerIn.getX(), playerIn.getY(), playerIn.getZ()).inflate(RANGE), EntitySelector.ENTITY_STILL_ALIVE);

		//Affects a conical region
		//Removes entities from the list if they aren't in the conical region in the direction the player is looking
		Vec3 look = playerIn.getLookAngle().scale(RANGE);
		Vec3 playPos = playerIn.position();
		entities.removeIf((Entity e) -> {
			Vec3 ePos = e.position().subtract(playPos);
			return ePos.length() >= RANGE || ePos.dot(look) / (ePos.length() * look.length()) <= ANGLE;
		});

		for(Entity ent : entities){
			Vec3 motVec = playerIn.position().subtract(ent.position()).scale(0.25D);
			ent.push(motVec.x, motVec.y, motVec.z);
		}

		playerIn.getItemInHand(hand).hurtAndBreak(1, playerIn, p -> p.broadcastBreakEvent(hand));

		return InteractionResultHolder.success(playerIn.getItemInHand(hand));
	}
}
