package zmaster587.advancedRocketry.tile.multiblock.energy;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.Heightmap.Type;
import net.minecraftforge.api.distmarker.Dist;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import zmaster587.advancedRocketry.api.AdvancedRocketryTileEntityType;
import zmaster587.advancedRocketry.api.ARConfiguration;
import zmaster587.advancedRocketry.api.satellite.SatelliteBase;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.dimension.DimensionProperties;
import zmaster587.advancedRocketry.item.ItemSatelliteIdentificationChip;
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
import zmaster587.libVulpes.util.ZUtils;

import java.util.LinkedList;
import java.util.List;

public class TileMicrowaveReciever extends TileMultiPowerProducer implements ITickableTileEntity {

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
	int powerMadeLastTick, prevPowerMadeLastTick;
	ModuleText textModule;
	public TileMicrowaveReciever() {
		super(AdvancedRocketryTileEntityType.TILE_MICROWAVE_RECIEVER);
		connectedSatellites = new LinkedList<Long>();
		initialCheck = false;
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
	public void tick() {

		if(!initialCheck && !world.isRemote) {
			completeStructure = attemptCompleteStructure(world.getBlockState(pos));
			onInventoryUpdated();
			initialCheck = true;
		}

		if(!isComplete())
			return;

		//Periodically check for obstructing blocks above the panel
		if(!world.isRemote && getPowerMadeLastTick() > 0 && world.getGameTime() % 100 == 0) {
			Vector3F<Integer> offset = getControllerOffset(getStructure());


			List<Entity> entityList = world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(this.getPos().getX() - offset.x, this.getPos().getY(), this.getPos().getZ() - offset.z, this.getPos().getX() - offset.x + getStructure()[0][0].length, 256, this.getPos().getZ() - offset.z + getStructure()[0].length));

			for(Entity e : entityList) {
				e.setFire(5);
			}

			for(int x=0 ; x < getStructure()[0][0].length; x++) {
				for(int z=0 ; z < getStructure()[0].length; z++) {

					BlockPos pos2;
					BlockState state = world.getBlockState(pos2 = (world.getHeight(Type.WORLD_SURFACE , pos.add(x - offset.x, 128, z - offset.z)).add(0, -1, 0)));

					if(pos2.getY() > this.getPos().getY()) {
						if(!world.isAirBlock(pos2.add(0,1,0))) {
							world.removeBlock(pos2, false);
							world.playSound((double)pos2.getX(), (double)pos2.getY(), (double)pos2.getZ(), new SoundEvent(new ResourceLocation("fire.fire")), SoundCategory.BLOCKS, 1f, 3f, false);
						}
					}
				}
			}
		}

		DimensionProperties properties;
		if(!world.isRemote && (DimensionManager.getInstance().isDimensionCreated(ZUtils.getDimensionIdentifier(world)) || ZUtils.getDimensionIdentifier(world) == DimensionManager.overworldProperties.getId())) {
			properties = DimensionManager.getInstance().getDimensionProperties(world);

			int energyRecieved = 0;

			if(enabled) {
				for(long lng : connectedSatellites) {
					SatelliteBase satellite =  properties.getSatellite(lng);

					if(satellite instanceof IUniversalEnergyTransmitter) {
						energyRecieved += ((IUniversalEnergyTransmitter)satellite).transmitEnergy(Direction.UP, false);
					}
				}
			}
			powerMadeLastTick = (int) (energyRecieved*ARConfiguration.getCurrentConfig().microwaveRecieverMulitplier);

			if(powerMadeLastTick != prevPowerMadeLastTick) {
				prevPowerMadeLastTick = powerMadeLastTick;
				PacketHandler.sendToNearby(new PacketMachine(this, (byte)1), world,pos, 128);

			}
			producePower(powerMadeLastTick);
		}
		if(world.isRemote)
			textModule.setText(LibVulpes.proxy.getLocalizedString("msg.microwaverec.generating") + powerMadeLastTick + " " + LibVulpes.proxy.getLocalizedString("msg.powerunit.rfpertick"));
	}

	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		CompoundNBT nbt = new CompoundNBT();
		nbt.putBoolean("canRender", canRender);
		nbt.putInt("amtPwr", powerMadeLastTick);
		writeNetworkData(nbt);
		return new SUpdateTileEntityPacket(pos, 0, nbt);
	}

	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		CompoundNBT nbt = pkt.getNbtCompound();

		canRender = nbt.getBoolean("canRender");
		powerMadeLastTick = nbt.getInt("amtPwr");
		readNetworkData(nbt);
	}
	
	@Override
	public CompoundNBT getUpdateTag() {
		CompoundNBT nbt = new CompoundNBT();
		nbt.putBoolean("canRender", canRender);
		nbt.putInt("amtPwr", powerMadeLastTick);
		write(nbt);
		return nbt;
	}
	
	@Override
	public void handleUpdateTag(BlockState state, CompoundNBT nbt) {
		powerMadeLastTick = nbt.getInt("amtPwr");
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
	public CompoundNBT write(CompoundNBT nbt) {
		super.write(nbt);

		int[] intArray = new int[connectedSatellites.size()*2];

		for( int i =0; i < connectedSatellites.size()*2; i += 2 ) {
			connectedSatellites.get(i/2);
			intArray[i] = (int) (connectedSatellites.get(i/2) & 0xFFFFFFFF);
			intArray[i+1] = (int) ((connectedSatellites.get(i/2) >>> 32) & 0xFFFFFFFF);
		}

		nbt.putIntArray("satilliteList", intArray);

		return nbt;
	}

	@Override
	public void func_230337_a_(BlockState state, CompoundNBT nbt) {
		super.func_230337_a_(state, nbt);

		int intArray[] = nbt.getIntArray("satilliteList");
		connectedSatellites.clear();
		for( int i =0; i < intArray.length/2; i+=2 ) {
			connectedSatellites.add(intArray[i] | (((long)intArray[i+1]) << 32));
		}

	}

}
