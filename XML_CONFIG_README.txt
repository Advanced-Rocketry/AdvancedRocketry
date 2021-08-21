                      __..+======|++|;___.                      
                 _.:===;======;====|=++++|++__.                 
              _:============;==;=;==+|+|++++|=|+_.
           _:====;====;=;=;==;=======++++|+++++++|+_.
         _====;=;==;=;==;==;==;=;=;=;=+|+++|+|+|+++++;.         
       _==;=;====;==;==;==;==;==;==;===+++|=++++++|+|+|;.       
     .:==;===;=;==;==;==;==;==;==;===;==|+++|++|---     -_      
    .===;==;==;=;==;==;==;==;==;==;=;====+|+--          .+;     
   .======;==;===;==;==;==;==;==;==;==;==-`            .+|+;    
  .==;=;=;==;==;==;==;==;==;==;==;===;:-   ____       .+|=++;   
 .====;==;=;==;==;==;==;==;==;==;==;-     +++++:     ;|+++|+|;  
 :==;===;====;==;==;==;==;==;==;=:-       -|+|+`   _|++++|=+++. 
.==;==;=;=;=;==;==;-          --                ._+++++|+++|+|; 
.==;=;=====;==;=:                             _+|+|+|++++|+++++ 
=======;=;==;==;==...                      _;++|=+++++|+++++|++:
==;=;=;=;=;==;===;====;..               .:=+|++++|++|+++|+|+++|:
====;==;===;==;=;====;==              :====+++|++++|=++|=++++|=:
:==;==;==;==;===;=;:   -:.           .=====+|+++|+|=|+|=+|++|=+`
.====;==;==;=:----==:    :.  .       .==;==++++|=+++++++|=+|=|+ 
 ==;==;==;:-    --====.   :=;=       ===;==+|++++|+|++|+++|=++: 
 .===;==;=:--..====;=;=: .====.     .=;====|=+|+++++++++|+++|+  
  :=;==;= _.  .==;=-= ======;=.    .:==;=;+++|=+|+|++|+++++|=`  
   :==;====  :-  =   .==;==;==.  .:==;==;=|+|=|+++++|=+|+|++`   
    :===;-    .. .= :=====;==;=:=====;=;=|=+++++|++|=+|=+++`    
     :=.....=: _:=:====;=;==;====;==;====++|+|+++++++|=|+|`     
       ==============;==;==;==;=;==;==;=|++++++|++|+|=|=;
        -========;=;==;==;==;==;==;==;=+++|+|++++|=+++;`
          -==;=;==;==;==;==;==;==;==;=+|++++++|++++|;`          
             -===;==;==;==;==;==;==;=+|=+|+|++++|;-             
                -=;==;==;==;==;==;==|+++|=+++|:-                
                    --=;==;==;==;==|=+|++;~-                    
                          -----------`                          


Welcome to the Advanced Rocketry(AR) advanced configuration readme!

This document will guide you through manually or semi-manually defining planets for your world!

To use manual xml planet configuration, download and modify https://github.com/zmaster587/AdvancedRocketry/blob/master/Template.xml and rename to "planetDefs.xml" in the config/advancedRocketry folder


Explaination of usable tags:
===============================================================================================================================

The "planets" tag should be at the root of the document, this tells AR you are defining your set of planets in the body of this
tag.  The "numPlanet" attribute defines how many random planets should be defined in the solar systems, if not specified then
AR will default to six.

Example usage; generates one random planet around a star named Sol with the temperature of the sun at origin:
<galaxy>
<star name="Sol" temp="100" x="0" y="0" numPlanets="1">
    ...
</star>
</galaxy>

------------------------------------------------------------------------------------------------------------------------------

The "planet" tag surrounds the definition of a planet.  If a planet tag is used in the body of another planet tag, the inner
planet tag defines a moon of the outer planet.  The planet tag can have the attribute "name".  The name attribute specifies the
name of the planet.  If the name attribute is not present then the planet is automatically named "Sol-planet_id".

Example usage; generates one random planet and one planet with manually specified properties named "Earth" with a moon
named "Luna" and another manually specified planet "Mars"

<galaxy>
<star name="Sol" temp="100" x="0" y="0" numPlanets="1">
    <planet name="Earth">
        ...
        <planet name="Luna">
        ...
        </planet>
    </planet>
    <planet name="Mars">
    ...
    </planet>
