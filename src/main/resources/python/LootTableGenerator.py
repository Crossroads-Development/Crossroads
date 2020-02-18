import os
# Generates a basic loot table entry for every essentials blocks with a defined blockstate file,
# where the blocks drops itself

blockstates = os.listdir("../assets/crossroads/blockstates/")

regNames = [os.path.basename(bstate) for bstate in blockstates]

loottablePath = "../data/crossroads/loot_tables/blocks"

for prevTable in os.listdir(loottablePath):
	if os.path.isfile(prevTable):
		os.unlink(prevTable)

def writeGem(file, blockName, gemName):
	file.write("{\n\t\"type\": \"minecraft:block\",\n\t\"pools\": [\n\t\t{\n\t\t\t\"rolls\": 1,\n\t\t\t\"entries\": [\n\t\t\t\t{\n\t\t\t\t\t\"type\": \"minecraft:alternatives\",\n\t\t\t\t\t\"children\": [\n\t\t\t\t\t\t{\n\t\t\t\t\t\t\t\"type\": \"minecraft:item\",\n\t\t\t\t\t\t\t\"conditions\": [\n\t\t\t\t\t\t\t\t{\n\t\t\t\t\t\t\t\t\t\"condition\": \"minecraft:match_tool\",\n\t\t\t\t\t\t\t\t\t\"predicate\": {\n\t\t\t\t\t\t\t\t\t\t\"enchantments\": [\n\t\t\t\t\t\t\t\t\t\t\t{\n\t\t\t\t\t\t\t\t\t\t\t\t\"enchantment\": \"minecraft:silk_touch\",\n\t\t\t\t\t\t\t\t\t\t\t\t\"levels\": {\n\t\t\t\t\t\t\t\t\t\t\t\t\t\"min\": 1\n\t\t\t\t\t\t\t\t\t\t\t\t}\n\t\t\t\t\t\t\t\t\t\t\t}\n\t\t\t\t\t\t\t\t\t\t]\n\t\t\t\t\t\t\t\t\t}\n\t\t\t\t\t\t\t\t}\n\t\t\t\t\t\t\t],\n\t\t\t\t\t\t\t\"name\": \"")
	file.write(blockName)
	file.write("\"\n\t\t\t\t\t\t},\n\t\t\t\t\t\t{\n\t\t\t\t\t\t\t\"type\": \"minecraft:item\",\n\t\t\t\t\t\t\t\"functions\": [\n\t\t\t\t\t\t\t\t{\n\t\t\t\t\t\t\t\t\t\"function\": \"minecraft:apply_bonus\",\n\t\t\t\t\t\t\t\t\t\"enchantment\": \"minecraft:fortune\",\n\t\t\t\t\t\t\t\t\t\"formula\": \"minecraft:ore_drops\"\n\t\t\t\t\t\t\t\t},\n\t\t\t\t\t\t\t\t{\n\t\t\t\t\t\t\t\t\t\"function\": \"minecraft:explosion_decay\"\n\t\t\t\t\t\t\t\t}\n\t\t\t\t\t\t\t],\n\t\t\t\t\t\t\t\"name\": \"")
	file.write(gemName)
	file.write("\"\n\t\t\t\t\t\t}\n\t\t\t\t\t]\n\t\t\t\t}\n\t\t\t]\n\t\t}\n\t]\n}")

for name in regNames:
	filepath = loottablePath + "/" + name
	with open(filepath, "w+") as f:
		if name.startswith("stamp_mill_top") or name.startswith("light_cluster"):
			# Stamp mill tops and light clusters drop nothing
			f.write("{\n\t\"type\": \"minecraft:block\",\n\t\"pools\": [\n\t\t{\n\t\t\t\"rolls\": 1,\n\t\t\t\"entries\": [\n\t\t\t\t\n\t\t\t],\n\t\t\t\"conditions\": [\n\t\t\t\t{\n\t\t\t\t\t\"condition\": \"minecraft:survives_explosion\"\n\t\t\t\t}\n\t\t\t]\n\t\t}\n\t]\n}")
		elif name.startswith("ore_void"):
			writeGem(f, "crossroads:ore_void", "crossroads:void_crystal")
		elif name.startswith("ore_ruby"):
			writeGem(f, "crossroads:ore_ruby", "crossroads:gem_ruby")
		else:
			f.write("{\n\t\"type\": \"minecraft:block\",\n\t\"pools\": [\n\t\t{\n\t\t\t\"rolls\": 1,\n\t\t\t\"entries\": [\n\t\t\t\t{\n\t\t\t\t\t\"type\": \"minecraft:item\",\n\t\t\t\t\t\"name\": \"crossroads:" + name.replace(".json", "", 1) + "\"\n\t\t\t\t}\n\t\t\t],\n\t\t\t\"conditions\": [\n\t\t\t\t{\n\t\t\t\t\t\"condition\": \"minecraft:survives_explosion\"\n\t\t\t\t}\n\t\t\t]\n\t\t}\n\t]\n}")

		f.close()
