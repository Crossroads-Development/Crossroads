# Generate all the heat cable blockstate files in the model folder

sides = {"up": ("0", "0", "0"), "down": ("1", "180", "0"), "east": ("2", "90", "90"), "west": ("3", "90", "270"), "north": ("4", "90", "0"), "south": ("5", "90", "180")}
conductors = ["copper", "diamond", "iron", "quartz"]
insulators = ["ceramic", "obsidian", "wool", "slime", "dirt", "ice", "densus"]
redstoneVals = [True, False]

path = "../../../../../resources/assets/crossroads/blockstates/"

for insulator in insulators:
	for reds in redstoneVals:
		suffix = "-redstone" if reds else ""
		with open(path + ("redstone_" if reds else "") + "heat_cable_" + insulator + ".json", "w+") as f:
			f.write("{\n\t\"forge_marker\": 1,\n\t\"defaults\": {\n\t\t\"textures\": {\n\t\t\t\"side\": \"crossroads:blocks/heatcable/")  # Begin file
			f.write(insulator + "-copper" + suffix)  # Default texture
			f.write("\"\n\t\t},\n\t\t\"model\": \"crossroads:conduit/heat_cable_core\",\n\t\t\"uvlock\": false\n\t},\n\t\"variants\": {\n")  # Set defaults

			# Basic property branches

			# Various skins
			f.write("\t\t\"skin\": {\n")
			for skin in conductors:
				f.write("\t\t\t\"" + skin + "\": { \"textures\": {\"side\": \"crossroads:blocks/heatcable/" + insulator + "-" + skin + suffix + "\"}}")
				if skin == conductors[-1]:
					f.write("\n")
				else:
					f.write(",\n")
			f.write("\t\t},\n")

			# Redstone bool, empty (only if reds)
			if reds:
				f.write("\t\t\"redstone_bool\": {\n")
				f.write("\t\t\t\"true\": {},\n")
				f.write("\t\t\t\"false\": {}\n")
				f.write("\t\t},\n")

			# Each of the directions
			# These are empty if reds
			for dir in sides.keys():
				f.write("\t\t\"" + dir + "\": {\n")
				if reds:
					f.write("\t\t\t\"true\": {},\n")
					f.write("\t\t\t\"false\": {}\n\t\t},\n")
				else:
					f.write("\t\t\t\"true\": {\n")
					# Rotated top section submodel
					f.write("\t\t\t\t\"submodel\": { \"end_" + sides[dir][0] + "\": {\"model\": \"crossroads:conduit/heat_cable_top\", \"x\": " + sides[dir][1] + ", \"y\": " + sides[dir][2] + " }}\n")
					f.write("\t\t\t},\n")
					f.write("\t\t\t\"false\": {}\n\t\t}" + ("\n" if dir == "south" else ",\n"))

			if reds:
				bools = {False: "false", True: "true"}
				# This gets a little messy- specifically, it gets 2^8 individual variant definitions messy
				# We only use the submodels for a redstone cable if redstone_bool is true, but combining conditions requires enumerating every possible combo for forge blockstates
				for skin in conductors:
					for b0 in bools.keys():
						for b1 in bools.keys():
							for b2 in bools.keys():
								for b3 in bools.keys():
									for b4 in bools.keys():
										for b5 in bools.keys():
											f.write("\t\t\"redstone_bool=true,skin=" + skin + ",down=" + bools[b1] + ",up=" + bools[b0] + ",north=" + bools[b4] + ",south=" + bools[b5] + ",west=" + bools[b3] + ",east=" + bools[b2] + "\": { \"submodel\": {")
											models = ""

											if b0:
												dir = "up"
												models += " \"end_0\": {\"model\": \"crossroads:conduit/heat_cable_top\", \"x\": " + sides[dir][1] + ", \"y\": " + sides[dir][2] + " },"

											if b1:
												dir = "down"
												models += " \"end_1\": {\"model\": \"crossroads:conduit/heat_cable_top\", \"x\": " + sides[dir][1] + ", \"y\": " + sides[dir][2] + " },"

											if b2:
												dir = "east"
												models += " \"end_2\": {\"model\": \"crossroads:conduit/heat_cable_top\", \"x\": " + sides[dir][1] + ", \"y\": " + sides[dir][2] + " },"

											if b3:
												dir = "west"
												models += " \"end_3\": {\"model\": \"crossroads:conduit/heat_cable_top\", \"x\": " + sides[dir][1] + ", \"y\": " + sides[dir][2] + " },"

											if b4:
												dir = "north"
												models += " \"end_4\": {\"model\": \"crossroads:conduit/heat_cable_top\", \"x\": " + sides[dir][1] + ", \"y\": " + sides[dir][2] + " },"

											if b5:
												dir = "south"
												models += " \"end_5\": {\"model\": \"crossroads:conduit/heat_cable_top\", \"x\": " + sides[dir][1] + ", \"y\": " + sides[dir][2] + " },"

											if len(models) > 1:
												models = models[0:-1]  # Remove the final closing comma

											f.write(models)

											if b0 and b1 and b2 and b3 and b4 and b5 and skin == conductors[-1]:
												f.write("}}\n")
											else:
												f.write("}},\n")

			f.write("\t}\n}\n")  # Closing
