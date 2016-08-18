package zmaster587.advancedRocketry.client;

import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;

public class ResourceIcon implements IIcon {

	ResourceLocation location;
	IIcon subIcon;

	public ResourceIcon(ResourceLocation location) {
		this.location = location;
		subIcon = null;
	}

	public ResourceIcon(ResourceLocation location, IIcon subIcon) {
		this.location = location;
		this.subIcon = subIcon;
	}
	
	public ResourceLocation getResourceLocation() {
		return location;
	}

	@Override
	public int getIconWidth() {
		return 0;
	}

	@Override
	public int getIconHeight() {
		return 0;
	}

	@Override
	public float getMinU() {
		if(subIcon == null)
			return 0;
		else 
			return subIcon.getMinU();
	}

	@Override
	public float getMaxU() {
		if(subIcon == null)
			return 1f;
		else 
			return subIcon.getMaxU();
	}

	@Override
	public float getInterpolatedU(double p_94214_1_) {
		if(subIcon == null)
			return 0;
		else 
			return subIcon.getInterpolatedU(p_94214_1_);
	}

	@Override
	public float getMinV() {
		if(subIcon == null)
			return 0f;
		else 
			return subIcon.getMinV();
	}

	@Override
	public float getMaxV() {
		if(subIcon == null)
			return 1f;
		else 
			return subIcon.getMaxV();
	}

	@Override
	public float getInterpolatedV(double p_94207_1_) {
		if(subIcon == null)
			return 0f;
		else 
			return subIcon.getInterpolatedV(p_94207_1_);
	}

	@Override
	public String getIconName() {
		return null;
	}

}
