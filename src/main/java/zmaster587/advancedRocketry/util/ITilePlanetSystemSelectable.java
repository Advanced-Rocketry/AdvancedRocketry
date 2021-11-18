package zmaster587.advancedRocketry.util;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.List;

public interface ITilePlanetSystemSelectable {
	ItemStack getChipWithId(ResourceLocation id);
	
	void setSelectedPlanetId(ResourceLocation id);
	
	List<ResourceLocation> getVisiblePlanets();
}
