package zmaster587.advancedRocketry.tile;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import zmaster587.advancedRocketry.api.EntityRocketBase;
import zmaster587.advancedRocketry.api.IInfrastructure;
import zmaster587.advancedRocketry.entity.EntityRocket;
import zmaster587.advancedRocketry.mission.IMission;
import zmaster587.libVulpes.block.multiblock.BlockHatch;
import zmaster587.libVulpes.item.ItemLinker;
import zmaster587.libVulpes.tile.multiblock.hatch.TileInventoryHatch;

public class TileRocketUnloader extends TileInventoryHatch implements IInfrastructure {
	EntityRocket rocket;
	
	public TileRocketUnloader(int size) {
		super(size);
	}
	
	@Override
	public String getModularInventoryName() {
		return "tile.loader.2.name";
	}
	
	@Override
	public void updateEntity() {
		super.updateEntity();

		//Move a stack of items
		if(rocket != null ) {
			List<TileEntity> tiles = rocket.storage.getUsableTiles();
			boolean foundStack = false;
			boolean rocketContainsNoItems = true;
			out:
				//Function returns if something can be moved
				for(TileEntity tile : tiles) {
					if(tile instanceof IInventory && !(tile instanceof TileGuidanceComputer)) {
						IInventory inv = ((IInventory)tile);
						for(int i = 0; i < inv.getSizeInventory(); i++) {
							if(inv.getStackInSlot(i) != null) {
								rocketContainsNoItems = false;
								//Loop though this inventory's slots and find a suitible one
								for(int j = 0; j < getSizeInventory(); j++) {
									if(getStackInSlot(j) == null) {
										inventory.setInventorySlotContents(j, inv.getStackInSlot(i));
										inv.setInventorySlotContents(i,null);
										break out;
									}
									else if(inv.getStackInSlot(i) != null && isItemValidForSlot(j, inv.getStackInSlot(i))) {
										ItemStack stack2 = inv.decrStackSize(i, getStackInSlot(j).getMaxStackSize() - getStackInSlot(j).stackSize);
										getStackInSlot(j).stackSize += stack2.stackSize;
										if(inv.getStackInSlot(i) == null)
											break out;
										foundStack = true;
									}
								}
							}
							if(foundStack)
								break out;
						}
					}
				}

			//Update redstone state
			((BlockHatch)AdvancedRocketryBlocks.blockLoader).setRedstoneState(worldObj, xCoord, yCoord, zCoord, rocketContainsNoItems);

		}
	}

	@Override
	public boolean onLinkStart(ItemStack item, TileEntity entity,
			EntityPlayer player, World world) {

		ItemLinker.setMasterCoords(item, this.xCoord, this.yCoord, this.zCoord);

		if(this.rocket != null) {
			this.rocket.unlinkInfrastructure(this);
			this.unlinkRocket();
		}

		if(player.worldObj.isRemote)
			Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage((new ChatComponentText("You program the linker with the fueling station at: " + this.xCoord + " " + this.yCoord + " " + this.zCoord)));
		return true;
	}

	@Override
	public boolean onLinkComplete(ItemStack item, TileEntity entity,
			EntityPlayer player, World world) {
		if(player.worldObj.isRemote)
			Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage((new ChatComponentText("This must be the first machine to link!")));
		return false;
	}

	@Override
	public void unlinkRocket() {
		rocket = null;
		((BlockHatch)AdvancedRocketryBlocks.blockLoader).setRedstoneState(worldObj, xCoord, yCoord, zCoord, false);
		//On unlink prevent the tile from ticking anymore
		//if(!worldObj.isRemote)
			//worldObj.loadedTileEntityList.remove(this);
	}

	@Override
	public boolean disconnectOnLiftOff() {
		return true;
	}

	@Override
	public boolean linkRocket(EntityRocketBase rocket) {
		//On linked allow the tile to tick
		//if(!worldObj.isRemote)
			//worldObj.loadedTileEntityList.add(this);
		this.rocket = (EntityRocket) rocket;
		return true;
	}

	@Override
	public boolean linkMission(IMission misson) {
		return false;
	}

	@Override
	public void unlinkMission() {

	}

	@Override
	public boolean canUpdate() {
		return true;
	}
	
	@Override
	public int getMaxLinkDistance() {
		return 32;
	}
	
	public boolean canRenderConnection() {
		return true;
	}
}
