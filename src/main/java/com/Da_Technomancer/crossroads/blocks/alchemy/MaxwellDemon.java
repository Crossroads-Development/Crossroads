package com.Da_Technomancer.crossroads.blocks.alchemy;

import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.Da_Technomancer.crossroads.tileentities.alchemy.MaxwellDemonTileEntity;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.BlockGetter;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class MaxwellDemon extends BaseEntityBlock{

	public MaxwellDemon(){
		super(CRBlocks.getRockProperty());
		String name = "maxwell_demon";
		setRegistryName(name);
		CRBlocks.toRegister.add(this);
		CRBlocks.blockAddQue(this, new Item.Properties().tab(CRItems.TAB_CROSSROADS).rarity(CRItems.BOBO_RARITY));
	}

	@Override
	public BlockEntity newBlockEntity(BlockGetter worldIn){
		return new MaxwellDemonTileEntity();
	}

	@Override
	public RenderShape getRenderShape(BlockState state){
		return RenderShape.MODEL;
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, @Nullable BlockGetter world, List<Component> tooltip, TooltipFlag advanced){
		double rate = CRConfig.demonPower.get();
		tooltip.add(new TranslatableComponent("tt.crossroads.maxwell_demon.top", MaxwellDemonTileEntity.MAX_TEMP, rate));
		tooltip.add(new TranslatableComponent("tt.crossroads.maxwell_demon.bottom", MaxwellDemonTileEntity.MIN_TEMP, rate));
		tooltip.add(new TranslatableComponent("tt.crossroads.maxwell_demon.quip").setStyle(MiscUtil.TT_QUIP));
	}
}
