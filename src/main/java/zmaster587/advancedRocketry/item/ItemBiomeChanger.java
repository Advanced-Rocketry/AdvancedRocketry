package zmaster587.advancedRocketry.item;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.biome.BiomeGenBase;
import zmaster587.libVulpes.inventory.TextureResources;
import zmaster587.libVulpes.inventory.modules.IButtonInventory;
import zmaster587.libVulpes.inventory.modules.IModularInventory;
import zmaster587.libVulpes.inventory.modules.ModuleBase;
import zmaster587.libVulpes.inventory.modules.ModuleButton;
import zmaster587.libVulpes.inventory.modules.ModuleContainerPan;

public class ItemBiomeChanger extends Item implements IModularInventory, IButtonInventory {

	@Override
	public List<ModuleBase> getModules(int id, EntityPlayer player) {
		List<ModuleBase> list = new LinkedList<ModuleBase>();
		
		List<ModuleBase> list2 = new LinkedList<ModuleBase>();
		
		for(int i = 0; i < BiomeGenBase.getBiomeGenArray().length; i++) {
			BiomeGenBase biome = BiomeGenBase.getBiomeGenArray()[i];
			if(biome != null)
				list2.add(new ModuleButton(32, 16 + 12*(i++), i, biome.biomeName, this, TextureResources.buttonBuild));
			
		}
		//Relying on a bug, is this safe?
		ModuleContainerPan pan = new ModuleContainerPan(0, 16, list2, new LinkedList<ModuleBase>(), null, 128, 128, 0, -64, 0, 1000);
	
		list.add(pan);
		
		return list;
	}
	
	private int getBiomeId(ItemStack stack) {
		if(stack.hasTagCompound())
			return stack.getTagCompound().getInteger("biome");
		else
			return -1;
	}
	
	private int getRadius(ItemStack stack) {
		if(stack.hasTagCompound())
			return stack.getTagCompound().getInteger("radius");
		else
			return -1;
	}

	@Override
	public String getModularInventoryName() {
		return null;
	}

	@Override
	public boolean canInteractWithContainer(EntityPlayer entity) {
		return false;
	}

	@Override
	public void onInventoryButtonPressed(int buttonId) {
		// TODO Auto-generated method stub
		
	}

}