</star>
</galaxy>

------------------------------------------------------------------------------------------------------------------------------

The "fogColor" tag specifes the color of the fog on a planet.  The body takes three comma seperated values corresponding to
Red, Green, and Blue respectivly.  These values can be any decimal number between 0 and 1 inclusive.  A 24-bit (6-byte) 
Hex color can also be specified by prepending the code with "0x".

Example usage; specifes a teal color fog using the RGB format.
<galaxy>
<star name="Sol" temp="100" x="0" y="0" numPlanets="1">
    <planet name="Earth">
        <fogColor>0.5,1,1</fogColor>
        ...
    </planet>
</star>
</galaxy>

Example usage; specifes the same teal color fog as the previous example using hex format.
<galaxy>
<star name="Sol" temp="100" x="0" y="0" numPlanets="1">
    <planet name="Earth">
        <fogColor>0x7FFFFFF</fogColor>
        ...
    </planet>
</star>
</galaxy>

------------------------------------------------------------------------------------------------------------------------------

The "fogColor" tag specifes the color of the sky on a planet.  The body takes three comma seperated values corresponding to
Red, Green, and Blue respectivly.  These values can be any decimal number between 0 and 1 inclusive.  A 24-bit (6-byte) 
Hex color can also be specified by prepending the code with "0x".

Example usage; specifes a teal color sky using the RGB format.
<galaxy>
<star name="Sol" temp="100" x="0" y="0" numPlanets="1">
    <planet name="Earth">
        <skyColor>0.5,1,1</skyColor>
        ...
    </planet>
</star>

Example usage; specifes the same teal color sky as the previous example using hex format.
<galaxy>
<star name="Sol" temp="100" x="0" y="0" numPlanets="1">
    <planet name="Earth">
        <fogColor>0x7FFFFFF</fogColor>
        ...
    </planet>
</star>
</galaxy>

------------------------------------------------------------------------------------------------------------------------------

The "atmosphereDensity" tag specifes the density of the atmosphere on a planet.  Any value greater than 75 is breathable, 
100 is Earthlike, anything higher than 100 has a denser atmosphere than Earth and will have thicker fog.  Any value less than 75
is unbreathable and will require a spacesuit and will generate craters.

Atmosphere density also has an impact on the temerature of the planets, planets with thinner will be colder 
and planets with thicker atmospheres will be warmer.

Max: 200
Default: 100
Min: 0

Example usage; specifes an atmosphere with the same density as Earth
<galaxy>
<star name="Sol" temp="100" x="0" y="0" numPlanets="1">
    <planet name="Earth">
        <atmosphereDensity>100</atmosphereDensity>
        ...
    </planet>
</star>
</galaxy>

------------------------------------------------------------------------------------------------------------------------------

The "gravitationalMultiplier" tag specifes the density of the atmosphere on a planet.  100 is earthlike.  Any value less than 100
will result in a gravitational pull less than that of Earth.  Any value higher than 110 may result in players being UNABLE to jump
up blocks without assistance from stairs.  Values very close to 0 ( < 10) may result in players being unable to fall.
YOU HAVE BEEN WARNED.

Max: 200
Default: 100
Min: 0
Recommended Max: 110
Recommended Min: 10

Example usage; specifes an atmosphere with the same density as Earth
<galaxy>
<star name="Sol" temp="100" x="0" y="0" numPlanets="1">
    <planet name="Earth">
        <gravitationalMultiplier>100</gravitationalMultiplier>
        ...
    </planet>
</star>
</galaxy>

------------------------------------------------------------------------------------------------------------------------------

The "orbitalDistance" tag specifes the distance of the planet from the body it is orbiting.
For planets orbiting the SUN:
    100 is defined as an earthlike and will result in the sun appearing normal in size.  200 is very far from the sun and will result
    in the sun appearing very small.  0 is nearly touching the surface of the host star and will result in the host star taking up a
    majority of the sky.
    Orbital distance also has an impact on the temerature of the planets, planets far away will be colder and planets closer to the host
    star will be warmer.
