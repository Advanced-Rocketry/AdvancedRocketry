package zmaster587.advancedRocketry.tile.multiblock;

import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.biome.Biome;
import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.api.AdvancedRocketryBiomes;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import zmaster587.advancedRocketry.api.AdvancedRocketryTileEntityType;
import zmaster587.advancedRocketry.api.stations.ISpaceObject;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.dimension.DimensionProperties;
import zmaster587.advancedRocketry.stations.SpaceObjectManager;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.api.LibVulpesBlocks;
import zmaster587.libVulpes.inventory.modules.ModuleBase;
import zmaster587.libVulpes.inventory.modules.ModuleContainerPan;
import zmaster587.libVulpes.inventory.modules.ModuleImage;
import zmaster587.libVulpes.inventory.modules.ModuleText;
import zmaster587.libVulpes.tile.multiblock.TileMultiPowerConsumer;

import javax.annotation.Nonnull;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class TileBiomeScanner extends TileMultiPowerConsumer {

	public TileBiomeScanner() {
		super(AdvancedRocketryTileEntityType.TILE_BIOME_SCANNER);
	}

	private static final Object[][][] structure = new Object[][][]{

		{	{null, null, null, null, null}, 
			{null, null, null, null, null},
			{null, null, 'c', null, null},
			{null, null, null, null, null},
			{null, null, null, null, null}},

			{	{null, null, null, null, null}, 
				{null, null, null, null, null},
				{null, null, LibVulpesBlocks.motors, null, null},
				{null, null, null, null, null},
				{null, null, null, null, null}},

				{	{null,new ResourceLocation("forge", "storage_blocks/aluminum"),new ResourceLocation("forge", "storage_blocks/aluminum"),new ResourceLocation("forge", "storage_blocks/aluminum"),null},
					{new ResourceLocation("forge", "storage_blocks/aluminum"), new ResourceLocation("forge", "storage_blocks/aluminum"), AdvancedRocketryBlocks.blockStructureTower, new ResourceLocation("forge", "storage_blocks/aluminum"), new ResourceLocation("forge", "storage_blocks/aluminum")},
					{new ResourceLocation("forge", "storage_blocks/aluminum"), AdvancedRocketryBlocks.blockStructureTower, LibVulpesBlocks.blockMachineStructure, AdvancedRocketryBlocks.blockStructureTower, new ResourceLocation("forge", "storage_blocks/aluminum")},
					{new ResourceLocation("forge", "storage_blocks/aluminum"), new ResourceLocation("forge", "storage_blocks/aluminum"), AdvancedRocketryBlocks.blockStructureTower, new ResourceLocation("forge", "storage_blocks/aluminum"), new ResourceLocation("forge", "storage_blocks/aluminum")},
					{null,new ResourceLocation("forge", "storage_blocks/aluminum"),new ResourceLocation("forge", "storage_blocks/aluminum"),new ResourceLocation("forge", "storage_blocks/aluminum"),null}},

					{	{Blocks.AIR, Blocks.AIR, Blocks.AIR, Blocks.AIR, Blocks.AIR}, 
						{Blocks.AIR, Blocks.AIR, Blocks.AIR, Blocks.AIR, Blocks.AIR},
						{Blocks.AIR, Blocks.AIR, Blocks.REDSTONE_BLOCK, Blocks.AIR, Blocks.AIR},
						{Blocks.AIR, Blocks.AIR, Blocks.AIR, Blocks.AIR, Blocks.AIR},
						{Blocks.AIR, Blocks.AIR, Blocks.AIR, Blocks.AIR, Blocks.AIR}}};


	@Override
	public Object[][][] getStructure() {
		return structure;
	}

	@Override
	public List<ModuleBase> getModules(int ID, PlayerEntity player) {
		List<ModuleBase> list = new LinkedList<>();//super.getModules(ID, player);

		boolean suitable = true;
		for(int y = this.getPos().getY() - 4; y > 0; y--) {
			if(!world.isAirBlock(new BlockPos( this.getPos().getX(), y, this.getPos().getZ()))) {
				suitable = false;
				break;
			}
		}

		if(world.isRemote) {
			list.add(new ModuleImage(24, 14, zmaster587.advancedRocketry.inventory.TextureResources.earthCandyIcon));

			ISpaceObject spaceObject = SpaceObjectManager.getSpaceManager().getSpaceStationFromBlockCoords(pos);
			if(suitable && !SpaceObjectManager.WARPDIMID.equals(spaceObject.getOrbitingPlanetId())) {

				DimensionProperties properties = DimensionManager.getInstance().getDimensionProperties(spaceObject.getOrbitingPlanetId());
				List<ModuleBase> list2 = new LinkedList<>();
				if(properties.isGasGiant()) {
					list2.add(new ModuleText(32, 16, LibVulpes.proxy.getLocalizedString("msg.biomescanner.gas"), 0x202020));
				} 
				else if (properties.isStar()) {
					list2.add(new ModuleText(32, 16, LibVulpes.proxy.getLocalizedString("msg.biomescanner.star"), 0x202020));
				} else {


					int i = 0;
					if(properties.getId() == DimensionManager.overworldProperties.getId()) {
						Iterator<Biome> itr = AdvancedRocketryBiomes.getAllBiomes();
						while (itr.hasNext()) {
							Biome biome = itr.next();
							if(biome != null)
								list2.add(new ModuleText(32, 16 + 12*(i++), AdvancedRocketry.proxy.getNameFromBiome(biome), 0x202020));
						}
					}
					else {
						for (Biome biome : properties.getBiomes()) {
							list2.add(new ModuleText(32, 16 + 12 * (i++), AdvancedRocketry.proxy.getNameFromBiome(biome), 0x202020));
						}
					}
				}
				//Relying on a bug, is this safe?
				ModuleContainerPan pan = new ModuleContainerPan(0, 16, list2, new LinkedList<>(), null, 148, 110, 0, -64, 0, 1000);
				list.add(pan);
			}
			else
				list.add(new ModuleText(32, 16, TextFormatting.OBFUSCATED + "Foxes, that is all", 0x202020));
		}

		return list;
	}

	@Override
	@Nonnull
	public AxisAlignedBB getRenderBoundingBox() {

		return new AxisAlignedBB(pos.add(-5,-3,-5),pos.add(5,3,5));
	}

	@Override
	public String getMachineName() {
		return "block.advancedrocketry.biomescanner";
	}
}
