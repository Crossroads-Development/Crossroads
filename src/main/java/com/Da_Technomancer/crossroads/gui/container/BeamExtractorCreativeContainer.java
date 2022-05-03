package com.Da_Technomancer.crossroads.gui.container;

import com.Da_Technomancer.crossroads.API.beams.BeamUnit;
import com.Da_Technomancer.crossroads.Crossroads;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(Crossroads.MODID)
public class BeamExtractorCreativeContainer extends AbstractContainerMenu{

	public BeamUnit output;
	public String[] conf;
	public BlockPos pos;

	@ObjectHolder("beam_extractor_creative")
	private static MenuType<BeamExtractorCreativeContainer> TYPE = null;

	public BeamExtractorCreativeContainer(int id, Inventory playerInventory, FriendlyByteBuf data){
		this(id, playerInventory, data == null ? BeamUnit.EMPTY : new BeamUnit(data.readVarIntArray(4)), data == null ? null : new String[] {data.readUtf(), data.readUtf(), data.readUtf(), data.readUtf()}, data == null ? null : data.readBlockPos());
	}

	public BeamExtractorCreativeContainer(int id, Inventory playerInventory, BeamUnit output, String[] settingStrings, BlockPos pos){
		super(TYPE, id);
		this.output = output;
		this.conf = settingStrings;
		this.pos = pos;
	}

	@Override
	public boolean stillValid(Player playerIn){
		return true;
	}
}
