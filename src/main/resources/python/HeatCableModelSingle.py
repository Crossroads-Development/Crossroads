import os

# Generate all the heat cable blockstate files in the model folder

# sides = {"up": ("0", "0", "0"), "down": ("1", "180", "0"), "east": ("2", "90", "90"), "west": ("3", "90", "270"), "north": ("4", "90", "0"), "south": ("5", "90", "180")}
conductors = ["copper", "diamond", "iron", "quartz"]
insulators = ["ceramic", "obsidian", "wool", "slime", "dirt", "ice", "densus"]
# redstoneVals = [True, False]

stateOutPath = "../assets/crossroads/blockstates/"
modelOutPath = "../assets/crossroads/models/block/conduit/"

templatePath = "../python/templates/heat/"
models = ["heat_cable_core_", "heat_cable_top_"]
states = ["heat_cable_", "redstone_heat_cable_"]

# Write the model templates
for model in models:
    with open(templatePath + model + ".json", "r") as template:
        for conductor in conductors:
            for insulator in insulators:
                with open(
                    modelOutPath + model + insulator + "_" + conductor + ".json", "w+"
                ) as fileOut:
                    lines = template.readlines()
                    template.seek(0)
                    lines = [
                        line.replace(
                            "<TEXT>",
                            "crossroads:block/heatcable/" + insulator + "-" + conductor,
                        )
                        for line in lines
                    ]
                    fileOut.writelines(lines)
                with open(
                    modelOutPath + model + insulator + "_" + conductor + "_reds.json",
                    "w+",
                ) as fileOut:
                    lines = template.readlines()
                    template.seek(0)
                    lines = [
                        line.replace(
                            "<TEXT>",
                            "crossroads:block/heatcable/"
                            + insulator
                            + "-"
                            + conductor
                            + "-redstone",
                        )
                        for line in lines
                    ]
                    fileOut.writelines(lines)

# Write the blockstate templates
for state in states:
    with open(templatePath + state + ".json", "r") as template:
        suffix = "_reds" if state.startswith("redstone") else ""
        for insulator in insulators:
            with open(stateOutPath + state + insulator + ".json", "w+") as fileOut:
                lines = template.readlines()
                template.seek(0)
                lines = [
                    line.replace(
                        "<CORE-copper>",
                        "crossroads:block/conduit/heat_cable_core_"
                        + insulator
                        + "_copper"
                        + suffix,
                    )
                    .replace(
                        "<CORE-iron>",
                        "crossroads:block/conduit/heat_cable_core_"
                        + insulator
                        + "_iron"
                        + suffix,
                    )
                    .replace(
                        "<CORE-quartz>",
                        "crossroads:block/conduit/heat_cable_core_"
                        + insulator
                        + "_quartz"
                        + suffix,
                    )
                    .replace(
                        "<CORE-diamond>",
                        "crossroads:block/conduit/heat_cable_core_"
                        + insulator
                        + "_diamond"
                        + suffix,
                    )
                    .replace(
                        "<END-copper>",
                        "crossroads:block/conduit/heat_cable_top_"
                        + insulator
                        + "_copper"
                        + suffix,
                    )
                    .replace(
                        "<END-iron>",
                        "crossroads:block/conduit/heat_cable_top_"
                        + insulator
                        + "_iron"
                        + suffix,
                    )
                    .replace(
                        "<END-quartz>",
                        "crossroads:block/conduit/heat_cable_top_"
                        + insulator
                        + "_quartz"
                        + suffix,
                    )
                    .replace(
                        "<END-diamond>",
                        "crossroads:block/conduit/heat_cable_top_"
                        + insulator
                        + "_diamond"
                        + suffix,
                    )
                    for line in lines
                ]
                fileOut.writelines(lines)
