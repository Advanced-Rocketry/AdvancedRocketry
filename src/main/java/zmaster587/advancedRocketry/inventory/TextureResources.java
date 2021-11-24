package zmaster587.advancedRocketry.inventory;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import zmaster587.libVulpes.client.util.IndicatorBarImage;
import zmaster587.libVulpes.client.util.ProgressBarImage;
import zmaster587.libVulpes.util.IconResource;

public class TextureResources {
	public static final  ResourceLocation progressBars =  new ResourceLocation("advancedrocketry","textures/gui/progressbars/progressbars.png");
	public static final  ResourceLocation rocketHud = new ResourceLocation("advancedrocketry","textures/gui/rockethud.png");
	public static final  ResourceLocation laserGui = new ResourceLocation("advancedrocketry", "textures/gui/lasertile.png");
	public static final ResourceLocation[] buttonKill = {new ResourceLocation("advancedrocketry", "textures/gui/buttons/kill.png"), new ResourceLocation("advancedrocketry", "textures/gui/buttons/kill_hover.png"), null, null};
	public static final ResourceLocation[] buttonCopy = {new ResourceLocation("advancedrocketry", "textures/gui/buttons/copy.png"), new ResourceLocation("advancedrocketry", "textures/gui/buttons/copy_hover.png"), null, null};
	public static final ResourceLocation[] buttonAsteroid = {new ResourceLocation("advancedrocketry", "textures/gui/buttons/buttonasteroid.png"), new ResourceLocation("advancedrocketry", "textures/gui/buttons/buttonasteroid_hover.png"), null, null};
	public static final ResourceLocation[] buttonGeneric = {new ResourceLocation("advancedrocketry", "textures/gui/buttons/buttongeneric.png"), new ResourceLocation("advancedrocketry", "textures/gui/buttons/buttongeneric_hover.png"), null, null};
	public static final ResourceLocation[] buttonAutoEject = {new ResourceLocation("advancedrocketry", "textures/gui/buttons/buttonautoeject.png"), new ResourceLocation("advancedrocketry", "textures/gui/buttons/buttonautoeject_hover.png"), new ResourceLocation("advancedrocketry", "textures/gui/buttons/buttonautoeject_pressed.png"), null};
	public static final ResourceLocation[] tabAsteroid = {new ResourceLocation("advancedrocketry", "textures/gui/buttons/tabasteroid.png"), new ResourceLocation("advancedrocketry", "textures/gui/buttons/tabasteroid_hover.png"), null, null};
	public static final ResourceLocation[] tabData = {new ResourceLocation("advancedrocketry", "textures/gui/buttons/tabdata.png"), new ResourceLocation("advancedrocketry", "textures/gui/buttons/tabdata_hover.png"), null, null};
	public static final ResourceLocation[] tabWarp = {new ResourceLocation("advancedrocketry", "textures/gui/buttons/tabwarp.png"), new ResourceLocation("advancedrocketry", "textures/gui/buttons/tabwarp_hover.png"), null, null};
	public static final ResourceLocation[] tabPlanet = {new ResourceLocation("advancedrocketry", "textures/gui/buttons/tabplanet.png"), new ResourceLocation("advancedrocketry", "textures/gui/buttons/tabplanet_hover.png"), null, null};
	public static final ResourceLocation[] tabPlanetTracking = {new ResourceLocation("advancedrocketry", "textures/gui/buttons/tabguidance.png"), new ResourceLocation("advancedrocketry", "textures/gui/buttons/tabguidance_hover.png"), null, null};

	public static final  ResourceLocation locationSunPng = new ResourceLocation("advancedrocketry:textures/env/sun.png");
	public static final  ResourceLocation locationSunNew = new ResourceLocation("advancedrocketry:textures/env/sun2.png");
	public static final  ResourceLocation locationAccretionDisk = new ResourceLocation("advancedrocketry:textures/env/accretiondisk.png");
	public static final  ResourceLocation locationBlackHole = new ResourceLocation("advancedrocketry:textures/env/blackhole.png");
	public static final  ResourceLocation locationBlackHole_icon = new ResourceLocation("advancedrocketry:textures/env/blackhole_icon.png");
	public static final  ResourceLocation locationReticle = new ResourceLocation("advancedrocketry:textures/gui/recticle.png");
	public static final  ResourceLocation selectionCircle = new ResourceLocation("advancedrocketry:textures/gui/selection.png");
	public static final  ResourceLocation planetSelectorBar = new ResourceLocation("advancedrocketry:textures/gui/progressbars/planetselectorbars.png");
	public static final  ResourceLocation verticalBar = new ResourceLocation("advancedrocketry:textures/gui/bordervertical.png");
	public static final  ResourceLocation horizontalBar = new ResourceLocation("advancedrocketry:textures/gui/borderhorizontal.png");
	public static final  ResourceLocation jetpackIconEnabled = new ResourceLocation("advancedrocketry:textures/gui/jetpack.png");
	public static final  ResourceLocation jetpackIconDisabled = new ResourceLocation("advancedrocketry:textures/gui/jetpackdisabled.png");
	public static final  ResourceLocation jetpackIconHover = new ResourceLocation("advancedrocketry:textures/gui/jetpackhover.png");
	public static final  ResourceLocation modularHelm = new ResourceLocation("advancedrocketry:textures/gui/spacesuithelmet.png");
	public static final  ResourceLocation modularChest = new ResourceLocation("advancedrocketry:textures/gui/spacesuitchestpiece.png");
	public static final  ResourceLocation modularLegs = new ResourceLocation("advancedrocketry:textures/gui/spacesuitleggings.png");
	public static final  ResourceLocation modularBoots = new ResourceLocation("advancedrocketry:textures/gui/spacesuitboots.png");
	public static final  ResourceLocation frameHUDBG = new ResourceLocation("advancedrocketry:textures/gui/framebg.png");
	public static final  ResourceLocation[] armorSlots = new ResourceLocation[]{modularHelm, modularChest, modularLegs, modularBoots};
	public static final  ResourceLocation earthCandy = new ResourceLocation("advancedrocketry:textures/gui/eyecandy/earth.png");
	public static final  ResourceLocation metalPlate = new ResourceLocation("advancedrocketry:textures/models/metalplate.png");
	public static final  ResourceLocation diamondMetal = new ResourceLocation("advancedrocketry:textures/models/diamondmetal.png");
	public static final  ResourceLocation fan = new ResourceLocation("advancedrocketry:textures/models/fan.png");
	public static final  ResourceLocation genericStation = new ResourceLocation("advancedrocketry:textures/gui/genericstation.png");
	
