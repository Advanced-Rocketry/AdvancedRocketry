package zmaster587.advancedRocketry.block;

import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import cpw.mods.fml.common.registry.GameRegistry;
import zmaster587.advancedRocketry.api.AdvRocketryBlocks;
import zmaster587.advancedRocketry.api.AdvancedRocketryItems;
import zmaster587.advancedRocketry.api.MaterialRegistry;
import zmaster587.advancedRocketry.api.MaterialRegistry.AllowedProducts;
import zmaster587.advancedRocketry.item.ItemOre;
import zmaster587.advancedRocketry.item.ItemOreProduct;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.oredict.OreDictionary;

public class BlockOre extends Block {
	MaterialRegistry.Materials[] ores = new MaterialRegistry.Materials[16];
	IIcon[] textures = new IIcon[16];
	byte numBlocks;
	AllowedProducts product;
	
	public BlockOre(Material material) {
		super(material);
	}
	
	public AllowedProducts getProduct() {
		return product;
	}

	@Override
	public String getUnlocalizedName() {
		return super.getUnlocalizedName();
	}

	@Override
	public IIcon getIcon(int side, int meta) {
		return textures[meta];
	}

	@Override
	public void getSubBlocks(Item item, CreativeTabs tab,
			List list) {
		for(int i = 0; i < numBlocks; i++)
			if(product.isOfType(ores[i].getAllowedProducts()))
				list.add(new ItemStack(item, 1, i));
	}

	@Override
	public void registerBlockIcons(IIconRegister iconRegister) {
		for(int i = 0; i < numBlocks; i++) {
			if(product.isOfType(ores[i].getAllowedProducts()) )
				textures[i] = iconRegister.registerIcon("advancedrocketry:" + textureName + ores[i].getUnlocalizedName());
		}
	}

	@Override
	public int damageDropped(int meta) {
		return meta;
	}

	public String getUnlocalizedName(int itemDamage) {
		return  "material." + ores[itemDamage].getUnlocalizedName();
	}

	public static void registerOres(CreativeTabs tab) {
		int len = MaterialRegistry.Materials.values().length;
		int numberOfOreBlocks = (len/16) + 1;
		BlockOre ores;
		BlockOre metalBlocks;
		BlockOre coilBlocks;

		AdvancedRocketryItems.itemOreProduct = new ItemOreProduct[MaterialRegistry.AllowedProducts.values().length];

		for(int i = 0; i < MaterialRegistry.AllowedProducts.values().length; i++) {
			AdvancedRocketryItems.itemOreProduct[i] = new ItemOreProduct(MaterialRegistry.AllowedProducts.values()[i].name().toLowerCase());
			GameRegistry.registerItem(AdvancedRocketryItems.itemOreProduct[i], "product" + MaterialRegistry.AllowedProducts.values()[i].name().toLowerCase());
		}

		for(int i = 0; i < numberOfOreBlocks; i++) {

			String name = "ore";
			String metalBlockName = "metal";
			String coilName = "coil";

			metalBlocks = new BlockMetalBlock(Material.rock);
			metalBlocks.setBlockName(metalBlockName).setCreativeTab(tab).setHardness(4f).setBlockTextureName("block");
			metalBlocks.numBlocks = (byte)Math.min(len - (16*i), 16);
			metalBlocks.product = AllowedProducts.BLOCK;
			
			ores = new BlockOre(Material.rock);
			ores.setBlockName(name).setCreativeTab(tab).setHardness(4f).setBlockTextureName("ore");
			ores.numBlocks = (byte)Math.min(len - (16*i), 16);
			ores.product = AllowedProducts.ORE;
			
			coilBlocks = new BlockCoil(Material.rock, "advancedrocketry:coilSide", "advancedrocketry:coilPole");
			coilBlocks.setBlockName(coilName).setCreativeTab(tab).setHardness(4f).setBlockTextureName("coil");
			coilBlocks.numBlocks = (byte)Math.min(len - (16*i), 16);
			coilBlocks.product = AllowedProducts.COIL;

			GameRegistry.registerBlock(ores, ItemOre.class, name + i);
			GameRegistry.registerBlock(metalBlocks, ItemOre.class, metalBlockName + i);
			GameRegistry.registerBlock(coilBlocks, ItemOre.class, coilName + i);
			
			for(int j = 0; j < 16 && j < 16*i + (len % 16); j++) {
				int index = i*16 + j;
				MaterialRegistry.Materials ore = MaterialRegistry.Materials.values()[index];

				ores.ores[j] = ore;
				ores.setHarvestLevel(ore.getTool(), ore.getHarvestLevel(), j);
				
				metalBlocks.ores[j] = ore;
				metalBlocks.setHarvestLevel(ore.getTool(), ore.getHarvestLevel(), j);

				coilBlocks.ores[j] = ore;
				coilBlocks.setHarvestLevel(ore.getTool(), ore.getHarvestLevel(), j);
				
				for(MaterialRegistry.AllowedProducts product : MaterialRegistry.AllowedProducts.values()) {
					if(!product.isBlock() && product.isOfType(ore.getAllowedProducts()))
						((ItemOreProduct)AdvancedRocketryItems.itemOreProduct[product.ordinal()]).registerItem(index, ore);
				}

				for(String str : ore.getOreDictNames()) {
					OreDictionary.registerOre("ore" + str, new ItemStack(ores, 1 , j));
					OreDictionary.registerOre("block" + str, new ItemStack(metalBlocks, 1 , j));
					OreDictionary.registerOre("coil" + str, new ItemStack(coilBlocks, 1 , j));
				}
			}
			AdvRocketryBlocks.blockMetal.add(metalBlocks);
			AdvRocketryBlocks.blockCoil.add(coilBlocks);
			AdvRocketryBlocks.blockOre.add(ores);
		}
	}
}
