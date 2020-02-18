package zmaster587.advancedRocketry.tile.multiblock;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.commons.lang3.ArrayUtils;

import zmaster587.advancedRocketry.api.Constants;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.dimension.DimensionProperties;
import zmaster587.advancedRocketry.inventory.modules.ModulePlanetSelector;
import zmaster587.advancedRocketry.util.ITilePlanetSystemSelectable;
import zmaster587.libVulpes.inventory.TextureResources;
import zmaster587.libVulpes.inventory.modules.IModularInventory;
import zmaster587.libVulpes.inventory.modules.IProgressBar;
import zmaster587.libVulpes.inventory.modules.ISelectionNotify;
import zmaster587.libVulpes.inventory.modules.ModuleBase;
import zmaster587.libVulpes.network.PacketHandler;
import zmaster587.libVulpes.network.PacketMachine;
import zmaster587.libVulpes.tile.TilePointer;
import zmaster587.libVulpes.util.INetworkMachine;

import java.util.LinkedList;
import java.util.List;

public class TilePlanetSelector extends TilePointer implements ISelectionNotify, IModularInventory, IProgressBar, INetworkMachine {

	protected ModulePlanetSelector container;
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
		if(id == Constants.INVALID_PLANET)
			dimCache = null;
		else
			dimCache = DimensionManager.getInstance().getDimensionProperties(container.getSelectedSystem());
	}

	@Override
	public List<ModuleBase> getModules(int ID, EntityPlayer player) {

		List<ModuleBase> modules = new LinkedList<ModuleBase>();

                DimensionProperties props = DimensionManager.getEffectiveDimId(player.world, player.getPosition());
		container = new ModulePlanetSelector((props != null ? props.getStarId() : 0), TextureResources.starryBG, this, true);
		container.setOffset(1000, 1000);
		modules.add(container);

		//Transfer discovery values
		if(!world.isRemote) {
			markDirty();
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

		if(!world.isRemote) {
			return 25; /*
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
			}*/
		}
		else {
			return cachedProgressValues[id];
		}

		//return 400;
	}

	@Override
	public int getTotalProgress(int id) {
		if(dimCache == null)
			return 50;
		if(id == 0)
			return dimCache.getAtmosphereDensity()/2;
		else if(id == 1)
			return dimCache.orbitalDist/2;
		else //if(id == 2)
			return (int) (dimCache.gravitationalMultiplier*50);
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		NBTTagCompound comp = new NBTTagCompound();

		writeToNBTHelper(comp);
		writeAdditionalNBT(comp);
		return new SPacketUpdateTileEntity(pos, 0, comp);
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {

		super.onDataPacket(net, pkt);
		readAdditionalNBT(pkt.getNbtCompound());
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
		}
	}

	@Override
	public void onSystemFocusChanged(Object sender) {
		PacketHandler.sendToServer(new PacketMachine(this, (byte)0));
	}
}
