# Generate the 14 different heat cable item model JSONs

cables = ["ceramic", "densus", "dirt", "ice", "obsidian", "slime", "wool"]
path = "../assets/crossroads/models/item/heat_cable_"
rPath = "../assets/crossroads/models/item/redstone_heat_cable_"

for cable in cables:
	with open(path + cable + ".json", "w+") as f:
		f.write("{\n\t\"parent\": \"block/cube_all\",\n\t\"textures\": {\n\t\t\"all\": \"crossroads:block/heatcable/" + cable + "-copper\"\n\t},\n\t\"display\": {\n\t\t\"thirdperson\": {\n\t\t\t\"rotation\": [ 10, -45, 170],\n\t\t\t\"translation\": [ 0, 1.5, -2.75],\n\t\t\t\"scale\": [ 0.375, 0.375, 0.375]\n\t\t}\n\t}\n}\n")
		f.close()

	with open(rPath + cable + ".json", "w+") as f:
		f.write("{\n\t\"parent\": \"block/cube_all\",\n\t\"textures\": {\n\t\t\"all\": \"crossroads:block/heatcable/" + cable + "-copper-redstone\"\n\t},\n\t\"display\": {\n\t\t\"thirdperson\": {\n\t\t\t\"rotation\": [ 10, -45, 170],\n\t\t\t\"translation\": [ 0, 1.5, -2.75],\n\t\t\t\"scale\": [ 0.375, 0.375, 0.375]\n\t\t}\n\t}\n}\n")
		f.close()
