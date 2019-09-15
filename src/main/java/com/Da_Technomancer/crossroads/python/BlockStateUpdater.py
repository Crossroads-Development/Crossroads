import os
# Modifies old Forge blockstates to use the full model path, per the change in the format
blockstates = os.listdir("../../../../../resources/assets/crossroads/blockstates/")

regNames = [os.path.basename(bstate) for bstate in blockstates]

for state in blockstates:
	f = open("../../../../../resources/assets/crossroads/blockstates/" + state, "r")
	lines = f.readlines()
	f.close()
	if lines[1].find("\"forge_marker\"") != -1:
		# This is a forge blockstate file

		for i in range(len(lines)):
			index = lines[i].find("crossroads:")
			if index != -1:
				lines[i] = lines[i][0:index + 11] + "block/" + lines[i][index + 11:]

		f = open("../../../../../resources/assets/crossroads/blockstates/" + state, "w+")
		f.writelines(lines)
		f.close()
