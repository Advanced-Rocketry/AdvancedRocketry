package zmaster587.advancedRocketry.event;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class BucketHandler {
	
	public static final BucketHandler INSTANCE = new BucketHandler();
	private static Map<Block, Item> bucketMap = new HashMap<Block, Item>();

	@SubscribeEvent
	public void onBucketFill(FillBucketEvent event) {
		Block block = event.world.getBlock(event.target.blockX, event.target.blockY, event.target.blockZ);
		Item bucket = bucketMap.get(block);
		
		if(bucket != null && event.world.getBlockMetadata(event.target.blockX, event.target.blockY, event.target.blockZ) == 0) {
			event.world.setBlockToAir(event.target.blockX, event.target.blockY, event.target.blockZ);
			
			event.result = new ItemStack(bucket);
			
			bucket.hasContainerItem(event.result);
			
			event.setResult(Result.ALLOW);
		}
	}
	
	public void registerBucket(Block block, Item item) {
		bucketMap.put(block, item);
	}
}
