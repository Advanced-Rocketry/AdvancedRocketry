package zmaster587.advancedRocketry.tile;

import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.api.IAtmosphere;
import zmaster587.advancedRocketry.api.atmosphere.AtmosphereRegister;
import zmaster587.advancedRocketry.atmosphere.AtmosphereHandler;
import zmaster587.advancedRocketry.atmosphere.AtmosphereType;
import zmaster587.advancedRocketry.block.BlockRedstoneEmitter;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.inventory.modules.IButtonInventory;
import zmaster587.libVulpes.inventory.modules.IModularInventory;
import zmaster587.libVulpes.inventory.modules.ModuleBase;
import zmaster587.libVulpes.inventory.modules.ModuleButton;
import zmaster587.libVulpes.inventory.modules.ModuleContainerPan;
import zmaster587.libVulpes.network.PacketHandler;
import zmaster587.libVulpes.network.PacketMachine;
import zmaster587.libVulpes.util.INetworkMachine;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;

public class TileAtmosphereDetector extends TileEntity implements ITickable, IModularInventory, IButtonInventory, INetworkMachine {

	IAtmosphere atmosphereToDetect;

	public TileAtmosphereDetector() {
		atmosphereToDetect = AtmosphereType.AIR;
	}


	@Override
	public void update() {
		if(!worldObj.isRemote && worldObj.getWorldTime() % 10 == 0) {
			IBlockState state = worldObj.getBlockState(pos);
			boolean detectedAtm = false;

			//TODO: Galacticcraft support
			if(AtmosphereHandler.getOxygenHandler(worldObj.provider.getDimension()) == null) {
				detectedAtm = atmosphereToDetect == AtmosphereType.AIR;
			}
			else {
				for(EnumFacing  direction : EnumFacing.values()) {
					detectedAtm = (!worldObj.getBlockState(pos.offset(direction)).isOpaqueCube() && atmosphereToDetect == AtmosphereHandler.getOxygenHandler(worldObj.provider.getDimension()).getAtmosphereType(pos.offset(direction)));
					if(detectedAtm) break;
				}
			}

			if(((BlockRedstoneEmitter)state.getBlock()).getState(worldObj, state, pos) != detectedAtm) {
				((BlockRedstoneEmitter)state.getBlock()).setState(worldObj, state, pos, detectedAtm);
			}
		}
	}
	
	@Override
	public boolean shouldRefresh(World world, BlockPos pos,
			IBlockState oldState, IBlockState newSate) {
		return (oldState.getBlock() != newSate.getBlock());
	}

	@Override
	public List<ModuleBase> getModules(int id, EntityPlayer player) {
		List<ModuleBase> modules = new LinkedList<ModuleBase>();
		List<ModuleBase> btns = new LinkedList<ModuleBase>();

		Iterator<IAtmosphere> atmIter = AtmosphereRegister.getInstance().getAtmosphereList().iterator();

		int i = 0;
		while(atmIter.hasNext()) {
			IAtmosphere atm = atmIter.next();
			btns.add(new ModuleButton(60, 4 + i*24, i, LibVulpes.proxy.getLocalizedString(atm.getUnlocalizedName()), this,  zmaster587.libVulpes.inventory.TextureResources.buttonBuild));
			i++;
		}

		ModuleContainerPan panningContainer = new ModuleContainerPan(5, 20, btns, new LinkedList<ModuleBase>(), zmaster587.libVulpes.inventory.TextureResources.starryBG, 165, 120, 0, 500);
		modules.add(panningContainer);
		return modules;
	}

	@Override
	public String getModularInventoryName() {
		return "atmosphereDetector";
	}

	@Override
	public boolean canInteractWithContainer(EntityPlayer entity) {
		return true;
	}

	@Override
	public void onInventoryButtonPressed(int buttonId) {
		atmosphereToDetect = AtmosphereRegister.getInstance().getAtmosphereList().get(buttonId);
		PacketHandler.sendToServer(new PacketMachine(this, (byte)0));
	}

	@Override
	public void writeDataToNetwork(ByteBuf out, byte id) {
		//Send the unlocalized name over the net to reduce chances of foulup due to client/server inconsistencies
		if(id == 0) {
			PacketBuffer buf = new PacketBuffer(out);
			buf.writeShort(atmosphereToDetect.getUnlocalizedName().length());
			buf.writeString(atmosphereToDetect.getUnlocalizedName());
		}
	}

	@Override
	public void readDataFromNetwork(ByteBuf in, byte packetId,
			NBTTagCompound nbt) {
		if(packetId == 0) {
			PacketBuffer buf = new PacketBuffer(in);
			nbt.setString("uName", buf.readStringFromBuffer(buf.readShort()));
		}
	}

	@Override
	public void useNetworkData(EntityPlayer player, Side side, byte id,
			NBTTagCompound nbt) {
		if(id == 0) {
			String name = nbt.getString("uName");
			atmosphereToDetect = AtmosphereRegister.getInstance().getAtmosphere(name);
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);

		nbt.setString("atmName", atmosphereToDetect.getUnlocalizedName());
		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);

		atmosphereToDetect = AtmosphereRegister.getInstance().getAtmosphere(nbt.getString("atmName"));
	}
}
