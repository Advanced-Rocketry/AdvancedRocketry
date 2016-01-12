package zmaster587.advancedRocketry.inventory;

import zmaster587.advancedRocketry.client.render.util.ProgressBarImage;
import zmaster587.libVulpes.util.IconResource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.ForgeDirection;

public class TextureResources {
	public static final  ResourceLocation buttonNull[] = {new ResourceLocation("advancedrocketry","textures/gui/null.png" )};
	public static final  ResourceLocation buttonScan[] = {new ResourceLocation("advancedrocketry", "textures/gui/GuiScan.png"), new ResourceLocation("advancedrocketry", "textures/gui/GuiScan_hover.png"), new ResourceLocation("advancedrocketry", "textures/gui/GuiScan_pressed.png"), null};
	public static final  ResourceLocation buttonBuild[] = {new ResourceLocation("advancedrocketry", "textures/gui/GuiButtonRed.png"), new ResourceLocation("advancedrocketry", "textures/gui/GuiButtonRed_hover.png"), new ResourceLocation("advancedrocketry", "textures/gui/GuiButtonRed_pressed.png"),  new ResourceLocation("advancedrocketry", "textures/gui/GuiButtonRed_disabled.png")};
	public static final  ResourceLocation buttonDown[] = {new ResourceLocation("advancedrocketry", "textures/gui/GuiArrowDown.png"), new ResourceLocation("advancedrocketry", "textures/gui/GuiArrowDown_hover.png"), new ResourceLocation("advancedrocketry", "textures/gui/GuiArrowDown_pressed.png"), null};
	public static final  ResourceLocation buttonLeft[] = {new ResourceLocation("advancedrocketry", "textures/gui/GuiArrowLeft.png"), new ResourceLocation("advancedrocketry", "textures/gui/GuiArrowLeft_hover.png"), new ResourceLocation("advancedrocketry", "textures/gui/GuiArrowLeft_pressed.png"), null};
	public static final  ResourceLocation buttonRight[] = {new ResourceLocation("advancedrocketry", "textures/gui/GuiArrowRight.png"), new ResourceLocation("advancedrocketry", "textures/gui/GuiArrowRight_hover.png"), new ResourceLocation("advancedrocketry", "textures/gui/GuiArrowRight_pressed.png"), null};
	public static final  ResourceLocation buttonKill[] = {new ResourceLocation("advancedrocketry", "textures/gui/buttons/kill.png"), new ResourceLocation("advancedrocketry", "textures/gui/buttons/kill_hover.png"), null, null};
	public static final  ResourceLocation buttonCopy[] = {new ResourceLocation("advancedrocketry", "textures/gui/buttons/copy.png"), new ResourceLocation("advancedrocketry", "textures/gui/buttons/copy_hover.png"), null, null};
	public static final  ResourceLocation starryBG 	   = new ResourceLocation("advancedrocketry", "textures/gui/starryBg.png");
	public static final  ResourceLocation progressBars =  new ResourceLocation("advancedrocketry:textures/gui/progressBars/progressBars.png");
	public static final  ResourceLocation rocketHud = new ResourceLocation("advancedrocketry:textures/gui/rocketHUD.png");
	public static final  ResourceLocation buttonToggleImage[] = new ResourceLocation[] {new ResourceLocation("advancedrocketry:textures/gui/buttons/switchOn.png"), new ResourceLocation("advancedrocketry:textures/gui/buttons/switchOff.png")};
	public static final  ResourceLocation locationSunPng = new ResourceLocation("advancedrocketry:textures/env/sun.png");
	public static final  ResourceLocation selectionCircle = new ResourceLocation("advancedrocketry:textures/gui/Selection.png");
	public static final  ResourceLocation planetSelectorBar = new ResourceLocation("advancedrocketry:textures/gui/progressBars/PlanetSelectorBars.png");

	
	public static final IconResource ioSlot = new IconResource(212, 0, 18, 18, null);
	public static final IconResource idChip = new IconResource(230, 0, 18, 18, null);
	public static final IconResource functionComponent = new IconResource(212, 18, 18, 18, null);
	public static final IconResource powercomponent = new IconResource(230, 18, 18, 18, null);
	
	public static final ProgressBarImage massIndicator = new ProgressBarImage(0, 0, 81, 23, 6, 23, 75, 9, 2, 9, ForgeDirection.EAST, planetSelectorBar);
	public static final ProgressBarImage atmIndicator = new ProgressBarImage(0, 0, 81, 23, 6, 32, 75, 9, 2, 9, ForgeDirection.EAST, planetSelectorBar);
	public static final ProgressBarImage distanceIndicator = new ProgressBarImage(0, 0, 81, 23, 6, 41, 75, 9, 2, 9, ForgeDirection.EAST, planetSelectorBar);
	public static final ProgressBarImage progressScience = new ProgressBarImage(185, 0, 16, 24, 201, 0, 16, 24, 0, 0, ForgeDirection.UP, TextureResources.progressBars);
	
	public static final ProgressBarImage crystallizerProgressBar = new ProgressBarImage(0, 0, 31, 66, 31, 0, 23, 49, 4, 17, ForgeDirection.UP, TextureResources.progressBars);
	public static final ProgressBarImage cuttingMachineProgressBar = new ProgressBarImage(54, 0, 42, 42,96, 0, 36, 36, 3, 3, ForgeDirection.EAST, TextureResources.progressBars);
	public static final ProgressBarImage arcFurnaceProgressBar = new ProgressBarImage(0, 66, 42, 42, 42, 66, 42, 42, 0, 0, ForgeDirection.UP, TextureResources.progressBars);
	public static final ProgressBarImage smallPlatePresser = new ProgressBarImage(0, 108, 16, 48, 0, 0, 1, 1, ForgeDirection.DOWN, TextureResources.progressBars); //TODO
	public static final ProgressBarImage latheProgressBar = new ProgressBarImage(185, 24, 23, 4, 185, 28, 23, 4, ForgeDirection.EAST, TextureResources.progressBars);
	public static final ProgressBarImage rollingMachineProgressBar = new ProgressBarImage(84, 66, 41, 32, 125, 66, 41, 32, ForgeDirection.EAST, TextureResources.progressBars);

}
