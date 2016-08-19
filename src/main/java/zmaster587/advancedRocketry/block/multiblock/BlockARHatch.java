package zmaster587.advancedRocketry.block.multiblock;

import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import zmaster587.advancedRocketry.tile.TileRocketLoader;
import zmaster587.advancedRocketry.tile.TileRocketUnloader;
import zmaster587.advancedRocketry.tile.hatch.TileDataBus;
import zmaster587.advancedRocketry.tile.hatch.TileSatelliteHatch;
import zmaster587.libVulpes.block.multiblock.BlockHatch;

public class BlockARHatch extends BlockHatch {

	public BlockARHatch(Material material) {
		super(material);
	}

	IIcon data, satellite;
	
	@Override
	public void registerBlockIcons(IIconRegister iconRegister) {
		super.registerBlockIcons(iconRegister);
		data = iconRegister.registerIcon("advancedrocketry:dataHatch");
		satellite = iconRegister.registerIcon("advancedrocketry:satelliteBay");
	}
	
	@Override
	public IIcon getIcon(int side, int meta) {
		if((meta & 7) == 0) {
			return data;
		}else if((meta & 7) == 1 ) {
			return satellite;
		}else if((meta & 7) == 2) {
			return output;
		}
		else
			return blockIcon;
	}
	
	@Override
	public void getSubBlocks(Item item, CreativeTabs tab,
			List list) {
		list.add(new ItemStack(item, 1, 0));
		list.add(new ItemStack(item, 1, 1));
		list.add(new ItemStack(item, 1, 2));
		list.add(new ItemStack(item, 1, 3));
	}
	
	@Override
	public TileEntity createTileEntity(World world, int metadata) {
		//TODO: multiple sized Hatches
		if((metadata & 7) == 0)
			return new TileDataBus(4);
		else if((metadata & 7) == 1)
			return new TileSatelliteHatch(1);	
		else if((metadata & 7) == 2)
			return new TileRocketUnloader(4);
		else if((metadata & 7) == 3)
			return new TileRocketLoader(4);
		
		return null;
	}
}
