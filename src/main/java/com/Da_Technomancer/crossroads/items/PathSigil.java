package com.Da_Technomancer.crossroads.items;

import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.api.AdvancementTracker;
import com.Da_Technomancer.crossroads.api.EnumPath;
import com.Da_Technomancer.crossroads.api.MiscUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class PathSigil extends Item{

	private final EnumPath path;

	protected PathSigil(EnumPath path){
		super(new Properties());
		this.path = path;
		String name = "sigil_" + path.toString();
		CRItems.queueForRegister(name, this);
	}

	public EnumPath getPath(){
		return path;
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn){
		tooltip.add(Component.translatable("tt.crossroads.path_sigil.desc"));
		if(CRConfig.forgetPaths.get()){
			tooltip.add(Component.translatable("tt.crossroads.path_sigil.desc.forget"));
		}
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand hand){
		if(playerIn == null){
			return super.use(worldIn, playerIn, hand);
		}
		if(worldIn.isClientSide){
			AdvancementTracker.listen();
		}

		ItemStack held = playerIn.getItemInHand(hand);

		if(playerIn.isShiftKeyDown()){
			if(CRConfig.forgetPaths.get()){
				if(path.isUnlocked(playerIn)){
					if(!worldIn.isClientSide()){
						MiscUtil.displayMessage(playerIn, Component.translatable("tt.crossroads.path_sigil.forget", path.getLocalName()));
					}
					path.setUnlocked(playerIn, false);
					held.shrink(1);
					return InteractionResultHolder.success(held);
				}else{
					if(!worldIn.isClientSide()){
						MiscUtil.displayMessage(playerIn, Component.translatable("tt.crossroads.path_sigil.forget.fail"));
					}
					return InteractionResultHolder.fail(held);
				}
			}else{
				if(!worldIn.isClientSide()){
					MiscUtil.displayMessage(playerIn, Component.translatable("tt.crossroads.path_sigil.forget.fail.config"));
				}
				return InteractionResultHolder.fail(held);
			}
		}else if(EnumPath.canUnlockNewPath(playerIn)){
			if(path.isUnlocked(playerIn)){
				if(!worldIn.isClientSide()){
					MiscUtil.displayMessage(playerIn, Component.translatable("tt.crossroads.path_sigil.taken"));
				}
				return InteractionResultHolder.fail(held);
			}else{
				path.setUnlocked(playerIn, true);
				held.shrink(1);
				return InteractionResultHolder.success(held);
			}
		}else{
			if(!worldIn.isClientSide()){
				MiscUtil.displayMessage(playerIn, Component.translatable("tt.crossroads.path_sigil.fail"));
			}
			return InteractionResultHolder.fail(held);
		}
	}
}
