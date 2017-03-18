package com.Da_Technomancer.crossroads.integration.GuideAPI;

import java.util.List;

import com.Da_Technomancer.crossroads.API.enums.MagicElements;
import com.Da_Technomancer.crossroads.API.packets.StoreNBTToClient;

import amerifrance.guideapi.api.IPage;
import amerifrance.guideapi.api.impl.Book;
import amerifrance.guideapi.api.impl.abstraction.CategoryAbstract;
import amerifrance.guideapi.entry.EntryItemStack;
import amerifrance.guideapi.gui.GuiCategory;
import amerifrance.guideapi.page.PageText;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ElementEntry extends EntryItemStack{

	private final boolean lore;
	
	public ElementEntry(List<IPage> pageList, String unlocEntryName, ItemStack stack, boolean lore){
		super(pageList, unlocEntryName, stack, GuideBooks.smallText);
		this.lore = lore;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void onInit(Book book, CategoryAbstract category, GuiCategory guiCategory, EntityPlayer player, ItemStack bookStack){
		pageList.clear();
		pageList.add(new PageText("Information about the various elements will be added to this entry as you discover them. To add an element to this entry, discover it then close and re-open this entry."));
		if(GuideBooks.smallText){
			((PageText) pageList.get(0)).setUnicodeFlag(true);
		}
		for(MagicElements elem : MagicElements.values()){
			if(elem == MagicElements.ENERGY || elem == MagicElements.POTENTIAL || elem == MagicElements.STABILITY || StoreNBTToClient.storedNBT.hasKey(elem.name())){
				//Reverse order, because for the most part the more basic elements are at the bottom of the list.
				pageList.add(1, lore ? getPage(elem) : getPageNoLore(elem));
				if(GuideBooks.smallText){
					((PageText) pageList.get(1)).setUnicodeFlag(true);
				}
			}
		}
	}
	
	private IPage getPage(MagicElements elem){
		switch(elem){
			case CHARGE:
				return new PageText(elem.toString() + ": Creates lightning wherever it hits. Turns any rock materials into redstone blocks. In a Crystalline Master Axis it adds 10 energy/tick in the + direction.");
			case ENCHANTMENT:
				return new PageText(elem.toString() + ": Enchants nearby items on the ground randomly. Higher power beams increase enchanting level. Can create treasure enchants with power >= 32.");
			case ENERGY:
				return new PageText(elem.toString() + ": Adds (power)*C/5 ticks to whatever heat devices it hits. In a Crystalling Master Axis is adds 10 energy/tick in the direction gears are currently spinning.");
			case EQUALIBRIUM:
				return new PageText(elem.toString() + ": The force of a stalemate, where equal powers fight against eachother leading to no net change. Does nothing on its own. In a Crystalline Master Axis it reduces the change in energy each tick by 75%.");
			case EXPANSION:
				return new PageText(elem.toString() + ": Expands items within a range of (power) into full blocks.");
			case FUSION:
				return new PageText(elem.toString() + ": Fuses the molecules of certain blocks into other blocks. Certain conversions require higher powers. Ex. Sand -> Pure Quartz Block when power >= 16, or Coal Block -> Diamond Block when power >= 128.");
			case LIGHT:
				return new PageText(elem.toString() + ": All magic is similar to light, but this element is closer than most. Converts rock materials into glowstone, and glass materials into glowglass (a light emitting glass that drops itself).");
			case NO_MATCH:
				return new PageText(elem.toString() + ": This element should not be obtainable. Report to mod author.");
			case POTENTIAL:
				return new PageText(elem.toString() + ": Brings out the potential in living things, growing crops faster based on power.");
			case RIFT:
				return new PageText(elem.toString() + ": A form of energy that makes things more stable. Such a thing is not possible in our universe, so it opens a rift into a neighboring universe where such a thing is possible. This universe is where mobs come from. Spawns hostile mobs (up to cap) at a rate based on (power). When power >= 128, spawns 1 mob every 5 ticks. Converts purpur blocks->shulkers, skulls->wither skeleton skulls, & stone->silverfish stone.");
			case STABILITY:
				return new PageText(elem.toString() + ": Does nothing on its own. In a Crystalline Master Axis it prevents power loss.");
			case TIME:
				return new PageText(elem.toString() + ": Speeds up tile entities and random block updates. DO NOT MIX WITH VOID!");
			case VOID:
				return new PageText(elem.toString() + ": A paradoxial element, the essence of nothingness that forms the barrier between worlds. If anything, this is what holes are made of. Destroys anything it touches.");
			default:
				return new PageText(elem.toString() + ": DOCUMENTATION NYI");
		}
	}
	
	private IPage getPageNoLore(MagicElements elem){
		switch(elem){
			case CHARGE:
				return new PageText(elem.toString() + ": Creates lightning wherever it hits. Turns any rock materials into redstone blocks. In a Crystalline Master Axis it adds 10 energy/tick in the + direction.");
			case ENCHANTMENT:
				return new PageText(elem.toString() + ": Enchants nearby items on the ground randomly. Higher power beams increase enchanting level. Can create treasure enchants when power >= 32.");
			case ENERGY:
				return new PageText(elem.toString() + ": Adds (power)*C/5 ticks to whatever heat devices it hits. In a Crystalling Master Axis is adds 10 energy/tick in the direction gears are currently spinning.");
			case EQUALIBRIUM:
				return new PageText(elem.toString() + ": Does nothing on its own. In a Crystalline Master Axis it reduces the change in energy each tick by 75%.");
			case EXPANSION:
				return new PageText(elem.toString() + ": Places items within a range of (power) as blocks.");
			case FUSION:
				return new PageText(elem.toString() + ": Converts certain blocks into other blocks. Certain conversions require higher powers. Ex. Sand -> Pure Quartz Block when power >= 16, or Coal Block -> Diamond Block when power >= 128.");
			case LIGHT:
				return new PageText(elem.toString() + ": Converts rock materials into glowstone, and glass materials into glowglass (a light emitting glass that drops itself).");
			case NO_MATCH:
				return new PageText(elem.toString() + ": This element should not be obtainable. Report to mod author.");
			case POTENTIAL:
				return new PageText(elem.toString() + ": Grows crops faster based on power.");
			case RIFT:
				return new PageText(elem.toString() + ": Spawns hostile mobs (Up to #of players * 3 * mob limit total). Converts purpur blocks to shulkers, skeleton skulls to wither skeleton skulls, and stone to silverfish stone.");
			case STABILITY:
				return new PageText(elem.toString() + ": Does nothing on its own. In a Crystalline Master Axis it prevents power loss.");
			case TIME:
				return new PageText(elem.toString() + ": Speeds up tile entities and random block updates. DO NOT MIX WITH VOID!");
			case VOID:
				return new PageText(elem.toString() + ": Destroys anything it touches.");
			default:
				return new PageText(elem.toString() + ": DOCUMENTATION NYI");
		}
	}
}
