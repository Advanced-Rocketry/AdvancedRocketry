package zmaster587.advancedRocketry.tile.multiblock.machine;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import zmaster587.advancedRocketry.api.ARConfiguration;
import zmaster587.advancedRocketry.api.AdvancedRocketryBlocks;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.advancedRocketry.util.AudioRegistry;
import zmaster587.libVulpes.LibVulpes;
import zmaster587.libVulpes.inventory.modules.IModularInventory;
import zmaster587.libVulpes.inventory.modules.ModuleBase;
import zmaster587.libVulpes.inventory.modules.ModuleProgress;
import zmaster587.libVulpes.inventory.modules.ModuleText;
import zmaster587.libVulpes.tile.multiblock.TileMultiblockMachine;

import java.util.List;

public class TileCrystallizer extends TileMultiblockMachine implements IModularInventory {


    public static final Object[][][] structure = {{{AdvancedRocketryBlocks.blockQuartzCrucible, AdvancedRocketryBlocks.blockQuartzCrucible, AdvancedRocketryBlocks.blockQuartzCrucible},
            {AdvancedRocketryBlocks.blockQuartzCrucible, AdvancedRocketryBlocks.blockQuartzCrucible, AdvancedRocketryBlocks.blockQuartzCrucible}},

            {{'O', 'c', 'I'},
                    {'l', 'P', 'L'}},

    };

    @Override
    public boolean shouldHideBlock(World world, BlockPos pos2, IBlockState tile) {
        return true;
    }

    @Override
    public Object[][][] getStructure() {
        return structure;
    }

    @Override
    public SoundEvent getSound() {
        return AudioRegistry.crystallizer;
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(pos.add(-2, -2, -2), pos.add(2, 2, 2));
    }

    public boolean isGravityWithinBounds() {
        if (!(ARConfiguration.getCurrentConfig().crystalliserMaximumGravity == 0)) {
            return ARConfiguration.getCurrentConfig().crystalliserMaximumGravity > DimensionManager.getInstance().getDimensionProperties(world.provider.getDimension()).gravitationalMultiplier;
        }
        return true;
    }

    @Override
    protected void onRunningPoweredTick() {
        if (isGravityWithinBounds()) {
            super.onRunningPoweredTick();
        }

    }

    @Override
    public List<ModuleBase> getModules(int ID, EntityPlayer player) {
        List<ModuleBase> modules = super.getModules(ID, player);

        modules.add(new ModuleProgress(100, 4, 0, TextureResources.crystallizerProgressBar, this));
        if (!isGravityWithinBounds()) {
            modules.add(new ModuleText(10, 75, LibVulpes.proxy.getLocalizedString("msg.crystalliser.gravityTooHigh"), 0xFF1b1b));
        }
        return modules;
    }

    @Override
    public String getMachineName() {
        return AdvancedRocketryBlocks.blockCrystallizer.getLocalizedName();
    }
}
