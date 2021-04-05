package zmaster587.advancedRocketry.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import zmaster587.advancedRocketry.api.ARConfiguration;
import zmaster587.advancedRocketry.api.IFuelTank;
import zmaster587.advancedRocketry.api.IRocketNuclearCore;

import java.util.Locale;

public class BlockNuclearCore extends Block implements IRocketNuclearCore {

	public BlockNuclearCore(Material mat) {
		super(mat);
	}

	@Override
	public int getMaxThrust(World world, BlockPos pos) { return (int)(1000 * ARConfiguration.getCurrentConfig().nuclearCoreThrustRatio); }


}
