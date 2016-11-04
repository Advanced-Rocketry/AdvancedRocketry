package zmaster587.advancedRocketry.unit;

import net.minecraft.util.EnumFacing;

public class RotationTest {
	public static void main(String str[]) {
		
		for(EnumFacing stationFacing : EnumFacing.values()) {
			for(EnumFacing dirFacing : EnumFacing.values() ) {
				EnumFacing cross = dirFacing.rotateAround(stationFacing.getAxis());
				System.out.println();
			}
		}
	}
}
