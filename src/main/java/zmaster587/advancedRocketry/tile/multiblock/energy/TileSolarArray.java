package zmaster587.advancedRocketry.tile.multiblock.energy;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import zmaster587.advancedRocketry.api.ARConfiguration;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.dimension.DimensionProperties;
import zmaster587.advancedRocketry.stations.SpaceObjectManager;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.block.BlockMeta;
import zmaster587.libVulpes.inventory.modules.ModuleBase;
import zmaster587.libVulpes.inventory.modules.ModuleText;
import zmaster587.libVulpes.network.PacketHandler;
import zmaster587.libVulpes.network.PacketMachine;
import zmaster587.libVulpes.tile.multiblock.TileMultiPowerProducer;
import zmaster587.libVulpes.util.Vector3F;

import java.util.List;

public class TileSolarArray extends TileMultiPowerProducer implements ITickable {

	static final Object[][][] structure = new Object[][][] {
		{
			{'p', 'c', 'p'},
		    {'*', '*', '*'},
			{'*', '*', '*'},
			{'*', '*', '*'},
			{'*', '*', '*'},
			{'*', '*', '*'},
			{'*', '*', '*'},
			{'*', '*', '*'},
			{'*', '*', '*'},
			{'*', '*', '*'},
			{'*', '*', '*'},
			{'*', '*', '*'},
			{'*', '*', '*'},
			{'*', '*', '*'},
			{'*', '*', '*'},
			{'*', '*', '*'},
			{'*', '*', '*'},
			{'*', '*', '*'},
			{'*', '*', '*'},
			{'*', '*', '*'},
			{'*', '*', '*'},
			{'*', '*', '*'}
		}};

	boolean initialCheck;
	int powerMadeLastTick, prevPowerMadeLastTick;
	int numPanels;
	ModuleText textModule;
	public TileSolarArray() {
		textModule = new ModuleText(40, 20, LibVulpes.proxy.getLocalizedString("msg.microwaverec.notgenerating"), 0x2b2b2b);
	}

	@Override
	public List<ModuleBase> getModules(int ID, EntityPlayer player) {
		List<ModuleBase> modules = super.getModules(ID, player);

		modules.add(textModule);

		return modules;
	}

	@Override
	public boolean shouldHideBlock(World world, BlockPos pos, IBlockState tile) {
		return false;
	}

	@Override
	public Object[][][] getStructure() {
		return structure;
	}

	@Override
	public List<BlockMeta> getAllowableWildCardBlocks() {
		List<BlockMeta> blocks = super.getAllowableWildCardBlocks();

		blocks.add(new BlockMeta(AdvancedRocketryBlocks.blockSolarArrayPanel, -1));
		blocks.add(new BlockMeta(Blocks.AIR));

		return blocks;
	}

	@Override
	protected boolean completeStructure(IBlockState state) {
		//Needed definitions
		EnumFacing front = this.getFrontDirection(state);
		Vector3F<Integer> offset = this.getControllerOffset(structure);

		//Panel-checker iterator
		numPanels = 0;
		for(int y = 0; y < structure.length; ++y) {
			for(int z = 0; z < structure[0].length; ++z) {
				for(int x = 0; x < structure[0][0].length; ++x) {
					int globalX = this.pos.getX() + (x - offset.x) * front.getFrontOffsetZ() - (z - offset.z) * front.getFrontOffsetX();
					int globalY = this.pos.getY() - y + offset.y;
					int globalZ = this.pos.getZ() - (x - offset.x) * front.getFrontOffsetX() - (z - offset.z) * front.getFrontOffsetZ();
					if (world.getBlockState(new BlockPos(globalX, globalY, globalZ)).getBlock() == AdvancedRocketryBlocks.blockSolarArrayPanel) {
						numPanels++;
					}
				}
			}
		}
		return super.completeStructure(state);
	}

	@Override
	public String getMachineName() {
		return "tile.solararray.name";
	}

	@Override
	public void update() {

		if(!initialCheck && !world.isRemote) {
			completeStructure = attemptCompleteStructure(world.getBlockState(pos));
			initialCheck = true;
		}

		if(!isComplete())
			return;

		if(!world.isRemote) {
			DimensionProperties properties =DimensionManager.getInstance().getDimensionProperties(world.provider.getDimension());
			double insolationPowerMultiplier = (world.provider.getDimension() == ARConfiguration.getCurrentConfig().spaceDimId) ? SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(this.pos).getInsolationMultiplier() : properties.getPeakInsolationMultiplier();
			int energyRecieved = 0;
			if(enabled && ((world.isDaytime()  && world.canBlockSeeSky(this.pos.up())) || (world.provider.getDimension() == ARConfiguration.getCurrentConfig().spaceDimId && world.canBlockSeeSky(this.pos.down())))) {
				//Multiplied by two for 520W = 1 RF/t becoming 2 RF/t @ 100% efficiency, and by insolation mult for solar stuff
				//Slight adjustment to make Earth 0.9995 into a 1.0
				energyRecieved = (int) (numPanels * 1.0005d * 2 * insolationPowerMultiplier);
			}
			powerMadeLastTick = (int) (energyRecieved*ARConfiguration.getCurrentConfig().solarGeneratorMult);

			if(powerMadeLastTick != prevPowerMadeLastTick) {
				prevPowerMadeLastTick = powerMadeLastTick;
				PacketHandler.sendToNearby(new PacketMachine(this, (byte)1), world.provider.getDimension(),pos, 128);

			}
			producePower(powerMadeLastTick);
		}
		if(world.isRemote)
			textModule.setText(LibVulpes.proxy.getLocalizedString("msg.microwaverec.generating") + " " + powerMadeLastTick + " " + LibVulpes.proxy.getLocalizedString("msg.powerunit.rfpertick"));
	}

	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setInteger("amtPwr", powerMadeLastTick);
		writeNetworkData(nbt);
		return new SPacketUpdateTileEntity(pos, 0, nbt);
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		NBTTagCompound nbt = pkt.getNbtCompound();

		powerMadeLastTick = nbt.getInteger("amtPwr");
		readNetworkData(nbt);
	}
	
	@Override
	public NBTTagCompound getUpdateTag() {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setInteger("powerMadeLastTick", powerMadeLastTick);
		nbt.setInteger("numPanels", numPanels);
		writeToNBT(nbt);
		return nbt;
	}

	@Override
	public void handleUpdateTag(NBTTagCompound nbt) {
		powerMadeLastTick = nbt.getInteger("powerMadeLastTick");
		numPanels = nbt.getInteger("numPanels");
		canRender = nbt.getBoolean("canRender");
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
	protected void writeNetworkData(NBTTagCompound nbt) {
		super.writeNetworkData(nbt);
		nbt.setInteger("numPanels", this.numPanels);
	}

	@Override
	protected void readNetworkData(NBTTagCompound nbt) {
		super.readNetworkData(nbt);
		this.numPanels = nbt.getInteger("numPanels");
	}
}
