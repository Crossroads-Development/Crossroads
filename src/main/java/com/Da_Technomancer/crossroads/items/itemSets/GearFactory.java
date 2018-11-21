package com.Da_Technomancer.crossroads.items.itemSets;

import com.Da_Technomancer.crossroads.ModConfig;
import com.Da_Technomancer.crossroads.items.crafting.ModCrafting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

public class GearFactory{

	public static final ArrayList<GearMaterial> gearMats = new ArrayList<>();
	public static final HashMap<GearMaterial, GearProfile> gearTypes = new HashMap<>();

	protected static void init(){
		String[] rawInput = ModConfig.getConfigStringList(ModConfig.gearTypes, true);

		Pattern pattern = Pattern.compile("\\w++ \\p{XDigit}{6}+ [+]?[0-9]*.?[0-9]+");

		for(String raw : rawInput){
			//An enormous amount of input sanitization is involved here because the average config tweaker is slightly better at following instructions than the average walrus. And not one of those clever performing walruses (walri?) in aquariums, but a stupid walrus
			//Unless of course you're reading this because you're having trouble editing the config option, in which was you are way smarter than a clever walrus, thoroughly above average, and a genius, and the insults above definitely don't apply to you

			//Check for stupid whitespace
			raw = raw.trim();
			//Check the basic structure
			if(!pattern.matcher(raw).matches()){
				continue;
			}
			int spaceIndex = raw.indexOf(' ');
			String metal = "" + Character.toUpperCase(raw.charAt(0));
			Color col;
			//Make sure they aren't trying to register a one character metal
			//First character is capitalized for OreDict
			metal += raw.substring(1, spaceIndex);

			String colorString = '#' + raw.substring(spaceIndex + 1, spaceIndex + 7);
			try{
				col = Color.decode(colorString);
			}catch(NumberFormatException e){
				//Pick a random color because the user messed up, and if the user ends up with hot-pink lead that's their problem
				col = Color.getHSBColor((float) Math.random(), 1F, 1F);
			}

			double density = Double.parseDouble(raw.substring(spaceIndex + 8));

			//We survived user-input sanitization hell! Hazah!
			//This for-loop could have been like four lines if we could trust users to not ram flaming knives up their own bums and then blame the devs when they get mocked in the ER
			GearMaterial typ = new GearMaterial(metal, density, col, gearMats.size());
			if(!gearMats.contains(typ)){
				gearMats.add(typ);
				gearTypes.put(typ, new GearProfile(new BasicGear(typ), new LargeGear(typ), new ToggleGear(typ, false), new ToggleGear(typ, true), new Axle(typ), new Clutch(false, typ), new Clutch(true, typ)));
			}
		}
	}

	public static void craftingInit(){
		for(Map.Entry<GearMaterial, GearProfile> entry : gearTypes.entrySet()){
			GearProfile prof = entry.getValue();
			String metal = entry.getKey().getName();
			ModCrafting.toRegister.add(new ShapedOreRecipe(null, new ItemStack(prof.getSmallGear(), 9), " ? ", "?#?", " ? ", '#', "block" + metal, '?', "ingot" + metal));
			ModCrafting.toRegister.add(new ShapedOreRecipe(null, new ItemStack(prof.getSmallGear(), 1), " ? ", "?#?", " ? ", '#', "ingot" + metal, '?', "nugget" + metal));
			ModCrafting.toRegister.add(new ShapelessOreRecipe(null, new ItemStack(prof.getToggleGear(), 1), Blocks.LEVER, "gear" + metal));
			ModCrafting.toRegister.add(new ShapelessOreRecipe(null, new ItemStack(prof.getInvToggleGear(), 1), Blocks.REDSTONE_TORCH, prof.getToggleGear()));
			ModCrafting.toRegister.add(new ShapelessOreRecipe(null, new ItemStack(prof.getToggleGear(), 1), prof.getInvToggleGear()));
			ModCrafting.toRegister.add(new ShapedOreRecipe(null, new ItemStack(prof.getLargeGear(), 2), "###", "#$#", "###", '#', "gear" + metal, '$', "block" + metal));
			ModCrafting.toRegister.add(new ShapedOreRecipe(null, new ItemStack(prof.getAxle(), 2), "#", "?", "#", '#', "ingot" + metal, '?', "stickWood"));
			ModCrafting.toRegister.add(new ShapedOreRecipe(null, new ItemStack(prof.getClutch(), 1), " *", "| ", '|', "stick" + metal, '*', "ingotTin"));
			ModCrafting.toRegister.add(new ShapelessOreRecipe(null, new ItemStack(prof.getInvClutch(), 1), Blocks.REDSTONE_TORCH, prof.getClutch()));
			ModCrafting.toRegister.add(new ShapelessOreRecipe(null, new ItemStack(prof.getClutch(), 1), prof.getInvClutch()));
		}
	}

