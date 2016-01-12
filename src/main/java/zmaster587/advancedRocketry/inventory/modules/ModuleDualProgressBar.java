package zmaster587.advancedRocketry.inventory.modules;

import java.util.LinkedList;
import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.util.MathHelper;
import zmaster587.advancedRocketry.client.render.util.ProgressBarImage;

public class ModuleDualProgressBar extends ModuleProgress {

	private float multiplier;
	
	public ModuleDualProgressBar(int offsetX, int offsetY, int id,
			ProgressBarImage progressBar, IProgressBar progress) {
		super(offsetX, offsetY, id, progressBar, progress);
		multiplier =1f;
	}
	
	public ModuleDualProgressBar(int offsetX, int offsetY, int id,
			ProgressBarImage progressBar, IProgressBar progress, String tooltip) {
		super(offsetX, offsetY, id, progressBar, progress, tooltip);
		multiplier =1f;
	}
	
	
	public void setTooltipValueMultiplier(float multiplier) {
		this.multiplier = multiplier;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	protected List<String> getToolTip() {
		List<String> modifiedList = new LinkedList<String>();
		
		for(String string : tooltip) {
			int centerPoint = progress.getTotalProgress(id);
			int variation = progress.getProgress(id);
			
			String newStr = string.replaceAll("%b", String.format("%.2f",multiplier*MathHelper.clamp_float(centerPoint - variation/2,0,100)));
			newStr = newStr.replaceAll("%a", String.format("%.2f",multiplier*MathHelper.clamp_float(centerPoint + variation/2,0,100)));
			modifiedList.add(newStr);
		}
		return modifiedList;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void renderBackground(GuiContainer gui, int x, int y, int mouseX,
			int mouseY, FontRenderer font) {
		
		float totalProgress = progress.getTotalProgress(id)/100f;
		float randomnessFactor = progress.getProgress(id)/100f;
		
		progressBar.renderProgressBarPartial(x + offsetX, y + offsetY, totalProgress , 0.5f*randomnessFactor, gui);
	}
}
