gamerule commandBlockOutput false
#replaceitem entity @a[nbt={SelectedItem:{id:"minecraft:sheep_spawn_egg"}}] weapon.mainhand diamond_sword{CustomModelData:1,display:{Name:"{\"text\":\"Obsidian Sword\",\"italic\":false}"}}
#execute as @e[name="Angriest Zombie"] at @s run tp @s ~ ~ ~ facing entity @p[limit=1]
#execute as @e[type=wolf,name="Magma Cube"] at @s run data modify entity @s Owner set from entity @p[limit=1] UUID

#Prism Ore
scoreboard objectives add REffect dummy
scoreboard players add @a[nbt={SelectedItem:{id:"minecraft:golden_apple",tag:{CustomModelData:1}}}] REffect 1
execute as @e[type=item,nbt={Item:{id:"minecraft:petrified_oak_slab"}}] at @s run data merge entity @s {Item:{id:"minecraft:golden_apple",Count:1b,tag:{Enchantments:[{}],CustomModelData:1,display:{Name:"{\"text\":\"Prism\",\"italic\":false}"}}}}
scoreboard objectives add PrismEat minecraft.used:minecraft.golden_apple

execute as @a[nbt={ActiveEffects:[{Id:27b}]}] at @s run fill ~3 ~3 ~3 ~-3 ~-3 ~-3 void_air replace air
execute as @a at @s run execute unless entity @s[nbt={ActiveEffects:[{Id:27b}]}] run fill ~3 ~3 ~3 ~-3 ~-3 ~-3 air replace void_air

effect clear @a[scores={PrismEat=1..,REffect=1..}]

effect give @a[scores={PrismEat=1..,REffect=1}] unluck 30 2
effect give @a[scores={PrismEat=1..,REffect=2}] speed 30 2
effect give @a[scores={PrismEat=1..,REffect=3}] jump_boost 30 2
effect give @a[scores={PrismEat=1..,REffect=4}] strength 30 2
effect give @a[scores={PrismEat=1..,REffect=5}] haste 30 2

scoreboard players set @a[scores={REffect=5..}] REffect 1

scoreboard players set @a[scores={PrismEat=1..}] PrismEat 0
execute as @a at @s run execute unless entity @s[nbt={SelectedItem:{id:"minecraft:golden_apple",tag:{CustomModelData:1}}}] run scoreboard players set @s REffect 0

#Teleportation System
execute as @e[scores={Sleep=10},nbt={Dimension:"minecraft:overworld"}] at @s run data modify storage sleep Data set from entity @s Pos

execute as @a[tag=Sleep] at @s run execute if entity @s[nbt={SleepTimer:0s}] run scoreboard players set @s Sleep 0
execute as @a[tag=Sleep] at @s run execute if entity @s[nbt={SleepTimer:0s}] run tag @s remove Sleep

tag @a[nbt={SleepTimer:1s},nbt={SelectedItem:{id:"minecraft:lilac"},Dimension:"minecraft:overworld"}] add Sleep
scoreboard objectives add Sleep dummy
scoreboard players add @a[tag=Sleep] Sleep 1

execute as @a[tag=Sleep,scores={Sleep=90}] at @s run execute in minecraft:dream_dimension run tp @s ~ ~ ~
execute as @a[tag=Sleep,scores={Sleep=90..}] at @s run time set day
execute as @a[tag=Sleep,scores={Sleep=90..}] at @s run execute if block ~ ~-1 ~ air run spreadplayers ~ ~ 1 100 false @s
execute as @a[tag=Sleep,scores={Sleep=90..}] at @s run execute if block ~ ~1 ~ air run spreadplayers ~ ~ 1 100 false @s

tag @a[tag=Sleep,scores={Sleep=100..}] remove Sleep

#Wake Up
execute as @a[tag=Wake] at @s run execute if entity @s[nbt={SleepTimer:0s}] run scoreboard players set @s Sleep 0
execute as @a[tag=Wake] at @s run execute if entity @s[nbt={SleepTimer:0s}] run tag @s remove Wake


