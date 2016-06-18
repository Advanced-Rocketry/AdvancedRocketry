package zmaster587.advancedRocketry.inventory;

import zmaster587.advancedRocketry.inventory.modules.IModularInventory;
import zmaster587.advancedRocketry.tile.TileSpaceLaser;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler {

	public enum guiId {
		RocketBuilder,
		BlastFurnace,
		SpaceLaser,
		MODULAR,
		MODULARNOINV,
		MODULARFULLSCREEN
	}

	//X coord is entity ID num if entity
	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world,
			int x, int y, int z) {

		Object tile;

		if(y > -1)
			tile = world.getTileEntity(x, y, z);
		else if(x == -1) {
			ItemStack stack = player.getHeldItem();
			
			//If there is latency or some desync odd things can happen so check for that
			if(stack == null || !(stack.getItem() instanceof IModularInventory)) {
				return null;
			}
			
			tile = player.getHeldItem().getItem();
		}
		else
			tile = world.getEntityByID(x);

		if(ID == guiId.SpaceLaser.ordinal()) {
			return new ContainerSpaceLaser(player.inventory, (TileSpaceLaser)tile);
		}
		else if(ID == guiId.MODULAR.ordinal() || ID == guiId.MODULARNOINV.ordinal() || ID == guiId.MODULARFULLSCREEN.ordinal()) {
			return new ContainerModular(player, ((IModularInventory)tile).getModules(ID, player), ((IModularInventory)tile), ID == guiId.MODULAR.ordinal(), ID != guiId.MODULARFULLSCREEN.ordinal());
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world,
			int x, int y, int z) {

		Object tile;
		
		if(y > -1)
			tile = world.getTileEntity(x, y, z);
		else if(x == -1) {
			ItemStack stack = player.getHeldItem();
			
			//If there is latency or some desync odd things can happen so check for that
			if(stack == null || !(stack.getItem() instanceof IModularInventory)) {
				return null;
			}
			
			tile = player.getHeldItem().getItem();
		}
		else
			tile = world.getEntityByID(x);

		if(ID == guiId.SpaceLaser.ordinal()) {
			return new GuiSpaceLaser(player.inventory, (TileSpaceLaser)tile);
		}
		else if(ID == guiId.MODULAR.ordinal() || ID == guiId.MODULARNOINV.ordinal()) {
			IModularInventory modularTile = ((IModularInventory)tile);
			return new GuiModular(player, modularTile.getModules(ID, player), modularTile, ID == guiId.MODULAR.ordinal(), true, modularTile.getModularInventoryName());
		}
		else if(ID == guiId.MODULARFULLSCREEN.ordinal()) {
			IModularInventory modularTile = ((IModularInventory)tile);
			return new GuiModularFullScreen(player,modularTile.getModules(ID, player), modularTile, ID == guiId.MODULAR.ordinal(), false, modularTile.getModularInventoryName());
		}
		return null;
	}
}