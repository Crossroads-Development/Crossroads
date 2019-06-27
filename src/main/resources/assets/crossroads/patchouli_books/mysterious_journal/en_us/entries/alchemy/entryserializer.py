import json
import os

for filename in os.listdir("."):
    if filename.endswith(".json"):
#filename = "laboratory_glassware.json"
        print(filename)
        with open(filename, 'r+') as f:
            data = json.load(f)
            data["advancement"] = "crossroads:crossroads/alchemy"
        os.remove(filename)
        with open(filename, 'w') as f:
            json.dump(data, f, indent=4)
