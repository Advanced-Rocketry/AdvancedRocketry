package zmaster587.advancedRocketry.tile.multiblock.energy;

import io.netty.buffer.ByteBuf;

import java.util.LinkedList;
import java.util.List;

import cpw.mods.fml.relauncher.Side;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import zmaster587.advancedRocketry.api.Configuration;
import zmaster587.advancedRocketry.api.satellite.SatelliteBase;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.dimension.DimensionProperties;
import zmaster587.advancedRocketry.item.ItemSatelliteIdentificationChip;
import zmaster587.advancedRocketry.util.AstronomicalBodyHelper;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.api.IUniversalEnergyTransmitter;
import zmaster587.libVulpes.block.BlockMeta;
import zmaster587.libVulpes.inventory.modules.ModuleBase;
import zmaster587.libVulpes.inventory.modules.ModuleText;
import zmaster587.libVulpes.network.PacketHandler;
import zmaster587.libVulpes.network.PacketMachine;
import zmaster587.libVulpes.tile.multiblock.TileMultiBlock;
import zmaster587.libVulpes.tile.multiblock.TileMultiPowerProducer;
import zmaster587.libVulpes.util.Vector3F;

public class TileMicrowaveReciever extends TileMultiPowerProducer {

	static final BlockMeta iron_block = new BlockMeta(AdvancedRocketryBlocks.blockSolarPanel);
	static final Object[][][] structure = new Object[][][] {
		{
			{iron_block, '*', '*', '*', iron_block},
			{'*', iron_block, iron_block, iron_block, '*'},
			{'*', iron_block, 'c', iron_block,'*'},
			{'*', iron_block, iron_block, iron_block, '*'},
			{iron_block, '*', '*', '*', iron_block},
		}};

	List<Long> connectedSatellites;
	boolean initialCheck;
	double insolationPowerMultiplier;
	int powerMadeLastTick, prevPowerMadeLastTick;
	ModuleText textModule;
	public TileMicrowaveReciever() {
		connectedSatellites = new LinkedList<Long>();
		initialCheck = false;
		insolationPowerMultiplier = 0;
		textModule = new ModuleText(40, 20, LibVulpes.proxy.getLocalizedString("msg.microwaverec.notgenerating"), 0x2b2b2b);
	}

