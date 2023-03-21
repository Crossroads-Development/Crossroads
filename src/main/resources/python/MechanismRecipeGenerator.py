import os

# Generates several copies of each json in python.templates, with several fields filled in

# Each entry is in order material name, block tag, ingot tag, nugget tag
types = [
    ("iron", "forge:storage_blocks/iron", "forge:ingots/iron", "forge:nuggets/iron"),
    ("gold", "forge:storage_blocks/gold", "forge:ingots/gold", "forge:nuggets/gold"),
    (
        "copper",
        "forge:storage_blocks/copper",
        "forge:ingots/copper",
        "forge:nuggets/copper",
    ),
    ("tin", "forge:storage_blocks/tin", "forge:ingots/tin", "forge:nuggets/tin"),
    (
        "bronze",
        "forge:storage_blocks/bronze",
        "forge:ingots/bronze",
        "forge:nuggets/bronze",
    ),
    (
        "copshowium",
        "crossroads:storage_blocks/copshowium",
        "crossroads:ingots/copshowium",
        "crossroads:nuggets/copshowium",
    ),
]

srcDir = "templates/rotary/"
templates = os.listdir(srcDir)
# Name, including .json ending
templateNames = [os.path.basename(template) for template in templates]

outputPath = "../data/crossroads/recipes/mechanisms"

# Delete all previous files in the outputPath
for prevTable in os.listdir(outputPath):
    if os.path.isfile(prevTable):
        os.unlink(prevTable)


def writeTemplate(fileOut, fileTemplate, material: tuple):
    lines = fileTemplate.readlines()
    fileTemplate.seek(0)
    lines = [
        line.replace("MAT", material[0])
        .replace("BLOCK", material[1])
        .replace("INGOT", material[2])
        .replace("NUGGET", material[3])
        for line in lines
    ]
    fileOut.writelines(lines)


for template in templateNames:
    with open(srcDir + template, "r") as fTemp:
        for type in types:
            with open(
                outputPath + "/" + template.replace(".json", type[0] + ".json"), "w+"
            ) as fOut:
                writeTemplate(fOut, fTemp, type)
                fOut.close()
        fTemp.close()
