package zmaster587.advancedRocketry.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import zmaster587.advancedRocketry.api.satellite.SatelliteBase;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.satellite.SatelliteOreMapping;
import zmaster587.libVulpes.inventory.modules.IModularInventory;

public class GuiHandler implements IGuiHandler {

    //X coord is entity ID num if entity
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world,
                                      int x, int y, int z) {

        Object tile;

        if (x == -1 && y < -1) {
            ItemStack stack = player.getHeldItem(EnumHand.MAIN_HAND);

            //If there is latency or some desync odd things can happen so check for that
            if (stack.isEmpty() || !(stack.getItem() instanceof IModularInventory)) {
                return null;
            }
        }

        if (ID == guiId.OreMappingSatellite.ordinal()) {
            SatelliteBase satellite = DimensionManager.getInstance().getSatellite(y);

            if (!(satellite instanceof SatelliteOreMapping) || satellite.getDimensionId() != world.provider.getDimension())
                satellite = null;

            return new ContainerOreMappingSatellite((SatelliteOreMapping) satellite, player.inventory);
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world,
                                      int x, int y, int z) {

        if (x == -1 && y < -1) {
            ItemStack stack = player.getHeldItem(EnumHand.MAIN_HAND);

            //If there is latency or some desync odd things can happen so check for that
            if (stack.isEmpty() || !(stack.getItem() instanceof IModularInventory)) {
                return null;
            }
        }

        if (ID == guiId.OreMappingSatellite.ordinal()) {

            SatelliteBase satellite = DimensionManager.getInstance().getSatellite(y);

            if (!(satellite instanceof SatelliteOreMapping) || satellite.getDimensionId() != world.provider.getDimension())
                satellite = null;

            return new GuiOreMappingSatellite((SatelliteOreMapping) satellite, player);
        }
        return null;
    }

    public enum guiId {
        RocketBuilder,
        BlastFurnace,
        OreMappingSatellite,
        StationChip
    }
}