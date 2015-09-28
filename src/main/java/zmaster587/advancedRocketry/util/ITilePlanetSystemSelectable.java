package zmaster587.advancedRocketry.util;

import java.util.List;

import net.minecraft.item.ItemStack;

public interface ITilePlanetSystemSelectable {
	public ItemStack getChipWithId(int id);
	
	public void setSelectedPlanetId(int id);
	
	public List<Integer> getVisiblePlanets();
}
