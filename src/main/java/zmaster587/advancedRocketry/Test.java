package zmaster587.advancedRocketry;

import net.minecraft.util.math.BlockPos;
import zmaster587.advancedRocketry.api.Configuration;
public class Test {

	public static void main(String[] args) {

		for(int x = 2048; x < 2048+1024; x++) {
			int y = (int) Math.round((x)/(2f*Configuration.stationSize));
			
			if(y != 1) {
				System.out.println("x: " + y);
			}
		}
		/*return;
		for(int i = 1; i < 64; i++) {
			BlockPos pos =  registerSpaceObject(i);
			//if(thing2(pos) != i)
			System.out.println(String.format("input id: %d, output id: %d,  coords: %d %d", i, thing2(pos), pos.getX(), pos.getZ()));
		}*/
	}

	public static int thing2(BlockPos pos) {
		int x = pos.getX(); int z = pos.getZ();
		x = (x-Configuration.stationSize/2)/(2*Configuration.stationSize);
		z = (z-Configuration.stationSize/2)/(2*Configuration.stationSize);
		int radius = Math.max(Math.abs(x), Math.abs(z));

		int index = (int) Math.pow((2*radius-1),2) + x + radius;

		if(Math.abs(z) != radius) {
			index = (int) Math.pow((2*radius-1),2) + z + radius + (4*radius + 2) - 1;

			if(x > 0)
				index += 2*radius-1;
		}
		else if(z > 0)
			index += 2*radius+1;

		return index;
	}

	public static BlockPos registerSpaceObject(int stationId) {

		/*Calculate the location of a space station along a square spiral
		 * here the top and bottom(including the corner locations) are filled first then the left and right last
		 * 
		 * Example shown below:
		 *9 A B C D
		 *  1 2 3
		 *  7 0 8
		 *  4 5 6
		 *E F.....
		 */

		int radius = (int) Math.floor(Math.ceil(Math.sqrt(stationId+1))/2);
		int ringIndex = (int) (stationId-Math.pow((radius*2) - 1,2));
		int x,z;

		if(ringIndex < (radius*2 + 1)*2) {
			x = ringIndex % (radius*2 + 1) - radius;
			if(ringIndex < (radius*2 + 1))
				z = -radius;
			else
				z = radius;
		}
		else {
			int newIndex = ringIndex - (radius*2 + 1)*2;
			z = newIndex % ((radius-1)*2 + 1) - (radius - 1);
			if(newIndex < ((radius-1)*2 + 1))
				x = -radius;
			else
				x = radius;
		}
		return new BlockPos(2*Configuration.stationSize*x + Configuration.stationSize/2,0,2*Configuration.stationSize*z + Configuration.stationSize/2);
	}
}
