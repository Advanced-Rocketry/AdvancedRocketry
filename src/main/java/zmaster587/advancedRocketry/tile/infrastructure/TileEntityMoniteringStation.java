package zmaster587.advancedRocketry.tile.infrastructure;

import java.util.LinkedList;
import java.util.List;

import io.netty.buffer.ByteBuf;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import zmaster587.advancedRocketry.api.Configuration;
import zmaster587.advancedRocketry.api.EntityRocketBase;
import zmaster587.advancedRocketry.api.IInfrastructure;
import zmaster587.advancedRocketry.api.satellite.SatelliteBase;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.entity.EntityRocket;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.advancedRocketry.mission.IMission;
import zmaster587.libVulpes.client.util.IndicatorBarImage;
import zmaster587.libVulpes.client.util.ProgressBarImage;
import zmaster587.libVulpes.interfaces.ILinkableTile;
import zmaster587.libVulpes.inventory.modules.IButtonInventory;
import zmaster587.libVulpes.inventory.modules.IModularInventory;
import zmaster587.libVulpes.inventory.modules.IProgressBar;
import zmaster587.libVulpes.inventory.modules.ModuleBase;
import zmaster587.libVulpes.inventory.modules.ModuleButton;
import zmaster587.libVulpes.inventory.modules.ModuleProgress;
import zmaster587.libVulpes.inventory.modules.ModuleText;
import zmaster587.libVulpes.items.ItemLinker;
import zmaster587.libVulpes.network.PacketEntity;
import zmaster587.libVulpes.network.PacketHandler;
import zmaster587.libVulpes.network.PacketMachine;
import zmaster587.libVulpes.util.IAdjBlockUpdate;
import zmaster587.libVulpes.util.INetworkMachine;

public class TileEntityMoniteringStation extends TileEntity  implements IModularInventory, IAdjBlockUpdate, IInfrastructure, ILinkableTile, INetworkMachine, IButtonInventory, IProgressBar  {

	EntityRocketBase linkedRocket;
	IMission mission;
	ModuleText missionText;

	int rocketHeight;
	int velocity;
	int fuelLevel, maxFuelLevel;

	public TileEntityMoniteringStation() {
		mission = null;
		missionText = new ModuleText(20, 90, "Mission Progress: N/A", 0x2b2b2b);
	}

	@Override
	public void invalidate() {
		super.invalidate();

		if(linkedRocket != null) {
			linkedRocket.unlinkInfrastructure(this);
			unlinkRocket();
		}
		if(mission != null) {
			mission.unlinkInfrastructure(this);
			unlinkMission();
		}
	}

