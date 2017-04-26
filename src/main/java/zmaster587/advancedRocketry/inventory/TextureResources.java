package zmaster587.advancedRocketry.inventory;

import zmaster587.libVulpes.client.util.IndicatorBarImage;
import zmaster587.libVulpes.client.util.ProgressBarImage;
import zmaster587.libVulpes.util.IconResource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

public class TextureResources {
	public static final  ResourceLocation progressBars =  new ResourceLocation("advancedrocketry:textures/gui/progressBars/progressBars.png");
	public static final  ResourceLocation rocketHud = new ResourceLocation("advancedrocketry:textures/gui/rocketHUD.png");
	public static final  ResourceLocation laserGui = new ResourceLocation("advancedrocketry", "textures/gui/LaserTile.png");
	public static final  ResourceLocation buttonKill[] = {new ResourceLocation("advancedrocketry", "textures/gui/buttons/kill.png"), new ResourceLocation("advancedrocketry", "textures/gui/buttons/kill_hover.png"), null, null};
	public static final  ResourceLocation buttonCopy[] = {new ResourceLocation("advancedrocketry", "textures/gui/buttons/copy.png"), new ResourceLocation("advancedrocketry", "textures/gui/buttons/copy_hover.png"), null, null};
	public static final  ResourceLocation buttonAsteroid[] = {new ResourceLocation("advancedrocketry", "textures/gui/buttons/buttonAsteroid.png"), new ResourceLocation("advancedrocketry", "textures/gui/buttons/buttonAsteroid_hover.png"), null, null};
	public static final  ResourceLocation tabAsteroid[] = {new ResourceLocation("advancedrocketry", "textures/gui/buttons/tabAsteroid.png"), new ResourceLocation("advancedrocketry", "textures/gui/buttons/tabAsteroid_hover.png"), null, null};
	public static final  ResourceLocation tabData[] = {new ResourceLocation("advancedrocketry", "textures/gui/buttons/tabData.png"), new ResourceLocation("advancedrocketry", "textures/gui/buttons/tabData_hover.png"), null, null};
	public static final  ResourceLocation tabWarp[] = {new ResourceLocation("advancedrocketry", "textures/gui/buttons/tabWarp.png"), new ResourceLocation("advancedrocketry", "textures/gui/buttons/tabWarp_hover.png"), null, null};
	public static final  ResourceLocation tabPlanet[] = {new ResourceLocation("advancedrocketry", "textures/gui/buttons/tabPlanet.png"), new ResourceLocation("advancedrocketry", "textures/gui/buttons/tabPlanet_hover.png"), null, null};
	public static final  ResourceLocation tabPlanetTracking[] = {new ResourceLocation("advancedrocketry", "textures/gui/buttons/tabGuidance.png"), new ResourceLocation("advancedrocketry", "textures/gui/buttons/tabGuidance_hover.png"), null, null};

	public static final  ResourceLocation locationSunPng = new ResourceLocation("advancedrocketry:textures/env/sun.png");
	public static final  ResourceLocation locationSunNew = new ResourceLocation("advancedrocketry:textures/env/sun2.png");
	public static final  ResourceLocation selectionCircle = new ResourceLocation("advancedrocketry:textures/gui/Selection.png");
	public static final  ResourceLocation planetSelectorBar = new ResourceLocation("advancedrocketry:textures/gui/progressBars/PlanetSelectorBars.png");
	public static final  ResourceLocation verticalBar = new ResourceLocation("advancedrocketry:textures/gui/BorderVertical.png");
	public static final  ResourceLocation horizontalBar = new ResourceLocation("advancedrocketry:textures/gui/BorderHorizontal.png");
	public static final  ResourceLocation jetpackIconEnabled = new ResourceLocation("advancedrocketry:textures/gui/jetpack.png");
	public static final  ResourceLocation jetpackIconDisabled = new ResourceLocation("advancedrocketry:textures/gui/jetpackDisabled.png");
	public static final  ResourceLocation jetpackIconHover = new ResourceLocation("advancedrocketry:textures/gui/jetpackHover.png");
	public static final  ResourceLocation modularHelm = new ResourceLocation("advancedrocketry:textures/gui/space_helmet.png");
	public static final  ResourceLocation modularChest = new ResourceLocation("advancedrocketry:textures/gui/space_chestplate.png");
	public static final  ResourceLocation modularLegs = new ResourceLocation("advancedrocketry:textures/gui/space_leggings.png");
	public static final  ResourceLocation modularBoots = new ResourceLocation("advancedrocketry:textures/gui/space_boots.png");
	public static final  ResourceLocation frameHUDBG = new ResourceLocation("advancedrocketry:textures/gui/FrameBG.png");
	public static final  ResourceLocation[] armorSlots = new ResourceLocation[]{modularHelm, modularChest, modularLegs, modularBoots};
	public static final  ResourceLocation earthCandy = new ResourceLocation("advancedrocketry:textures/gui/eyeCandy/Earth.png");
	public static final  ResourceLocation metalPlate = new ResourceLocation("advancedRocketry:textures/models/metalPlate.png");
	public static final  ResourceLocation diamondMetal = new ResourceLocation("advancedRocketry:textures/models/diamondMetal.png");
	public static final  ResourceLocation fan = new ResourceLocation("advancedRocketry:textures/models/fan.png");
	