	@Override
	public List<ModuleBase> getModules(int ID, EntityPlayer player) {
		List<ModuleBase> modules = super.getModules(ID, player);
		
		modules.add(textModule);
		
		return modules;
	}
	
	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return super.getRenderBoundingBox().expand(0, 2000, 0).offset(0, 1000, 0);
	}

	@Override
	public boolean shouldHideBlock(World world, int x, int y, int z, Block tile) {
		return false;
	}

	@Override
	public Object[][][] getStructure() {
		return structure;
	}

	@Override
	public List<BlockMeta> getAllowableWildCardBlocks() {
		List<BlockMeta> blocks = super.getAllowableWildCardBlocks();

		blocks.addAll(TileMultiBlock.getMapping('I'));
		blocks.add(iron_block);
		blocks.addAll(TileMultiBlock.getMapping('p'));

		return blocks;
	}

	@Override
	public boolean canUpdate() {
		return true;
	}

	@Override
	public String getMachineName() {
		return "tile.microwaveReciever.name";
	}
	
	public int getPowerMadeLastTick() {
		return powerMadeLastTick;
	}

	@Override
	public void onInventoryUpdated() {
		super.onInventoryUpdated();

		List list = new LinkedList<Long>();

		for(IInventory inv : itemInPorts) {
			for(int i = 0; i < inv.getSizeInventory(); i++) {
				ItemStack stack = inv.getStackInSlot(i);
				if(stack != null && stack.getItem() instanceof ItemSatelliteIdentificationChip) {
					ItemSatelliteIdentificationChip item = (ItemSatelliteIdentificationChip)stack.getItem();
					list.add(item.getSatelliteId(stack));
				}
			}
		}


		connectedSatellites = list;

	}

	@Override
	public void updateEntity() {
		super.updateEntity();

		if(!initialCheck && !worldObj.isRemote) {
			completeStructure = attemptCompleteStructure();
			onInventoryUpdated();
			initialCheck = true;
		}

		if(insolationPowerMultiplier == 0) {
			DimensionProperties properties = DimensionManager.getInstance().getDimensionProperties(worldObj.provider.dimensionId);
			//Gets insolation relative to Earth, without atmosphere because we're in space and meter-long microwaves don't attenuate in the atmosphere
			double insolationMultiplier = AstronomicalBodyHelper.getStellarBrightness(properties.getStar(), properties.getSolarOrbitalDistance());
			//Multiply by Earth LEO/Earth Surface for ratio relative to Earth surface (1360/1040)
			insolationPowerMultiplier = insolationMultiplier * 1.308d;
		}

		if(!isComplete())
			return;
		
		//Periodically check for obstructing blocks above the panel
		if(!worldObj.isRemote && getPowerMadeLastTick() > 0 && worldObj.getTotalWorldTime() % 100 == 0) {
			Vector3F<Integer> offset = getControllerOffset(getStructure());


			List<Entity> entityList = worldObj.getEntitiesWithinAABB(Entity.class, AxisAlignedBB.getBoundingBox(xCoord - offset.x, yCoord, zCoord - offset.z, xCoord - offset.x + getStructure()[0][0].length, 256, zCoord - offset.z + getStructure()[0].length));

			for(Entity e : entityList) {
				e.setFire(powerMadeLastTick/10);
			}

			for(int x=0 ; x < getStructure()[0][0].length; x++) {
				for(int z=0 ; z < getStructure()[0].length; z++) {
					int y = worldObj.getHeightValue(xCoord + x - offset.x, zCoord +  z - offset.z);
					Block block = worldObj.getBlock(xCoord + x - offset.x, y-1, zCoord +  z - offset.z);

					if(y > this.yCoord + 1) {
						if(!block.isAir(worldObj, xCoord + x - offset.x, y, zCoord +  z - offset.z)) {
							worldObj.setBlockToAir(xCoord + x - offset.x,  y - 1, zCoord + z - offset.z);
							worldObj.playSoundEffect(xCoord + x - offset.x,  y - 1, zCoord + z - offset.z, "fire.fire", 1, 3);
						}
					}
				}
			}
		}

		DimensionProperties properties;
		if(!worldObj.isRemote && (DimensionManager.getInstance().isDimensionCreated(worldObj.provider.dimensionId) || worldObj.provider.dimensionId == 0)) {
			properties = DimensionManager.getInstance().getDimensionProperties(worldObj.provider.dimensionId);

			int energyRecieved = 0;

			if(enabled) {
				for(long lng : connectedSatellites) {
					SatelliteBase satellite =  properties.getSatellite(lng);

					if(satellite instanceof IUniversalEnergyTransmitter) {
						energyRecieved += ((IUniversalEnergyTransmitter)satellite).transmitEnergy(ForgeDirection.UNKNOWN, false);
					}
				}
				//Multiplied by two for 520W = 1 RF/t becoming 2 RF/t @ 100% efficiency, and by insolation mult for solar stuff
				energyRecieved *= 2 * insolationPowerMultiplier;
			}
			powerMadeLastTick = (int) (energyRecieved*Configuration.microwaveRecieverMulitplier);

			if(powerMadeLastTick != prevPowerMadeLastTick) {
				prevPowerMadeLastTick = powerMadeLastTick;
				PacketHandler.sendToNearby(new PacketMachine(this, (byte)1), worldObj.provider.dimensionId, xCoord, yCoord, zCoord, 128);

			}
			producePower(powerMadeLastTick);
		}
		if(worldObj.isRemote)
			textModule.setText(LibVulpes.proxy.getLocalizedString("msg.microwaverec.generating") + powerMadeLastTick + " " + LibVulpes.proxy.getLocalizedString("msg.powerunit.rfpertick"));
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setBoolean("canRender", canRender);
		nbt.setInteger("amtPwr", powerMadeLastTick);
		writeNetworkData(nbt);
		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, nbt);
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
		NBTTagCompound nbt = pkt.func_148857_g();

		canRender = nbt.getBoolean("canRender");
		powerMadeLastTick = nbt.getInteger("amtPwr");
		readNetworkData(nbt);
	}

	@Override
	public void writeDataToNetwork(ByteBuf out, byte id) {
		super.writeDataToNetwork(out, id);

		if(id == 1) {
			out.writeInt(powerMadeLastTick);
		}
	}

	@Override
	public void readDataFromNetwork(ByteBuf in, byte packetId,
			NBTTagCompound nbt) {
		super.readDataFromNetwork(in, packetId, nbt);	

		if(packetId == 1) {
			nbt.setInteger("amtPwr", in.readInt());
		}
	}

	@Override
	public void useNetworkData(EntityPlayer player, Side side, byte id,
			NBTTagCompound nbt) {
		super.useNetworkData(player, side, id, nbt);

		if(id == 1) {
			powerMadeLastTick = nbt.getInteger("amtPwr");
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);

		int[] intArray = new int[connectedSatellites.size()*2];

		for( int i =0; i < connectedSatellites.size()*2; i += 2 ) {
			connectedSatellites.get(i/2);
			intArray[i] = (int) (connectedSatellites.get(i/2) & 0xFFFFFFFF);
			intArray[i+1] = (int) ((connectedSatellites.get(i/2) >>> 32) & 0xFFFFFFFF);
		}

		nbt.setIntArray("satilliteList", intArray);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);

		int intArray[] = nbt.getIntArray("satilliteList");
		connectedSatellites.clear();
		for( int i =0; i < intArray.length/2; i+=2 ) {
			connectedSatellites.add(intArray[i] | (((long)intArray[i+1]) << 32));
		}

	}

}
