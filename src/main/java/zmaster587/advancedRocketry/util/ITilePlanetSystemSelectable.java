package zmaster587.advancedRocketry.util;

import net.minecraft.item.ItemStack;

import java.util.List;

public interface ITilePlanetSystemSelectable {
	public ItemStack getChipWithId(int id);
	
	public void setSelectedPlanetId(int id);
	
	public List<Integer> getVisiblePlanets();
}
