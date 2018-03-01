package zmaster587.advancedRocketry.tile.station;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraftforge.fml.relauncher.Side;
import zmaster587.advancedRocketry.api.stations.ISpaceObject;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.advancedRocketry.network.PacketStationUpdate;
import zmaster587.advancedRocketry.stations.SpaceObjectManager;
import zmaster587.advancedRocketry.world.provider.WorldProviderSpace;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.inventory.modules.*;
import zmaster587.libVulpes.network.PacketHandler;
import zmaster587.libVulpes.network.PacketMachine;
import zmaster587.libVulpes.util.INetworkMachine;

import java.util.LinkedList;
import java.util.List;

public class TileStationAltitudeController extends TileEntity implements IModularInventory, ITickable, INetworkMachine, ISliderBar {

	int gravity;
	int progress;

	private ModuleText moduleGrav, numGravPylons, maxGravBuildSpeed, targetGrav;

	public TileStationAltitudeController() {
		moduleGrav = new ModuleText(6, 15, "Altitude: ", 0xaa2020);
		//numGravPylons = new ModuleText(10, 25, "Number Of Thrusters: ", 0xaa2020);
		maxGravBuildSpeed = new ModuleText(6, 25, LibVulpes.proxy.getLocalizedString("msg.stationaltctrl.maxaltrate"), 0xaa2020);
		targetGrav = new ModuleText(6, 35, LibVulpes.proxy.getLocalizedString("msg.stationaltctrl.tgtalt"), 0x202020);
	}

	@Override
	public List<ModuleBase> getModules(int id, EntityPlayer player) {
		List<ModuleBase> modules = new LinkedList<ModuleBase>();
		modules.add(moduleGrav);
		//modules.add(numThrusters);
		modules.add(maxGravBuildSpeed);

		modules.add(targetGrav);
		modules.add(new ModuleSlider(6, 60, 0, TextureResources.doubleWarningSideBarIndicator, (ISliderBar)this));

		updateText();
		return modules;
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		NBTTagCompound nbt = writeToNBT(new NBTTagCompound());
		nbt.setInteger("gravity", gravity);
		

		SPacketUpdateTileEntity packet = new SPacketUpdateTileEntity(pos, 0, nbt);
		return packet;
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		super.onDataPacket(net, pkt);

		gravity = pkt.getNbtCompound().getInteger("gravity");

	}
	
	private void updateText() {
		if(world.isRemote) {
			ISpaceObject object = SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(pos);
			if(object != null) {
				moduleGrav.setText(String.format("%s %.0fKm",LibVulpes.proxy.getLocalizedString("msg.stationaltctrl.alt"), object.getOrbitalDistance()*200 + 100 ));
				maxGravBuildSpeed.setText(String.format("%s%.1f", LibVulpes.proxy.getLocalizedString("msg.stationaltctrl.maxaltrate"), 7200D*object.getMaxRotationalAcceleration()));
			}

			//numThrusters.setText("Number Of Thrusters: 0");

			targetGrav.setText(String.format("%s %d", LibVulpes.proxy.getLocalizedString("msg.stationaltctrl.tgtalt"), gravity*200 + 100));
		}
	}

	@Override
	public void update() {
		if(this.world.provider instanceof WorldProviderSpace) {

			if(!world.isRemote) {
				ISpaceObject object = SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(pos);

				if(object != null) {
					
					double targetGravity = gravity;
					double angVel = object.getOrbitalDistance();
					double acc = 0.1*(getTotalProgress(0) - angVel + 1)/(float)getTotalProgress(0);

					double difference = targetGravity - angVel;

					if(difference != 0) {
						double finalVel = angVel;
						if(difference < 0) {
							finalVel = angVel + Math.max(difference, -acc);
						}
						else if(difference > 0) {
							finalVel = angVel + Math.min(difference, acc);
						}

						object.setOrbitalDistance((float)finalVel);
						if(!world.isRemote) {
							//PacketHandler.sendToNearby(new PacketStationUpdate(object, PacketStationUpdate.Type.ROTANGLE_UPDATE), this.worldObj.provider.dimensionId, this.xCoord, this.yCoord, this.zCoord, 1024);
							PacketHandler.sendToAll(new PacketStationUpdate(object, PacketStationUpdate.Type.ALTITUDE_UPDATE));
						}
						else
							updateText();
					}
				}
			}
			else
				updateText();
		}
	}
	@Override
	public String getModularInventoryName() {
		return "tile.altitudeController.name";
	}

	@Override
	public boolean canInteractWithContainer(EntityPlayer entity) {
		return true;
	}

	@Override
	public void writeDataToNetwork(ByteBuf out, byte id) {
		if(id == 0) {
			out.writeShort(progress);
		}
	}

	@Override
	public void readDataFromNetwork(ByteBuf in, byte packetId,
			NBTTagCompound nbt) {
		if(packetId == 0) {
			setProgress(0, in.readShort());
		}
	}

	@Override
	public void useNetworkData(EntityPlayer player, Side side, byte id,
			NBTTagCompound nbt) {

	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setShort("numRotations", (short)gravity);
		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		gravity = nbt.getShort("numRotations");
	}


	@Override
	public float getNormallizedProgress(int id) {
		return getProgress(0)/(float)getTotalProgress(0);
	}

	@Override
	public void setProgress(int id, int progress) {

		this.progress = progress;
		gravity = progress;
	}

	@Override
	public int getProgress(int id) {
		return this.progress;
	}

	@Override
	public int getTotalProgress(int id) {
		return 190;
	}

	@Override
	public void setTotalProgress(int id, int progress) {

	}

	@Override
	public void setProgressByUser(int id, int progress) {
		setProgress(id, progress);
		PacketHandler.sendToServer(new PacketMachine(this, (byte)0));
	}
}
