package com.Da_Technomancer.crossroads.items;

import com.Da_Technomancer.crossroads.API.MiscUtil;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

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
	public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn){
		tooltip.add(new TranslationTextComponent("tt.crossroads.vacuum.quip").setStyle(MiscUtil.TT_QUIP));
	}

	@Override
	public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand hand){
		ArrayList<Entity> entities = (ArrayList<Entity>) worldIn.getEntitiesOfClass(Entity.class, new AxisAlignedBB(playerIn.getX(), playerIn.getY(), playerIn.getZ(), playerIn.getX(), playerIn.getY(), playerIn.getZ()).inflate(RANGE), EntityPredicates.ENTITY_STILL_ALIVE);

		//Affects a conical region
		//Removes entities from the list if they aren't in the conical region in the direction the player is looking
		Vector3d look = playerIn.getLookAngle().scale(RANGE);
		Vector3d playPos = playerIn.position();
		entities.removeIf((Entity e) -> {
			Vector3d ePos = e.position().subtract(playPos);
			return ePos.length() >= RANGE || ePos.dot(look) / (ePos.length() * look.length()) <= ANGLE;
		});

		for(Entity ent : entities){
			Vector3d motVec = playerIn.position().subtract(ent.position()).scale(0.25D);
			ent.push(motVec.x, motVec.y, motVec.z);
		}

		playerIn.getItemInHand(hand).hurtAndBreak(1, playerIn, p -> p.broadcastBreakEvent(hand));

		return ActionResult.success(playerIn.getItemInHand(hand));
	}
}
