package zmaster587.advancedRocketry.block.plant;

import java.util.List;
import java.util.Random;

import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockLeaves;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockAlienLeaves extends BlockLeaves {

	public BlockAlienLeaves() {
		super();
		this.lightValue = 8;
	}
	
	protected static final String[] names = {"blueLeaf"};
	protected static final String[][] textures = new String[][] {{"leaves_oak"}, {"leaves_oak_opaque"}};
	
    @SideOnly(Side.CLIENT)
    public int getRenderColor(int meta)
    {
    	return meta == 0 ? 0x55ffe1 : super.getRenderColor(meta);
    }
    
    public int quantityDropped(Random p_149745_1_)
    {
        return p_149745_1_.nextInt(100) == 0 ? 1 : 0;
    }
    
    @Override
    public int getFlammability(IBlockAccess world, int x, int y, int z,
    		ForgeDirection face) {
    	return 50;
    }
   
    @Override
    public int getFireSpreadSpeed(IBlockAccess world, int x, int y, int z,
    		ForgeDirection face) {
    	return 50;
    }
    
    @Override
    public boolean isOpaqueCube()
    {
        return false;
    }
    
    protected void func_150124_c(World world, int x, int y, int z, int p_150124_5_, int p_150124_6_)
    {
        if ((p_150124_5_ & 3) == 0 && world.rand.nextInt(p_150124_6_) == 0)
        {
            //TODO make drop
        	//this.dropBlockAsItem(world, x, y, z, new ItemStack(Items.apple, 1, 0));
        }
    }
	
    /**
     * returns a list of blocks with the same ID, but different meta (eg: wood returns 4 blocks)
     */
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item item, CreativeTabs tab, List list)
    {
    	list.add(new ItemStack(item, 1,0));
    }
    
	@Override
	public IIcon getIcon(int side, int meta) {
		return field_150129_M[Minecraft.isFancyGraphicsEnabled() ? 0 : 1][0];
	}

    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister p_149651_1_)
    {
        for (int i = 0; i < textures.length; ++i)
        {
            this.field_150129_M[i] = new IIcon[textures[i].length];

            for (int j = 0; j < textures[i].length; ++j)
            {
                this.field_150129_M[i][j] = p_149651_1_.registerIcon(textures[i][j]);
            }
        }
    }
	
    public Item getItemDropped(int p_149650_1_, Random p_149650_2_, int p_149650_3_)
    {
        return Item.getItemFromBlock(AdvancedRocketryBlocks.blockAlienSapling);
    }
	
	@Override
	public String[] func_150125_e() {
		return names;
	}
	
    @SideOnly(Side.CLIENT)
    @Override
    public boolean shouldSideBeRendered(IBlockAccess p_149646_1_, int p_149646_2_, int p_149646_3_, int p_149646_4_, int p_149646_5_)
    {
    	//return super.shouldSideBeRendered(p_149646_1_, p_149646_2_, p_149646_3_, p_149646_4_, p_149646_5_);
    	return Minecraft.isFancyGraphicsEnabled() ? true : super.shouldSideBeRendered(p_149646_1_, p_149646_2_, p_149646_3_, p_149646_4_, p_149646_5_);// !this.field_150121_P;
    }

}
