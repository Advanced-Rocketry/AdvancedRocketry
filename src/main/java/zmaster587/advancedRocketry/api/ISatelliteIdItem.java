package zmaster587.advancedRocketry.api;

import net.minecraft.item.ItemStack;
import zmaster587.advancedRocketry.api.satellite.SatelliteProperties;

import org.jetbrains.annotations.NotNull;

public interface ISatelliteIdItem {
	void setSatellite(@NotNull ItemStack stack, SatelliteProperties properties);
}