	public static final IconResource ioSlot = new IconResource(212, 0, 18, 18, null);
	public static final IconResource idChip = new IconResource(230, 0, 18, 18, null);
	public static final IconResource slotO2 = new IconResource(238, 238, 18, 18, progressBars);
	
	public static final IconResource functionComponent = new IconResource(212, 18, 18, 18, null);
	public static final IconResource powercomponent = new IconResource(230, 18, 18, 18, null);
	public static final IconResource laserGuiBG = new IconResource(8, 16, 65, 70, laserGui);
	public static final IconResource earthCandyIcon = new IconResource(0, 0, 128, 128, earthCandy);
	
	public static final ProgressBarImage doubleWarningSideBarIndicator = new IndicatorBarImage(0, 84, 142, 16, 0, 100, 2, 9, 2, 2, EnumFacing.EAST, planetSelectorBar);
	public static final ProgressBarImage doubleWarningSideBar = new ProgressBarImage(0, 59, 142, 16, 0, 75, 136, 9, 2, 2, EnumFacing.EAST, planetSelectorBar);
	public static final ProgressBarImage massIndicator = new ProgressBarImage(0, 0, 81, 23, 6, 23, 75, 9, 2, 9, EnumFacing.EAST, planetSelectorBar);
	public static final ProgressBarImage atmIndicator = new ProgressBarImage(0, 0, 81, 23, 6, 32, 75, 9, 2, 9, EnumFacing.EAST, planetSelectorBar);
	public static final ProgressBarImage distanceIndicator = new ProgressBarImage(0, 0, 81, 23, 6, 41, 75, 9, 2, 9, EnumFacing.EAST, planetSelectorBar);
	public static final ProgressBarImage genericSlider = new ProgressBarImage(0, 0, 81, 23, 6, 41, 75, 9, 2, 9, EnumFacing.EAST, planetSelectorBar);
	public static final ProgressBarImage progressScience = new ProgressBarImage(185, 0, 16, 24, 201, 0, 16, 24, 0, 0, EnumFacing.UP, TextureResources.progressBars);
	
	public static final ProgressBarImage progressToMission = new ProgressBarImage(25, 248, 112, 8, 25, 240, 112, 8, EnumFacing.EAST, TextureResources.progressBars);
	public static final ProgressBarImage progressFromMission = new ProgressBarImage(25, 232, 112, 8, 25, 224, 112, 8, EnumFacing.WEST, TextureResources.progressBars);
	public static final ProgressBarImage workMission = new ProgressBarImage(25, 216, 112, 8, 25, 208, 112, 8, EnumFacing.EAST, TextureResources.progressBars);
	
	
	public static final ProgressBarImage crystallizerProgressBar = new ProgressBarImage(0, 0, 31, 66, 31, 0, 23, 49, 4, 17, EnumFacing.UP, TextureResources.progressBars);
	public static final ProgressBarImage cuttingMachineProgressBar = new ProgressBarImage(54, 0, 42, 42,96, 0, 36, 36, 3, 3, EnumFacing.EAST, TextureResources.progressBars);
	public static final ProgressBarImage arcFurnaceProgressBar = new ProgressBarImage(0, 66, 42, 42, 42, 66, 42, 42, 0, 0, EnumFacing.UP, TextureResources.progressBars);
	public static final ProgressBarImage smallPlatePresser = new ProgressBarImage(0, 108, 16, 48, 0, 0, 1, 1, EnumFacing.DOWN, TextureResources.progressBars); //TODO
	public static final ProgressBarImage latheProgressBar = new ProgressBarImage(185, 24, 23, 4, 185, 28, 23, 4, EnumFacing.EAST, TextureResources.progressBars);
	public static final ProgressBarImage rollingMachineProgressBar = new ProgressBarImage(84, 66, 41, 32, 125, 66, 41, 32, EnumFacing.EAST, TextureResources.progressBars);
	public static final ProgressBarImage terraformProgressBar = new ProgressBarImage(16, 109, 106, 30, 16, 138, 106, 30, EnumFacing.EAST, TextureResources.progressBars);

}
