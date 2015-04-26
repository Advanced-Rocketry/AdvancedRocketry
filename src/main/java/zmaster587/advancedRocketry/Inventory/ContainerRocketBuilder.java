package zmaster587.advancedRocketry.Inventory;

import com.sun.org.apache.xml.internal.security.Init;

import zmaster587.advancedRocketry.tile.TileRocketBuilder;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerRocketBuilder extends Container {

	private enum values {
		FUELRATE,
		WEIGHT,
		THRUST,
		FUEL,
		SCANTIME,
		TOTALSCANTIME
	}

	int[] prevValues;

	TileRocketBuilder tile;
	public ContainerRocketBuilder(InventoryPlayer playerInv, TileRocketBuilder tile) {

		this.tile = tile;

		// Player hotbar
		for (int j1 = 0; j1 < 9; j1++) {
			addSlotToContainer(new Slot(playerInv, j1, 8 + j1 * 18, 147));
		}
		
		

		prevValues = new int[values.values().length];
		prevValues[values.FUELRATE.ordinal()] = tile.getRocketStats().getFuelRate();
		prevValues[values.THRUST.ordinal()] = tile.getRocketStats().getThrust();
		prevValues[values.WEIGHT.ordinal()] = tile.getRocketStats().getWeight();
		prevValues[values.FUEL.ordinal()] = tile.getRocketStats().getFuel();
		prevValues[values.TOTALSCANTIME.ordinal()] = tile.getScanTotalBlocks();
		prevValues[values.SCANTIME.ordinal()] = tile.getScanTime();
	}

	@Override
	public ItemStack slotClick(int p_75144_1_, int p_75144_2_, int p_75144_3_,
			EntityPlayer p_75144_4_) {
		// TODO Auto-generated method stub
		return super.slotClick(p_75144_1_, p_75144_2_, p_75144_3_, p_75144_4_);
	}
	
	@Override
	public void addCraftingToCrafters(ICrafting crafter) {
		super.addCraftingToCrafters(crafter);
		
		crafter.sendProgressBarUpdate(this,  values.FUELRATE.ordinal(), tile.getRocketStats().getFuelRate());
		crafter.sendProgressBarUpdate(this,  values.WEIGHT.ordinal(), tile.getRocketStats().getWeight());
		crafter.sendProgressBarUpdate(this,  values.THRUST.ordinal(), tile.getRocketStats().getThrust());
		crafter.sendProgressBarUpdate(this,  values.FUEL.ordinal(), tile.getRocketStats().getFuel());
		crafter.sendProgressBarUpdate(this,  values.SCANTIME.ordinal(),  tile.getScanTime());
		crafter.sendProgressBarUpdate(this,  values.TOTALSCANTIME.ordinal(), tile.getScanTotalBlocks());
		
	}
	
	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();

		for (int j = 0; j < this.crafters.size(); ++j) {

			if(prevValues[values.FUELRATE.ordinal()] != tile.getRocketStats().getFuelRate()) {

				int value = prevValues[values.FUELRATE.ordinal()] = tile.getRocketStats().getFuelRate();
				((ICrafting)this.crafters.get(j)).sendProgressBarUpdate(this, values.FUELRATE.ordinal(), value);
			}
			
			if(prevValues[values.FUEL.ordinal()] != tile.getRocketStats().getFuel()) {

				int value = prevValues[values.FUEL.ordinal()] = tile.getRocketStats().getFuel();
				((ICrafting)this.crafters.get(j)).sendProgressBarUpdate(this, values.FUEL.ordinal(), value);
			}
			
			if(prevValues[values.THRUST.ordinal()] != tile.getRocketStats().getThrust()) {

				int value = prevValues[values.THRUST.ordinal()] = tile.getRocketStats().getThrust();
				((ICrafting)this.crafters.get(j)).sendProgressBarUpdate(this, values.THRUST.ordinal(), value);
			}
			
			if(prevValues[values.WEIGHT.ordinal()] != tile.getRocketStats().getWeight()) {

				int value = prevValues[values.WEIGHT.ordinal()] = tile.getRocketStats().getWeight();
				((ICrafting)this.crafters.get(j)).sendProgressBarUpdate(this, values.WEIGHT.ordinal(), value);
			}
			
			if(prevValues[values.SCANTIME.ordinal()] != tile.getScanTime()) {

				int value = prevValues[values.SCANTIME.ordinal()] = tile.getScanTime();
				((ICrafting)this.crafters.get(j)).sendProgressBarUpdate(this, values.SCANTIME.ordinal(), value);
			}
			
			if(prevValues[values.TOTALSCANTIME.ordinal()] != tile.getScanTime()) {

				int value = prevValues[values.TOTALSCANTIME.ordinal()] = tile.getScanTotalBlocks();
				((ICrafting)this.crafters.get(j)).sendProgressBarUpdate(this, values.TOTALSCANTIME.ordinal(), value);
			}
		}
	}
	
	@Override
	public void updateProgressBar(int id, int value) {
		super.updateProgressBar(id, value);
		
		if(id == values.FUELRATE.ordinal()) {
			tile.getRocketStats().setFuelRate(value);
		}
		
		if(id == values.FUEL.ordinal()) {
			tile.getRocketStats().setFuel(value);
		}
		
		if(id == values.THRUST.ordinal()) {
			tile.getRocketStats().setThrust(value);
		}
		
		if(id == values.WEIGHT.ordinal()) {
			tile.getRocketStats().setWeight(value);
		}
		
		if(id == values.SCANTIME.ordinal()) {
			tile.setScanTime(value);
		}
		
		if(id == values.TOTALSCANTIME.ordinal()) {
			tile.setScanTotalBlocks(value);
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return player.getDistance(tile.xCoord, tile.yCoord, tile.zCoord) < 64;
	}

}
