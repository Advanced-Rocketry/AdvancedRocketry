package zmaster587.advancedRocketry.tile.station;

import io.netty.buffer.ByteBuf;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import zmaster587.advancedRocketry.api.stations.ISpaceObject;
import zmaster587.advancedRocketry.api.stations.SpaceObjectManager;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.advancedRocketry.network.PacketStationUpdate;
import zmaster587.advancedRocketry.world.provider.WorldProviderSpace;
import zmaster587.libVulpes.inventory.modules.IModularInventory;
import zmaster587.libVulpes.inventory.modules.ISliderBar;
import zmaster587.libVulpes.inventory.modules.ModuleBase;
import zmaster587.libVulpes.inventory.modules.ModuleSlider;
import zmaster587.libVulpes.inventory.modules.ModuleText;
import zmaster587.libVulpes.network.PacketHandler;
import zmaster587.libVulpes.network.PacketMachine;
import zmaster587.libVulpes.util.INetworkMachine;
import cpw.mods.fml.relauncher.Side;

public class TileStationGravityController extends TileEntity implements IModularInventory, INetworkMachine, ISliderBar {

	int gravity;
	int progress;

	private ModuleText moduleGrav, numGravPylons, maxGravBuildSpeed, targetGrav;

	public TileStationGravityController() {
		moduleGrav = new ModuleText(6, 15, "Artifical Gravity: ", 0xaa2020);
		//numGravPylons = new ModuleText(10, 25, "Number Of Thrusters: ", 0xaa2020);
		maxGravBuildSpeed = new ModuleText(6, 25, "Max Gravity Change Rate: ", 0xaa2020);
		targetGrav = new ModuleText(6, 35, "Target Gravity:", 0x202020);
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
	public boolean canUpdate() {
		return true;
	}

	private void updateText() {
		if(worldObj.isRemote) {
			ISpaceObject object = SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(this.xCoord, this.zCoord);
			if(object != null) {
				moduleGrav.setText(String.format("Artifical Gravity: %.2f", object.getProperties().getGravitationalMultiplier()));
				maxGravBuildSpeed.setText(String.format("Max Gravity Change Rate: %.1f", 7200D*object.getMaxRotationalAcceleration()));
			}

			//numThrusters.setText("Number Of Thrusters: 0");

			targetGrav.setText(String.format("Target Gravity: %d", gravity));
		}
	}

	@Override
	public void updateEntity() {
		super.updateEntity();

		if(this.worldObj.provider instanceof WorldProviderSpace) {
			ISpaceObject object = SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(this.xCoord, this.zCoord);

			if(object != null) {
				if(gravity == 0)
					gravity = 15;
				double targetGravity = gravity/100D;
				double angVel = object.getProperties().getGravitationalMultiplier();
				double acc = 0.001;

				double difference = targetGravity - angVel;

				if(difference != 0) {
					double finalVel = angVel;
					if(difference < 0) {
						finalVel = angVel + Math.max(difference, -acc);
					}
					else if(difference > 0) {
						finalVel = angVel + Math.min(difference, acc);
					}

					object.getProperties().setGravitationalMultiplier((float)finalVel);
					if(!worldObj.isRemote) {
						//PacketHandler.sendToNearby(new PacketStationUpdate(object, PacketStationUpdate.Type.ROTANGLE_UPDATE), this.worldObj.provider.dimensionId, this.xCoord, this.yCoord, this.zCoord, 1024);
						PacketHandler.sendToAll(new PacketStationUpdate(object, PacketStationUpdate.Type.DIM_PROPERTY_UPDATE));
					}
					else
						updateText();
				}
			}
		}
	}
	@Override
	public String getModularInventoryName() {
		return "tile.gravityControl.name";
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
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setShort("numRotations", (short)gravity);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		gravity = nbt.getShort("numRotations");
		progress = gravity -10;
	}


	@Override
	public float getNormallizedProgress(int id) {
		return getProgress(0)/(float)getTotalProgress(0);
	}

	@Override
	public void setProgress(int id, int progress) {

		this.progress = progress;
		gravity = progress + 10;
	}

	@Override
	public int getProgress(int id) {
		return this.progress;
	}

	@Override
	public int getTotalProgress(int id) {
		return 90;
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
