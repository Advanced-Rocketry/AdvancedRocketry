package zmaster587.advancedRocketry.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import zmaster587.advancedRocketry.api.satellite.SatelliteBase;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.satellite.SatelliteOreMapping;
import zmaster587.libVulpes.inventory.modules.IModularInventory;

public class GuiHandler implements IGuiHandler {

	public enum guiId {
		RocketBuilder,
		BlastFurnace,
		OreMappingSatellite,
		StationChip
	}

	//X coord is entity ID num if entity
	@Override
	public Object getServerGuiElement(int ID, PlayerEntity player, World world,
			int x, int y, int z) {

		Object tile;

		if(y > -1)
			tile = world.getTileEntity(new BlockPos(x, y, z));
		else if(x == -1) {
			ItemStack stack = player.getHeldItem(Hand.MAIN_HAND);
			
			//If there is latency or some desync odd things can happen so check for that
			if(stack == null || !(stack.getItem() instanceof IModularInventory)) {
				return null;
			}
			
			tile = player.getHeldItem(Hand.MAIN_HAND).getItem();
		}
		else
			tile = world.getEntityByID(x);

		if(ID == guiId.OreMappingSatellite.ordinal()) {
			SatelliteBase satellite = DimensionManager.getInstance().getSatellite(y);
			
			if(satellite == null || !(satellite instanceof SatelliteOreMapping) || satellite.getDimensionId() != world.provider.getDimension())
				satellite = null;
			
			return new ContainerOreMappingSatallite((SatelliteOreMapping) satellite, player.inventory);
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, PlayerEntity player, World world,
			int x, int y, int z) {

		Object tile;
		
		if(y > -1)
			tile = world.getTileEntity(new BlockPos(x, y, z));
		else if(x == -1) {
			ItemStack stack = player.getHeldItem(Hand.MAIN_HAND);
			
			//If there is latency or some desync odd things can happen so check for that
			if(stack == null || !(stack.getItem() instanceof IModularInventory)) {
				return null;
			}
			
			tile = player.getHeldItem(Hand.MAIN_HAND).getItem();
		}
		else
			tile = world.getEntityByID(x);

		if(ID == guiId.OreMappingSatellite.ordinal()) {
			
			SatelliteBase satellite = DimensionManager.getInstance().getSatellite(y);
			
			if(satellite == null || !(satellite instanceof SatelliteOreMapping) || satellite.getDimensionId() != world.provider.getDimension())
				satellite = null;
			
			return new GuiOreMappingSatellite((SatelliteOreMapping) satellite, player);
		}
		return null;
	}
}