tag @a[nbt={SleepTimer:1s},nbt={Dimension:"minecraft:dream_dimension"}] add Wake
scoreboard players add @a[tag=Wake] Sleep 1

execute as @a[tag=Wake,scores={Sleep=90}] at @s run execute in minecraft:overworld run tp @s ~ ~ ~
execute as @a[tag=Wake,scores={Sleep=90..}] at @s run time set day
effect give @a[scores={Sleep=90..},tag=Wake] resistance 1 10 true
execute as @a[tag=Wake,scores={Sleep=90..}] at @s run execute unless entity @e[distance=0..,name=Rsp] run summon armor_stand ~ ~ ~ {Tags:["Rsp"],CustomName:"\"Rsp\"",Small:1b,Invisible:1b}


execute as @e[name=Rsp] at @s run data modify entity @s Pos set from storage minecraft:sleep Data
execute as @e[name=Rsp] at @s run tp @p ~ ~ ~
execute as @e[name=Rsp] at @s run execute if entity @p[distance=0..1] run data remove storage minecraft:sleep Data
execute as @e[name=Rsp] at @s run execute if entity @p[distance=0..1] run kill @s


tag @a[tag=Wake,scores={Sleep=100..}] remove Wake
scoreboard players set @a[scores={Sleep=1..},tag=!Wake,tag=!Sleep] Sleep 0

#####



#Echo Saving Feature
execute as @a[nbt={Dimension:"minecraft:overworld"},scores={Sleep=1}] at @s run setblock ~ ~-3 ~ structure_block{name:"minecraft:echoes",sizeX:20,sizeY:20,sizeZ:20,posX:-10,posY:1,posZ:-10,mode:"SAVE",showboundingbox:0b}
execute as @a[nbt={Dimension:"minecraft:overworld"},scores={Sleep=5}] at @s run setblock ~ ~-4 ~ stone_button[face=ceiling,powered=true]
execute as @a[nbt={Dimension:"minecraft:overworld"},scores={Sleep=6}] at @s run setblock ~ ~-3 ~ air
execute as @a[nbt={Dimension:"minecraft:overworld"},scores={Sleep=10}] at @s run setblock ~ ~-4 ~ air





#Custom Blocks
#/give @s item_frame{CustomModelData:1,display:{Name:"{\"text\":\"Dream Wood\",\"italic\":false}"},EntityTag:{CustomName:"\"B1\"",Fixed:1b,Item:{id:"minecraft:item_frame",Count:1b,tag:{CustomModelData:1}},Invisible:1b,Invulnerable:1b}}
#/give @s item_frame{CustomModelData:2,display:{Name:"{\"text\":\"Dream Wood\",\"italic\":false}"},EntityTag:{CustomName:"\"B2\"",Fixed:1b,Item:{id:"minecraft:item_frame",Count:1b,tag:{CustomModelData:2}},Invisible:1b,Invulnerable:1b}}
#/give @s item_frame{CustomModelData:3,display:{Name:"{\"text\":\"Dream Wood\",\"italic\":false}"},EntityTag:{CustomName:"\"B3\"",Fixed:1b,Item:{id:"minecraft:item_frame",Count:1b,tag:{CustomModelData:3}},Invisible:1b,Invulnerable:1b}}

execute as @e[type=item_frame] at @s run execute if block ~ ~ ~ dirt run setblock ~ ~ ~ farmland[moisture=7]
#Block 1

execute as @e[name=B1,tag=!Fin] at @s run execute unless entity @s[nbt={Facing:1b}] run summon item_frame ~ ~ ~ {CustomName:"\"B1\"",Facing:1b,Fixed:1b,Item:{id:"minecraft:item_frame",Count:1b,tag:{CustomModelData:1}},Invisible:1b,Invulnerable:1b}
execute as @e[name=B1,tag=!Fin] at @s run execute unless entity @s[nbt={Facing:1b}] run kill @s
execute as @e[name=B1,tag=!Fin] at @s run data merge entity @s {Facing:1b}
execute as @e[name=B1,tag=!Fin] at @s run setblock ~ ~ ~ farmland[moisture=7]
tag @e[name=B1] add Fin

