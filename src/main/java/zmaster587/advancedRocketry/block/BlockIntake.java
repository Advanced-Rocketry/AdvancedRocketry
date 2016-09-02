package zmaster587.advancedRocketry.block;

import net.minecraft.block.material.Material;
import zmaster587.advancedRocketry.api.IIntake;

public class BlockIntake extends BlockGeneric implements IIntake {

	public BlockIntake(Material material) {
		super(material);
	}

	@Override
	public int getIntakeAmt(int meta) {
		return 10;
	}

}
