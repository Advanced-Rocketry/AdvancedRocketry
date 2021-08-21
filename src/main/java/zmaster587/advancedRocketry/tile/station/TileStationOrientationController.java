package zmaster587.advancedRocketry.tile.station;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.fml.relauncher.Side;
import zmaster587.advancedRocketry.api.stations.ISpaceObject;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.advancedRocketry.network.PacketStationUpdate;
import zmaster587.advancedRocketry.stations.SpaceObjectManager;
import zmaster587.advancedRocketry.stations.SpaceStationObject;
import zmaster587.advancedRocketry.world.provider.WorldProviderSpace;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.inventory.modules.*;
import zmaster587.libVulpes.network.PacketHandler;
import zmaster587.libVulpes.network.PacketMachine;
import zmaster587.libVulpes.util.INetworkMachine;

import java.util.LinkedList;
import java.util.List;

public class TileStationOrientationController extends TileEntity implements ITickable, IModularInventory, INetworkMachine, ISliderBar, IButtonInventory {

	private int[] progress;

	private ModuleText moduleAngularVelocity, numThrusters, maxAngularAcceleration, targetRotations;

	public TileStationOrientationController() {
		moduleAngularVelocity = new ModuleText(6, 15, LibVulpes.proxy.getLocalizedString("msg.stationorientctrl.alt"), 0xaa2020);
		//numThrusters = new ModuleText(10, 25, "Number Of Thrusters: ", 0xaa2020);
		targetRotations = new ModuleText(6, 25, LibVulpes.proxy.getLocalizedString("msg.stationorientctrl.tgtalt"), 0x202020);
		progress = new int[3];

		progress[0] = getTotalProgress(0)/2;
		progress[1] = getTotalProgress(1)/2;
		progress[2] = getTotalProgress(2)/2;
	}

	@Override
	public List<ModuleBase> getModules(int id, EntityPlayer player) {
		List<ModuleBase> modules = new LinkedList<>();
		modules.add(moduleAngularVelocity);
		//modules.add(numThrusters);
		//modules.add(maxAngularAcceleration);
		modules.add(targetRotations);
		
		modules.add(new ModuleText(10, 54, "X:", 0x202020));
		modules.add(new ModuleText(10, 69, "Y:", 0x202020)); //AYYYY

		modules.add(new ModuleSlider(24, 50, 0, TextureResources.doubleWarningSideBarIndicator, this));
		modules.add(new ModuleSlider(24, 65, 1, TextureResources.doubleWarningSideBarIndicator, this));
		modules.add(new ModuleButton(25, 35, 2, LibVulpes.proxy.getLocalizedString("msg.spacelaser.reset"), this,  zmaster587.libVulpes.inventory.TextureResources.buttonBuild, 36, 15));
		//modules.add(new ModuleSlider(24, 35, 2, TextureResources.doubleWarningSideBarIndicator, (ISliderBar)this));

		updateText();
		return modules;
	}

	private void updateText() {
		if(world.isRemote) {
			ISpaceObject spaceObject = SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(pos);
			if(spaceObject != null) {
				moduleAngularVelocity.setText(String.format("%s%.1f %.1f %.1f", LibVulpes.proxy.getLocalizedString("msg.stationorientctrl.alt"), 72000D * spaceObject.getDeltaRotation(EnumFacing.EAST), 72000D * spaceObject.getDeltaRotation(EnumFacing.UP), 7200D * spaceObject.getDeltaRotation(EnumFacing.NORTH)));
				//maxAngularAcceleration.setText(String.format("Maximum Angular Acceleration: %.1f", 7200D*object.getMaxRotationalAcceleration()));

				//numThrusters.setText("Number Of Thrusters: 0");
				int[] targetRotationsPerHour = ((SpaceStationObject) spaceObject).targetRotationsPerHour;
				targetRotations.setText(String.format("%s%d %d %d", LibVulpes.proxy.getLocalizedString("msg.stationorientctrl.tgtalt"), targetRotationsPerHour[0], targetRotationsPerHour[1], targetRotationsPerHour[2]));
			}
		}
	}

