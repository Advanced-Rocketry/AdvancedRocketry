import os

class Material:
    
    def __init__(self, name, color, outputs):
        self.name = name
        self.color = color
        self.outputs = outputs

materials = [Material("dilithium", 0xddcecb, ("DUST", "GEM")),
Material("iron", 0xafafaf, ("SHEET", "STICK", "DUST", "PLATE")),
Material("gold", 0xffff5d, ("DUST", "COIL", "PLATE")),
Material("silicon", 0x2c2c2b, ("INGOT", "DUST", "BOULE", "NUGGET", "PLATE")),
Material("copper", 0xd55e28, ("ORE", "COIL", "BLOCK", "STICK", "INGOT", "NUGGET", "DUST", "PLATE", "SHEET")),
Material("tin", 0xcdd5d8, ("ORE", "BLOCK", "PLATE", "INGOT", "NUGGET", "DUST")),
Material("steel", 0x55555d, ("BLOCK", "FAN", "PLATE", "INGOT", "NUGGET", "DUST", "STICK", "GEAR", "SHEET")),
Material("titanium", 0xb2669e, ("PLATE", "COIL", "INGOT", "NUGGET", "DUST", "STICK", "BLOCK", "GEAR", "SHEET")),
Material("rutile", 0xbf936a, ("ORE",)),
Material("aluminum", 0xb3e4dc, ("ORE", "COIL", "BLOCK", "INGOT", "PLATE", "SHEET", "DUST", "NUGGET", "SHEET")),
Material("iridium", 0xdedcce, ("ORE", "COIL", "BLOCK", "DUST", "INGOT", "NUGGET", "PLATE", "STICK")),
Material("titaniumaluminide", 0xaec2de, ("GEAR", "COIL", "BLOCK", "INGOT", "PLATE", "SHEET", "DUST", "NUGGET", "SHEET")),
Material("titaniumiridium", 0xd7dfe4, ("GEAR", "COIL", "BLOCK", "INGOT", "PLATE", "SHEET", "DUST", "NUGGET", "SHEET"))]


def getNamespace(material):
    if material == "titaniumaluminide" or material == "titaniumiridium":
        return "advancedrocketry"
    else:
        return "libvulpes"


vanilla = ["iron", "gold"]

itemIngredientTag = '\n        {\n            "tag": "<TAG>"\n        }'

jsonTemplateMachine = '{\n"type": "advancedrocketry:<MACHINE>",\n    "itemingredients":\n    [\n        <INPUTI>\n    ],\n    "time": <TIME>,\n    "energy": <ENERGY>,\n    "itemresults":\n    {\n            "tag":\n"<OUTPUTITEM>",\n            "count": <OUTPUTCOUNT>\n    }\n}'

jsonTemplateMachineWater = '{\n    "type": "advancedrocketry:<MACHINE>",\n    "itemingredients":\n    [\n        <INPUTI>\n    ],\n    "fluidingredients":\n    [\n        {\n            "fluid": "minecraft:water",\n            "amount": 10\n        }\n    ],\n    "time": <TIME>,\n    "energy": <ENERGY>,\n    "itemresults":\n    {\n            "tag": "<OUTPUTITEM>",\n            "count": <OUTPUTCOUNT>\n    }\n}'

jsonfurnaceTemplate = '{\n  "type": "minecraft:smelting",\n  "ingredient": {\n    "item": "<INPUT>"\n  },\n  "result": "<OUTPUT>",\n  "experience": 0.0,\n  "cookingtime": 200\n}'

jsonshapedcraftblock = '{\n  "type": "minecraft:crafting_shaped",\n  "pattern": [\n    "###",\n    "###",\n    "###"\n  ],\n  "key": {\n    "#": {\n      "tag": "<INPUT>"\n    }\n  },\n  "result": {\n    "item": "<OUTPUT>"\n  }\n}'

