package zmaster587.advancedRocketry.api;

import net.minecraft.item.ItemStack;
import zmaster587.advancedRocketry.api.satellite.SatelliteProperties;

import javax.annotation.Nonnull;

public interface ISatelliteIdItem {
    void setSatellite(@Nonnull ItemStack stack, SatelliteProperties properties);
}
