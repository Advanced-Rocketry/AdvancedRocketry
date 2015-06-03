package zmaster587.advancedRocketry.Inventory;

import zmaster587.advancedRocketry.api.FuelRegistry.FuelType;
import zmaster587.advancedRocketry.tile.TileRocketBuilder;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerRocketBuilder extends ContainerPowered {

	private enum values {
		FUELRATE,
		WEIGHT,
		THRUST,
		FUEL,
		SCANTIME,
		TOTALSCANTIME,
		STATUS;

		int id;
		public void setId( int id) {this.id = id; }
		public int getId() { return id; }
	}

	int[] prevValues;
	TileRocketBuilder tile;
	
	public ContainerRocketBuilder(InventoryPlayer playerInv, TileRocketBuilder tile) {
		super(playerInv, tile);
		this.tile = (TileRocketBuilder)super.tile;
		
		//Create ids
		for(values val : values.values())
			val.setId(nextSlot());
			
		// Player hotbar
		for (int j1 = 0; j1 < 9; j1++) {
			addSlotToContainer(new Slot(playerInv, j1, 8 + j1 * 18, 147));
		}
		
		

		prevValues = new int[values.values().length];
		prevValues[values.FUELRATE.getId()] = tile.getRocketStats().getFuelRate(FuelType.LIQUID);
		prevValues[values.THRUST.getId()] = tile.getRocketStats().getThrust();
		prevValues[values.WEIGHT.getId()] = tile.getRocketStats().getWeight();
		prevValues[values.FUEL.getId()] = tile.getRocketStats().getFuelCapacity(FuelType.LIQUID);
		prevValues[values.TOTALSCANTIME.getId()] = tile.getScanTotalBlocks();
		prevValues[values.SCANTIME.getId()] = tile.getScanTime();
		prevValues[values.STATUS.getId()] = tile.getStatus().ordinal();
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
		
		crafter.sendProgressBarUpdate(this,  values.FUELRATE.getId(), tile.getRocketStats().getFuelRate(FuelType.LIQUID));
		crafter.sendProgressBarUpdate(this,  values.WEIGHT.getId(), tile.getRocketStats().getWeight());
		crafter.sendProgressBarUpdate(this,  values.THRUST.getId(), tile.getRocketStats().getThrust());
		crafter.sendProgressBarUpdate(this,  values.FUEL.getId(), tile.getRocketStats().getFuelCapacity(FuelType.LIQUID));
		crafter.sendProgressBarUpdate(this,  values.SCANTIME.getId(),  tile.getScanTime());
		crafter.sendProgressBarUpdate(this,  values.TOTALSCANTIME.getId(), tile.getScanTotalBlocks());
		crafter.sendProgressBarUpdate(this,  values.STATUS.getId(), tile.getStatus().ordinal());
	}
	
	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();

		for (int j = 0; j < this.crafters.size(); ++j) {

			if(prevValues[values.FUELRATE.getId()] != tile.getRocketStats().getFuelRate(FuelType.LIQUID)) {

				int value = prevValues[values.FUELRATE.getId()] = tile.getRocketStats().getFuelRate(FuelType.LIQUID);
				((ICrafting)this.crafters.get(j)).sendProgressBarUpdate(this, values.FUELRATE.getId(), value);
			}
			
			if(prevValues[values.FUEL.getId()] != tile.getRocketStats().getFuelCapacity(FuelType.LIQUID)) {

				int value = prevValues[values.FUEL.getId()] = tile.getRocketStats().getFuelCapacity(FuelType.LIQUID);
				((ICrafting)this.crafters.get(j)).sendProgressBarUpdate(this, values.FUEL.getId(), value);
			}
			
			if(prevValues[values.THRUST.getId()] != tile.getRocketStats().getThrust()) {

				int value = prevValues[values.THRUST.getId()] = tile.getRocketStats().getThrust();
				((ICrafting)this.crafters.get(j)).sendProgressBarUpdate(this, values.THRUST.getId(), value);
			}
			
			if(prevValues[values.WEIGHT.getId()] != tile.getRocketStats().getWeight()) {

				int value = prevValues[values.WEIGHT.getId()] = tile.getRocketStats().getWeight();
				((ICrafting)this.crafters.get(j)).sendProgressBarUpdate(this, values.WEIGHT.getId(), value);
			}
			
			if(prevValues[values.SCANTIME.getId()] != tile.getScanTime()) {

				int value = prevValues[values.SCANTIME.getId()] = tile.getScanTime();
				((ICrafting)this.crafters.get(j)).sendProgressBarUpdate(this, values.SCANTIME.getId(), value);
			}
			
			if(prevValues[values.TOTALSCANTIME.getId()] != tile.getScanTime()) {

				int value = prevValues[values.TOTALSCANTIME.getId()] = tile.getScanTotalBlocks();
				((ICrafting)this.crafters.get(j)).sendProgressBarUpdate(this, values.TOTALSCANTIME.getId(), value);
			}
			
			if(prevValues[values.STATUS.getId()] != tile.getStatus().ordinal()) {

				int value = prevValues[values.STATUS.getId()] = tile.getStatus().ordinal();
				((ICrafting)this.crafters.get(j)).sendProgressBarUpdate(this, values.STATUS.getId(), value);
			}
		}
	}
	
	@Override
	public void updateProgressBar(int id, int value) {
		super.updateProgressBar(id, value);
		
		if(id == values.FUELRATE.getId()) {
			tile.getRocketStats().setFuelRate(FuelType.LIQUID,value);
		}
		
		if(id == values.FUEL.getId()) {
			tile.getRocketStats().setFuelCapacity(FuelType.LIQUID,value);
		}
		
		if(id == values.THRUST.getId()) {
			tile.getRocketStats().setThrust(value);
		}
		
		if(id == values.WEIGHT.getId()) {
			tile.getRocketStats().setWeight(value);
		}
		
		if(id == values.SCANTIME.getId()) {
			tile.setScanTime(value);
		}
		
		if(id == values.TOTALSCANTIME.getId()) {
			tile.setScanTotalBlocks(value);
		}
		if(id == values.STATUS.getId()) {
			tile.setStatus(value);
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return player.getDistance(tile.xCoord, tile.yCoord, tile.zCoord) < 64;
	}

}