execute as @e[tag=Fin,name=B1] at @s run execute if block ~ ~ ~ air run particle block purple_wool ~ ~1 ~ 0.1 0.1 0.1 1 5
execute as @e[tag=Fin,name=B1] at @s run execute if block ~ ~ ~ air run summon item ~ ~ ~ {Motion:[0.05d,0.3d,0.0d],Item:{id:"minecraft:item_frame",Count:1b,tag:{CustomModelData:1,display:{Name:"{\"text\":\"Dream Wood\",\"italic\":false}"},EntityTag:{CustomName:"\"B1\"",Fixed:1b,Item:{id:"minecraft:item_frame",Count:1b,tag:{CustomModelData:1}},Invisible:1b,Invulnerable:1b}}}}

execute as @e[tag=Fin,name=B1] at @s run execute if block ~ ~ ~ air run kill @s


#Block 2

execute as @e[name=B2,tag=!Fin] at @s run execute unless entity @s[nbt={Facing:1b}] run summon item_frame ~ ~ ~ {CustomName:"\"B2\"",Facing:1b,Fixed:1b,Item:{id:"minecraft:item_frame",Count:1b,tag:{CustomModelData:2}},Invisible:1b,Invulnerable:1b}
execute as @e[name=B2,tag=!Fin] at @s run execute unless entity @s[nbt={Facing:1b}] run kill @s
execute as @e[name=B2,tag=!Fin] at @s run data merge entity @s {Facing:1b}
execute as @e[name=B2,tag=!Fin] at @s run setblock ~ ~ ~ farmland[moisture=7]
tag @e[name=B2] add Fin

execute as @e[tag=Fin,name=B2] at @s run execute if block ~ ~ ~ air run particle block cyan_wool ~ ~1 ~ 0.1 0.1 0.1 1 5
execute as @e[tag=Fin,name=B2] at @s run execute if block ~ ~ ~ air run summon item ~ ~ ~ {Motion:[0.05d,0.3d,0.0d],Item:{id:"minecraft:item_frame",Count:1b,tag:{CustomModelData:2,display:{Name:"{\"text\":\"Dream Wood\",\"italic\":false}"},EntityTag:{CustomName:"\"B2\"",Fixed:1b,Item:{id:"minecraft:item_frame",Count:1b,tag:{CustomModelData:2}},Invisible:1b,Invulnerable:1b}}}}

execute as @e[tag=Fin,name=B2] at @s run execute if block ~ ~ ~ air run kill @s

#Block 3

execute as @e[name=B3,tag=!Fin] at @s run execute unless entity @s[nbt={Facing:1b}] run summon item_frame ~ ~ ~ {CustomName:"\"B3\"",Facing:1b,Fixed:1b,Item:{id:"minecraft:item_frame",Count:1b,tag:{CustomModelData:3}},Invisible:1b,Invulnerable:1b}
execute as @e[name=B3,tag=!Fin] at @s run execute unless entity @s[nbt={Facing:1b}] run kill @s
execute as @e[name=B3,tag=!Fin] at @s run data merge entity @s {Facing:1b}
execute as @e[name=B3,tag=!Fin] at @s run setblock ~ ~ ~ farmland[moisture=7]
tag @e[name=B3] add Fin

execute as @e[tag=Fin,name=B3] at @s run execute if block ~ ~ ~ air run particle block red_wool ~ ~1 ~ 0.1 0.1 0.1 1 5
execute as @e[tag=Fin,name=B3] at @s run execute if block ~ ~ ~ air run summon item ~ ~ ~ {Motion:[0.05d,0.3d,0.0d],Item:{id:"minecraft:item_frame",Count:1b,tag:{CustomModelData:3,display:{Name:"{\"text\":\"Dream Wood\",\"italic\":false}"},EntityTag:{CustomName:"\"B3\"",Fixed:1b,Item:{id:"minecraft:item_frame",Count:1b,tag:{CustomModelData:3}},Invisible:1b,Invulnerable:1b}}}}

