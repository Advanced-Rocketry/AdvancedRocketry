package zmaster587.advancedRocketry.inventory;

<<<<<<< HEAD
public class GuiHandler {
=======
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
>>>>>>> origin/feature/nuclearthermalrockets

	public enum guiId {
		RocketBuilder,
		BlastFurnace,
		OreMappingSatellite,
		StationChip
	}

	//X coord is entity ID num if entity
	/*	@Override
	public Object getServerGuiElement(int ID, PlayerEntity player, World world,
			int x, int y, int z) {

		Object tile;

<<<<<<< HEAD
		if(y > -1)
			tile = world.getTileEntity(new BlockPos(x, y, z));
		else if(x == -1) {
			ItemStack stack = player.getHeldItem(Hand.MAIN_HAND);
=======
		if(x == -1 && y < -1) {
			ItemStack stack = player.getHeldItem(EnumHand.MAIN_HAND);
>>>>>>> origin/feature/nuclearthermalrockets
			
			//If there is latency or some desync odd things can happen so check for that
			if(stack.isEmpty() || !(stack.getItem() instanceof IModularInventory)) {
				return null;
			}
<<<<<<< HEAD
			
			tile = player.getHeldItem(Hand.MAIN_HAND).getItem();
=======
>>>>>>> origin/feature/nuclearthermalrockets
		}

		if(ID == guiId.OreMappingSatellite.ordinal()) {
			SatelliteBase satellite = DimensionManager.getInstance().getSatellite(y);
			
			if(!(satellite instanceof SatelliteOreMapping) || satellite.getDimensionId() != world.provider.getDimension())
				satellite = null;
			
			return new ContainerOreMappingSatellite((SatelliteOreMapping) satellite, player.inventory);
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, PlayerEntity player, World world,
			int x, int y, int z) {

<<<<<<< HEAD
		Object tile;
		
		if(y > -1)
			tile = world.getTileEntity(new BlockPos(x, y, z));
		else if(x == -1) {
			ItemStack stack = player.getHeldItem(Hand.MAIN_HAND);
=======
		if(x == -1 && y < -1) {
			ItemStack stack = player.getHeldItem(EnumHand.MAIN_HAND);
>>>>>>> origin/feature/nuclearthermalrockets
			
			//If there is latency or some desync odd things can happen so check for that
			if(stack.isEmpty() || !(stack.getItem() instanceof IModularInventory)) {
				return null;
			}
<<<<<<< HEAD
			
			tile = player.getHeldItem(Hand.MAIN_HAND).getItem();
=======
>>>>>>> origin/feature/nuclearthermalrockets
		}

		if(ID == guiId.OreMappingSatellite.ordinal()) {
			
			SatelliteBase satellite = DimensionManager.getInstance().getSatellite(y);
			
			if(!(satellite instanceof SatelliteOreMapping) || satellite.getDimensionId() != world.provider.getDimension())
				satellite = null;
			
			return new GuiOreMappingSatellite((SatelliteOreMapping) satellite, player);
		}
		return null;
	}*/
}