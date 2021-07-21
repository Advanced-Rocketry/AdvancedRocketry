package zmaster587.advancedRocketry.util;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.List;

public interface ITilePlanetSystemSelectable {
	public ItemStack getChipWithId(ResourceLocation id);
	
	public void setSelectedPlanetId(ResourceLocation id);
	
	public List<ResourceLocation> getVisiblePlanets();
}