execute as @e[tag=Fin,name=B3] at @s run execute if block ~ ~ ~ air run kill @s





#Nightmares and Sheep
effect give @e[name=Nightmare] invisibility 2 2 true
execute as @e[name=Nightmare] at @s run particle smoke ~ ~ ~ 0.5 0.5 0.5 0 20

effect give @e[name="Nightmare Archer"] invisibility 2 2 true
execute as @e[name="Nightmare Archer"] at @s run particle smoke ~ ~ ~ 0.5 0.5 0.5 0 20

kill @e[name=Summon_Nightmare]

execute as @a[nbt={Dimension:"minecraft:dream_dimension"}] at @s run execute in minecraft:dream_dimension run tag @e[distance=0..,type=cow,tag=!FlHusk] add Husk
execute as @e[type=cow,tag=Husk,tag=!FlHusk] at @s run data merge entity @s {DeathLootTable:"empty"}
execute as @e[type=cow,tag=Husk,tag=!FlHusk] at @s run summon bee ~ ~ ~ {CustomName:"\"Nightmare\"",Passengers:[{id:"minecraft:husk",CustomName:"\"Nightmare\"",Tags:["FlHusk"]}]}
execute as @e[type=cow,tag=Husk,tag=!FlHusk] at @s run tp @s 0 0 0
execute as @e[type=cow,tag=Husk,tag=!FlHusk] at @s run kill @s

scoreboard objectives add Ded minecraft.custom:minecraft.deaths

#Creepers 
scoreboard objectives add Creeper minecraft.killed_by:minecraft.creeper
execute as @a[scores={Creeper=5..}] at @s run execute if entity @e[distance=0..,name=Nightmare] run tag @e[name=Nightmare,tag=!Creeper,tag=!Arrows,tag=!Fall,tag=!Drown,tag=!Fire,tag=!Lava] add Creeper
execute as @a[scores={Creeper=5..}] at @s run execute if entity @e[distance=0..,name=Nightmare] run scoreboard players set @s Creeper 0
#Arrows
scoreboard objectives add A1 minecraft.killed_by:minecraft.skeleton
scoreboard objectives add A2 minecraft.killed_by:minecraft.pillager
scoreboard objectives add A3 dummy
scoreboard players add @a[scores={A1=1..}] A3 1
scoreboard players add @a[scores={A2=1..}] A3 1
execute as @a[scores={A3=5..}] at @s run execute if entity @e[distance=0..,name=Nightmare] run tag @e[name=Nightmare,tag=!Creeper,tag=!Arrows,tag=!Fall,tag=!Drown,tag=!Fire,tag=!Lava] add Arrows
execute as @a[scores={A3=5..}] at @s run execute if entity @e[distance=0..,name=Nightmare] run scoreboard players set @s A3 0
scoreboard players set @a[scores={A1=1..}] A1 0
scoreboard players set @a[scores={A2=1..}] A2 0
#Falls
scoreboard objectives add Fall1 minecraft.custom:minecraft.fall_one_cm
scoreboard objectives add Fall2 dummy
scoreboard players add @a[scores={Ded=1..,Fall1=1..}] Fall2 1
execute as @a[scores={Fall2=5..}] at @s run execute if entity @e[distance=0..,name=Nightmare] run tag @e[name=Nightmare,tag=!Creeper,tag=!Arrows,tag=!Fall,tag=!Drown,tag=!Fire,tag=!Lava] add Fall
execute as @a[scores={Fall2=5..}] at @s run execute if entity @e[distance=0..,name=Nightmare] run scoreboard players set @s Fall2 0