jsonshapedcraftcoil = '{\n  "type": "minecraft:crafting_shaped",\n  "pattern": [\n    "###",\n    "# #",\n    "###"\n  ],\n  "key": {\n    "#": {\n      "tag": "<INPUT>"\n    }\n  },\n  "result": {\n    "item": "<OUTPUT>"\n  }\n}'

jsonshapedcraftstick = '{\n  "type": "minecraft:crafting_shaped",\n  "pattern": [\n    "  #",\n    " # ",\n    "#  "\n  ],\n  "key": {\n    "#": {\n      "tag": "<INPUT>"\n    }\n  },\n  "result": {\n    "item": "<OUTPUT>"\n  }\n}'

jsonshapedcraftfan = '{\n  "type": "minecraft:crafting_shaped",\n  "pattern": [\n    "p p",\n    " r ",\n    "p p"\n  ],\n  "key": {\n    "r": {\n      "tag": "<INPUT>"\n    },\n    "p": {\n      "tag": "<INPUT2>"\n    }\n  },\n  "result": {\n    "item": "<OUTPUT>"\n  }\n}'

jsonshapedcraftgear = '{\n  "type": "minecraft:crafting_shaped",\n  "pattern": [\n    "sps",\n    " r ",\n    "sps"\n  ],\n  "key": {\n    "s": {\n      "tag": "<INPUT>"\n    },\n    "p": {\n      "tag": "<INPUT2>"\n    },\n    "r": {\n      "tag": "<INPUT3>"\n    }\n  },\n  "result": {\n    "item": "<OUTPUT>"\n  }\n}'

jsonnugget = '{\n  "type": "minecraft:crafting_shapeless",\n  "ingredients": [\n    {\n      "tag": "<INPUT>"\n    }\n  ],\n  "result": {\n    "item": "<OUTPUT>",\n    "count": <COUNT>\n  }\n}'

def writeOutJsonFile(json, filename):
    dirs = "src/main/resources/data/advancedrocketry/recipes/autogen/"
    f = open(dirs + filename + ".json", 'w')
    f.write(json)

def createRecipeSimple(name, machine, time, energy, inputs, output, outputCount=1):
    # make inputs
    inputList = []
    for inputItem in inputs:
        inputList.append(itemIngredientTag.replace("<TAG>", inputItem))
    
    json = jsonTemplateMachine.replace("<MACHINE>", machine) \
        .replace("<TIME>", str(time)) \
        .replace("<ENERGY>", str(energy)) \
        .replace("<OUTPUTITEM>", output) \
        .replace("<OUTPUTCOUNT>", str(outputCount)) \
        .replace("<INPUTI>", ",".join(inputList))
    writeOutJsonFile(json, name)
    
def createRecipeWater(name, machine, time, energy, inputs, output, outputCount=1):
    
    # make inputs
    inputList = []
    for inputItem in inputs:
        inputList.append(itemIngredientTag.replace("<TAG>", inputItem))
    
    json = jsonTemplateMachineWater.replace("<MACHINE>", machine) \
        .replace("<TIME>", str(time)) \
        .replace("<ENERGY>", str(energy)) \
        .replace("<OUTPUTITEM>", output) \
        .replace("<OUTPUTCOUNT>", str(outputCount)) \
        .replace("<INPUTI>", ",".join(inputList))
    writeOutJsonFile(json, name)

def smeltingRecipe(name, inputs, outputs):
    json = jsonfurnaceTemplate.replace("<INPUT>", inputs).replace("<OUTPUT>", outputs)
    writeOutJsonFile(json, name)
    
def generalCrafting(name, template, output, item1, item2="", item3="", count=1):
    json = template.replace("<INPUT>", item1).replace("<INPUT2>", item2).replace("<INPUT3>", item3).replace("<OUTPUT>", output).replace("<COUNT>", str(count))
    writeOutJsonFile(json, name)


