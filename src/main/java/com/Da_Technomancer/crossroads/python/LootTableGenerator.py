import os
# Generates a basic loot table entry for every essentials blocks with a defined blockstate file,
# where the blocks drops itself

blockstates = os.listdir("../../../../../resources/assets/crossroads/blockstates/")

regNames = [os.path.basename(bstate) for bstate in blockstates]

loottablePath = "../../../../../resources/data/crossroads/loot_tables/blocks"

for prevTable in os.listdir(loottablePath):
	if os.path.isfile(prevTable):
		os.unlink(prevTable)

for name in regNames:
	filepath = loottablePath + "/" + name
	with open(filepath, "w+") as f:
		if name.startswith("stamp_mill_top"):
			# Stamp mill tops drop nothing
			f.write("{\n\t\"type\": \"minecraft:block\",\n\t\"pools\": [\n\t\t{\n\t\t\t\"rolls\": 1,\n\t\t\t\"entries\": [\n\t\t\t\t\n\t\t\t],\n\t\t\t\"conditions\": [\n\t\t\t\t{\n\t\t\t\t\t\"condition\": \"minecraft:survives_explosion\"\n\t\t\t\t}\n\t\t\t]\n\t\t}\n\t]\n}")
		else:
			f.write("{\n\t\"type\": \"minecraft:block\",\n\t\"pools\": [\n\t\t{\n\t\t\t\"rolls\": 1,\n\t\t\t\"entries\": [\n\t\t\t\t{\n\t\t\t\t\t\"type\": \"minecraft:item\",\n\t\t\t\t\t\"name\": \"essentials:" + name.replace(".json", "", 1) + "\"\n\t\t\t\t}\n\t\t\t],\n\t\t\t\"conditions\": [\n\t\t\t\t{\n\t\t\t\t\t\"condition\": \"minecraft:survives_explosion\"\n\t\t\t\t}\n\t\t\t]\n\t\t}\n\t]\n}")

		f.close()
