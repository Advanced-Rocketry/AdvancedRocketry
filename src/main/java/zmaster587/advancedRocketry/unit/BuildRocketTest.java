package zmaster587.advancedRocketry.unit;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import zmaster587.advancedRocketry.api.AdvancedRocketryItems;
import zmaster587.advancedRocketry.entity.EntityRocket;
import zmaster587.advancedRocketry.item.ItemStationChip;
import zmaster587.advancedRocketry.tile.TileRocketBuilder;
import zmaster587.advancedRocketry.world.provider.WorldProviderSpace;
import zmaster587.libVulpes.api.LibVulpesBlocks;
import zmaster587.libVulpes.block.RotatableBlock;

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
	int originalWorldId;
	BlockPos originalPos;
	
	BuildRocketTest()
	{
		this.name = "Basic Rocket Tests";
	}
	
	public void Phase1(World world, EntityPlayer player)
	{
		originalWorldId = world.provider.getDimension();
		teleportPlayerToStart(player);
		clearLandscape(world);
		buildBasicRocketPlatform(world, player);
		buildBasicRocketStructure(world, player, new BlockPos(3,65,3));
		buildRocket(world, player);
		
		try {
			IngameTestOrchestrator.scheduleEvent(world, 150, BuildRocketTest.class.getDeclaredMethod("Phase2", World.class, EntityPlayer.class), this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void Phase2(World world, EntityPlayer player)
	{
		EntityRocket rocket = findRocketOnPad(world);
		originalPos = rocket.getPosition();
		FuelRocket(rocket);
		putStationIntoRocket(rocket, 1);
		mountPlayerToRocket(player, rocket);
		rocket.prepareLaunch();
		
		try {
			IngameTestOrchestrator.scheduleEvent(world, 1500, BuildRocketTest.class.getDeclaredMethod("Phase3", World.class, EntityPlayer.class), this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public void Phase3(World world, EntityPlayer player)
	{
		// Make sure we're in space and riding a rocket
		if(!(player.world.provider instanceof WorldProviderSpace))
			throw new AssertionError("Expected to be on space station!");
		if(!(player.getRidingEntity() instanceof EntityRocket))
			throw new AssertionError("Expected player to be riding a rocket!");
		
		((EntityRocket)player.getRidingEntity()).prepareLaunch();
		
		try {
			IngameTestOrchestrator.scheduleEvent(world, 1600, BuildRocketTest.class.getDeclaredMethod("Phase4", World.class, EntityPlayer.class), this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void Phase4(World world, EntityPlayer player)
	{
		// Make sure we're in space and riding a rocket
		if(player.world.provider.getDimension() != originalWorldId)
			throw new AssertionError("Expected to return to the same world we started from");
		if(!(player.getRidingEntity() instanceof EntityRocket))
			throw new AssertionError("Expected player to be riding a rocket");
		if(!player.getRidingEntity().getPosition().equals(originalPos) )
			throw new AssertionError("Expected to land near where we took off!");
		
		success();
	}

	public void teleportPlayerToStart(EntityPlayer player)
	{
		player.setPositionAndUpdate(0, 100, 0);
	}
	
	public void clearLandscape(World world)
	{
		for(int x = -20; x < 20; x++)
		{
			for(int z = -20; z <= 20; z++)
			{
				for(int y = 62; y < 250; y++)
				{
					world.setBlockToAir(new BlockPos(x,y,z));
				}
			}
		}
	}
	
	public void buildBasicRocketPlatform(World world, EntityPlayer player)
	{
		final int padSize = 5;
		final int height = 6;
		BlockPos builderPos = new BlockPos(padSize/2, 65, -1);
		rocketBuilderPos = builderPos;
		// Build 5x5 pad
		for(int x = 0; x <= padSize; x++)
		{
			for(int z = 0; z <= padSize; z++)
			{
				world.setBlockState(new BlockPos(x, 64,z), AdvancedRocketryBlocks.blockLaunchpad.getDefaultState());
			}
		}
		
		//Structure tower
		for(int y = 0; y <= height; y++)
		{
			world.setBlockState(new BlockPos(-1, 64+y, height/2), AdvancedRocketryBlocks.blockStructureTower.getDefaultState());
		}
		
		world.setBlockState(builderPos, AdvancedRocketryBlocks.blockRocketBuilder.getDefaultState().withProperty(RotatableBlock.FACING, EnumFacing.NORTH));
		world.setBlockState(builderPos.up(), LibVulpesBlocks.blockCreativeInputPlug.getDefaultState());
		
		TileEntity tile = world.getTileEntity(builderPos);
		
		if(!(tile instanceof TileRocketBuilder))
			throw new AssertionError("Expected tile rocket builder!");
		if(((TileRocketBuilder)tile).getRocketPadBounds(world, builderPos) == null)
			throw new AssertionError("Invalid Rocket pad!");
	}
	
	public void buildBasicRocketStructure(World world, EntityPlayer player, BlockPos centerBottom)
	{
		final int centerX = centerBottom.getX();
		final int bottomY = centerBottom.getY();
		final int centerZ = centerBottom.getZ();
		
		world.setBlockState(new BlockPos(centerX-1,  bottomY, centerZ), AdvancedRocketryBlocks.blockAdvEngine.getDefaultState());
		world.setBlockState(new BlockPos(centerX+1,  bottomY, centerZ), AdvancedRocketryBlocks.blockAdvEngine.getDefaultState());
		
		for(int xOffset=-1; xOffset <= 1; xOffset++)
			for(int yOffset=1; yOffset <= 2; yOffset++)
				world.setBlockState(new BlockPos(centerX+xOffset,  bottomY+yOffset, centerZ), AdvancedRocketryBlocks.blockFuelTank.getDefaultState());
		
		world.setBlockState(new BlockPos(centerX,  bottomY+3, centerZ), AdvancedRocketryBlocks.blockGuidanceComputer.getDefaultState());
		world.setBlockState(new BlockPos(centerX,  bottomY+4, centerZ), AdvancedRocketryBlocks.blockGenericSeat.getDefaultState());
	}
	
	public void buildRocket(World world, EntityPlayer player)
	{
		buildRocket(world, player, rocketBuilderPos);
	}
	
	public void buildRocket(World world, EntityPlayer player, BlockPos tilePos)
	{
		TileEntity tile = world.getTileEntity(tilePos);
		if(!(tile instanceof TileRocketBuilder))
			throw new AssertionError("Expected tile rocket builder!");
		
		if(((TileRocketBuilder)tile).getRocketPadBounds(world, tilePos) == null)
			throw new AssertionError("Invalid Rocket pad!");
		
		//Build the rocket
		((TileRocketBuilder)tile).useNetworkData(player, Side.SERVER, (byte) 1, new NBTTagCompound());
	}
	
	public void mountPlayerToRocket(EntityPlayer player, EntityRocket rocket)
	{
		player.startRiding(rocket);
	}
	
	public void putStationIntoRocket(EntityRocket rocket, int stationId)
	{
		ItemStack stack = new ItemStack(AdvancedRocketryItems.itemSpaceStationChip);
		ItemStationChip.setUUID(stack, stationId);
		rocket.storage.getGuidanceComputer().setInventorySlotContents(0, stack);
	}
	
	
	public void FuelRocket(EntityRocket rocket)
	{
		rocket.setFuelAmountMonoproellant(rocket.getFuelCapacityMonopropellant());
	}
	
	public EntityRocket findRocketOnPad(World world)
	{	
		EntityRocket rocket = finishBuildingRocket(world);
		
		return rocket;
	}
	
	public EntityRocket finishBuildingRocket(World world)
	{
		List<EntityRocket> list = world.getEntitiesWithinAABB(EntityRocket.class, new AxisAlignedBB(new BlockPos(0,64,0), new BlockPos(6,72,6)));
		
		if(list.size() != 1)
			throw new AssertionError("Cannot find rocket!");
		
		return list.get(0);
	}
}
