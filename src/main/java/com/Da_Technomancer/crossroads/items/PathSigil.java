package com.Da_Technomancer.crossroads.items;

import com.Da_Technomancer.crossroads.API.AdvancementTracker;
import com.Da_Technomancer.crossroads.API.EnumPath;
import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.CRConfig;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class PathSigil extends Item{

	private final EnumPath path;

	protected PathSigil(EnumPath path){
		super(new Properties().group(CRItems.TAB_CROSSROADS));
		this.path = path;
		String name = "sigil_" + path.toString();
		setRegistryName(name);
		CRItems.toRegister.add(this);
	}

	public EnumPath getPath(){
		return path;
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn){
		tooltip.add(new TranslationTextComponent("tt.crossroads.path_sigil.desc"));
		if(CRConfig.forgetPaths.get()){
			tooltip.add(new TranslationTextComponent("tt.crossroads.path_sigil.desc.forget"));
		}
	}

	@Override
	public ActionResultType onItemUse(ItemUseContext context){
		if(context.getPlayer() == null){
			return super.onItemUse(context);
		}
		if(context.getWorld().isRemote){
			AdvancementTracker.listen();
		}

		if(context.getPlayer().isSneaking()){
			if(CRConfig.forgetPaths.get()){
				if(path.isUnlocked(context.getPlayer())){
					if(context.getWorld().isRemote()){
						MiscUtil.chatMessage(context.getPlayer(), new TranslationTextComponent("tt.crossroads.path_sigil.forget", path.getLocalName()));
					}
					path.setUnlocked(context.getPlayer(), false);
					context.getItem().shrink(1);
					return ActionResultType.CONSUME;
				}else{
					if(context.getWorld().isRemote()){
						MiscUtil.chatMessage(context.getPlayer(), new TranslationTextComponent("tt.crossroads.path_sigil.forget.fail"));
					}
					return ActionResultType.FAIL;
				}
			}else{
				if(context.getWorld().isRemote()){
					MiscUtil.chatMessage(context.getPlayer(), new TranslationTextComponent("tt.crossroads.path_sigil.forget.fail.config"));
				}
				return ActionResultType.FAIL;
			}
		}else if(EnumPath.canUnlockNewPath(context.getPlayer())){
			if(path.isUnlocked(context.getPlayer())){
				if(context.getWorld().isRemote()){
					MiscUtil.chatMessage(context.getPlayer(), new TranslationTextComponent("tt.crossroads.path_sigil.taken"));
				}
				return ActionResultType.FAIL;
			}else{
				path.setUnlocked(context.getPlayer(), true);
				context.getItem().shrink(1);
				return ActionResultType.CONSUME;
			}
		}else{
			if(context.getWorld().isRemote()){
				MiscUtil.chatMessage(context.getPlayer(), new TranslationTextComponent("tt.crossroads.path_sigil.fail"));
			}
			return ActionResultType.FAIL;
		}
	}
}
