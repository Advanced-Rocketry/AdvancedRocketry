package zmaster587.advancedRocketry.block;

import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerEntity.SleepResult;
import net.minecraft.item.DyeColor;
import net.minecraft.state.properties.BedPart;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Unit;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;

import javax.annotation.Nullable;

import com.mojang.datafixers.util.Either;

public class BlockAstroBed extends BedBlock {

	public BlockAstroBed(DyeColor colorIn, Properties properties) {
		super(colorIn, properties);
		// TODO Auto-generated constructor stub
	}
	
	/*@Override
	public boolean isBed(BlockState state, IBlockReader world, BlockPos pos, Entity player) {
		return this == AdvancedRocketryBlocks.blockAstroBed;
	}
	
	@Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (worldIn.isRemote)
        {
            return true;
        }
        else
        {
            if (state.get(PART) != BedPart.HEAD)
            {
                pos = pos.offset(state.get(HORIZONTAL_FACING));
                state = worldIn.getBlockState(pos);

                if (state.getBlock() != this)
                {
                    return true;
                }
            }

            if (worldIn.provider instanceof WorldProviderPlanet)
            {
                if (((Boolean)state.get(OCCUPIED)).booleanValue())
                {
                    PlayerEntity entityplayer = this.getPlayerInBed(worldIn, pos);

                    if (entityplayer != null)
                    {
                    	player.sendStatusMessage(new TranslationTextComponent("tile.bed.occupied"), true);
                        return true;
                    }

                    state = state.with(OCCUPIED, Boolean.valueOf(false));
                    worldIn.setBlockState(pos, state, 4);
                }

                Either<SleepResult, Unit> entityplayer$sleepresult = player.trySleep(pos);

                if (entityplayer$sleepresult == PlayerEntity.SleepResult.OK)
                {
                    state = state.with(OCCUPIED, Boolean.valueOf(true));
                    worldIn.setBlockState(pos, state, 4);
                    return true;
                }
                else
                {
                    if (entityplayer$sleepresult == PlayerEntity.SleepResult.NOT_POSSIBLE_NOW)
                    {
                        playerIn.sendMessage(new TranslationTextComponent("tile.bed.noSleep", new Object[0]));
                    }
                    else if (entityplayer$sleepresult == PlayerEntity.SleepResult.NOT_SAFE)
                    {
                        playerIn.sendMessage(new TranslationTextComponent("tile.bed.notSafe", new Object[0]));
                    }

                    return true;
                }
            }
        }
        return false;
    }
    
    @Nullable
    private PlayerEntity getPlayerInBed(World worldIn, BlockPos pos)
    {
        for (PlayerEntity entityplayer : worldIn.playerEntities)
        {
            if (entityplayer.isPlayerSleeping() && entityplayer.bedLocation.equals(pos))
            {
                return entityplayer;
            }
        }

        return null;
    }*/
}
