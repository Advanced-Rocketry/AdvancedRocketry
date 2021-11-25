package zmaster587.advancedRocketry.tile.multiblock.energy;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import zmaster587.advancedRocketry.api.AdvancedRocketryTileEntityType;
import zmaster587.advancedRocketry.api.ARConfiguration;
import zmaster587.advancedRocketry.api.stations.ISpaceObject;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.dimension.DimensionProperties;
import zmaster587.advancedRocketry.stations.SpaceObjectManager;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.api.LibVulpesBlocks;
import zmaster587.libVulpes.block.BlockMeta;
import zmaster587.libVulpes.inventory.modules.ModuleBase;
import zmaster587.libVulpes.inventory.modules.ModuleText;
import zmaster587.libVulpes.network.PacketHandler;
import zmaster587.libVulpes.network.PacketMachine;
import zmaster587.libVulpes.tile.multiblock.TileMultiBlock;
import zmaster587.libVulpes.tile.multiblock.TileMultiPowerProducer;
import zmaster587.libVulpes.util.MultiBattery;
import zmaster587.libVulpes.util.ZUtils;

import java.util.List;
import java.util.Map.Entry;

public class TileBlackHoleGenerator extends TileMultiPowerProducer {
	static final Object[][][] structure = new Object[][][] {
		{
			{null, null, null},
			{null, LibVulpesBlocks.blockAdvancedMachineStructure, null},
			{null, null, null}
		},
		{
			{null, 'c', null},
			{'*', LibVulpesBlocks.blockAdvancedMachineStructure, '*'},
			{null, '*', null},
		},
		{
			{null, LibVulpesBlocks.blockAdvancedMachineStructure, null},
			{null, LibVulpesBlocks.blockAdvancedMachineStructure, null},
			{null, null, null}
		},
		{
			{null, null, null},
			{null, LibVulpesBlocks.blockAdvancedMachineStructure, null},
			{null, null, null}
		},
		{
			{null, null, null},
			{null, LibVulpesBlocks.blockAdvancedMachineStructure, null},
			{null, null, null}
		}};

		private int powerMadeLastTick, prevPowerMadeLastTick;
		private ModuleText textModule;
		private long last_usage;

		public TileBlackHoleGenerator() {
			super(AdvancedRocketryTileEntityType.TILE_BLACK_HOLE_GENERATOR);
			textModule = new ModuleText(40, 20, LibVulpes.proxy.getLocalizedString("msg.microwaverec.notgenerating"), 0x2b2b2b);
		}

		@Override
		public List<ModuleBase> getModules(int ID, PlayerEntity player) {
			List<ModuleBase> modules = super.getModules(ID, player);

			modules.add(textModule);

			return modules;
		}

		@Override
		public AxisAlignedBB getRenderBoundingBox() {
			return super.getRenderBoundingBox().grow(0, 2000, 0).offset(0, 1000, 0);
		}

		@Override
		public boolean shouldHideBlock(World world, BlockPos pos, BlockState tile) {
			return world.getBlockState(pos).getBlock() == AdvancedRocketryBlocks.blockBlackHoleGenerator;
		}

		@Override
		public Object[][][] getStructure() {
			return structure;
		}

		@Override
		public List<BlockMeta> getAllowableWildCardBlocks() {
			List<BlockMeta> blocks = super.getAllowableWildCardBlocks();
			blocks.addAll(TileMultiBlock.getMapping('I'));
			blocks.add(new BlockMeta( LibVulpesBlocks.blockAdvancedMachineStructure));
			blocks.addAll(TileMultiBlock.getMapping('p'));

			return blocks;
		}

		@Override
		public String getMachineName() {
			return "block.advancedrocketry.blackholegenerator";
		}

		@Override
		public void onInventoryUpdated() {
			super.onInventoryUpdated();
			attemptFire();
		}

		private ItemStack consumeItem() {
			for (IInventory i : getItemInPorts()) {
				for(int slot = 0; slot < i.getSizeInventory(); slot++) {
					ItemStack stack = i.getStackInSlot(slot);

					if(!stack.isEmpty()) {
						return i.decrStackSize(slot, 1);
					}
				}
			}
			return ItemStack.EMPTY;
		}

		private int getTimeFromStack(ItemStack stack) {
			for(Entry<ItemStack, Integer>  i : ARConfiguration.getCurrentConfig().blackHoleGeneratorBlocks.entrySet()) {
				if(i.getKey().getItem() == stack.getItem() && i.getKey().getDamage() == stack.getDamage())
					return i.getValue();
			}
			return ARConfiguration.getCurrentConfig().defaultItemTimeBlackHole.get();
		}

		private void attemptFire() {
			if(enabled && isAroundBlackHole()) {
				if(last_usage <= this.world.getGameTime() && !isEnergyFull()) {
					ItemStack stack = consumeItem();
					if(!stack.isEmpty()) {
						last_usage = this.world.getGameTime() + getTimeFromStack(stack);
					}
				}
			}
		}

		private boolean isEnergyFull() {
			MultiBattery battery = getBatteries();
			return battery.getMaxEnergyStored() == battery.getUniversalEnergyStored();
		}

		public boolean isProducingPower() {
			return powerMadeLastTick > 0;
		}

		private boolean isAroundBlackHole() {
			if(DimensionManager.spaceId.equals(ZUtils.getDimensionIdentifier(world))) {
				ISpaceObject spaceObject = SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(this.pos);
				if(spaceObject != null) {
					DimensionProperties properties = (DimensionProperties) spaceObject.getProperties().getParentProperties();
					return properties != null && (properties.isStar() && properties.getStarData().isBlackHole());
				}
			}

			return false;
		}

		@Override
		public void tick() {
			super.tick();

			if(!isComplete())
				return;

			if(!world.isRemote) {
				if(isAroundBlackHole()) {
					float energyRecieved;

					//Check to see if we're ready for another injection
					attemptFire();

					energyRecieved = last_usage > this.world.getGameTime() ? 500f : 0f;
					powerMadeLastTick = (int) (energyRecieved*ARConfiguration.getCurrentConfig().blackHolePowerMultiplier.get());

					if(powerMadeLastTick != prevPowerMadeLastTick) {
						prevPowerMadeLastTick = powerMadeLastTick;
						PacketHandler.sendToNearby(new PacketMachine(this, (byte)1), world, pos, 128);
					}
					producePower(powerMadeLastTick);
				}

			}
			if(world.isRemote)
				textModule.setText(LibVulpes.proxy.getLocalizedString("msg.microwaverec.generating") + ": " + powerMadeLastTick + " " + LibVulpes.proxy.getLocalizedString("msg.powerunit.rfpertick") +
						"\n\nStatus: " + (isAroundBlackHole() ? "ready" : "No black hole"));
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
			nbt.putInt("amtPwr", powerMadeLastTick);
			write(nbt);
			return nbt;
		}

		@Override
		public void handleUpdateTag(BlockState state, CompoundNBT nbt) {
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
}
