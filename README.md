# AdvancedRocketry

MileStones:


Roadmap:

Milestone 1: (done)
- rockets can be dissassembled and reassembled (done)
	- rockets size determined by size of lauchpad (square) and height of structure tower (used for fueling/etc) (done)
- bounds for rockets can be set (done)
- player can sit on rocket (done)

Milestone 2: (done)
- rocket entity that renders all blocks and moves vertically
- rocket parts
	- engines (determines max blocks for rocket must be placed on bottom)
		- fuel (liquid, impulse, warp,  ion, nuclear)
	- fuel tank (determines farthest distance the rocket can fly, can only hold certain type of fuel)
	- guidance systems (decreases chance of needing to return home (manned) or being destroyed (unmanned))
- The moon

Milestone 3: (done)
- Internals of planetary relationships (Eg: orbits) Req for satillites (done)
- Planet characteristics (done)
- Dimension storage (done)

Milestone 4:
- unmanned rockets / satallites (done)
- satallite management system (done)
	- control block (done)
- Implementatation of satallites:
	- Ground to space missile ( circuit level compared to that of target; chance of destruction (10%)*(missle level / target level) )
	- Ore scanning (complete)
	- Mining satallites (complete) (upgrades: solar, provides more power; effiency, less power per block; power, faster mining)
		-higher level lense decreses chance of target block loss
		- mirrors can be used to deflect
	- "rods from God" (limited ammo)
	- mapping/spy
	- weather control (long cooldown) upgrades: efficiency (lowers cooldown time)
	- power beam (requires receptor on ground, works only during day, fries anything that gets in the way, can be weaponized with pvp mode)
		- (each level of solar panel increases effectiveness by 10% with base of 50RF/t)
		
	- circuit level determines resistivity to environmental(can be toggled off) and artifical (ground to space missles/lasers) hazards

Milestone 5:
- Pregenned solar system (some number of planets with certain values set (editable in config)
- space stations (WIP)
- orbital factories (some materials can only be produced in space and/or process faster and/or dupe more(ores)
- space elevator (eliminates need for rocket launch to get materials to orbital factories/ space stations/shipyards
- Interstellar shipyards (used to build Interstellar ships)

Milestone 6:
- Crafting recipies (done)
- clean rooms required for certain processes
- blast furnace for silicon (done)
- Chip creation machine list (basic teir is done)
- Machine API?

MileStone 7:
- "Search for habitable planets program"
	- Orbital satallite telescopes/detectors (done)
	- ground based telescopes
		- water detectors (increases info on %dry) space only
		- chemical detectors (increases chance of finding planets rich in certain ores) space only
		- atmospheric detectors (increases info on pressure; when combined with chemical can detect if planet has harvestable/hazardous gasses) ground and space
		- temperature detector (increases info on planetary temperature) space only
		- star scanner (increases the likelyhood of finding planets orbiting a star; higher level allows for smaller planets) ground and space
	- procedural generation of limited planet types (done)

Milestone 8:
- research tree

Milestone 9:
- Terraforming?

Random Ideas:
- Terraforming
	- after a certain amount of resources spent planet becomes "Habitable" Eg no suit required
	- Plants can grow
- machines dont render model if fast graphics options are set
- blocks used to build machine determines Teir thrust
- Rovers
- interactive spaceflight
- docking
- life support (determines farthest distance the rocket can fly) manned only
- Config file to use non-AdvancedRocketry dimensions as planets
	-(Required: host star/planet id, Optional: atmosphere color, gravity multiplier, atmosphere resource, humidity, atmosphere density, day/night length)
- Configs:
	Machine power usage
	Planets
- Support for other power systems