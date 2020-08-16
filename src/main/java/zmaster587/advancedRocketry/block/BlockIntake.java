package zmaster587.advancedRocketry.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import zmaster587.advancedRocketry.api.IIntake;

public class BlockIntake extends Block implements IIntake {

	public BlockIntake(Properties material) {
		super(material);
	}

	@Override
	public int getIntakeAmt(BlockState state) {
		return 10;
	}

}
