package com.Da_Technomancer.crossroads.integration.GuideAPI;

import java.util.ArrayList;
import java.util.function.Predicate;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Pair;

import com.Da_Technomancer.crossroads.Main;

import amerifrance.guideapi.api.IPage;
import amerifrance.guideapi.api.impl.Book;
import amerifrance.guideapi.api.impl.Page;
import amerifrance.guideapi.api.impl.abstraction.CategoryAbstract;
import amerifrance.guideapi.api.util.TextHelper;
import amerifrance.guideapi.entry.EntryItemStack;
import amerifrance.guideapi.gui.GuiCategory;
import amerifrance.guideapi.page.PageIRecipe;
import amerifrance.guideapi.page.PageImage;
import amerifrance.guideapi.page.PageText;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SmartEntry extends EntryItemStack{

	private final Object[] contents;
	private final Predicate<EntityPlayer> canSee;

	/**
	 * @param unlocalizedName The unlocalized name of this entry.
	 * @param icon The ItemStack displayed on this entry in the category.
	 * @param contents Determines what this entry contains. IPages will be stitched in, Strings will be localized, wrapped, and added (with formatting), IRecipes will be converted to a recipe page, ResourceLocations converted to an image page, Pair{@literal<String, Object[]>} will be treated as a String with the Object[] formatted in, a true boolean value will cause a page break, an Object[] will have each member interpreted in order, null or a false boolean value will be ignored, and a {@link java.util.function.Supplier}{@literal <Object>} will act as whatever is returned by the get() method.
	 * Any time formatting is used, it should be specified as �r�(formatting character). It will last until either the next formatting code or forced page break (due to ex. recipes). It will be carried over across true values.
	 * 
	 * This updates its contents every time it is reopened. 
	 */
	public SmartEntry(String unlocalizedName, ItemStack icon, Object... contents){
		this(unlocalizedName, null, icon, contents);
	}

	/**
	 * @param unlocalizedName The unlocalized name of this entry.
	 * @param canSee Controls whether the player can see this entry at all. A null value means this can always be seen.
	 * @param icon The ItemStack displayed on this entry in the category.
	 * @param contents Determines what this entry contains. IPages will be stitched in, Strings will be localized, wrapped, and added (with formatting), IRecipes will be converted to a recipe page, ResourceLocations converted to an image page, Pair{@literal<String, Object[]>} will be treated as a String with the Object[] formatted in, a true boolean value will cause a page break, an Object[] will have each member interpreted in order, null or a false boolean value will be ignored, and a {@link java.util.function.Supplier}{@literal <Object>} will act as whatever is returned by the get() method.
	 * Any time formatting is used, it should be specified as §r§(formatting character). It will last until either the next formatting code or forced page break (due to ex. recipes). It will be carried over across true values.
	 * 
	 * This updates its contents every time it is reopened. 
	 */
	public SmartEntry(String unlocalizedName, @Nullable Predicate<EntityPlayer> canSee, ItemStack icon, Object... contents){
		super(new ArrayList<IPage>(), unlocalizedName, icon, true);
		this.contents = contents;
		this.canSee = canSee;
	}

	@Override
	public boolean canSee(EntityPlayer player, ItemStack bookStack){
		return canSee == null || canSee.test(player);
	}

	@Override
	public String getLocalizedName() {
		return TextHelper.localize(name);
	}

	private String active;

	@Override
	@SideOnly(Side.CLIENT)
	public void onInit(Book book, CategoryAbstract category, GuiCategory guiCategory, EntityPlayer player, ItemStack bookStack){
		pageList.clear();
		active = "";
		for(Object part : contents){
			interpretInput(part);
		}
		createTextPages();
	}

	/**
	 * @param input A single object from the contents array.
	 */
	@SuppressWarnings("unchecked")
	private void interpretInput(Object input){
		if(input == null){
			return;
		}
		if(input instanceof Supplier){
			interpretInput(((Supplier<Object>) input).get());
			return;
		}
		if(input instanceof Object[]){
			for(Object o : (Object[]) input){
				interpretInput(o);
			}
			return;
		}
		if(input instanceof String){
			active += TextHelper.localize((String) input);
			return;
		}
		if(input instanceof Pair && ((Pair<?, ?>) input).getLeft() instanceof String && ((Pair<?, ?>) input).getRight() instanceof Object[]){
			Pair<String, Object[]> pair = ((Pair<String, Object[]>) input);
			active += TextHelper.localize(pair.getLeft(), pair.getRight());
			return;
		}
		if(input instanceof Boolean){
			if(((Boolean) input).booleanValue()){
				active += "</n>";
			}
			return;
		}
		if(input instanceof IRecipe){
			createTextPages();
			pageList.add(new PageIRecipe((IRecipe) input));
			return;
		}
		if(input instanceof IPage){
			createTextPages();
			pageList.add((IPage) input);
			return;
		}
		if(input instanceof ResourceLocation){
			createTextPages();
			pageList.add(new PageImage((ResourceLocation) input));
			return;
		}

		throw new IllegalArgumentException(Main.MODID + ": Unsupported object type passed to guide book. Type: " + input.getClass().getName());
	}

	/**
	 * Splits up a long string into pages. Uses the String active, will add a page break at each {@literal </n>}.
	 * PageHelper doesn't work for this because of the � symbol and {@literal </n>}.
	 */
	private void createTextPages(){

		final int PERPAGE = 370;
		final char symbol = 167;
		final String pageSkip = "</n>";
		String format = "";
		String formatTemp = "";

		int start = 0;
		double length = 0;
		for(int i = 0; i < active.length(); i++){
			if(active.length() - i >= 4){
				if(active.charAt(i) == symbol){
					formatTemp = active.substring(i, i + 4);
					i += 4;
					continue;
				}
				
				if(active.substring(i, i + 4).equals(pageSkip)){
					//The .replace is to fix a bug where somehow (no clue how) some of the § symbols get turned to character 157. This turns them back.
					pageList.add(new PageText((format + active.substring(start, i)).replace((char) 157, symbol)));
					((Page) pageList.get(pageList.size() - 1)).setUnicodeFlag(true);
					format = formatTemp;
					length = 0;
					i += 4;
					start = i;
					continue;
				}
			}

			if(start != i && (i == active.length() - 1 || (length >= PERPAGE && active.charAt(i) == ' '))){
				//The .replace is to fix a bug where somehow (no clue how) some of the § symbols get turned to character 157. This turns them back.
				pageList.add(new PageText((format + active.substring(start, i + 1)).replace((char) 157, symbol)));
				((Page) pageList.get(pageList.size() - 1)).setUnicodeFlag(true);
				format = formatTemp;
				length = 0;
				start = i + 1;
			}else{
				//Bold text is thicker than normal text.
				length += formatTemp.equals("§r§l") ? 1.34D : 1;
			}
		}

		active = "";
	}
}
