package zmaster587.advancedRocketry.block;

import java.util.Iterator;

import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import zmaster587.advancedRocketry.world.provider.WorldProviderPlanet;
import net.minecraft.block.BlockBed;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockAstroBed extends BlockBed {
	/**
     * Called upon block activation (right click on the block.)
     */
	
	@Override
	public boolean isBed(IBlockAccess world, int x, int y, int z,
			EntityLivingBase player) {
		return this == AdvancedRocketryBlocks.blockAstroBed;
	}
	
    public boolean onBlockActivated(World p_149727_1_, int p_149727_2_, int p_149727_3_, int p_149727_4_, EntityPlayer p_149727_5_, int p_149727_6_, float p_149727_7_, float p_149727_8_, float p_149727_9_)
    {
        if (p_149727_1_.isRemote)
        {
            return true;
        }
        else
        {
            int i1 = p_149727_1_.getBlockMetadata(p_149727_2_, p_149727_3_, p_149727_4_);

            if (!isBlockHeadOfBed(i1))
            {
                int j1 = getDirection(i1);
                p_149727_2_ += field_149981_a[j1][0];
                p_149727_4_ += field_149981_a[j1][1];

                if (p_149727_1_.getBlock(p_149727_2_, p_149727_3_, p_149727_4_) != this)
                {
                    return true;
                }

                i1 = p_149727_1_.getBlockMetadata(p_149727_2_, p_149727_3_, p_149727_4_);
            }

            if (p_149727_1_.provider instanceof WorldProviderPlanet)
            {
                if (func_149976_c(i1))
                {
                    EntityPlayer entityplayer1 = null;
                    Iterator iterator = p_149727_1_.playerEntities.iterator();

                    while (iterator.hasNext())
                    {
                        EntityPlayer entityplayer2 = (EntityPlayer)iterator.next();

                        if (entityplayer2.isPlayerSleeping())
                        {
                            ChunkCoordinates chunkcoordinates = entityplayer2.playerLocation;

                            if (chunkcoordinates.posX == p_149727_2_ && chunkcoordinates.posY == p_149727_3_ && chunkcoordinates.posZ == p_149727_4_)
                            {
                                entityplayer1 = entityplayer2;
                            }
                        }
                    }

                    if (entityplayer1 != null)
                    {
                        p_149727_5_.addChatComponentMessage(new ChatComponentTranslation("tile.bed.occupied", new Object[0]));
                        return true;
                    }

                    func_149979_a(p_149727_1_, p_149727_2_, p_149727_3_, p_149727_4_, false);
                }

                EntityPlayer.EnumStatus enumstatus = p_149727_5_.sleepInBedAt(p_149727_2_, p_149727_3_, p_149727_4_);

                if (enumstatus == EntityPlayer.EnumStatus.OK)
                {
                    func_149979_a(p_149727_1_, p_149727_2_, p_149727_3_, p_149727_4_, true);
                    return true;
                }
                else
                {
                    if (enumstatus == EntityPlayer.EnumStatus.NOT_POSSIBLE_NOW)
                    {
                        p_149727_5_.addChatComponentMessage(new ChatComponentTranslation("tile.bed.noSleep", new Object[0]));
                    }
                    else if (enumstatus == EntityPlayer.EnumStatus.NOT_SAFE)
                    {
                        p_149727_5_.addChatComponentMessage(new ChatComponentTranslation("tile.bed.notSafe", new Object[0]));
                    }

                    return true;
                }
            }
        }
		return false;
    }
}
