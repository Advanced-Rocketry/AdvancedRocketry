package zmaster587.advancedRocketry.event;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.event.world.ChunkEvent;
import zmaster587.advancedRocketry.AdvancedRocketry;
import zmaster587.advancedRocketry.cable.NetworkRegistry;
import zmaster587.advancedRocketry.tile.cables.TilePipe;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;

public class CableTickHandler {

	@SubscribeEvent
	public void onTick(TickEvent.ServerTickEvent tick) {
		try {
			if(tick.phase == Phase.END) {
				NetworkRegistry.dataNetwork.tickAllNetworks();
				NetworkRegistry.energyNetwork.tickAllNetworks();
				NetworkRegistry.liquidNetwork.tickAllNetworks();
			}
		} catch (ConcurrentModificationException e) {
			e.printStackTrace();
		}
	}

	@SubscribeEvent
	public void chunkLoadedEvent(ChunkEvent.Load event) {

		Map map = event.getChunk().chunkTileEntityMap;
		Iterator<Entry> iter = map.entrySet().iterator();

		try {
			while(iter.hasNext()) {
				Object obj = iter.next().getValue();

				if(obj instanceof TilePipe) {
					((TilePipe)obj).markForUpdate();
				}
			}
		} catch ( ConcurrentModificationException e) {
			AdvancedRocketry.logger.warn("You have been visited by the rare pepe.. I mean error of pipes not loading, this is not good, some pipe systems may not work right away.  But it's better than a corrupt world");
		}

	}

	@SubscribeEvent
	public void onBlockBroken(BreakEvent event) {

		if(event.block.hasTileEntity(event.blockMetadata)) {

			TileEntity homeTile = event.world.getTileEntity(event.x , event.y, event.z);

			if(homeTile instanceof TilePipe) {

				//removed in favor of pipecount
				//boolean lastInNetwork =true;

				((TilePipe)homeTile).setDestroyed();
				((TilePipe)homeTile).setInvalid();

				int pipecount=0;

				for(ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
					TileEntity tile = event.world.getTileEntity(event.x + dir.offsetX, event.y + dir.offsetY, event.z + dir.offsetZ);
					if(tile instanceof TilePipe) 
						pipecount++;
				}
				//TODO: delete check if sinks/sources need removal
				if(pipecount > 1) {
					for(ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
						TileEntity tile = event.world.getTileEntity(event.x + dir.offsetX, event.y + dir.offsetY, event.z + dir.offsetZ);

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
				((TilePipe)homeTile).markDirty();
			}
			else if(homeTile != null) {
				for(ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
					TileEntity tile = event.world.getTileEntity(event.x + dir.offsetX, event.y + dir.offsetY, event.z + dir.offsetZ);

					if(tile instanceof TilePipe) {
						((TilePipe)tile).getNetworkHandler().removeFromAllTypes((TilePipe)tile, homeTile);
					}
				}
			}
		}
	}
}
