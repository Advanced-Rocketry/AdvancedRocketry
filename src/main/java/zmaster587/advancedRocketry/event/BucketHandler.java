package zmaster587.advancedRocketry.event;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class BucketHandler {
	
	public static final BucketHandler INSTANCE = new BucketHandler();
	private static Map<Block, Item> bucketMap = new HashMap<Block, Item>();

	@SubscribeEvent
	public void onBucketFill(FillBucketEvent event) {
		IBlockState state =  event.getWorld().getBlockState(new BlockPos(event.getTarget().getBlockPos()));
		Block block = state.getBlock();
		Item bucket = bucketMap.get(block);
		
		if(bucket != null && state.equals(block.getDefaultState())) {
			event.getWorld().setBlockToAir(new BlockPos(event.getTarget().getBlockPos()));
			
			event.setFilledBucket(new ItemStack(bucket));
			
			bucket.hasContainerItem(event.getFilledBucket());
			
			event.setResult(Result.ALLOW);
		}
	}
	
	public void registerBucket(Block block, Item item) {
		bucketMap.put(block, item);
	}
}