	@Override
	public void update() {

		if(this.world.provider instanceof WorldProviderSpace) {
			if(!world.isRemote) {
				ISpaceObject spaceObject = SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(pos);
				boolean update = false;

				if(spaceObject != null) {

					EnumFacing[] dirs = { EnumFacing.EAST, EnumFacing.UP, EnumFacing.NORTH };
					int[] targetRotationsPerHour = ((SpaceStationObject) spaceObject).targetRotationsPerHour;
					for (int i = 0; i < 3; i++) {
						setProgress(i, targetRotationsPerHour[i] + (getTotalProgress(i)/2));
					}


					for(int i = 0; i < 3; i++) {

						double targetAngularVelocity = targetRotationsPerHour[i]/72000D;
						double angVel = spaceObject.getDeltaRotation(dirs[i]);
						double acc = spaceObject.getMaxRotationalAcceleration();

						double difference = targetAngularVelocity - angVel;

						if(difference != 0) {
							double finalVel = angVel;
							if(difference < 0) {
								finalVel = angVel + Math.max(difference, -acc);
							}
							else if(difference > 0) {
								finalVel = angVel + Math.min(difference, acc);
							}

							spaceObject.setDeltaRotation(finalVel, dirs[i]);
							update = true;
						}
					}
					
					if(!world.isRemote && update) {
						//PacketHandler.sendToNearby(new PacketStationUpdate(spaceObject, PacketStationUpdate.Type.ROTANGLE_UPDATE), this.worldObj.provider.dimensionId, this.xCoord, this.yCoord, this.zCoord, 1024);
						PacketHandler.sendToAll(new PacketStationUpdate(spaceObject, PacketStationUpdate.Type.ROTANGLE_UPDATE));
					}
				}
				else
					updateText();
			}
			else
				updateText();
		}
	}
	@Override
	public String getModularInventoryName() {
		return "tile.orientationControl.name";
	}

	@Override
	public boolean canInteractWithContainer(EntityPlayer entity) {
		return true;
	}

	@Override
	public void writeDataToNetwork(ByteBuf out, byte id) {
		if(id == 0) {
			out.writeShort(progress[0]);
			out.writeShort(progress[1]);
			out.writeShort(progress[2]);
		}
	}

	@Override
	public void readDataFromNetwork(ByteBuf in, byte packetId,
			NBTTagCompound nbt) {
		if(packetId == 0) {
			setProgress(0, in.readShort());
			setProgress(1, in.readShort());
			setProgress(2, in.readShort());
		}
	}

	@Override
	public void useNetworkData(EntityPlayer player, Side side, byte id,
			NBTTagCompound nbt) {

	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
	}


	@Override
	public float getNormallizedProgress(int id) {
		return getProgress(id)/(float)getTotalProgress(id);
	}

	@Override
	public void setProgress(int id, int progress) {

		this.progress[id] = progress;
		if (SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(this.pos) != null) {
			((SpaceStationObject) (SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(this.pos))).setTargetRotationsPerHour(id, (progress - getTotalProgress(id)/2));
		}
	}

	@Override
	public int getProgress(int id) {
		return this.progress[id];
	}

	@Override
	public int getTotalProgress(int id) {
		return 120;
	}

	@Override
	public void setTotalProgress(int id, int progress) {

	}

	@Override
	public void setProgressByUser(int id, int progress) {
		setProgress(id, progress);
		PacketHandler.sendToServer(new PacketMachine(this, (byte)0));
	}

	@Override
	public void onInventoryButtonPressed(int i) {
        if(i == 2) {
        	setProgress(0, getTotalProgress(0)/2);
			setProgress(1, getTotalProgress(1)/2);
			setProgress(2, getTotalProgress(2)/2);
		}
        PacketHandler.sendToServer(new PacketMachine(this, (byte)0));
	}
}
