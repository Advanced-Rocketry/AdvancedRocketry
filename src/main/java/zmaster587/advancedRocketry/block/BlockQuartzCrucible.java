package zmaster587.advancedRocketry.block;

import java.util.Random;

import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.api.AdvancedRocketryItems;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockCauldron;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class BlockQuartzCrucible extends BlockCauldron {

	private static IIcon innerIcon, topIcon, bottomIcon;
	
	public BlockQuartzCrucible() {
		super();
		setHardness(1F).setResistance(8F).setBlockTextureName("qcrucible");
	}
    
    @Override
    public void registerBlockIcons(IIconRegister par1IconRegister)
    {
        this.innerIcon = par1IconRegister.registerIcon("advancedRocketry:" + this.getTextureName() + "_inner");
        this.topIcon = par1IconRegister.registerIcon("advancedRocketry:" + this.getTextureName() + "_top");
        this.bottomIcon = par1IconRegister.registerIcon("advancedRocketry:" + this.getTextureName() + "_bottom");
        this.blockIcon = par1IconRegister.registerIcon("advancedRocketry:" + this.getTextureName() + "_side");
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(int par1, int par2)
    {
        return par1 == 1 ? this.topIcon : (par1 == 0 ? this.bottomIcon : this.blockIcon);
    }
    
    @Override
    public boolean onBlockActivated(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer, int par6, float par7, float par8, float par9) { return false; }
    
    @Override
    public void fillWithRain(World par1World, int par2, int par3, int par4){}
    
    
    
    @Override
    public Item getItemDropped(int par1, Random par2Random, int par3)
    {	
    	return AdvancedRocketryItems.quartzCrucible;
    }
    
    @SideOnly(Side.CLIENT)
    public Item getItem(World p_149694_1_, int p_149694_2_, int p_149694_3_,
    		int p_149694_4_) {
    	return getItemPicked(p_149694_1_, p_149694_2_, p_149694_3_, p_149694_4_);
    }
    
    @SideOnly(Side.CLIENT)
    public Item getItemPicked(World par1World, int par2, int par3, int par4)
    {
        return AdvancedRocketryItems.quartzCrucible;
    }
 
    @SideOnly(Side.CLIENT)
    public static IIcon getCauldronIcon(String par0Str)
    {
        return par0Str.equals("inner") ? innerIcon : (par0Str.equals("bottom") ? bottomIcon : null);
    }
}
