package zmaster587.advancedRocketry.entity;

import zmaster587.advancedRocketry.tile.station.TilePlanetaryHologram;
import zmaster587.libVulpes.inventory.modules.IButtonInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class EntityUIButton extends EntityUIPlanet {

	int id;
	TilePlanetaryHologram tile;
	
	public EntityUIButton(World worldIn, int id, TilePlanetaryHologram tile) {
		this(worldIn);
		this.id = id;
		this.tile = tile;
	}
	
	public EntityUIButton(World worldIn) {
		super(worldIn);
		setSize(0.2f, 0.2f);
	}
	
	@Override
	protected void entityInit() {
		this.dataWatcher.addObject(PLANET_ID, id);
		this.dataWatcher.addObject(SCALE_ID, 1f);
		this.dataWatcher.addObject(selected, (byte)0);
		
	}
	
	@Override
	public boolean interactFirst(EntityPlayer player) {
		if(!worldObj.isRemote && tile != null) {
			tile.onInventoryButtonPressed(getPlanetID());
		}
		return true;
	}
	
	public int getPlanetID() {
		return this.dataWatcher.getWatchableObjectInt(PLANET_ID);
	}
}
