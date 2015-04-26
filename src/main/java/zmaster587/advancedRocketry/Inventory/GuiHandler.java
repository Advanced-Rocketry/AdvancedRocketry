package zmaster587.advancedRocketry.Inventory;

import zmaster587.advancedRocketry.tile.TileRocketBuilder;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler {

	public enum guiId {
		RocketBuilder
	}
	
	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world,
			int x, int y, int z) {
		TileEntity tile = world.getTileEntity(x, y, z);
		
		if(ID  == guiId.RocketBuilder.ordinal())
		{
			return new ContainerRocketBuilder(player.inventory, (TileRocketBuilder) tile);
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world,
			int x, int y, int z) {
		TileEntity tile = world.getTileEntity(x, y, z);
		
		if(ID  == guiId.RocketBuilder.ordinal())
		{
			return new GuiRocketBuilder(player.inventory, (TileRocketBuilder) tile);
		}
		return null;
	}
}