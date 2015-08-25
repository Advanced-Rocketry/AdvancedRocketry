package zmaster587.advancedRocketry.Inventory;

import zmaster587.advancedRocketry.Inventory.modules.IModularInventory;
import zmaster587.advancedRocketry.entity.EntityRocket;
import zmaster587.advancedRocketry.tile.TileRocketBuilder;
import zmaster587.advancedRocketry.tile.TileSpaceLaser;
import zmaster587.advancedRocketry.tile.infrastructure.TileEntityFuelingStation;
import zmaster587.advancedRocketry.tile.multiblock.TileEntityBlastFurnace;
import zmaster587.libVulpes.api.IUniversalEnergy;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler {

	public enum guiId {
		RocketBuilder,
		BlastFurnace,
		SpaceLaser,
		MODULAR,
		MODULARNOINV
	}

	//X coord is entity ID num if entity
	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world,
			int x, int y, int z) {

		Object tile;

		if(y > -1)
			tile = world.getTileEntity(x, y, z);
		else
			tile = world.getEntityByID(x);

		if(ID == guiId.BlastFurnace.ordinal()) {
			return new ContainerBlastFurnace(player.inventory, (TileEntityBlastFurnace)tile);
		}
		else if(ID == guiId.SpaceLaser.ordinal()) {
			return new ContainerSpaceLaser(player.inventory, (TileSpaceLaser)tile);
		}
		else if(ID == guiId.MODULAR.ordinal() || ID == guiId.MODULARNOINV.ordinal()) {
			return new ContainerModular(player, ((IModularInventory)tile).getModules(), ((IModularInventory)tile), ID == guiId.MODULAR.ordinal());
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world,
			int x, int y, int z) {

		Object tile;

		if(y > -1)
			tile = world.getTileEntity(x, y, z);
		else
			tile = world.getEntityByID(x);

		if(ID == guiId.BlastFurnace.ordinal()) {
			return new GuiBlastFurnace(player.inventory, (TileEntityBlastFurnace)tile);
		}
		else if(ID == guiId.SpaceLaser.ordinal()) {
			return new GuiSpaceLaser(player.inventory, (TileSpaceLaser)tile);
		}
		else if(ID == guiId.MODULAR.ordinal() || ID == guiId.MODULARNOINV.ordinal()) {
			IModularInventory modularTile = ((IModularInventory)tile);
			return new GuiModular(player,modularTile.getModules(), modularTile, ID == guiId.MODULAR.ordinal(), modularTile.getModularInventoryName());
		}
		return null;
	}
}