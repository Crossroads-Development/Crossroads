package com.Da_Technomancer.crossroads.api.technomancy;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.Keys;
import com.Da_Technomancer.crossroads.api.crafting.CraftingUtil;
import com.Da_Technomancer.crossroads.crafting.CRItemTags;
import com.Da_Technomancer.crossroads.effects.goggles_effects.EmeraldGoggleEffect;
import com.Da_Technomancer.crossroads.effects.goggles_effects.QuartzGoggleEffect;
import com.Da_Technomancer.crossroads.effects.goggles_effects.RubyGoggleEffect;
import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.extensions.IKeyMappingExtension;
import net.neoforged.neoforge.common.Tags;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;

public enum EnumGoggleLenses implements StringRepresentable{

	//Don't reorder these unless you want to rename all the goggle texture files.
	RUBY(CRItemTags.GEMS_RUBY, "_ruby", new RubyGoggleEffect(), () -> Keys.controlEnergy, true),
	EMERALD(Tags.Items.GEMS_EMERALD, "_emerald", new EmeraldGoggleEffect(), () -> Keys.controlPotential, true),
	DIAMOND(Tags.Items.GEMS_DIAMOND, "_diamond", IGoggleEffect.EMPTY, () -> Keys.controlStability, false),//Effect in SendGoggleConfigureToServer
	QUARTZ(CRItemTags.GEMS_PURE_QUARTZ, "_quartz", new QuartzGoggleEffect(), null, false),
	AMETHYST(Tags.Items.GEMS_AMETHYST, "_amethyst", IGoggleEffect.EMPTY, () -> Keys.controlZoom, true),//Empty effect, the actual effect is done through EventHandlerClient::viewZoom
	VOID(CRItemTags.GEMS_VOID, "_void", IGoggleEffect.EMPTY, () -> Keys.controlVoid, true);//Empty effect, the actual effect is done through EventHandlers that check for the void lens.

	public static final Codec<EnumGoggleLenses> CODEC = StringRepresentable.fromEnum(EnumGoggleLenses::values);

	private final TagKey<Item> item;
	private final String texturePath;
	private final IGoggleEffect effect;
	//This is a supplier to allow lazy-loading the keys, which may not be registered at initialization time
	@Nullable
	private final Supplier<IKeyMappingExtension> key;
	private final boolean requireEnable;
	private final ResourceLocation textureLocation;

	EnumGoggleLenses(TagKey<Item> item, String texturePath, IGoggleEffect effect, @Nullable Supplier<IKeyMappingExtension> toggleKey, boolean requireEnable){
		this.item = item;
		this.texturePath = texturePath;
		this.effect = effect;
		this.key = toggleKey;
		this.requireEnable = requireEnable;
		this.textureLocation = ResourceLocation.fromNamespaceAndPath(Crossroads.MODID, "textures/models/armor/technomancy_goggle" + texturePath + ".png");
	}

	public boolean matchesRecipe(ItemStack stack){
		return CraftingUtil.tagContains(item, stack.getItem());
	}

	public ResourceLocation getTextureLocation(){
		return textureLocation;
	}

	@Nullable
	@OnlyIn(Dist.CLIENT)
	public IKeyMappingExtension getKey(){
		if(key == null){
			return null;
		}
		return key.get();
	}

	public boolean requireEnableKey(){
		return requireEnable;
	}

	/**
	 * Call on the server side ONLY.
	 */
	public void doEffect(Level world, Player player){
		effect.armorTick(world, player);
	}

	@Override
	public String toString(){
		return name().toLowerCase(Locale.US);
	}

	@Override
	public String getSerializedName(){
		return toString();
	}

	private static final List<ArmorMaterial.Layer> EXTRA_BLANK_LAYERS;

	static{
		EXTRA_BLANK_LAYERS = new ArrayList<>(values().length + 1);
		int totalLensTypes = values().length;
		for(int i = 0; i < totalLensTypes; i++){
			EXTRA_BLANK_LAYERS.add(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(Crossroads.MODID, "technomancy"), "_blank_" + i + "_", false));
		}
	}

	public static List<ArmorMaterial.Layer> makeBlankLayers(ArmorMaterial.Layer firstLayer){
		List<ArmorMaterial.Layer> layers = new ArrayList<>(values().length + 1);
		layers.add(firstLayer);
		layers.addAll(EXTRA_BLANK_LAYERS);
		return layers;
	}
}