	@SideOnly(Side.CLIENT)
	protected static void clientInit(){
		ItemColors itemColor = Minecraft.getMinecraft().getItemColors();
		for(GearMaterial typ : gearMats){
			IItemColor itemColoring = (ItemStack stack, int tintIndex) -> tintIndex == 0 ? typ.getColor().getRGB() : -1;
			GearProfile prof = gearTypes.get(typ);
			itemColor.registerItemColorHandler(itemColoring, prof.getSmallGear(), prof.getLargeGear(), prof.getToggleGear(), prof.getInvToggleGear());
			itemColor.registerItemColorHandler(itemColoring, prof.getAxle(), prof.getClutch(), prof.getInvClutch());
		}
	}

	public static GearMaterial findMaterial(String metalName){
		for(GearMaterial mat : gearMats){
			if(mat.getName().equals(metalName)){
				return mat;
			}
		}
		return gearMats.get(0);
	}

	public static class GearProfile{

		private final BasicGear smallGear;
		private final LargeGear largeGear;
		private final ToggleGear toggleGear;
		private final ToggleGear invToggleGear;
		private final Axle axle;
		private final Clutch clutch;
		private final Clutch invClutch;

		public GearProfile(BasicGear smallGear, LargeGear largeGear, ToggleGear toggleGear, ToggleGear invToggleGear, Axle axle, Clutch clutch, Clutch invClutch){
			this.smallGear = smallGear;
			this.largeGear = largeGear;
			this.toggleGear = toggleGear;
			this.invToggleGear = invToggleGear;
			this.axle = axle;
			this.clutch = clutch;
			this.invClutch = invClutch;
		}

		public BasicGear getSmallGear(){
			return smallGear;
		}

		public LargeGear getLargeGear(){
			return largeGear;
		}

		public ToggleGear getToggleGear(){
			return toggleGear;
		}

		public ToggleGear getInvToggleGear(){
			return invToggleGear;
		}

		public Axle getAxle(){
			return axle;
		}

		public Clutch getClutch(){
			return clutch;
		}

		public Clutch getInvClutch(){
			return invClutch;
		}
	}

	public static class GearMaterial{

		// The densities for the materials used here are kg/cubic meter of
		// the substance, for gears multiply by the number of cubic meters
		// it occupies.

//		IRON(8000D, new Color(160, 160, 160)),
//		GOLD(20000D, Color.YELLOW),
//		COPPER(9000D, new Color(255, 120, 60)),
//		TIN(7300D, new Color(240, 240, 240)),
//		BRONZE(8800D, new Color(255, 160, 60)),
//		COPSHOWIUM(0, new Color(255, 130, 0)),
//		LEAD(11000D, new Color(116, 105, 158)),
//		SILVER(10000D, new Color(189, 243, 238)),
//		NICKEL(9000D, new Color(241, 242, 196)),
//		INVAR(8000D, new Color(223, 237, 216)),
//		PLATINUM(21000D, new Color(116, 245, 255)),
//		ELECTRUM(15000D, new Color(254, 255, 138));

		private final String name;
		private final double density;
		private final Color color;
		private final int index;

		private GearMaterial(String nameIn, double matDensity, Color matColor, int index){
			name = nameIn;
			density = matDensity;
			color = matColor;
			this.index = index;
		}

		public String getName(){
			return name;
		}

		public double getDensity(){
			return density;
		}

		public Color getColor(){
			return color;
		}

		public int getIndex(){
			return index;
		}

		/**This will return the name with all but the first char being lowercase,
		 * so COPPER becomes Copper, which is good for oreDict and registry
		 */
		@Override
		public String toString(){
//			String name = name();
//			char char1 = name.charAt(0);
//			name = name.substring(1);
//			name = name.toLowerCase();
//			name = char1 + name;
//			return name;
			return getName();
		}

		@Override
		public boolean equals(Object o){
			if(this == o){
				return true;
			}
			if(o == null || getClass() != o.getClass()){
				return false;
			}
			GearMaterial that = (GearMaterial) o;
			return Objects.equals(name, that.name);
		}

		@Override
		public int hashCode(){
			return Objects.hash(name);
		}
	}
}
