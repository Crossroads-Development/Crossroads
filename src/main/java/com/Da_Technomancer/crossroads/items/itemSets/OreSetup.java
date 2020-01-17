package com.Da_Technomancer.crossroads.items.itemSets;

import com.Da_Technomancer.crossroads.API.EnergyConverters;
import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.blocks.BasicBlock;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.Da_Technomancer.crossroads.items.crafting.ItemRecipePredicate;
import com.Da_Technomancer.crossroads.items.crafting.ModCrafting;
import com.Da_Technomancer.crossroads.items.crafting.RecipeHolder;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public final class OreSetup{

	public static Item ingotTin;
	public static Item nuggetTin;
	public static BasicBlock blockTin;
	public static BasicBlock oreTin;

	public static Item ingotCopper;
	public static Item nuggetCopper;
	public static BasicBlock blockCopper;
	public static BasicBlock oreCopper;

	public static Item ingotBronze;
	public static Item nuggetBronze;
	public static BasicBlock blockBronze;

	public static Item gemRuby;
	public static BasicBlock blockRuby;
	public static BasicBlock oreRuby;

	public static Item ingotCopshowium;
	public static Item nuggetCopshowium;
	public static BasicBlock blockCopshowium;

	public static Item voidCrystal;
	public static BasicBlock oreVoid;

	public static final HashMap<String, OreProfile> metalStages = new HashMap<>();

	protected static void init(){
		//Register CR metal ores, blocks, ingots, nuggets manually
		ingotTin = new Item(CRItems.itemProp).setRegistryName("ingot_tin");
		blockTin = new BasicBlock("block_tin", Block.Properties.create(Material.IRON).hardnessAndResistance(5));
		nuggetTin = new Item(CRItems.itemProp).setRegistryName("nugget_tin", "nuggetTin");
		oreTin = new BasicBlock("ore_tin", Block.Properties.create(Material.ROCK).hardnessAndResistance(3));

		ingotCopper = new Item(CRItems.itemProp).setRegistryName("ingot_copper", "ingotCopper");
		blockCopper = new BasicBlock("block_copper", Block.Properties.create(Material.IRON).hardnessAndResistance(5));
		nuggetCopper = new Item(CRItems.itemProp).setRegistryName("nugget_copper", "nuggetCopper");
		oreCopper = new BasicBlock("ore_copper", Block.Properties.create(Material.ROCK).hardnessAndResistance(3));

		ingotBronze = new Item(CRItems.itemProp).setRegistryName("ingot_bronze", "ingotBronze");
		blockBronze = new BasicBlock("block_bronze", Block.Properties.create(Material.IRON).hardnessAndResistance(5));
		nuggetBronze = new Item(CRItems.itemProp).setRegistryName("nugget_bronze", "nuggetBronze");

		gemRuby = new Item(CRItems.itemProp).setRegistryName("gem_ruby", "gemRuby");
		blockRuby = new BasicBlock("block_ruby", Block.Properties.create(Material.ROCK).hardnessAndResistance(5));
		oreRuby = new BasicBlock("ore_ruby", Block.Properties.create(Material.ROCK).hardnessAndResistance(3).harvestLevel(3));

		ingotCopshowium = new Item(CRItems.itemProp).setRegistryName("ingot_copshowium", "ingotCopshowium");
		blockCopshowium = new BasicBlock("block_copshowium", Block.Properties.create(Material.IRON).harvestLevel(5));
		nuggetCopshowium = new Item(CRItems.itemProp).setRegistryName("nugget_copshowium", "nuggetCopshowium");

		voidCrystal = new Item(CRItems.itemProp).setRegistryName("void_crystal");
		oreVoid = new BasicBlock("ore_void", Block.Properties.create(Material.ROCK).harvestLevel(3));


		String[] rawInput = CRConfig.getConfigStringList(CRConfig.processableOres, true);

		//It's a HashMap instead of an ArrayList just in case a user decides to (incorrectly) list a metal twice
		HashMap<String, Color> metals = new HashMap<>(rawInput.length);
		Pattern pattern = Pattern.compile("\\w++ \\p{XDigit}{6}+");

		for(String raw : rawInput){
			//An enormous amount of input sanitization is involved here because the average config tweaker is slightly better at following instructions than the average walrus. And not one of those clever performing walruses (walri?) in aquariums, but a stupid walrus
			//Unless of course you're reading this because you're having trouble editing the config option, in which was you are way smarter than a clever walrus, thoroughly above average, and a genius, and the insults above definitely don't apply to you

			//Check for stupid whitespace
			raw = raw.trim();
			//Check the basic structure
			if(!pattern.matcher(raw).matches()){
				continue;
			}
			int spaceIndex = raw.length() - 7;
			String metal = "" + Character.toUpperCase(raw.charAt(0));
			Color col;
			//Make sure they aren't trying to register a one character metal
			//First character is capitalized for OreDict
			metal += raw.substring(1, spaceIndex);

			String colorString = '#' + raw.substring(spaceIndex + 1);
			try{
				col = Color.decode(colorString);
			}catch(NumberFormatException e){
				//Pick a random color because the user messed up, and if the user ends up with hot-pink lead that's their problem
				col = Color.getHSBColor((float) Math.random(), 1F, 1F);
			}

			//We survived user-input sanitization hell! Hazah!
			//This for-loop could have been like four lines if we could trust users to not ram flaming knives up their own bums and then blame the devs when they get mocked in the ER
			metals.put(metal, col);
		}

		ModelResourceLocation dustModel = new ModelResourceLocation(Crossroads.MODID + ":ore_dust", "inventory");
		ModelResourceLocation gravelModel = new ModelResourceLocation(Crossroads.MODID + ":ore_gravel", "inventory");
		ModelResourceLocation clumpModel = new ModelResourceLocation(Crossroads.MODID + ":ore_clump", "inventory");

		for(Map.Entry<String, Color> type : metals.entrySet()){
			String lowercaseMetal = type.getKey().toLowerCase();


			//Register dust, clump, gravel, and liquid
			Item dust = new Item(){
				@Override
				public String getItemStackDisplayName(ItemStack stack){
					return String.format(super.getItemStackDisplayName(stack), getMatName(type.getKey()));
				}
			}.setRegistryName(Crossroads.MODID, "dust_" + lowercaseMetal).setCreativeTab(CRItems.TAB_CROSSROADS).setTranslationKey("dust_metal");
			CRItems.toRegister.add(dust);
			CRItems.toClientRegister.put(Pair.of(dust, 0), dustModel);
			ModCrafting.toRegisterOreDict.add(Pair.of(dust, new String[] {"dust" + type.getKey()}));
			Item gravel = new Item(){
				@Override
				public String getItemStackDisplayName(ItemStack stack){
					return String.format(super.getItemStackDisplayName(stack), getMatName(type.getKey()));
				}
			}.setRegistryName(Crossroads.MODID, "gravel_" + lowercaseMetal).setCreativeTab(CRItems.TAB_CROSSROADS).setTranslationKey("gravel_metal");
			CRItems.toRegister.add(gravel);
			CRItems.toClientRegister.put(Pair.of(gravel, 0), gravelModel);
			Item clump = new Item(){
				@Override
				public String getItemStackDisplayName(ItemStack stack){
					return String.format(super.getItemStackDisplayName(stack), getMatName(type.getKey()));
				}
			}.setRegistryName(Crossroads.MODID, "clump_" + lowercaseMetal).setCreativeTab(CRItems.TAB_CROSSROADS).setTranslationKey("clump_metal");
			CRItems.toRegister.add(clump);
			CRItems.toClientRegister.put(Pair.of(clump, 0), clumpModel);

			Fluid fluid = new Fluid(lowercaseMetal, new ResourceLocation(Crossroads.MODID, "blocks/molten_metal_still"), new ResourceLocation(Crossroads.MODID, "blocks/molten_metal_flow")){
				@Override
				public String getLocalizedName(FluidStack stack){
					return String.format(super.getLocalizedName(stack), getMatName(type.getKey()));
				}
			}.setDensity(3000).setTemperature(1500).setLuminosity(15).setViscosity(1300).setColor(type.get()).setUnlocalizedName("molten_metal");
			FluidRegistry.registerFluid(fluid);
			BlockFluidClassic fluidBlock = (BlockFluidClassic) new BlockFluidClassic(fluid, Material.LAVA){
				@Override
				public String getLocalizedName(){
					return String.format(super.getLocalizedName(), getMatName(type.getKey()));
				}
			}.setTranslationKey("molten_metal").setRegistryName(Crossroads.MODID + ":molten_metal_" + lowercaseMetal);
			fluid.setBlock(fluidBlock);
			FluidRegistry.addBucketForFluid(fluid);
			CRBlocks.toRegister.add(fluidBlock);


			RecipeHolder.millRecipes.put(new TagCraftingStack("ore" + type.getKey()), new ItemStack[] {new ItemStack(dust, 2), new ItemStack(Blocks.SAND, 1)});
			RecipeHolder.millRecipes.put(new TagCraftingStack("ingot" + type.getKey()), new ItemStack[] {new ItemStack(dust, 1)});
			RecipeHolder.crucibleRecipes.put(new TagCraftingStack("ingot" + type.getKey()), new FluidStack(fluid, EnergyConverters.INGOT_MB));
			RecipeHolder.crucibleRecipes.put(new TagCraftingStack("nugget" + type.getKey()), new FluidStack(fluid, EnergyConverters.INGOT_MB / 9));
			RecipeHolder.crucibleRecipes.put(new TagCraftingStack("dust" + type.getKey()), new FluidStack(fluid, EnergyConverters.INGOT_MB));
			RecipeHolder.crucibleRecipes.put(new TagCraftingStack("ore" + type.getKey()), new FluidStack(fluid, 2 * EnergyConverters.INGOT_MB));
			RecipeHolder.stampMillRecipes.put(new TagCraftingStack("ore" + type.getKey()), new ItemStack(gravel, 3));
			RecipeHolder.oreCleanserRecipes.put(new ItemRecipePredicate(gravel, 0), new ItemStack(clump, 1));
			RecipeHolder.blastFurnaceRecipes.put(new ItemRecipePredicate(gravel, 0), Pair.of(new FluidStack(fluid, EnergyConverters.INGOT_MB), 2));
			RecipeHolder.blastFurnaceRecipes.put(new ItemRecipePredicate(clump, 0), Pair.of(new FluidStack(fluid, EnergyConverters.INGOT_MB), 1));

			OreProfile profile = new OreProfile(dust, gravel, clump, fluid, fluidBlock, type.get());
			metalStages.put(type.getKey(), profile);
		}
	}

	protected static void initCrafting(){
		for(Map.Entry<String, OreProfile> ent : metalStages.entrySet()){
			if(FMLCommonHandler.instance().getSide() == Side.CLIENT){
				registerColor(ent.get());
			}

			ItemStack ingot = MiscUtil.getOredictStack("ingot" + ent.getKey(), 1);
			if(!ingot.isEmpty()){
				GameRegistry.addSmelting(new ItemStack(ent.get().dust, 1), ingot, .7F);
				RecipeHolder.fluidCoolingRecipes.put(ent.get().molten, Pair.of(EnergyConverters.INGOT_MB, Triple.of(ingot, 1500D, 250D)));
			}
		}

		OreProfile iron = OreSetup.metalStages.get("Iron");
		if(iron != null){
			RecipeHolder.crucibleRecipes.put(new ItemRecipePredicate(Items.MINECART, 0), new FluidStack(iron.molten, 5 * EnergyConverters.INGOT_MB));
			RecipeHolder.crucibleRecipes.put(new ItemRecipePredicate(Items.IRON_DOOR, 0), new FluidStack(iron.molten, 2 * EnergyConverters.INGOT_MB));
			RecipeHolder.crucibleRecipes.put(new ItemRecipePredicate(Items.BUCKET, 0), new FluidStack(iron.molten, 3 * EnergyConverters.INGOT_MB));
			RecipeHolder.crucibleRecipes.put(new ItemRecipePredicate(Blocks.IRON_TRAPDOOR, 0), new FluidStack(iron.molten, 4 * EnergyConverters.INGOT_MB));
			RecipeHolder.crucibleRecipes.put(new ItemRecipePredicate(Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE, 0), new FluidStack(iron.molten, 2 * EnergyConverters.INGOT_MB));
			RecipeHolder.crucibleRecipes.put(new ItemRecipePredicate(Blocks.CAULDRON, 0), new FluidStack(iron.molten, 7 * EnergyConverters.INGOT_MB));
		}
		OreProfile gold = OreSetup.metalStages.get("Gold");
		if(gold != null){
			RecipeHolder.crucibleRecipes.put(new ItemRecipePredicate(Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE, 0), new FluidStack(gold.molten, 2 * EnergyConverters.INGOT_MB));
		}
	}

	@OnlyIn(Dist.CLIENT)
	private static void registerColor(OreProfile profile){
		IItemColor itemColoring = (ItemStack stack, int tintIndex) -> tintIndex == 0 ? profile.col.getRGB() : -1;
		Minecraft.getInstance().getItemColors().registerItemColorHandler(itemColoring, profile.dust, profile.gravel, profile.clump);
	}

	public static class OreProfile{

		public final Item dust;
		public final Item gravel;
		public final Item clump;
		public final Fluid molten;
		public final BlockFluidClassic moltenBlock;
		public final Color col;


		private OreProfile(Item dust, Item gravel, Item clump, Fluid molten, BlockFluidClassic fluidBlock, Color col){
			this.dust = dust;
			this.gravel = gravel;
			this.clump = clump;
			this.molten = molten;
			this.moltenBlock = fluidBlock;
			this.col = col;
		}
	}

	private static String getMatName(String oreName){
		//So this is my mad scheme for getting the material names for things defined via config
		//Step 1: Assume it has an ingot registered in the oreDict
		//Step 2: Get that ingot's name
		//Step 3: Assume the name takes the format "matName ingot"
		//Step 4: Hope this works in other languages
		ItemStack ingot = MiscUtil.getOredictStack("ingot" + oreName, 1);
		if(!ingot.isEmpty()){
			String ingotName = ingot.getItem().getItemStackDisplayName(ingot);
			return ingotName.substring(0, ingotName.lastIndexOf(' ')).trim();
		}

		//Default to returning the oreName back, because atleast it's something
		return oreName;
	}
}
