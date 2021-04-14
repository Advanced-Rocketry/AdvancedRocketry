package zmaster587.advancedRocketry.tile.multiblock.energy;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import zmaster587.advancedRocketry.api.ARConfiguration;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import zmaster587.advancedRocketry.api.AdvancedRocketryTileEntityType;
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

public class TileSolarArray extends TileMultiPowerProducer implements ITickableTileEntity {

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
		super(AdvancedRocketryTileEntityType.TILE_SOLAR_ARRAY);
		textModule = new ModuleText(40, 20, LibVulpes.proxy.getLocalizedString("msg.microwaverec.notgenerating"), 0x2b2b2b);
	}

	@Override
	public List<ModuleBase> getModules(int ID, PlayerEntity player) {
		List<ModuleBase> modules = super.getModules(ID, player);

		modules.add(textModule);

		return modules;
	}

	@Override
	public boolean shouldHideBlock(World world, BlockPos pos, BlockState tile) {
		return true;
	}

	@Override
	public Object[][][] getStructure() {
		return structure;
	}

	@Override
	public List<BlockMeta> getAllowableWildCardBlocks() {
		List<BlockMeta> blocks = super.getAllowableWildCardBlocks();

		blocks.add(new BlockMeta(AdvancedRocketryBlocks.blockSolarArrayPanel, true));
		blocks.add(new BlockMeta(Blocks.AIR));

		return blocks;
	}

	@Override
	protected boolean completeStructure(BlockState state) {
		//Needed definitions
		Direction front = this.getFrontDirection(state);
		Vector3F<Integer> offset = this.getControllerOffset(structure);

		//Panel-checker iterator
		numPanels = 0;
		for(int y = 0; y < structure.length; ++y) {
			for(int z = 0; z < structure[0].length; ++z) {
				for(int x = 0; x < structure[0][0].length; ++x) {
					int globalX = this.pos.getX() + (x - offset.x) * front.getZOffset() - (z - offset.z) * front.getXOffset();
					int globalY = this.pos.getY() - y + offset.y;
					int globalZ = this.pos.getZ() - (x - offset.x) * front.getXOffset() - (z - offset.z) * front.getZOffset();
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
		return "block.advancedrocketry.solararray";
	}

	@Override
	public void tick() {

		if(!initialCheck && !world.isRemote) {
			completeStructure = attemptCompleteStructure(world.getBlockState(pos));
			initialCheck = true;
		}

		if(!isComplete())
			return;

		if(!world.isRemote) {
			boolean isSpaceDim = DimensionManager.getInstance().isSpaceDimension(world);
			DimensionProperties properties =DimensionManager.getInstance().getDimensionProperties(world);
			double insolationPowerMultiplier = isSpaceDim ? SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(this.pos).getInsolationMultiplier() : properties.getPeakInsolationMultiplier();
			int energyRecieved = 0;
			if(enabled && ((world.isDaytime()  && world.canBlockSeeSky(this.pos.up())) || isSpaceDim && world.canBlockSeeSky(this.pos.down()))) {
				//Multiplied by two for 520W = 1 RF/t becoming 2 RF/t @ 100% efficiency, and by insolation mult for solar stuff
				//Slight adjustment to make Earth 0.9995 into a 1.0
				energyRecieved = (int) (numPanels * 1.0005d * 2 * insolationPowerMultiplier);
			}
			powerMadeLastTick = (int) (energyRecieved*ARConfiguration.getCurrentConfig().solarGeneratorMult.get());

			if(powerMadeLastTick != prevPowerMadeLastTick) {
				prevPowerMadeLastTick = powerMadeLastTick;
				PacketHandler.sendToNearby(new PacketMachine(this, (byte)1), world, pos, 128);

			}
			producePower(powerMadeLastTick);
		}
		if(world.isRemote)
			textModule.setText(LibVulpes.proxy.getLocalizedString("msg.microwaverec.generating") + " " + powerMadeLastTick + " " + LibVulpes.proxy.getLocalizedString("msg.powerunit.rfpertick"));
	}

	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		CompoundNBT nbt = new CompoundNBT();
		nbt.putInt("amtPwr", powerMadeLastTick);
		writeNetworkData(nbt);
		return new SUpdateTileEntityPacket(pos, 0, nbt);
	}

	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		CompoundNBT nbt = pkt.getNbtCompound();

		powerMadeLastTick = nbt.getInt("amtPwr");
		readNetworkData(nbt);
	}
	
	@Override
	public CompoundNBT getUpdateTag() {
		CompoundNBT nbt = new CompoundNBT();
		nbt.putInt("powerMadeLastTick", powerMadeLastTick);
		nbt.putInt("numPanels", numPanels);
		write(nbt);
		return nbt;
	}

	@Override
	public void handleUpdateTag(BlockState state, CompoundNBT nbt) {
		powerMadeLastTick = nbt.getInt("powerMadeLastTick");
		numPanels = nbt.getInt("numPanels");
		canRender = nbt.getBoolean("canRender");
		readNetworkData(nbt);
	}
	
	

	@Override
	public void writeDataToNetwork(PacketBuffer out, byte id) {
		super.writeDataToNetwork(out, id);

		if(id == 1) {
			out.writeInt(powerMadeLastTick);
		}
	}

	@Override
	public void readDataFromNetwork(PacketBuffer in, byte packetId,
			CompoundNBT nbt) {
		super.readDataFromNetwork(in, packetId, nbt);	

		if(packetId == 1) {
			nbt.putInt("amtPwr", in.readInt());
		}
	}

	@Override
	public void useNetworkData(PlayerEntity player, Dist side, byte id,
			CompoundNBT nbt) {
		super.useNetworkData(player, side, id, nbt);

		if(id == 1) {
			powerMadeLastTick = nbt.getInt("amtPwr");
		}
	}

	@Override
	protected void writeNetworkData(CompoundNBT nbt) {
		super.writeNetworkData(nbt);
		nbt.putInt("numPanels", this.numPanels);
	}

	@Override
	protected void readNetworkData(CompoundNBT nbt) {
		super.readNetworkData(nbt);
		this.numPanels = nbt.getInt("numPanels");
	}
}
