package zmaster587.advancedRocketry.tile.infrastructure;

import java.util.LinkedList;
import java.util.List;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import zmaster587.advancedRocketry.api.Configuration;
import zmaster587.advancedRocketry.api.EntityRocketBase;
import zmaster587.advancedRocketry.api.IInfrastructure;
import zmaster587.advancedRocketry.api.satellite.SatelliteBase;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.entity.EntityRocket;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.advancedRocketry.api.IMission;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.client.util.IndicatorBarImage;
import zmaster587.libVulpes.client.util.ProgressBarImage;
import zmaster587.libVulpes.interfaces.ILinkableTile;
import zmaster587.libVulpes.inventory.modules.IButtonInventory;
import zmaster587.libVulpes.inventory.modules.IModularInventory;
import zmaster587.libVulpes.inventory.modules.IProgressBar;
import zmaster587.libVulpes.inventory.modules.ModuleBase;
import zmaster587.libVulpes.inventory.modules.ModuleButton;
import zmaster587.libVulpes.inventory.modules.ModuleProgress;
import zmaster587.libVulpes.inventory.modules.ModuleRedstoneOutputButton;
import zmaster587.libVulpes.inventory.modules.ModuleText;
import zmaster587.libVulpes.items.ItemLinker;
import zmaster587.libVulpes.network.PacketEntity;
import zmaster587.libVulpes.network.PacketHandler;
import zmaster587.libVulpes.network.PacketMachine;
import zmaster587.libVulpes.util.IAdjBlockUpdate;
import zmaster587.libVulpes.util.INetworkMachine;
import zmaster587.libVulpes.util.ZUtils.RedstoneState;

public class TileEntityMoniteringStation extends TileEntity  implements IModularInventory, IAdjBlockUpdate, IInfrastructure, ILinkableTile, INetworkMachine, IButtonInventory, IProgressBar  {

	EntityRocketBase linkedRocket;
	IMission mission;
	ModuleText missionText;
	RedstoneState state;
	ModuleRedstoneOutputButton redstoneControl;

	int rocketHeight;
	int velocity;
	int fuelLevel, maxFuelLevel;