#Drowning
scoreboard objectives add Drown dummy
tag @a[nbt={Air:0s}] add Drowning
tag @a[nbt={Air:300s}] remove Drowning
execute as @a[tag=Drowning,scores={Ded=1..}] at @s run execute if block ~ ~ ~ water run scoreboard players add @s Drown 1
execute as @a[scores={Drown=5..}] at @s run execute if entity @e[distance=0..,name=Nightmare] run tag @e[name=Nightmare,tag=!Creeper,tag=!Arrows,tag=!Fall,tag=!Drown,tag=!Fire,tag=!Lava] add Drown
execute as @a[scores={Drown=5..}] at @s run execute if entity @e[distance=0..,name=Nightmare] run scoreboard players set @s Drown 0

#Fire
scoreboard objectives add Fire dummy
execute as @a at @s run execute unless entity @s[nbt={Fire:-20s}] run tag @s add Fire
execute as @a[tag=Fire,scores={Ded=1..}] at @s run execute unless block ~ ~ ~ lava run scoreboard players add @s Fire 1
execute as @a[scores={Fire=5..}] at @s run execute if entity @e[distance=0..,name=Nightmare] run tag @e[name=Nightmare,tag=!Creeper,tag=!Arrows,tag=!Fall,tag=!Drown,tag=!Fire,tag=!Lava] add Drown
execute as @a[scores={Fire=5..}] at @s run execute if entity @e[distance=0..,name=Nightmare] run scoreboard players set @s Fire 0
#Lava
scoreboard objectives add Lava dummy
execute as @a[tag=Fire,scores={Ded=1..}] at @s run execute if block ~ ~ ~ lava run scoreboard players add @s Lava 1
execute as @a[scores={Lava=5..}] at @s run execute if entity @e[distance=0..,name=Nightmare] run tag @e[name=Nightmare,tag=!Creeper,tag=!Arrows,tag=!Fall,tag=!Drown,tag=!Fire,tag=!Lava] add Drown
execute as @a[scores={Lava=5..}] at @s run execute if entity @e[distance=0..,name=Nightmare] run scoreboard players set @s Lava 0

execute as @a at @s run execute if entity @s[nbt={Fire:-20s}] run tag @s remove Fire
scoreboard players set @a[scores={Ded=1..}] Ded 0



#Creeper
execute as @e[tag=Creeper,name=Nightmare] at @s run execute if entity @p[distance=0..2] run summon tnt
#Arrows
execute as @e[tag=Arrows,name=Nightmare] at @s run execute if entity @p[distance=0..10] run execute unless entity @e[distance=0..,name="Nightmare Archer",tag=1] run summon pillager ~ ~ ~ {HandItems:[{id:"minecraft:crossbow",Count:1b},{}],CustomName:"\"Nightmare Archer\"",Tags:["1"]}
execute as @e[tag=Arrows,name=Nightmare] at @s run execute if entity @p[distance=0..10] run execute unless entity @e[distance=0..,name="Nightmare Archer",tag=2] run summon pillager ~ ~ ~ {HandItems:[{id:"minecraft:crossbow",Count:1b},{}],CustomName:"\"Nightmare Archer\"",Tags:["2"]}
execute as @e[tag=Arrows,name=Nightmare] at @s run execute if entity @p[distance=0..10] run execute unless entity @e[distance=0..,name="Nightmare Archer",tag=3] run summon pillager ~ ~ ~ {HandItems:[{id:"minecraft:crossbow",Count:1b},{}],CustomName:"\"Nightmare Archer\"",Tags:["3"]}
#Drowning
execute as @e[tag=Drown,name=Nightmare] at @s run effect give @s water_breathing 10 10 true
execute as @e[tag=Drown,name=Nightmare] at @s run execute as @p[distance=0..2] at @s run fill ~1 ~2 ~1 ~-1 ~-1 ~-1 water replace air
execute as @e[tag=Drown,name=Nightmare] at @s run execute as @p[distance=0..15] at @s run execute if block ~ ~1 ~ water run execute if block ~ ~2 ~ air run tp @s ~ ~-0.2 ~
#Falling
execute as @e[tag=Fall,name=Nightmare] at @s run effect give @p[distance=0..2,nbt={ActiveEffects:[{Id:17b}]}] levitation 10 5
execute as @e[tag=Fall,name=Nightmare] at @s run effect clear @p[distance=0..2,nbt={ActiveEffects:[{Id:17b}]}] hunger
#Fire
execute as @e[tag=Fire,name=Nightmare] at @s run effect give @s fire_resistance 10 10 true
execute as @e[tag=Fire,name=Nightmare] at @s run setblock ~ ~ ~ fire
#Lava
execute as @e[tag=Lava,name=Nightmare] at @s run effect give @s fire_resistance 10 10 true
execute as @e[tag=Lava,name=Nightmare] at @s run setblock ~ ~ ~ lava

