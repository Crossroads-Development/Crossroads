import os
# Generates a basic loot table entry for every CR blocks with a defined blockstate file,
# where the blocks drops itself
# Also adds the block to the pickaxe tag unless it is already in another tool tag


def writeGem(file, blockName, gemName):
	file.write("{\n\t\"type\": \"minecraft:block\",\n\t\"pools\": [\n\t\t{\n\t\t\t\"rolls\": 1,\n\t\t\t\"entries\": [\n\t\t\t\t{\n\t\t\t\t\t\"type\": \"minecraft:alternatives\",\n\t\t\t\t\t\"children\": [\n\t\t\t\t\t\t{\n\t\t\t\t\t\t\t\"type\": \"minecraft:item\",\n\t\t\t\t\t\t\t\"conditions\": [\n\t\t\t\t\t\t\t\t{\n\t\t\t\t\t\t\t\t\t\"condition\": \"minecraft:match_tool\",\n\t\t\t\t\t\t\t\t\t\"predicate\": {\n\t\t\t\t\t\t\t\t\t\t\"enchantments\": [\n\t\t\t\t\t\t\t\t\t\t\t{\n\t\t\t\t\t\t\t\t\t\t\t\t\"enchantment\": \"minecraft:silk_touch\",\n\t\t\t\t\t\t\t\t\t\t\t\t\"levels\": {\n\t\t\t\t\t\t\t\t\t\t\t\t\t\"min\": 1\n\t\t\t\t\t\t\t\t\t\t\t\t}\n\t\t\t\t\t\t\t\t\t\t\t}\n\t\t\t\t\t\t\t\t\t\t]\n\t\t\t\t\t\t\t\t\t}\n\t\t\t\t\t\t\t\t}\n\t\t\t\t\t\t\t],\n\t\t\t\t\t\t\t\"name\": \"")
	file.write(blockName)
	file.write("\"\n\t\t\t\t\t\t},\n\t\t\t\t\t\t{\n\t\t\t\t\t\t\t\"type\": \"minecraft:item\",\n\t\t\t\t\t\t\t\"functions\": [\n\t\t\t\t\t\t\t\t{\n\t\t\t\t\t\t\t\t\t\"function\": \"minecraft:apply_bonus\",\n\t\t\t\t\t\t\t\t\t\"enchantment\": \"minecraft:fortune\",\n\t\t\t\t\t\t\t\t\t\"formula\": \"minecraft:ore_drops\"\n\t\t\t\t\t\t\t\t},\n\t\t\t\t\t\t\t\t{\n\t\t\t\t\t\t\t\t\t\"function\": \"minecraft:explosion_decay\"\n\t\t\t\t\t\t\t\t}\n\t\t\t\t\t\t\t],\n\t\t\t\t\t\t\t\"name\": \"")
	file.write(gemName)
	file.write("\"\n\t\t\t\t\t\t}\n\t\t\t\t\t]\n\t\t\t\t}\n\t\t\t]\n\t\t}\n\t]\n}")


blockstates = os.listdir("../assets/crossroads/blockstates/")

# Note these have a .json suffix
regNames = [os.path.basename(bstate) for bstate in blockstates]

loottablePath = "../data/crossroads/loot_tables/blocks"
templatePath = '../python/templates/loot_tables/'
mineablePath = '../data/minecraft/tags/blocks/mineable'
pickaxeTagPath = mineablePath + '/pickaxe.json'

# Delete existing loot tables
for prevTable in os.listdir(loottablePath):
	if os.path.isfile(loottablePath + '/' + prevTable):
		os.unlink(loottablePath + '/' + prevTable)

# Delete existing pickaxe tag
if os.path.isfile(pickaxeTagPath):
	os.unlink(pickaxeTagPath)

