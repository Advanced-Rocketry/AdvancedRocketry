package zmaster587.advancedRocketry.block.plant;

import java.util.List;
import java.util.Random;

import zmaster587.advancedRocketry.world.gen.WorldGenAlienTree;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSapling;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public class BlockAlienSapling extends BlockSapling {

	public String[] names = new String[] { "blueTree" };

	public IIcon[] icons = new IIcon[names.length];

	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item item, CreativeTabs creativeTab, List list)
	{
		list.add(new ItemStack(item, 1, 0));
	}

	/**
	 * Gets the block's texture. Args: side, meta
	 */
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta)
	{
		meta &= 7;
		return icons[MathHelper.clamp_int(meta, 0, names.length)];
	}

	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconRegister)
	{
		for (int i = 0; i < names.length; ++i)
		{
			icons[i] = iconRegister.registerIcon(this.getTextureName() + "_" + names[i]);
		}
	}

	@Override
	public void func_149878_d(World world, int x, int y, int z, Random random)
	{
		if (!net.minecraftforge.event.terraingen.TerrainGen.saplingGrowTree(world, random, x, y, z)) 
			return;

		int l = world.getBlockMetadata(x, y, z) & 7;
		Object object = new WorldGenAlienTree(true);
		int i1 = 0;
		int j1 = 0;

		if (!((WorldGenerator)object).generate(world, random, x + i1, y, z + j1))
		{
			world.setBlock(x, y, z, this, l, 4);
		}
	}
}
