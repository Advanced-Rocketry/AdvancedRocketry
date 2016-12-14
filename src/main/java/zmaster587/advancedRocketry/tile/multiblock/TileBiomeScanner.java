package zmaster587.advancedRocketry.tile.multiblock;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeManager.BiomeEntry;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import zmaster587.advancedRocketry.api.stations.ISpaceObject;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.dimension.DimensionProperties;
import zmaster587.advancedRocketry.stations.SpaceObjectManager;
import zmaster587.libVulpes.inventory.TextureResources;
import zmaster587.libVulpes.inventory.modules.ModuleBase;
import zmaster587.libVulpes.inventory.modules.ModuleContainerPan;
import zmaster587.libVulpes.inventory.modules.ModuleImage;
import zmaster587.libVulpes.inventory.modules.ModuleText;
import zmaster587.libVulpes.tile.multiblock.TileMultiPowerConsumer;
import zmaster587.libVulpes.util.IconResource;

public class TileBiomeScanner extends TileMultiPowerConsumer {

	private static final Object[][][] structure = new Object[][][]{

		{	{null, null, null, null, null}, 
			{null, null, null, null, null},
			{null, null, 'c', null, null},
			{null, null, null, null, null},
			{null, null, null, null, null}},

			{	{null, null, null, null, null}, 
				{null, null, null, null, null},
				{null, null, AdvancedRocketryBlocks.blockMotor, null, null},
				{null, null, null, null, null},
				{null, null, null, null, null}},

				{	{null,Blocks.iron_block,Blocks.iron_block,Blocks.iron_block,null}, 
					{Blocks.iron_block, Blocks.iron_block, Blocks.iron_block, Blocks.iron_block, Blocks.iron_block},
					{Blocks.iron_block, Blocks.iron_block, Blocks.iron_block, Blocks.iron_block, Blocks.iron_block},
					{Blocks.iron_block, Blocks.iron_block, Blocks.iron_block, Blocks.iron_block, Blocks.iron_block},
					{null,Blocks.iron_block,Blocks.iron_block,Blocks.iron_block,null}},

					{	{Blocks.air, Blocks.air, Blocks.air, Blocks.air, Blocks.air}, 
						{Blocks.air, Blocks.air, Blocks.air, Blocks.air, Blocks.air},
						{Blocks.air, Blocks.air, Blocks.redstone_block, Blocks.air, Blocks.air},
						{Blocks.air, Blocks.air, Blocks.air, Blocks.air, Blocks.air},
						{Blocks.air, Blocks.air, Blocks.air, Blocks.air, Blocks.air}}};


	@Override
	public Object[][][] getStructure() {
		return structure;
	}

	@Override
	public List<ModuleBase> getModules(int ID, EntityPlayer player) {
		List<ModuleBase> list = new LinkedList<ModuleBase>();//super.getModules(ID, player);

		boolean suitable = true;
		for(int y = this.yCoord - 4; y > 0; y--) {
			if(!worldObj.isAirBlock(this.xCoord, y, this.zCoord)) {
				suitable = false;
				break;
			}
		}

		if(worldObj.isRemote) {
			list.add(new ModuleImage(24, 14, zmaster587.advancedRocketry.inventory.TextureResources.earthCandyIcon));
		}

		ISpaceObject spaceObject = SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(this.xCoord, this.zCoord);
		if(suitable && SpaceObjectManager.WARPDIMID != spaceObject.getOrbitingPlanetId()) {

			DimensionProperties properties = DimensionManager.getInstance().getDimensionProperties(spaceObject.getOrbitingPlanetId());
			List<ModuleBase> list2 = new LinkedList<ModuleBase>();
			if(properties.isGasGiant()) {
				list2.add(new ModuleText(32, 16, "nyehhh, Gassy, ain't it?", 0x202020));
			} else {
				

				int i = 0;
				if(properties.getId() == 0) {
					for(BiomeGenBase biome : BiomeGenBase.getBiomeGenArray()) {
						if(biome != null)
							list2.add(new ModuleText(32, 16 + 12*(i++), biome.biomeName, 0x202020));
					}
				}
				else
					for(BiomeEntry biome : properties.getBiomes()) {
						list2.add(new ModuleText(32, 16 + 12*(i++), biome.biome.biomeName, 0x202020));
					}
			}
			//Relying on a bug, is this safe?
			ModuleContainerPan pan = new ModuleContainerPan(0, 16, list2, new LinkedList<ModuleBase>(), null, 148, 128, 0, -64, 0, 1000);
			list.add(pan);
		}
		else
			list.add(new ModuleText(32, 16, EnumChatFormatting.OBFUSCATED + "Foxes, that is all", 0x202020));

		return list;
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return AxisAlignedBB.getBoundingBox(xCoord -5,yCoord -3, zCoord -5, xCoord +5, yCoord + 3, zCoord + 5);
	}

	@Override
	public String getMachineName() {
		return "tile.biomeScanner.name";
	}
}
