package zmaster587.advancedRocketry.block.plant;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockLog;
import net.minecraft.block.BlockWood;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockAlienWood extends BlockLog  {
	
    public static final String[] names = new String[] {"blue"};

    /**
     * returns a list of blocks with the same ID, but different meta (eg: wood returns 4 blocks)
     */
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item item, CreativeTabs tab, List itemList)
    {
    	for(int i = 0; i < names.length; i++)
    		itemList.add(new ItemStack(item, 1, i));
    }
    
    @Override
    public int getFireSpreadSpeed(IBlockAccess world, int x, int y, int z,
    		ForgeDirection face) {
    	return 50;
    }
    
    @Override
    public int getFlammability(IBlockAccess world, int x, int y, int z,
    		ForgeDirection face) {
    	return 50;
    }

    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister p_149651_1_)
    {
        this.field_150167_a = new IIcon[names.length];
        this.field_150166_b = new IIcon[names.length];

        for (int i = 0; i < this.field_150167_a.length; ++i)
        {
            this.field_150167_a[i] = p_149651_1_.registerIcon(this.getTextureName() + "_" + names[i]);
            this.field_150166_b[i] = p_149651_1_.registerIcon(this.getTextureName() + "_" + names[i] + "_top");
        }
    }
}
