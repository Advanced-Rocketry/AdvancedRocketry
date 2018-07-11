# AdvancedRocketry
A mod about space, exploration, and resources

# How to build for Minecraft 1.7.10:

Before you start: It is assumed you know how to clone and checkout different branches

1. Create a new Directory named advancedRocketryProject
2. Enter the directory 'advancedRocketryProject'
3. clone "https://github.com/zmaster587/libVulpes.git" (git clone https://github.com/zmaster587/libVulpes.git)
4. clone "https://github.com/zmaster587/AdvancedRocketry.git" (git clone https://github.com/zmaster587/AdvancedRocketry.git)
5. If step 3 and 4 were performed correctly you should now have the folders "libVulpes" and "AdvancedRocketry" inside the folder "advancedRocketryProject".  The libVulpes folder being properly named is important!
6. Download https://ci.micdoodle8.com/job/Galacticraft-1.7/474/artifact/Forge/build/libs/GalacticraftCore-1.7-3.0.12.474.jar into the AdvancedRocketry/libs folder (if the AdvancedRocketry/libs folder doesn't exist, create it)
7. Repeat step 6 but put the file in libVulpes/libs (you may also need to create that)
8. In the AdvancedRocketry folder, shift-rightclick and select open command window from the context menu, 
9. From the command window run ./gradlew.bat build


# How to build for Minecraft 1.10.2:
The steps are the same as building for 1.7.10 except you do not need Galacticcraft and after step 4 you must make sure to checkout the MC1_10 branch of BOTH libVulpes and Advanced rocketry in their respective folders


## Current Features:
- Rockets can be built from almost any block
- Space stations orbiting any planet/moon
- Warpships to take players between said planets
- planet selection guidance
- Basic machinery (to be redone at future date)
- Generates a fixed number of planets in a single solar system
- fuel mechanics
- Gas system (O2)
- Satellites
- Data collection of planets (not currently fully featured)
- Support for IC2 and RF
- planets can be generated from XML
- Gravity generators for space stations
- stations with day/night cycles
- more Satellites
    - mining Satellite (ish, is spacestation bound)
    - energy collection satellite
    - ore scanning satallite
- collection of materials from asteroids
- asteroids
    - automated harvesting
    - research system
        - random asteroid parameters ( size, composition, location (polar coords)
        - research can be done to determine properties
- Terraforming
- collection of materials from gas giants
- planets actually moving in their orbits
- Railguns to transfer goods between planets and stations
- config for mapping dims added by other mods to planets
- Gravity generators for local areas on planets

## Future Features:
- Other engine types for space ships(ion/plasma)
- Orbital factories
- rovers (maybe? need a use)
- docking (maybe..)
- planetary mapping system (feasible?)
- more Satellites
    - Weather control satellite
    - mapping satellite (feasible?)
- clean rooms
- research tree (possible use for planetary data, hire villagers as scientists for you lab!)
- supports for more power systems
- remove RF dependance
- stations can be positioned over certain locations on planets
- colony management system (seed testificates throughout the universe?)
- ground based telescopes
        - water detectors (increases info on %dry) space only
        - chemical detectors (increases chance of finding planets rich in certain ores) space only
        - atmospheric detectors (increases info on pressure; when combined with chemical can detect if planet has harvestable/hazardous gasses) ground and space
        - temperature detector (increases info on planetary temperature) space only
        - star scanner (increases the likelyhood of finding planets orbiting a star; higher level allows for smaller planets) ground and space
- asteroids
    - manual harvesting
    - research system
        - player can select as a destination
            - temporary dim in created, destroyed when last player leaves
- Hardmode
    - must protect space stations from meteors
    - rockets require life support
    - space ships require statis chamber and must be provided with food (unless warp) - tranz9999
    - heat system for machines and plasma engine