# Build list of blocks which are already in a mineable tag, and shouldn't be added to pickaxe
existingMineables = []
for mineableTag in os.listdir(mineablePath):
	with open(mineablePath + '/' + mineableTag, "r") as f:
		# Note that this also adds a lot of gibberish (ex. empty lines, "replace": false, as replace:false, etc, but these shouldn't cause trouble
		existingMineables.extend([line.replace("\t", "").replace(",", "").replace(" ", "").replace("\"", "").replace("\n", "") for line in f.readlines()])
		f.close()

with open(pickaxeTagPath, "w+") as pickaxeTagFile:
	# Write opening lines of the pickaxe tag
	pickaxeTagFile.write("{\n\t\"replace\": false,\n\t\"values\": [")
	firstLine = True

	for name in regNames:
		filepath = loottablePath + "/" + name
		if "molten_" in name or "liquid_" in name or "distilled_water" in name or "dirty_water" in name or "steam.json" == name or "soul_essence" in name or "_solution" in name:
			# Fluids don't have loot tables
			continue

		blockRegID = name.replace(".json", "", 1)

		# Append to pickaxe tag
		if not ("crossroads:" + blockRegID) in existingMineables:
			if firstLine:
				pickaxeTagFile.write("\n\t\t\"crossroads:" + blockRegID + "\"")
				firstLine = False
			else:
				pickaxeTagFile.write(",\n\t\t\"crossroads:" + blockRegID + "\"")

		with open(filepath, "w+") as f:
			if name.startswith("stamp_mill_top") or name.startswith("light_cluster") or name.startswith("stamp_mill_top") or "large_gear_" in name or "mechanism" in name or "reactive_spot" in name:
				# Stamp mill tops and light clusters drop nothing; gear drops handled by TE
				f.write("{\n\t\"type\": \"minecraft:block\",\n\t\"pools\": [\n\t\t{\n\t\t\t\"rolls\": 1,\n\t\t\t\"entries\": [\n\t\t\t\t\n\t\t\t],\n\t\t\t\"conditions\": [\n\t\t\t\t{\n\t\t\t\t\t\"condition\": \"minecraft:survives_explosion\"\n\t\t\t\t}\n\t\t\t]\n\t\t}\n\t]\n}")
			elif name.startswith("ore_void"):
				writeGem(f, "crossroads:ore_void", "crossroads:void_crystal")
			elif name.startswith("ore_ruby"):
				writeGem(f, "crossroads:ore_ruby", "crossroads:gem_ruby")
			elif name.startswith("redstone_crystal"):
				# Redstone crystal drops 1-4 redstone dust (silk touch & fortune applies)
				f.write("{\n\t\"type\": \"minecraft:block\",\n\t\"pools\": [\n\t\t{\n\t\t\t\"rolls\": 1,\n\t\t\t\"entries\": [\n\t\t\t\t{\n\t\t\t\t\t\"type\": \"minecraft:alternatives\",\n\t\t\t\t\t\"children\": [\n\t\t\t\t\t\t{\n\t\t\t\t\t\t\t\"type\": \"minecraft:item\",\n\t\t\t\t\t\t\t\"conditions\": [\n\t\t\t\t\t\t\t\t{\n\t\t\t\t\t\t\t\t\t\"condition\": \"minecraft:match_tool\",\n\t\t\t\t\t\t\t\t\t\"predicate\": {\n\t\t\t\t\t\t\t\t\t\t\"enchantments\": [\n\t\t\t\t\t\t\t\t\t\t\t{\n\t\t\t\t\t\t\t\t\t\t\t\t\"enchantment\": \"minecraft:silk_touch\",\n\t\t\t\t\t\t\t\t\t\t\t\t\"levels\": {\n\t\t\t\t\t\t\t\t\t\t\t\t\t\"min\": 1\n\t\t\t\t\t\t\t\t\t\t\t\t}\n\t\t\t\t\t\t\t\t\t\t\t}\n\t\t\t\t\t\t\t\t\t\t]\n\t\t\t\t\t\t\t\t\t}\n\t\t\t\t\t\t\t\t}\n\t\t\t\t\t\t\t],\n\t\t\t\t\t\t\t\"name\": \"crossroads:redstone_crystal\"\n\t\t\t\t\t\t},\n\t\t\t\t\t\t{\n\t\t\t\t\t\t\t\"type\": \"minecraft:item\",\n\t\t\t\t\t\t\t\"functions\": [\n\t\t\t\t\t\t\t\t{\n\t\t\t\t\t\t\t\t\t\"function\": \"minecraft:set_count\",\n\t\t\t\t\t\t\t\t\t\"count\": {\n\t\t\t\t\t\t\t\t\t\t\"min\": 2.0,\n\t\t\t\t\t\t\t\t\t\t\"max\": 4.0,\n\t\t\t\t\t\t\t\t\t\t\"type\": \"minecraft:uniform\"\n\t\t\t\t\t\t\t\t\t}\n\t\t\t\t\t\t\t\t},\n\t\t\t\t\t\t\t\t{\n\t\t\t\t\t\t\t\t\t\"function\": \"minecraft:apply_bonus\",\n\t\t\t\t\t\t\t\t\t\"enchantment\": \"minecraft:fortune\",\n\t\t\t\t\t\t\t\t\t\"formula\": \"minecraft:uniform_bonus_count\",\n\t\t\t\t\t\t\t\t\t\"parameters\": {\n\t\t\t\t\t\t\t\t\t\t\"bonusMultiplier\": 1\n\t\t\t\t\t\t\t\t\t}\n\t\t\t\t\t\t\t\t},\n\t\t\t\t\t\t\t\t{\n\t\t\t\t\t\t\t\t\t\"function\": \"minecraft:limit_count\",\n\t\t\t\t\t\t\t\t\t\"limit\": {\n\t\t\t\t\t\t\t\t\t\t\"max\": 4,\n\t\t\t\t\t\t\t\t\t\t\"min\": 1\n\t\t\t\t\t\t\t\t\t}\n\t\t\t\t\t\t\t\t},\n\t\t\t\t\t\t\t\t{\n\t\t\t\t\t\t\t\t\t\"function\": \"minecraft:explosion_decay\"\n\t\t\t\t\t\t\t\t}\n\t\t\t\t\t\t\t],\n\t\t\t\t\t\t\t\"name\": \"minecraft:redstone\"\n\t\t\t\t\t\t}\n\t\t\t\t\t]\n\t\t\t\t}\n\t\t\t]\n\t\t}\n\t]\n}")
			elif name.startswith("densus_plate") or name.startswith("anti_densus_plate"):
				# Special loot table for density plate to drop the layers. From a template file
				with open(templatePath + "densus_plate.json", 'r') as template:
					lines = template.readlines()
					template.seek(0)
					lines = [line.replace("<BLOCK>", "crossroads:" + name[:-5]) for line in lines]
					f.writelines(lines)
			elif name.startswith("wheezewort"):
				# Special loot table for two tall plants. From a template file
				with open(templatePath + "double_plant.json", 'r') as template:
					lines = template.readlines()
					template.seek(0)
					lines = [line.replace("<BLOCK>", "crossroads:" + name[:-5]).replace("<ITEM>", "crossroads:wheezewort_seeds") for line in lines]
					f.writelines(lines)
			else:
				f.write("{\n\t\"type\": \"minecraft:block\",\n\t\"pools\": [\n\t\t{\n\t\t\t\"rolls\": 1,\n\t\t\t\"entries\": [\n\t\t\t\t{\n\t\t\t\t\t\"type\": \"minecraft:item\",\n\t\t\t\t\t\"name\": \"crossroads:" + blockRegID + "\"\n\t\t\t\t}\n\t\t\t],\n\t\t\t\"conditions\": [\n\t\t\t\t{\n\t\t\t\t\t\"condition\": \"minecraft:survives_explosion\"\n\t\t\t\t}\n\t\t\t]\n\t\t}\n\t]\n}")

			f.close()

	# Write closing lines of pickaxe tag
	pickaxeTagFile.write("\n\t]\n}")