package zmaster587.advancedRocketry.util;

import net.minecraft.item.ItemStack;

import org.jetbrains.annotations.NotNull;
import java.util.List;

public interface ITilePlanetSystemSelectable {
	@NotNull
	ItemStack getChipWithId(int id);
	
	void setSelectedPlanetId(int id);
	
	List<Integer> getVisiblePlanets();
}
