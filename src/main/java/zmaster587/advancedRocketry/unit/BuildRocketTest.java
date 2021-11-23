package zmaster587.advancedRocketry.unit;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import zmaster587.advancedRocketry.api.AdvancedRocketryItems;
import zmaster587.advancedRocketry.api.fuel.FuelRegistry;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.entity.EntityRocket;
import zmaster587.advancedRocketry.item.ItemStationChip;
import zmaster587.advancedRocketry.tile.TileRocketAssembler;
import zmaster587.libVulpes.api.LibVulpesBlocks;
import zmaster587.libVulpes.block.RotatableBlock;
import zmaster587.libVulpes.util.ZUtils;

import java.util.List;

public class BuildRocketTest extends BaseTest {
	
	/*
	 * Clear land for launch pad
	 * Build launch pad
	 * Build rocket on launch pad
	 * Build rocket
	 * Wait for Builder to build rocket
	 * Fuel Rocket
	 * Put station chip into rocket
	 * Go to station
	 * Go back
	 * Check for same position
	 */

	BlockPos rocketBuilderPos;
	ResourceLocation originalWorldId;
	BlockPos originalPos;
	
	BuildRocketTest()
	{
		this.name = "Basic Rocket Tests";
	}
	
	public void Phase1(World world, PlayerEntity player) {
		originalWorldId = ZUtils.getDimensionIdentifier(world);
		teleportPlayerToStart(player);
		clearLandscape(world);
		buildBasicRocketPlatform(world, player);
		buildBasicRocketStructure(world, player, new BlockPos(3,65,3));
		buildRocket(world, player);
		
		try {
			IngameTestOrchestrator.scheduleEvent(world, 150, BuildRocketTest.class.getDeclaredMethod("Phase2", World.class, PlayerEntity.class), this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void Phase2(World world, PlayerEntity player) {
		EntityRocket rocket = findRocketOnPad(world);
		originalPos = new BlockPos(rocket.getPositionVec());
		FuelRocket(rocket);
		putStationIntoRocket(rocket, new ResourceLocation("station", "1"));
		mountPlayerToRocket(player, rocket);
		rocket.prepareLaunch();
		
		try {
			IngameTestOrchestrator.scheduleEvent(world, 1500, BuildRocketTest.class.getDeclaredMethod("Phase3", World.class, PlayerEntity.class), this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public void Phase3(World world, PlayerEntity player) {
		// Make sure we're in space and riding a rocket
		if(!(DimensionManager.spaceId.equals(ZUtils.getDimensionIdentifier(world))))
			throw new AssertionError("Expected to be on space station!");
		if(!(player.getRidingEntity() instanceof EntityRocket))
			throw new AssertionError("Expected player to be riding a rocket!");
		
		((EntityRocket)player.getRidingEntity()).prepareLaunch();
		
		try {
			IngameTestOrchestrator.scheduleEvent(world, 1600, BuildRocketTest.class.getDeclaredMethod("Phase4", World.class, PlayerEntity.class), this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void Phase4(World world, PlayerEntity player) {
		// Make sure we're in space and riding a rocket
		if(ZUtils.getDimensionIdentifier(player.world) != originalWorldId)
			throw new AssertionError("Expected to return to the same world we started from");
		if(!(player.getRidingEntity() instanceof EntityRocket))
			throw new AssertionError("Expected player to be riding a rocket");
		if(!new BlockPos(player.getRidingEntity().getPositionVec()).equals(originalPos) )
			throw new AssertionError("Expected to land near where we took off!");
		
		success();
	}

	public void teleportPlayerToStart(PlayerEntity player)
	{
		player.setPositionAndUpdate(0, 100, 0);
	}
	
	public void clearLandscape(World world) {
		for(int x = -20; x < 20; x++) {
			for(int z = -20; z <= 20; z++) {
				for(int y = 62; y < 250; y++) {
					world.removeBlock(new BlockPos(x,y,z), false);
				}
			}
		}
	}
	
	public void buildBasicRocketPlatform(World world, PlayerEntity player) {
		final int padSize = 5;
		final int height = 6;
		BlockPos builderPos = new BlockPos(padSize/2, 65, -1);
		rocketBuilderPos = builderPos;
		// Build 5x5 pad
		for(int x = 0; x <= padSize; x++) {
			for(int z = 0; z <= padSize; z++) {
				world.setBlockState(new BlockPos(x, 64,z), AdvancedRocketryBlocks.blockLaunchpad.getDefaultState());
			}
		}
		
		//Structure tower
		for(int y = 0; y <= height; y++) {
			world.setBlockState(new BlockPos(-1, 64+y, height/2), AdvancedRocketryBlocks.blockStructureTower.getDefaultState());
		}
		
		world.setBlockState(builderPos, AdvancedRocketryBlocks.blockRocketAssembler.getDefaultState().with(RotatableBlock.FACING, Direction.NORTH));
		world.setBlockState(builderPos.up(), LibVulpesBlocks.blockCreativeInputPlug.getDefaultState());
		
		TileEntity tile = world.getTileEntity(builderPos);
		
		if(!(tile instanceof TileRocketAssembler))
			throw new AssertionError("Expected tile rocket builder!");
		if(((TileRocketAssembler)tile).getRocketPadBounds(world, builderPos) == null)
			throw new AssertionError("Invalid Rocket pad!");
	}
	
	public void buildBasicRocketStructure(World world, PlayerEntity player, BlockPos centerBottom) {
		final int centerX = centerBottom.getX();
		final int bottomY = centerBottom.getY();
		final int centerZ = centerBottom.getZ();
		
		world.setBlockState(new BlockPos(centerX-1,  bottomY, centerZ), AdvancedRocketryBlocks.blockAdvancedMonopropellantEngine.getDefaultState());
		world.setBlockState(new BlockPos(centerX+1,  bottomY, centerZ), AdvancedRocketryBlocks.blockAdvancedMonopropellantEngine.getDefaultState());
		
		for(int xOffset=-1; xOffset <= 1; xOffset++)
			for(int yOffset=1; yOffset <= 2; yOffset++)
				world.setBlockState(new BlockPos(centerX+xOffset,  bottomY+yOffset, centerZ), AdvancedRocketryBlocks.blockMonopropellantFuelTank.getDefaultState());
		
		world.setBlockState(new BlockPos(centerX,  bottomY+3, centerZ), AdvancedRocketryBlocks.blockGuidanceComputer.getDefaultState());
		world.setBlockState(new BlockPos(centerX,  bottomY+4, centerZ), AdvancedRocketryBlocks.blockSeat.getDefaultState());
	}
	
	public void buildRocket(World world, PlayerEntity player)
	{
		buildRocket(world, player, rocketBuilderPos);
	}
	
	public void buildRocket(World world, PlayerEntity player, BlockPos tilePos) {
		TileEntity tile = world.getTileEntity(tilePos);
		if(!(tile instanceof TileRocketAssembler))
			throw new AssertionError("Expected tile rocket builder!");
		
		if(((TileRocketAssembler)tile).getRocketPadBounds(world, tilePos) == null)
			throw new AssertionError("Invalid Rocket pad!");
		
		//Build the rocket
		((TileRocketAssembler)tile).useNetworkData(player, Dist.DEDICATED_SERVER, (byte) 1, new CompoundNBT());
	}
	
	public void mountPlayerToRocket(PlayerEntity player, EntityRocket rocket)
	{
		player.startRiding(rocket);
	}
	
	public void putStationIntoRocket(EntityRocket rocket, ResourceLocation stationId) {
		ItemStack stack = new ItemStack(AdvancedRocketryItems.itemSpaceStationChip);
		ItemStationChip.setUUID(stack, stationId);
		rocket.storage.getGuidanceComputer().setInventorySlotContents(0, stack);
	}
	
	
	public void FuelRocket(EntityRocket rocket) {
		rocket.setFuelAmount(FuelRegistry.FuelType.LIQUID_MONOPROPELLANT, rocket.getFuelCapacity(FuelRegistry.FuelType.LIQUID_MONOPROPELLANT));
	}
	
	public EntityRocket findRocketOnPad(World world)
	{
		return finishBuildingRocket(world);
	}
	
	public EntityRocket finishBuildingRocket(World world) {
		List<EntityRocket> list = world.getEntitiesWithinAABB(EntityRocket.class, new AxisAlignedBB(new BlockPos(0,64,0), new BlockPos(6,72,6)));
		
		if(list.size() != 1)
			throw new AssertionError("Cannot find rocket!");
		
		return list.get(0);
	}
}
