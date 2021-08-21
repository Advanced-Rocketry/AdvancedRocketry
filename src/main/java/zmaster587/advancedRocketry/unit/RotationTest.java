package zmaster587.advancedRocketry.unit;

import net.minecraft.util.Direction;
import zmaster587.libVulpes.util.ZUtils;

public class RotationTest {
	public static void main(String[] str) {
		
		for(Direction stationFacing : Direction.values()) {
			for(Direction dirFacing : Direction.values() ) {
				Direction cross = ZUtils.rotateAround(dirFacing, stationFacing);
				System.out.println();
			}
		}
	}
}
