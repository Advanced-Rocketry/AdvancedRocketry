package zmaster587.advancedRocketry.Inventory;

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
	public static final  ResourceLocation progressBars =  new ResourceLocation("advancedrocketry:textures/gui/progressBars/progressBars.png");
	public static final  ResourceLocation rocketHud = new ResourceLocation("advancedrocketry:textures/gui/rocketHUD.png");
	public static final  ResourceLocation buttonToggleImage[] = new ResourceLocation[] {new ResourceLocation("advancedrocketry:textures/gui/buttons/switchOn.png"), new ResourceLocation("advancedrocketry:textures/gui/buttons/switchOff.png")};
	
	public static final IconResource ioSlot = new IconResource(212, 0, 18, 18, null);
	public static final IconResource idChip = new IconResource(230, 0, 18, 18, null);
	public static final IconResource functionComponent = new IconResource(212, 18, 18, 18, null);
	public static final IconResource powercomponent = new IconResource(230, 18, 18, 18, null);
}
