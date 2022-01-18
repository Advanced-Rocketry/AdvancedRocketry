package zmaster587.advancedRocketry.tile.multiblock.energy;

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

public class TileSolarArray extends TileMultiPowerProducer {

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

	int powerMadeLastTick, prevPowerMadeLastTick;
	int numWorkingPanels;
	boolean isSpaceDim;
	ModuleText textModule;
	public TileSolarArray() {
		super(AdvancedRocketryTileEntityType.TILE_SOLAR_ARRAY);
		textModule = new ModuleText(40, 20, LibVulpes.proxy.getLocalizedString("msg.microwaverec.notgenerating"), 0x2b2b2b);
		isSpaceDim = DimensionManager.getInstance().isSpaceDimension(world);
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
	public boolean attemptCompleteStructure(BlockState state) {
		checkSolarAmounts();
		return super.attemptCompleteStructure(state);
	}

	@Override
	public String getMachineName() {
		return "block.advancedrocketry.solararray";
	}

	@Override
	public void tick() {
		super.tick();

		if(!isComplete()) return;

		if(!world.isRemote) {
			//Check for panel obstructions
			if (world.getGameTime() % 600 == 0) checkSolarAmounts();

			if (enabled && numWorkingPanels > 0) {
				//More numbers we need to check
				DimensionProperties properties = DimensionManager.getInstance().getDimensionProperties(world);
				double insolationMultiplier = isSpaceDim ? SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(this.pos).getInsolationMultiplier() : properties.getPeakInsolationMultiplier();

				//Slight adjustment to make Earth 0.9995 into a 1.0
				powerMadeLastTick= (int) Math.min((1.0005d * 2d * ARConfiguration.getCurrentConfig().solarGeneratorMult.get() * insolationMultiplier * numWorkingPanels), 10000);

				if (powerMadeLastTick != prevPowerMadeLastTick) {
					prevPowerMadeLastTick = powerMadeLastTick;
					PacketHandler.sendToNearby(new PacketMachine(this, (byte) 1), world, pos, 128);

				}
				producePower(powerMadeLastTick);
			}
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
		nbt.putInt("numPanels", numWorkingPanels);
		write(nbt);
		return nbt;
	}

	@Override
	public void handleUpdateTag(BlockState state, CompoundNBT nbt) {
		powerMadeLastTick = nbt.getInt("powerMadeLastTick");
		numWorkingPanels = nbt.getInt("numPanels");
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
	public void readDataFromNetwork(PacketBuffer in, byte packetId, CompoundNBT nbt) {
		super.readDataFromNetwork(in, packetId, nbt);
		if(packetId == 1) {
			nbt.putInt("amtPwr", in.readInt());
		}
	}

	@Override
	public void useNetworkData(PlayerEntity player, Dist side, byte id, CompoundNBT nbt) {
		super.useNetworkData(player, side, id, nbt);
		if(id == 1) {
			powerMadeLastTick = nbt.getInt("amtPwr");
		}
	}

	@Override
	protected void writeNetworkData(CompoundNBT nbt) {
		super.writeNetworkData(nbt);
		nbt.putInt("numPanels", this.numWorkingPanels);
	}

	@Override
	protected void readNetworkData(CompoundNBT nbt) {
		super.readNetworkData(nbt);
		this.numWorkingPanels = nbt.getInt("numPanels");
	}

    private void checkSolarAmounts() {
		numWorkingPanels = 0;
		for(int y = 0; y < structure.length; ++y) {
			for (int z = 0; z < structure[0].length; ++z) {
				for (int x = 0; x < structure[0][0].length; ++x) {
					//Determine position to check
					Direction front = this.getFrontDirection(world.getBlockState(this.pos));
					Vector3F<Integer> offset = this.getControllerOffset(structure);
					BlockPos globalPos = new BlockPos(this.pos.getX() + (x - offset.x) * front.getZOffset() - (z - offset.z) * front.getXOffset(), this.pos.getY() - y + offset.y, this.pos.getZ() - (x - offset.x) * front.getXOffset() - (z - offset.z) * front.getZOffset());

					//Actual position check(s), after resetting the counter
					if (world.getBlockState(globalPos).getBlock() == AdvancedRocketryBlocks.blockSolarArrayPanel) {
						if (world.canBlockSeeSky(globalPos)) {
							numWorkingPanels++;
						}
						if (isSpaceDim) {
							int ypos = 1;
							while (world.getBlockState(globalPos.add(0, -ypos, 0)).isAir() && (pos.getY() - ypos) > 0)
								ypos++;
							numWorkingPanels += pos.getY() - ypos == 0 ? 1 : 0;
						}
					}
				}
			}
		}
	}
}
