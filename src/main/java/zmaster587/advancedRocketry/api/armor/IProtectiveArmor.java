package zmaster587.advancedRocketry.api.armor;

import net.minecraft.item.ItemStack;
import zmaster587.advancedRocketry.api.IAtmosphere;

import javax.annotation.Nonnull;

public interface IProtectiveArmor {
    /**
     * Called when checking for protection from an atmosphere
     * stack is the piece of armor being checked
     * commitProtection true if actually running protection, this exists so that it is possible to check if an armor protects
     * without performing actual item/fluid consumption.  This should only be true when called from AtmosphereHandler to prevent double consumtion
     */
    boolean protectsFromSubstance(IAtmosphere atmosphere, @Nonnull ItemStack stack, boolean commitProtection);
}