	@Override
	public void onAdjacentBlockUpdated() {
		if(!worldObj.isRemote && worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord) && linkedRocket != null) {
			linkedRocket.launch();
		}
	}

	@Override
	public int getMaxLinkDistance() {
		return 300000;
	}

	@Override
	public boolean onLinkStart(ItemStack item, TileEntity entity,
			EntityPlayer player, World world) {

		ItemLinker.setMasterCoords(item, this.xCoord, this.yCoord, this.zCoord);

		if(player.worldObj.isRemote)
			Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage((new ChatComponentText("You program the linker with the monitoring station at: " + this.xCoord + " " + this.yCoord + " " + this.zCoord)));
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
		linkedRocket = null;
	}

	@Override
	public boolean disconnectOnLiftOff() {
		return false;
	}

	@Override
	public boolean linkRocket(EntityRocketBase rocket) {
		this.linkedRocket = rocket;
		return true;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);

		if(nbt.hasKey("missionID")) {
			long id = nbt.getLong("missionID");
			int dimid = nbt.getInteger("missionDimId");

			SatelliteBase sat = DimensionManager.getInstance().getSatellite(id);

			if(sat instanceof IMission)
				mission = (IMission)sat;
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);

		if(mission != null) {
			nbt.setLong("missionID", mission.getMissionId());
			nbt.setInteger("missionDimId", mission.getOriginatingDimention());
		}
	}

	@Override
	public void writeDataToNetwork(ByteBuf out, byte id) {
		if(id == 1)
			out.writeLong(mission == null ? -1 : mission.getMissionId());
	}

	@Override
	public void readDataFromNetwork(ByteBuf in, byte packetId,
			NBTTagCompound nbt) {
		if(packetId == 1) {
			nbt.setLong("id", in.readLong());
		}
	}

	@Override
	public void useNetworkData(EntityPlayer player, Side side, byte id,
			NBTTagCompound nbt) {
		if(id == 1) {
			long idNum = nbt.getLong("id");
			if(idNum == -1) {
				mission = null;
				setMissionText();
			}
			else {
				SatelliteBase base = DimensionManager.getInstance().getSatellite(idNum);

				if(base instanceof IMission) {
					mission = (IMission)base;
					setMissionText();
				}
			}
		}
		if(id == 100) {
			if(linkedRocket != null)
				linkedRocket.launch();
		}
	}

	@Override
	public List<ModuleBase> getModules(int ID, EntityPlayer player) {

		LinkedList<ModuleBase> modules = new LinkedList<ModuleBase>();

		modules.add(new ModuleButton(20, 40, 0, "Launch!", this,  zmaster587.libVulpes.inventory.TextureResources.buttonBuild));
		modules.add(new ModuleProgress(98, 4, 0, new IndicatorBarImage(2, 7, 12, 81, 17, 0, 6, 6, 1, 0, ForgeDirection.UP, TextureResources.rocketHud), this));
		modules.add(new ModuleProgress(120, 14, 1, new IndicatorBarImage(2, 95, 12, 71, 17, 0, 6, 6, 1, 0, ForgeDirection.UP, TextureResources.rocketHud), this));
		modules.add(new ModuleProgress(142, 14, 2, new ProgressBarImage(2, 173, 12, 71, 17, 6, 3, 69, 1, 1, ForgeDirection.UP, TextureResources.rocketHud), this));

		setMissionText();

		modules.add(missionText);
		modules.add(new ModuleProgress(30, 110, 3, TextureResources.progressToMission, this));
		modules.add(new ModuleProgress(30, 120, 4, TextureResources.workMission, this));
		modules.add(new ModuleProgress(30, 130, 5, TextureResources.progressFromMission, this));

		if(!worldObj.isRemote) {
			PacketHandler.sendToPlayer(new PacketMachine(this, (byte)1), player);
		}

		return modules;
	}

	private void setMissionText() {
		if(mission != null) {
			int time = mission.getTimeRemainingInSeconds();
			int seconds = time % 60;
			int minutes = (time/60) % 60;
			int hours = time/3600;
			
			missionText.setText(((SatelliteBase)mission).getName() + " Progress: " + String.format("\n%02dhr:%02dm:%02ds", hours, minutes, seconds));
		}
		else
			missionText.setText("Mission Progess: N/A");
	}
	
	@Override
	public void onInventoryButtonPressed(int buttonId) {
		PacketHandler.sendToServer(new PacketMachine(this, (byte) (buttonId + 100)) );
	}

	@Override
	public String getModularInventoryName() {
		return "container.monitoringstation";
	}

	@Override
	public float getNormallizedProgress(int id) {
		if(id == 1) {
			return Math.max(Math.min(0.5f + (getProgress(id)/(float)getTotalProgress(id)), 1), 0f);
		}
		else if(id == 3) {
			if(mission == null)
				return 0f;
			return (float) Math.min(3f*mission.getProgress(this.worldObj), 1f);
		}
		else if(id == 4) {
			if(mission == null)
				return 0f;
			return (float) Math.min(Math.max( 3f*(mission.getProgress(this.worldObj) - 0.333f), 0f), 1f);
		}
		else if(id == 5) {
			if(mission == null)
				return 0f;
			return (float) Math.min(Math.max( 3f*(mission.getProgress(this.worldObj) - 0.666f), 0f), 1f);
		}
		
		//keep text updated
		if(worldObj.isRemote && mission != null)
			setMissionText();
		
		return getProgress(id)/(float)getTotalProgress(id);
	}

	@Override
	public void setProgress(int id, int progress) {
		if(id == 0)
			rocketHeight = progress;
		else if(id == 1)
			velocity = progress;
		else if(id == 2)
			fuelLevel = progress;
	}

	@Override
	public int getProgress(int id) {
		//Try to keep client synced with server, this also allows us to put the monitor on a different world altogether
		if(worldObj.isRemote)
			if(mission != null && id == 0)
				return getTotalProgress(id);
			else if(id == 0)
				return rocketHeight;
			else if(id == 1)
				return velocity;
			else if(id == 2)
				return fuelLevel;

		if(linkedRocket == null)
			return 0;
		if(id == 0)
			return (int)linkedRocket.posY;
		else if(id == 1)
			return (int)(linkedRocket.motionY*100);
		else if (id == 2)
			return (int)(linkedRocket.getFuelAmount());

		return 0;
	}

	@Override
	public int getTotalProgress(int id) {
		if(id == 0)
			return Configuration.orbit;
		else if(id == 1)
			return 200;
		else if(id == 2)
			if(worldObj.isRemote)
				return maxFuelLevel;
			else
				if(linkedRocket == null)
					return 0;
				else
					return linkedRocket.getFuelCapacity();

		return 1;
	}

	@Override
	public void setTotalProgress(int id, int progress) {
		//Should only become an issue if configs are desynced or fuel
		if(id == 2)
			maxFuelLevel = progress;
	}

	@Override
	public boolean canInteractWithContainer(EntityPlayer entity) {
		return true;
	}

	@Override
	public boolean linkMission(IMission misson) {
		this.mission = misson;
		PacketHandler.sendToNearby(new PacketMachine(this, (byte)1), worldObj.provider.dimensionId, xCoord, yCoord, zCoord, 16);
		return true;
	}

	@Override
	public void unlinkMission() {
		mission = null;
		setMissionText();
		PacketHandler.sendToNearby(new PacketMachine(this, (byte)1), worldObj.provider.dimensionId, xCoord, yCoord, zCoord, 16);
	}

	@Override
	public boolean canRenderConnection() {
		return false;
	}
}
