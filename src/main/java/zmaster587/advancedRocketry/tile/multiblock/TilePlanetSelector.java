package zmaster587.advancedRocketry.tile.multiblock;

import io.netty.buffer.ByteBuf;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import cpw.mods.fml.relauncher.Side;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import zmaster587.advancedRocketry.Inventory.TextureResources;
import zmaster587.advancedRocketry.Inventory.modules.IModularInventory;
import zmaster587.advancedRocketry.Inventory.modules.IProgressBar;
import zmaster587.advancedRocketry.Inventory.modules.ISelectionNotify;
import zmaster587.advancedRocketry.Inventory.modules.ModuleBase;
import zmaster587.advancedRocketry.Inventory.modules.ModulePlanetSelector;
import zmaster587.advancedRocketry.item.ItemPlanetIdentificationChip;
import zmaster587.advancedRocketry.network.PacketHandler;
import zmaster587.advancedRocketry.network.PacketMachine;
import zmaster587.advancedRocketry.util.DataStorage;
import zmaster587.advancedRocketry.util.DataStorage.DataType;
import zmaster587.advancedRocketry.util.ITilePlanetSystemSelectable;
import zmaster587.advancedRocketry.world.DimensionManager;
import zmaster587.advancedRocketry.world.DimensionProperties;
import zmaster587.libVulpes.tile.TilePointer;
import zmaster587.libVulpes.util.INetworkMachine;

public class TilePlanetSelector extends TilePointer implements ISelectionNotify, IModularInventory, IProgressBar, INetworkMachine {

	ModulePlanetSelector container;
	public static final int certaintyDataValue = 5000;
	DimensionProperties dimCache;

	int[] cachedProgressValues;

	public TilePlanetSelector() {
		cachedProgressValues = new int[] { -1, -1, -1};
	}

	@Override
	public void onSelectionConfirmed(Object sender) {

		//Container Cannot be null at this time
		TileEntity tile = getMasterBlock();
		if(tile instanceof ITilePlanetSystemSelectable) {
			((ITilePlanetSystemSelectable)tile).setSelectedPlanetId(container.getSelectedSystem());
		}
		onSelected(sender);
	}

	@Override
	public void onSelected(Object sender) {

		selectSystem(container.getSelectedSystem());

		PacketHandler.sendToServer(new PacketMachine(this, (byte)0));
	}

	private void selectSystem(int id) {
		if(id == -1)
			dimCache = null;
		else
			dimCache = DimensionManager.getInstance().getDimensionProperties(container.getSelectedSystem());
	}

	@Override
	public List<ModuleBase> getModules() {

		List<ModuleBase> modules = new LinkedList<ModuleBase>();

		container = new ModulePlanetSelector(worldObj.provider.dimensionId, TextureResources.starryBG, this);
		container.setOffset(1000, 1000);
		modules.add(container);

		//Transfer discovery values
		if(!worldObj.isRemote) {
			markDirty();
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}

		return modules;
	}

	@Override
	public String getModularInventoryName() {
		return "";
	}

	@Override
	public boolean canInteractWithContainer(EntityPlayer entity) {
		return true;
	}

	@Override
	public float getNormallizedProgress(int id) {
		return 0;
	}

	@Override
	public void setProgress(int id, int progress) {
		cachedProgressValues[id] = progress;
	}

	@Override
	public int getProgress(int id) {

		if(!worldObj.isRemote) {
			if(getMasterBlock() != null) {

				ItemStack stack = ((ITilePlanetSystemSelectable)getMasterBlock()).getChipWithId(container.getSelectedSystem());

				if(stack != null) {

					DataType data;
					if(id == 0)
						data = DataType.ATMOSPHEREDENSITY;
					else if(id == 1)
						data = DataType.DISTANCE;
					else //if(id == 2)
						data = DataType.MASS;


					int dataAmt = ((ItemPlanetIdentificationChip)stack.getItem()).getData(stack, data);

					if(dataAmt != 0)
						return (int)(certaintyDataValue/(float)dataAmt);
				}
			}
		}
		else {
			return cachedProgressValues[id];
		}

		return 400;
	}

	@Override
	public int getTotalProgress(int id) {
		if(dimCache == null)
			return 50;
		if(id == 0)
			return dimCache.atmosphereDensity/2;
		else if(id == 1)
			return dimCache.orbitalDist/2;
		else //if(id == 2)
			return (int) (dimCache.gravitationalMultiplier*50);
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound comp = new NBTTagCompound();

		writeToNBTHelper(comp);
		writeAdditionalNBT(comp);
		return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 0, comp);
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {

		super.onDataPacket(net, pkt);
		readAdditionalNBT(pkt.func_148857_g());
	}

	public void writeAdditionalNBT(NBTTagCompound nbt) {
		if(getMasterBlock() != null) {
			List<Integer> list = ((ITilePlanetSystemSelectable)getMasterBlock()).getVisiblePlanets();

			Integer[] intList = new Integer[list.size()];

			nbt.setIntArray("visiblePlanets",ArrayUtils.toPrimitive(list.toArray(intList)));
		}

	}

	public void readAdditionalNBT(NBTTagCompound nbt) {
		if(container != null) {
			int[] intArray = nbt.getIntArray("visiblePlanets");
			for(int id : intArray)
				container.setPlanetAsKnown(id);
		}
	}

	@Override
	public void setTotalProgress(int id, int progress) {

	}

	@Override
	public void writeDataToNetwork(ByteBuf out, byte id) {
		if(id == 0)
			out.writeInt(container.getSelectedSystem());
	}

	@Override
	public void readDataFromNetwork(ByteBuf in, byte packetId,
			NBTTagCompound nbt) {
		if(packetId == 0)
			nbt.setInteger("id", in.readInt());
	}

	@Override
	public void useNetworkData(EntityPlayer player, Side side, byte id,
			NBTTagCompound nbt) {
		if(id == 0) {
			int dimId = nbt.getInteger("id");
			container.setSelectedSystem(dimId);
			selectSystem(dimId);

			//Update known planets
			markDirty();
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}
	}

	@Override
	public void onSystemFocusChanged(Object sender) {
		PacketHandler.sendToServer(new PacketMachine(this, (byte)0));
	}
}
