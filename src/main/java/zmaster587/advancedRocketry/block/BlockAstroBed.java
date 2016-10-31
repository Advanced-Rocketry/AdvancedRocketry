package zmaster587.advancedRocketry.block;

import javax.annotation.Nullable;

import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import zmaster587.advancedRocketry.world.provider.WorldProviderPlanet;
import net.minecraft.block.BlockBed;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Biomes;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockAstroBed extends BlockBed {
	
	@Override
	public boolean isBed(IBlockState state, IBlockAccess world, BlockPos pos,
			Entity player) {
		return this == AdvancedRocketryBlocks.blockAstroBed;
	}
	
	@Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        if (worldIn.isRemote)
        {
            return true;
        }
        else
        {
            if (state.getValue(PART) != BlockBed.EnumPartType.HEAD)
            {
                pos = pos.offset((EnumFacing)state.getValue(FACING));
                state = worldIn.getBlockState(pos);

                if (state.getBlock() != this)
                {
                    return true;
                }
            }

            if (worldIn.provider instanceof WorldProviderPlanet)
            {
                if (((Boolean)state.getValue(OCCUPIED)).booleanValue())
                {
                    EntityPlayer entityplayer = this.getPlayerInBed(worldIn, pos);

                    if (entityplayer != null)
                    {
                        playerIn.addChatComponentMessage(new TextComponentTranslation("tile.bed.occupied", new Object[0]));
                        return true;
                    }

                    state = state.withProperty(OCCUPIED, Boolean.valueOf(false));
                    worldIn.setBlockState(pos, state, 4);
                }

                EntityPlayer.SleepResult entityplayer$sleepresult = playerIn.trySleep(pos);

                if (entityplayer$sleepresult == EntityPlayer.SleepResult.OK)
                {
                    state = state.withProperty(OCCUPIED, Boolean.valueOf(true));
                    worldIn.setBlockState(pos, state, 4);
                    return true;
                }
                else
                {
                    if (entityplayer$sleepresult == EntityPlayer.SleepResult.NOT_POSSIBLE_NOW)
                    {
                        playerIn.addChatComponentMessage(new TextComponentTranslation("tile.bed.noSleep", new Object[0]));
                    }
                    else if (entityplayer$sleepresult == EntityPlayer.SleepResult.NOT_SAFE)
                    {
                        playerIn.addChatComponentMessage(new TextComponentTranslation("tile.bed.notSafe", new Object[0]));
                    }

                    return true;
                }
            }
        }
        return false;
    }
    
    @Nullable
    private EntityPlayer getPlayerInBed(World worldIn, BlockPos pos)
    {
        for (EntityPlayer entityplayer : worldIn.playerEntities)
        {
            if (entityplayer.isPlayerSleeping() && entityplayer.playerLocation.equals(pos))
            {
                return entityplayer;
            }
        }

        return null;
    }
}