	public TileEntityMoniteringStation() {
		mission = null;
		missionText = new ModuleText(20, 90, LibVulpes.proxy.getLocalizedString("msg.monitoringStation.missionProgressNA"), 0x2b2b2b);
		redstoneControl = new ModuleRedstoneOutputButton(174, 4, -1, "", this);
		state = RedstoneState.ON;
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
	
	public boolean getEquivilentPower() {
		if(state == RedstoneState.OFF)
			return false;

		boolean state2 = world.isBlockIndirectlyGettingPowered(pos) > 0;

		if(state == RedstoneState.INVERTED)
			state2 = !state2;
		return state2;
	}

	@Override
	public void onAdjacentBlockUpdated() {
		if(!world.isRemote && getEquivilentPower() && linkedRocket != null) {
			linkedRocket.prepareLaunch();
		}
	}

	@Override
	public int getMaxLinkDistance() {
		return 300000;
	}

	@Override
	public boolean onLinkStart(ItemStack item, TileEntity entity,
			EntityPlayer player, World world) {

		ItemLinker.setMasterCoords(item, getPos());

		if(player.world.isRemote)
			Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage((new TextComponentString(LibVulpes.proxy.getLocalizedString("msg.monitoringStation.link") + ": " + getPos().getX() + " " + getPos().getY() + " " + getPos().getZ())));
		return true;
	}

	@Override
	public boolean onLinkComplete(ItemStack item, TileEntity entity,
			EntityPlayer player, World world) {
		if(player.world.isRemote)
			Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage((new TextComponentString(LibVulpes.proxy.getLocalizedString("msg.linker.error.firstMachine"))));
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
		
		state = RedstoneState.values()[nbt.getByte("redstoneState")];
		redstoneControl.setRedstoneState(state);


		if(nbt.hasKey("missionID")) {
			long id = nbt.getLong("missionID");
			int dimid = nbt.getInteger("missionDimId");

			SatelliteBase sat = DimensionManager.getInstance().getSatellite(id);

			if(sat instanceof IMission)
				mission = (IMission)sat;
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setByte("redstoneState", (byte) state.ordinal());
		if(mission != null) {
			nbt.setLong("missionID", mission.getMissionId());
			nbt.setInteger("missionDimId", mission.getOriginatingDimention());
		}
		return nbt;
	}

	@Override
	public void writeDataToNetwork(ByteBuf out, byte id) {
		if(id == 1)
			out.writeLong(mission == null ? -1 : mission.getMissionId());
		else if(id == 2)
			out.writeByte(state.ordinal());
	}

	@Override
	public void readDataFromNetwork(ByteBuf in, byte packetId,
			NBTTagCompound nbt) {
		if(packetId == 1) {
			nbt.setLong("id", in.readLong());
		}
		else if(packetId == 2) {
			nbt.setByte("state", in.readByte());
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
			else if(id == 2) {
				state = RedstoneState.values()[nbt.getByte("state")];
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
				linkedRocket.prepareLaunch();
		}
	}

	@Override
	public List<ModuleBase> getModules(int ID, EntityPlayer player) {

		LinkedList<ModuleBase> modules = new LinkedList<ModuleBase>();

		modules.add(new ModuleButton(20, 40, 0, "Launch!", this,  zmaster587.libVulpes.inventory.TextureResources.buttonBuild));
		modules.add(new ModuleProgress(98, 4, 0, new IndicatorBarImage(2, 7, 12, 81, 17, 0, 6, 6, 1, 0, EnumFacing.UP, TextureResources.rocketHud), this));
		modules.add(new ModuleProgress(120, 14, 1, new IndicatorBarImage(2, 95, 12, 71, 17, 0, 6, 6, 1, 0, EnumFacing.UP, TextureResources.rocketHud), this));
		modules.add(new ModuleProgress(142, 14, 2, new ProgressBarImage(2, 173, 12, 71, 17, 6, 3, 69, 1, 1, EnumFacing.UP, TextureResources.rocketHud), this));

		modules.add(redstoneControl);
		setMissionText();

		modules.add(missionText);
		modules.add(new ModuleProgress(30, 110, 3, TextureResources.progressToMission, this));
		modules.add(new ModuleProgress(30, 120, 4, TextureResources.workMission, this));
		modules.add(new ModuleProgress(30, 130, 5, TextureResources.progressFromMission, this));

		if(!world.isRemote) {
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
			
			missionText.setText(((SatelliteBase)mission).getName() + LibVulpes.proxy.getLocalizedString("msg.monitoringStation.progress") + String.format("\n%02dhr:%02dm:%02ds", hours, minutes, seconds));
		}
		else
			missionText.setText(LibVulpes.proxy.getLocalizedString("msg.monitoringStation.missionProgressNA"));
	}
	
	@Override
	public void onInventoryButtonPressed(int buttonId) {
		if(buttonId != -1)
			PacketHandler.sendToServer(new PacketMachine(this, (byte) (buttonId + 100)) );
		else {
			state = redstoneControl.getState();
			PacketHandler.sendToServer(new PacketMachine(this, (byte)2));
		}
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
			return (float) Math.min(3f*mission.getProgress(this.world), 1f);
		}
		else if(id == 4) {
			if(mission == null)
				return 0f;
			return (float) Math.min(Math.max( 3f*(mission.getProgress(this.world) - 0.333f), 0f), 1f);
		}
		else if(id == 5) {
			if(mission == null)
				return 0f;
			return (float) Math.min(Math.max( 3f*(mission.getProgress(this.world) - 0.666f), 0f), 1f);
		}
		
		//keep text updated
		if(world.isRemote && mission != null)
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
		if(world.isRemote)
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
			if(world.isRemote)
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
		PacketHandler.sendToNearby(new PacketMachine(this, (byte)1), world.provider.getDimension(), getPos(), 16);
		return true;
	}

	@Override
	public void unlinkMission() {
		mission = null;
		setMissionText();
		PacketHandler.sendToNearby(new PacketMachine(this, (byte)1), world.provider.getDimension(), getPos(), 16);
	}

	@Override
	public boolean canRenderConnection() {
		return false;
	}
}