	public static final IconResource ioSlot = new IconResource(212, 0, 18, 18, null);
	public static final IconResource idChip = new IconResource(230, 0, 18, 18, null);
	public static final IconResource slotO2 = new IconResource(238, 238, 18, 18, progressBars);
	public static final IconResource slotSatellite = new IconResource(220, 238, 18, 18, progressBars);
	
	public static final IconResource functionComponent = new IconResource(212, 18, 18, 18, null);
	public static final IconResource powercomponent = new IconResource(230, 18, 18, 18, null);
	public static final IconResource laserGuiBG = new IconResource(8, 16, 65, 70, laserGui);
	public static final IconResource earthCandyIcon = new IconResource(0, 0, 128, 128, earthCandy);
	
	public static final ProgressBarImage doubleWarningSideBarIndicator = new IndicatorBarImage(0, 84, 142, 16, 0, 100, 2, 9, 2, 2, Direction.EAST, planetSelectorBar);
	public static final ProgressBarImage doubleWarningSideBar = new ProgressBarImage(0, 59, 142, 16, 0, 75, 136, 9, 2, 2, Direction.EAST, planetSelectorBar);
	public static final ProgressBarImage massIndicator = new ProgressBarImage(0, 0, 81, 23, 6, 23, 75, 9, 2, 9, Direction.EAST, planetSelectorBar);
	public static final ProgressBarImage atmIndicator = new ProgressBarImage(0, 0, 81, 23, 6, 32, 75, 9, 2, 9, Direction.EAST, planetSelectorBar);
	public static final ProgressBarImage distanceIndicator = new ProgressBarImage(0, 0, 81, 23, 6, 41, 75, 9, 2, 9, Direction.EAST, planetSelectorBar);
	public static final ProgressBarImage genericSlider = new ProgressBarImage(0, 0, 81, 23, 6, 41, 75, 9, 2, 9, Direction.EAST, planetSelectorBar);
	public static final ProgressBarImage progressScience = new ProgressBarImage(185, 0, 16, 24, 201, 0, 16, 24, 0, 0, Direction.UP, TextureResources.progressBars);
	
	public static final ProgressBarImage progressToMission = new ProgressBarImage(25, 248, 112, 8, 25, 240, 112, 8, Direction.EAST, TextureResources.progressBars);
	public static final ProgressBarImage progressFromMission = new ProgressBarImage(25, 232, 112, 8, 25, 224, 112, 8, Direction.WEST, TextureResources.progressBars);
	public static final ProgressBarImage workMission = new ProgressBarImage(25, 216, 112, 8, 25, 208, 112, 8, Direction.EAST, TextureResources.progressBars);
	
	
	public static final ProgressBarImage crystallizerProgressBar = new ProgressBarImage(0, 0, 31, 66, 31, 0, 23, 49, 4, 17, Direction.UP, TextureResources.progressBars);
	public static final ProgressBarImage cuttingMachineProgressBar = new ProgressBarImage(54, 0, 42, 42,96, 0, 36, 36, 3, 3, Direction.EAST, TextureResources.progressBars);
	public static final ProgressBarImage arcFurnaceProgressBar = new ProgressBarImage(0, 66, 42, 42, 42, 66, 42, 42, 0, 0, Direction.UP, TextureResources.progressBars);
	public static final ProgressBarImage smallPlatePresser = new ProgressBarImage(0, 108, 16, 48, 0, 0, 1, 1, Direction.DOWN, TextureResources.progressBars); //TODO
	public static final ProgressBarImage latheProgressBar = new ProgressBarImage(185, 24, 23, 4, 185, 28, 23, 4, Direction.EAST, TextureResources.progressBars);
	public static final ProgressBarImage rollingMachineProgressBar = new ProgressBarImage(84, 66, 41, 32, 125, 66, 41, 32, Direction.EAST, TextureResources.progressBars);
	public static final ProgressBarImage terraformProgressBar = new ProgressBarImage(16, 109, 106, 30, 16, 138, 106, 30, Direction.EAST, TextureResources.progressBars);

	
	public static final Map<String, ResourceLocation> planetResources = new HashMap<>();
}
