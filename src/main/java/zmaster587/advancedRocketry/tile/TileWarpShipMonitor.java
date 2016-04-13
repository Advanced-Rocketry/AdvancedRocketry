package zmaster587.advancedRocketry.tile;

import io.netty.buffer.ByteBuf;

import java.util.LinkedList;
import java.util.List;

import cpw.mods.fml.relauncher.Side;
import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.api.stations.ISpaceObject;
import zmaster587.advancedRocketry.api.stations.SpaceObjectManager;
import zmaster587.advancedRocketry.inventory.GuiHandler.guiId;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.advancedRocketry.inventory.modules.IButtonInventory;
import zmaster587.advancedRocketry.inventory.modules.IModularInventory;
import zmaster587.advancedRocketry.inventory.modules.IProgressBar;
import zmaster587.advancedRocketry.inventory.modules.ISelectionNotify;
import zmaster587.advancedRocketry.inventory.modules.ModuleBase;
import zmaster587.advancedRocketry.inventory.modules.ModuleButton;
import zmaster587.advancedRocketry.inventory.modules.ModuleImage;
import zmaster587.advancedRocketry.inventory.modules.ModulePlanetSelector;
import zmaster587.advancedRocketry.inventory.modules.ModuleScaledImage;
import zmaster587.advancedRocketry.inventory.modules.ModuleText;
import zmaster587.advancedRocketry.network.PacketHandler;
import zmaster587.advancedRocketry.network.PacketMachine;
import zmaster587.advancedRocketry.stations.SpaceObject;
import zmaster587.advancedRocketry.util.ITilePlanetSystemSelectable;
import	zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.dimension.DimensionProperties;
import zmaster587.libVulpes.util.INetworkMachine;
import zmaster587.libVulpes.util.IconResource;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class TileWarpShipMonitor extends TileEntity implements IModularInventory, ISelectionNotify, INetworkMachine, IButtonInventory, IProgressBar {

	protected ModulePlanetSelector container;
	DimensionProperties dimCache;

	public TileWarpShipMonitor() {
	}

	@Override
	public List<ModuleBase> getModules(int ID) {
		List<ModuleBase> modules = new LinkedList<ModuleBase>();

		if(ID == guiId.MODULARNOINV.ordinal()) {
			ISpaceObject station = SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(this.xCoord, this.zCoord);
			ResourceLocation location;
			boolean hasAtmo = true;
			String planetName;

			if(station != null) {
				DimensionProperties properties = DimensionManager.getInstance().getDimensionProperties(station.getOrbitingPlanetId());
				location = properties.getPlanetIcon();
				hasAtmo = properties.hasAtmosphere();
				planetName = properties.getName();
			}
			else {
				location = DimensionManager.getInstance().getDimensionProperties(0).getPlanetIcon();
				planetName = DimensionManager.getInstance().getDimensionProperties(0).getName();
			}


			//Source planet
			int baseX = 10;
			int baseY = 20;
			int sizeX = 70;
			int sizeY = 70;

			modules.add(new ModuleScaledImage(baseX,baseY,sizeX,sizeY, TextureResources.starryBG));
			modules.add(new ModuleScaledImage(baseX + 10,baseY + 10,sizeX - 20, sizeY - 20, location));

			if(hasAtmo)
				modules.add(new ModuleScaledImage(baseX + 10,baseY + 10,sizeX - 20, sizeY - 20,0.4f, DimensionProperties.getAtmosphereResource()));

			modules.add(new ModuleText(baseX + 4, baseY + 4, "Orbiting:", 0xFFFFFF));
			modules.add(new ModuleText(baseX + 4, baseY + 16, planetName, 0xFFFFFF));

			//Border
			modules.add(new ModuleScaledImage(baseX - 3,baseY,3,sizeY, TextureResources.verticalBar));
			modules.add(new ModuleScaledImage(baseX + sizeX, baseY, -3,sizeY, TextureResources.verticalBar));
			modules.add(new ModuleScaledImage(baseX,baseY,70,3, TextureResources.horizontalBar));
			modules.add(new ModuleScaledImage(baseX,baseY + sizeY - 3,70,-3, TextureResources.horizontalBar));

			modules.add(new ModuleButton(baseX - 3, baseY + sizeY, 0, "Select Planet", this, TextureResources.buttonBuild, sizeX + 6, 16));

			//DEST planet
			baseX = 94;
			baseY = 20;
			sizeX = 70;
			sizeY = 70;
			ModuleButton warp = new ModuleButton(baseX - 3, baseY + sizeY,1, "Warp!", this ,TextureResources.buttonBuild, sizeX + 6, 16);
			//warp.setEnabled(dimCache != null);
			modules.add(warp);
			modules.add(new ModuleScaledImage(baseX,baseY,sizeX,sizeY, TextureResources.starryBG));
			
			if(dimCache != null) {

				hasAtmo = dimCache.hasAtmosphere();
				planetName = dimCache.getName();
				location = dimCache.getPlanetIcon();

				
				modules.add(new ModuleScaledImage(baseX + 10,baseY + 10,sizeX - 20, sizeY - 20, location));

				if(hasAtmo)
					modules.add(new ModuleScaledImage(baseX + 10,baseY + 10,sizeX - 20, sizeY - 20,0.4f, DimensionProperties.getAtmosphereResource()));

				modules.add(new ModuleText(baseX + 4, baseY + 4, "Dest:", 0xFFFFFF));
				modules.add(new ModuleText(baseX + 4, baseY + 16, planetName, 0xFFFFFF));


			}
			else {
				modules.add(new ModuleText(baseX + 4, baseY + 4, "Dest:", 0xFFFFFF));
				modules.add(new ModuleText(baseX + 4, baseY + 16, "None", 0xFFFFFF));
			}
			//Border
			modules.add(new ModuleScaledImage(baseX - 3,baseY,3,sizeY, TextureResources.verticalBar));
			modules.add(new ModuleScaledImage(baseX + sizeX, baseY, -3,sizeY, TextureResources.verticalBar));
			modules.add(new ModuleScaledImage(baseX,baseY,70,3, TextureResources.horizontalBar));
			modules.add(new ModuleScaledImage(baseX,baseY + sizeY - 3,70,-3, TextureResources.horizontalBar));
		}
		else if (ID == guiId.MODULARFULLSCREEN.ordinal()) {
			container = new ModulePlanetSelector(worldObj.provider.dimensionId, TextureResources.starryBG, this);
			container.setOffset(1000, 1000);
			modules.add(container);
		}
		return modules;
	}

	@Override
	public String getModularInventoryName() {
		return "shipMonitor";
	}

	@Override
	public boolean canInteractWithContainer(EntityPlayer entity) {
		return true;
	}

	@Override
	public void onInventoryButtonPressed(int buttonId) {
		if(buttonId == 0)
			PacketHandler.sendToServer(new PacketMachine(this, (byte)0));
		else if(buttonId == 1) {
			PacketHandler.sendToServer(new PacketMachine(this, (byte)2));
		}
	}

	@Override
	public void writeDataToNetwork(ByteBuf out, byte id) {
		if(id == 1)
			out.writeInt(container.getSelectedSystem());
	}

	@Override
	public void readDataFromNetwork(ByteBuf in, byte packetId,
			NBTTagCompound nbt) {
		if(packetId == 1)
			nbt.setInteger("id", in.readInt());
	}

	@Override
	public void useNetworkData(EntityPlayer player, Side side, byte id,
			NBTTagCompound nbt) {
		if(id == 0)
			player.openGui(AdvancedRocketry.instance, guiId.MODULARFULLSCREEN.ordinal(), worldObj, this.xCoord, this.yCoord, this.zCoord);
		else if(id == 1) {
			int dimId = nbt.getInteger("id");
			container.setSelectedSystem(dimId);
			selectSystem(dimId);

			//Update known planets
			markDirty();
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}
		else if(id == 2) {
			ISpaceObject station = SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(this.xCoord, this.zCoord);

			if(station != null) {
				SpaceObjectManager.getSpaceManager().moveStationToBody(station, station.getDestOrbitingBody(), 2000);
			}
		}
	}

	@Override
	public void onSelectionConfirmed(Object sender) {
		//Container Cannot be null at this time
		onSelected(sender);
	}

	@Override
	public void onSelected(Object sender) {

		selectSystem(container.getSelectedSystem());

		PacketHandler.sendToServer(new PacketMachine(this, (byte)1));
	}

	private void selectSystem(int id) {
		if(id == -1)
			dimCache = null;
		else {
			dimCache = DimensionManager.getInstance().getDimensionProperties(container.getSelectedSystem());

			ISpaceObject station = SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(this.xCoord, this.zCoord);
			if(station != null) {
				station.setDestOrbitingBody(id);
			}
		}

	}

	@Override
	public void onSystemFocusChanged(Object sender) {
		PacketHandler.sendToServer(new PacketMachine(this, (byte)1));
	}

	@Override
	public float getNormallizedProgress(int id) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setProgress(int id, int progress) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getProgress(int id) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getTotalProgress(int id) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setTotalProgress(int id, int progress) {
		// TODO Auto-generated method stub

	}
}