#For Sheep
execute as @e[tag=FlSheep] at @s run execute store result entity @s Rotation[0] float 1 run data get entity @e[sort=nearest,limit=1,distance=0..1.5,type=bee,name=Sheep] Rotation[0]


execute as @e[name=Wings,type=armor_stand] at @s run execute store result entity @s Rotation[0] float 1 run data get entity @e[sort=nearest,limit=1,distance=0..1.5,tag=FlSheep] Rotation[0]
execute as @e[name=Wings,type=armor_stand] at @s run execute store result entity @s Rotation[1] float 1 run data get entity @e[sort=nearest,limit=1,distance=0..1.5,tag=FlSheep] Rotation[1]


execute as @a[nbt={Dimension:"minecraft:dream_dimension"}] at @s run execute in minecraft:dream_dimension run tag @e[distance=0..,type=sheep,tag=!FlSheep] add Sheep
execute as @e[type=sheep,tag=Sheep,tag=!FlSheep] at @s run data merge entity @s {DeathLootTable:"empty"}
execute as @e[type=sheep,tag=Sheep,tag=!FlSheep] at @s run execute unless entity @s[nbt={Age:0}] run summon bee ~ ~ ~ {Silent:1b,CustomName:"\"Sheep\"",Passengers:[{id:"minecraft:sheep",Age:-25000,Passengers:[{id:"minecraft:armor_stand",Small:1b,CustomName:"\"Wings\"",Invisible:1b,ArmorItems:[{},{},{},{id:"minecraft:spawner",Count:1b}]}],Tags:["FlSheep","Baby"]}]}
execute as @e[type=sheep,tag=Sheep,tag=!FlSheep] at @s run execute if entity @s[nbt={Age:0}] run summon bee ~ ~ ~ {Silent:1b,CustomName:"\"Sheep\"",Passengers:[{id:"minecraft:sheep",Passengers:[{id:"minecraft:armor_stand",CustomName:"\"Wings\"",Invisible:1b,ArmorItems:[{},{},{},{id:"minecraft:petrified_oak_slab",Count:1b}]}],Tags:["FlSheep"]}]}
execute as @e[type=sheep,tag=Sheep,tag=!FlSheep] at @s run tp @s 0 0 0
execute as @e[type=sheep,tag=Sheep,tag=!FlSheep] at @s run kill @s
effect give @e[type=bee,name=Sheep] invisibility 1 1 true

execute as @e[tag=Baby,nbt={Age:0}] at @s run execute as @e[name=Wings,distance=0..2,tag=!Aged] at @s run data merge entity @s {Tags:["Aged"],Small:0b,ArmorItems:[{},{},{},{id:"minecraft:petrified_oak_slab",Count:1b}]}

execute as @e[name=Wings] at @s run execute unless entity @e[distance=0..2,name=Sheep,type=bee] run kill @s
execute as @e[name=Sheep,type=bee] at @s run execute unless entity @e[distance=0..1,tag=FlSheep] run kill @s