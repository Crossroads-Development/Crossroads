package com.Da_Technomancer.crossroads.items;

import com.Da_Technomancer.crossroads.API.AdvancementTracker;
import com.Da_Technomancer.crossroads.API.EnumPath;
import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.CRConfig;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.InteractionResult;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

import net.minecraft.world.item.Item.Properties;

public class PathSigil extends Item{

	private final EnumPath path;

	protected PathSigil(EnumPath path){
		super(new Properties().tab(CRItems.TAB_CROSSROADS));
		this.path = path;
		String name = "sigil_" + path.toString();
		setRegistryName(name);
		CRItems.toRegister.add(this);
	}

	public EnumPath getPath(){
		return path;
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn){
		tooltip.add(new TranslatableComponent("tt.crossroads.path_sigil.desc"));
		if(CRConfig.forgetPaths.get()){
			tooltip.add(new TranslatableComponent("tt.crossroads.path_sigil.desc.forget"));
		}
	}

	@Override
	public InteractionResult useOn(UseOnContext context){
		if(context.getPlayer() == null){
			return super.useOn(context);
		}
		if(context.getLevel().isClientSide){
			AdvancementTracker.listen();
		}

		if(context.getPlayer().isShiftKeyDown()){
			if(CRConfig.forgetPaths.get()){
				if(path.isUnlocked(context.getPlayer())){
					if(context.getLevel().isClientSide()){
						MiscUtil.chatMessage(context.getPlayer(), new TranslatableComponent("tt.crossroads.path_sigil.forget", path.getLocalName()));
					}
					path.setUnlocked(context.getPlayer(), false);
					context.getItemInHand().shrink(1);
					return InteractionResult.CONSUME;
				}else{
					if(context.getLevel().isClientSide()){
						MiscUtil.chatMessage(context.getPlayer(), new TranslatableComponent("tt.crossroads.path_sigil.forget.fail"));
					}
					return InteractionResult.FAIL;
				}
			}else{
				if(context.getLevel().isClientSide()){
					MiscUtil.chatMessage(context.getPlayer(), new TranslatableComponent("tt.crossroads.path_sigil.forget.fail.config"));
				}
				return InteractionResult.FAIL;
			}
		}else if(EnumPath.canUnlockNewPath(context.getPlayer())){
			if(path.isUnlocked(context.getPlayer())){
				if(context.getLevel().isClientSide()){
					MiscUtil.chatMessage(context.getPlayer(), new TranslatableComponent("tt.crossroads.path_sigil.taken"));
				}
				return InteractionResult.FAIL;
			}else{
				path.setUnlocked(context.getPlayer(), true);
				context.getItemInHand().shrink(1);
				return InteractionResult.CONSUME;
			}
		}else{
			if(context.getLevel().isClientSide()){
				MiscUtil.chatMessage(context.getPlayer(), new TranslatableComponent("tt.crossroads.path_sigil.fail"));
			}
			return InteractionResult.FAIL;
		}
	}
}
