package zmaster587.advancedRocketry.tile.infrastructure;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import zmaster587.advancedRocketry.api.EntityRocketBase;
import zmaster587.advancedRocketry.api.IInfrastructure;
import zmaster587.advancedRocketry.entity.EntityRocket;
import zmaster587.advancedRocketry.api.IMission;
import zmaster587.advancedRocketry.tile.TileGuidanceComputer;
import zmaster587.advancedRocketry.tile.TileRocketBuilder;
import zmaster587.libVulpes.block.multiblock.BlockHatch;
import zmaster587.libVulpes.items.ItemLinker;
import zmaster587.libVulpes.tile.multiblock.hatch.TileInventoryHatch;

public class TileRocketLoader extends TileInventoryHatch implements IInfrastructure, ITickable {
	
	EntityRocket rocket;
	
	public TileRocketLoader() {}
	
	public TileRocketLoader(int size) {
		super(size);
	}
	
	@Override
	public void invalidate() {
		super.invalidate();
		if(getMasterBlock() instanceof TileRocketBuilder)
			((TileRocketBuilder)getMasterBlock()).removeConnectedInfrastructure(this);
	}
	
	@Override
	public String getModularInventoryName() {
		return "tile.loader.3.name";
	}

	@Override
	public void update() {

		//Move a stack of items
		if(rocket != null ) {
			List<TileEntity> tiles = rocket.storage.getInventoryTiles();
			boolean foundStack = false;
			boolean rocketContainsItems = false;
			out:
				//Function returns if something can be moved
				for(TileEntity tile : tiles) {
					if(tile instanceof IInventory && !(tile instanceof TileGuidanceComputer)) {
						IInventory inv = ((IInventory)tile);

						for(int i = 0; i < inv.getSizeInventory(); i++) {
							if(inv.getStackInSlot(i) == null)
								rocketContainsItems = true;
							
							//Loop though this inventory's slots and find a suitible one
							for(int j = 0; j < getSizeInventory(); j++) {
								if(inv.getStackInSlot(i) == null && inventory.getStackInSlot(j) != null) {
									inv.setInventorySlotContents(i, inventory.getStackInSlot(j));
									inventory.setInventorySlotContents(j,null);
									rocketContainsItems = true;
									break out;
								}
								else if(getStackInSlot(j) != null && inv.isItemValidForSlot(i, getStackInSlot(j)) && inv.getStackInSlot(i).getMaxStackSize() != inv.getStackInSlot(i).stackSize ) {
									ItemStack stack2 = inventory.decrStackSize(j, inv.getStackInSlot(i).getMaxStackSize() - inv.getStackInSlot(i).stackSize);
									inv.getStackInSlot(i).stackSize += stack2.stackSize;
									rocketContainsItems = true;
									
									if(inventory.getStackInSlot(j) == null)
										break out;
									
									foundStack = true;
								}
							}
							if(foundStack)
								break out;
						}
					}
				}

			//Update redstone state
			((BlockHatch)AdvancedRocketryBlocks.blockLoader).setRedstoneState(worldObj, worldObj.getBlockState(pos), pos, !rocketContainsItems);

		}
	}

	@Override
	public boolean onLinkStart(ItemStack item, TileEntity entity,
			EntityPlayer player, World world) {

		ItemLinker.setMasterCoords(item, this.pos);

		if(this.rocket != null) {
			this.rocket.unlinkInfrastructure(this);
			this.unlinkRocket();
		}

		if(player.worldObj.isRemote)
			Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage((new TextComponentString("You program the linker with the fueling station at: " + this.getPos().getX() + " " + this.getPos().getY() + " " + this.getPos().getZ())));
		return true;
	}

	@Override
	public boolean onLinkComplete(ItemStack item, TileEntity entity,
			EntityPlayer player, World world) {
		if(player.worldObj.isRemote)
			Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage((new TextComponentString("This must be the first machine to link!")));
		return false;
	}

	@Override
	public void unlinkRocket() {
		rocket = null;
		((BlockHatch)AdvancedRocketryBlocks.blockLoader).setRedstoneState(worldObj, worldObj.getBlockState(pos), pos, false);
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
	public boolean canUpdate() {
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
	public int getMaxLinkDistance() {
		return 32;
	}
	
	public boolean canRenderConnection() {
		return true;
	}
}
