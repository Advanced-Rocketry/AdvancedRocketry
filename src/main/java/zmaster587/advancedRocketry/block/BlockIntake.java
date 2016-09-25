package zmaster587.advancedRocketry.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import zmaster587.advancedRocketry.api.IIntake;

public class BlockIntake extends Block implements IIntake {

	public BlockIntake(Material material) {
		super(material);
	}

	@Override
	public int getIntakeAmt(IBlockState state) {
		return 10;
	}

}
