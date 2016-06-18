package zmaster587.advancedRocketry.tile;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.advancedRocketry.inventory.modules.IModularInventory;
import zmaster587.advancedRocketry.inventory.modules.ModuleBase;
import zmaster587.advancedRocketry.inventory.modules.ModuleScaledImage;
import zmaster587.advancedRocketry.inventory.modules.ModuleText;

public class TileMachineChipInfo extends TileEntity implements IModularInventory {

	ModuleText infoText;
	
	public TileMachineChipInfo() {
		infoText = new ModuleText(16, 16, "", 0x2f2f2f);
	}
	
	private void updateText() {
		//infoText
	}
	
	@Override
	public List<ModuleBase> getModules(int id, EntityPlayer player) {
		List<ModuleBase> modules = new LinkedList<ModuleBase>();
		if(worldObj.isRemote) {
			//Source planet
			int baseX = 10;
			int baseY = 20;
			int sizeX = 70;
			int sizeY = 70;
			
			//Border
			modules.add(new ModuleScaledImage(baseX - 3,baseY,3,sizeY, TextureResources.verticalBar));
			modules.add(new ModuleScaledImage(baseX + sizeX, baseY, -3,sizeY, TextureResources.verticalBar));
			modules.add(new ModuleScaledImage(baseX,baseY,70,3, TextureResources.horizontalBar));
			modules.add(new ModuleScaledImage(baseX,baseY + sizeY - 3,70,-3, TextureResources.horizontalBar));
		}
		
		//modules.add(new ModuleSlotArray(16,32, ,0,1));
		modules.add(infoText);
		return modules;
	}

	@Override
	public String getModularInventoryName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean canInteractWithContainer(EntityPlayer entity) {
		// TODO Auto-generated method stub
		return false;
	}

}