For MOONS orbiting other planets:
    The effects are the same as for planets orbiting a star except the observed host star size is determined by the planet orbiting the sun.
    I.E. the apparent size of the sun as seen from the moon is determined by the distance between the Earth and the sun.  The apparent
    distance of the host planet, however, will be changed by this value.  The apparent size of the moon as viewed from the host planet is
    also the direct result of this value.

For planets orbiting the sun, lower values result in higher temperatures.
For moons, this value has no effect on temperatures.

Max: 200
Default: 100
Min: 0

Example usage; specifes a distance from the host star to be the same as Earth
<galaxy>
<star name="Sol" temp="100" x="0" y="0" numPlanets="1">
    <planet name="Earth">
        <orbitalDistance>100</orbitalDistance>
        ...
    </planet>
</star>
</galaxy>

------------------------------------------------------------------------------------------------------------------------------

The "orbitalTheta" tag specifes the starting angular displacement relative to the origin in degrees.  

Max: 360
Default: 0
Min: 0

Example usage; specifes a planet to start exactly opposite the sun from Earth
<galaxy>
<star name="Sol" temp="100" x="0" y="0" numPlanets="1">
    <planet name="Earth">
        <orbitalTheta>180</orbitalTheta>
        ...
    </planet>
</star>
</galaxy>

------------------------------------------------------------------------------------------------------------------------------

The "orbitalTheta" tag specifes the angle of the plane on which the planet rotates around the star or it's host planet, 90 will cause the planet or sun to rise and set in the north and south (the planet would orbit such that it would pass over both poles) whereas 0 with be the normal procession (like orbit over the equator)

Max: 360
Default: 0
Min: 0

Example usage; specifes a planet to start exactly opposite the sun from Earth
<galaxy>
<star name="Sol" temp="100" x="0" y="0" numPlanets="1">
    <planet name="Earth">
        <orbitalPhi>180</orbitalPhi>
        ...
    </planet>
</star>
</galaxy>

------------------------------------------------------------------------------------------------------------------------------

The "rotationalPeriod" tag specifes length of a day night cycle for the planet in ticks.  Where 20 ticks = 1 second.  24,000/20 = 
1,200 seconds = 20 minutes.  I strongly recommend not using values < 400 as I found them to be very disorienting and somewhat
motion sickness inducing.

Max: 2^31 - 1 = 2,147,483,647 (java has no unsigned int...)
Default: 24000
Min: 1

Example usage; specifes a planet to start exactly opposite the sun from Earth
<galaxy>
<star name="Sol" temp="100" x="0" y="0" numPlanets="1">
    <planet name="Earth">
        <orbitalTheta>180</orbitalTheta>
        ...
    </planet>
</star>
</galaxy>

------------------------------------------------------------------------------------------------------------------------------

The "biomeIds" tag specifes a comma seperated list of biome ids to generate on the planet.  This list can include both vanilla
and modded biome ids.  If this tag is not included then the planet will automatically generate a list of biomes from its
atmosphere density, gravitationalMultiplier, and distance from the sun.

A list of vanilla biomes can be found at http://minecraft.gamepedia.com/Biome

Example usage; Planet will generate only ocean and ice plains
<galaxy>
<star name="Sol" temp="100" x="0" y="0" numPlanets="1">
    <planet name="Earth">
        <biomeIds>0,12</biomeIds>
        ...
    </planet>
</star>
</galaxy>

------------------------------------------------------------------------------------------------------------------------------

The "DIMID" attribute allows a user to specify the exact dimension id that the planet is going to occupy, useful for custom ore gen mods
and more control in general

Example usage; Planet will generate with the dimid 99
<galaxy>
<star name="Sol" temp="100" x="0" y="0" numPlanets="1">
    <planet name="Earth" DIMID="99">
        ...
    </planet>
</star>
</galaxy>

------------------------------------------------------------------------------------------------------------------------------

The "dimMapping" attribute allows a user to specify that the following planet is a dimension from another mod.  Note that it 
must be accompanied by a DIMID tag!!!

Be warned, if another mod does not have a dimension with that ID it will cause a crash if somebody tries to go there!

Example usage; Adding Twilight forests (with default configs) as a planet around Sol
<galaxy>
<star name="Sol" temp="100" x="0" y="0" numPlanets="1">
    <planet name="Earth" DIMID="7" dimMapping="">
        ...
    </planet>
</star>
</galaxy>