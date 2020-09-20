package zmaster587.advancedRocketry.tile;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.ITextComponent;
import zmaster587.advancedRocketry.api.AdvancedRocketryTileEntityType;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.libVulpes.api.LibvulpesGuiRegistry;
import zmaster587.libVulpes.inventory.ContainerModular;
import zmaster587.libVulpes.inventory.GuiHandler;
import zmaster587.libVulpes.inventory.GuiHandler.guiId;
import zmaster587.libVulpes.inventory.modules.IModularInventory;
import zmaster587.libVulpes.inventory.modules.ModuleBase;
import zmaster587.libVulpes.inventory.modules.ModuleScaledImage;
import zmaster587.libVulpes.inventory.modules.ModuleText;

import java.util.LinkedList;
import java.util.List;

public class TileMachineChipInfo extends TileEntity implements IModularInventory {

	ModuleText infoText;
	
	public TileMachineChipInfo() {
		super(AdvancedRocketryTileEntityType.TILE_CHIP_MACHINE);
		infoText = new ModuleText(16, 16, "", 0x2f2f2f);
	}
	
	private void updateText() {
		//infoText
	}
	
	@Override
	public List<ModuleBase> getModules(int id, PlayerEntity player) {
		List<ModuleBase> modules = new LinkedList<ModuleBase>();
		if(world.isRemote) {
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
	public boolean canInteractWithContainer(PlayerEntity entity) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ITextComponent getDisplayName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Container createMenu(int id, PlayerInventory inv, PlayerEntity player) {
		return new ContainerModular(LibvulpesGuiRegistry.CONTAINER_MODULAR_TILE, id, player, getModules(getModularInvType().ordinal(), player), this, getModularInvType());
	}

	@Override
	public GuiHandler.guiId getModularInvType() {
		return guiId.MODULAR;
	}

}
