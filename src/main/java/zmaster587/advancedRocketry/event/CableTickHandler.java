package zmaster587.advancedRocketry.event;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.cable.NetworkRegistry;
import zmaster587.advancedRocketry.tile.cables.TilePipe;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Set;

public class CableTickHandler {

	@SubscribeEvent
	public void onTick(TickEvent.ServerTickEvent tick) {
		try {
			if(tick.phase == Phase.END) {
				NetworkRegistry.dataNetwork.tickAllNetworks();
				//NetworkRegistry.energyNetwork.tickAllNetworks();
				//NetworkRegistry.liquidNetwork.tickAllNetworks();
			}
		} catch (ConcurrentModificationException e) {
			e.printStackTrace();
		}
	}

	@SubscribeEvent
	public void chunkLoadedEvent(ChunkEvent.Load event) {

		Set<BlockPos> map = event.getChunk().getTileEntitiesPos();
		Iterator<BlockPos> iter = map.iterator();

		try {
			while(iter.hasNext()) {
				TileEntity obj = event.getChunk().getTileEntity(iter.next());

				if(obj instanceof TilePipe) {
					obj.markDirty();
				}
			}
		} catch ( ConcurrentModificationException e) {
			AdvancedRocketry.logger.warn("You have been visited by the rare pepe.. I mean error of pipes not loading, this is not good, some pipe systems may not work right away.  But it's better than a corrupt world");
		}
	}

	@SubscribeEvent
	public void onBlockBroken(BreakEvent event) {
		if(event.getState().getBlock().hasTileEntity(event.getState())) {

			TileEntity homeTile = event.getWorld().getTileEntity(event.getPos());

			if(homeTile instanceof TilePipe) {

				//removed in favor of pipecount
				//boolean lastInNetwork =true;

				((TilePipe)homeTile).setDestroyed();
				((TilePipe)homeTile).setInvalid();

				int pipecount=0;

				for(Direction dir : Direction.values()) {
					TileEntity tile = event.getWorld().getTileEntity(event.getPos().offset(dir));
					if(tile instanceof TilePipe) 
						pipecount++;
				}
				//TODO: delete check if sinks/sources need removal
				if(pipecount > 1) {
					for(Direction dir : Direction.values()) {
						TileEntity tile = event.getWorld().getTileEntity(event.getPos().offset(dir));

						if(tile instanceof TilePipe) {
							((TilePipe) tile).getNetworkHandler().removeNetworkByID(((TilePipe) tile).getNetworkID());
							((TilePipe) tile).setInvalid();
							//lastInNetwork = false;
						}
						//HandlerCableNetwork.removeFromAllTypes((TilePipe)tile,event.world.getTileEntity(event.x, event.y, event.z));
					}
				}
				if(pipecount == 0) //lastInNetwork
					((TilePipe)homeTile).getNetworkHandler().removeNetworkByID(((TilePipe)homeTile).getNetworkID());
				homeTile.markDirty();
			}
			else if(homeTile != null) {
				for(Direction dir : Direction.values()) {
					TileEntity tile = event.getWorld().getTileEntity(event.getPos().offset(dir));

					if(tile instanceof TilePipe) {
						((TilePipe)tile).getNetworkHandler().removeFromAllTypes((TilePipe)tile, homeTile);
					}
				}
			}
		}
	}
}
