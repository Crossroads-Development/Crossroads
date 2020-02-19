import os
# Generates a simplified recipe unlock JSON for every Crossroads recipe
# Doesn't have proper unlocks, only checks if the player has already used the recipe. People use JEI, right?
# Maintains file structure


def writeTemplate(posOut: str, template, recName: str, recPath: str):
	with open(posOut + "unlock_rec_" + recName + ".json", "w+") as out:
		lines = template.readlines()
		template.seek(0)
		lines = [line.replace("<RECIPE>", "crossroads:" + recPath) for line in lines]
		out.writelines(lines)


with open("recipe_unlock_template.json", "r") as template:

	outputPath = "../data/crossroads/advancements/recipes/"
	srcPath = "../data/crossroads/recipes/"

	# Delete all previous files in the outputPath
	for prevTable in os.listdir(outputPath):
		if os.path.isfile(prevTable):
			os.unlink(prevTable)

	for dirName, subDirList, fileList in os.walk(srcPath):
		# print(dirName)
		outDir = dirName.replace('\\', '/').replace("/recipes", "/advancements/recipes")
		if outDir[-1] != '/':  # Any directory other than recipes root
			os.makedirs(outDir, exist_ok=True)
			outDir = outDir + '/'
		for file in fileList:
			if file.endswith(".json"):
				recName = file.replace(".json", "")
				fullRecName = outDir.replace("../data/crossroads/advancements/recipes/", "") + recName
				writeTemplate(outDir, template, recName, fullRecName)

	# with open("templates/" + template, "r") as fTemp:
	# 	for type in types:
	# 		with open(outputPath + "/" + template.replace(".json", type[0] + ".json"), "w+") as fOut:
	# 			writeTemplate(fOut, fTemp, type)
	# 			fOut.close()
	# 	fTemp.close()