for mat in materials:
    
    namespace_name = getNamespace(mat.name)
    
    if "BOULE" in mat.outputs:
        createRecipeSimple("crystalize_" + mat.name, "crystallizer", 300, 20, ("forge:ingots/"+ mat.name, "forge:nuggets/" + mat.name), "forge:boules/" + mat.name)
    if "GEM" in mat.outputs:
        createRecipeSimple("crystalize_gem_" + mat.name, "crystallizer", 300, 20, ("forge:dusts/"+ mat.name,), "forge:gems/" + mat.name)
    if "STICK" in mat.outputs:
        createRecipeSimple("lathe_" + mat.name, "lathe", 300, 20, ("forge:ingots/"+ mat.name,), "forge:sticks/" + mat.name, 2)
        generalCrafting("stick_hand_" + mat.name, jsonshapedcraftstick, namespace_name + ":stick"+mat.name, "forge:ingots/" + mat.name)
    if "PLATE" in mat.outputs:
        createRecipeWater("rolling_" + mat.name, "rollingmachine", 300, 20, ("forge:ingots/"+ mat.name,), "forge:plates/" + mat.name)
        if "BLOCK" in mat.outputs or mat.name in vanilla:
            createRecipeWater("rolling_block_" + mat.name, "rollingmachine", 300, 20, ("forge:blocks/"+ mat.name,), "forge:plates/" + mat.name, 9)
            createRecipeSimple("crusher_block_plate" + mat.name, "smallplate", 300, 20, ("forge:blocks/"+ mat.name,), "forge:plates/" + mat.name, 4)
    if "SHEET" in mat.outputs:
        createRecipeWater("rolling_sheet_" + mat.name, "rollingmachine", 300, 20, ("forge:plates/"+ mat.name,), "forge:sheets/" + mat.name, 2)
    if "DUST" in mat.outputs:
        createRecipeSimple("crusher_dust_" + mat.name, "smallplate", 300, 20, ("forge:ores/"+ mat.name,), "forge:dusts/" + mat.name, 2)
        if "INGOT" in mat.outputs:
            if mat.name in vanilla:
                smeltingRecipe("smelt_dust_" + mat.name, namespace_name + ":dust" + mat.name, "minecraft:" + mat.name + "_ingot")
            else:
                smeltingRecipe("smelt_dust_" + mat.name, namespace_name + ":dust" + mat.name, namespace_name + ":ingot" + mat.name)
    if "NUGGET" in mat.outputs and mat.name not in vanilla:
            generalCrafting("nugget_" + mat.name, jsonshapedcraftblock, namespace_name + ":ingot" + mat.name, "forge:nuggets/" + mat.name)
            generalCrafting("unnugget_" + mat.name, jsonnugget, namespace_name + ":nugget" + mat.name, "forge:ingots/" + mat.name, "","", 9)
    if "GEAR" in mat.outputs:
        generalCrafting("gear_" + mat.name, jsonshapedcraftgear, namespace_name + ":gear" + mat.name, "forge:sticks/" + mat.name, "forge:plates/" + mat.name, "forge:ingots/" + mat.name)
    if "FAN" in mat.outputs:
        generalCrafting("fan_" + mat.name, jsonshapedcraftfan, namespace_name + ":fan" + mat.name, "forge:sticks/" + mat.name, "forge:plates/" + mat.name)
    if "COIL" in mat.outputs:
        generalCrafting("coil_" + mat.name, jsonshapedcraftcoil, namespace_name + ":coil" + mat.name, "forge:ingots/" + mat.name)
    if "BLOCK" in mat.outputs:
        if mat.name not in vanilla:
            generalCrafting("block_" + mat.name, jsonshapedcraftblock, namespace_name + ":block" + mat.name, "forge:ingots/" + mat.name)
            generalCrafting("unblock" + mat.name, jsonnugget, namespace_name + ":ingot" + mat.name, "forge:blocks/" + mat.name, "","", 9)
    if "ORE" in mat.outputs and "INGOT" in mat.outputs:
        smeltingRecipe("smelt_ore_" + mat.name, namespace_name + ":ore" + mat.name, namespace_name + ":ingot" + mat.name)
