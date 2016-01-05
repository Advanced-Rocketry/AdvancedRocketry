package zmaster587.advancedRocketry.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import zmaster587.advancedRocketry.api.AdvancedRocketryItems;

import java.util.Random;

public class BlockDoor2 extends BlockDoor {

	public BlockDoor2(Material p_i45402_1_) {
		super(p_i45402_1_);
	}

    @SideOnly(Side.CLIENT)
    @Override
    public Item getItem(World world, int x, int y, int z) {
    	//if(world.getBlock(x, y, z) == AdvancedRocketryBlocks.blockAirLock)
    	return AdvancedRocketryItems.itemSmallAirlockDoor;
    }

    @Override
    public Item getItemDropped(int p_149650_1_, Random p_149650_2_, int p_149650_3_)
    {
        return (p_149650_1_ & 8) != 0 ? null : AdvancedRocketryItems.itemSmallAirlockDoor;
    }
    
    @Override
    public boolean onBlockActivated(World p_149727_1_, int p_149727_2_, int p_149727_3_, int p_149727_4_, EntityPlayer p_149727_5_, int p_149727_6_, float p_149727_7_, float p_149727_8_, float p_149727_9_)
    {
    	return false;
    }
}
