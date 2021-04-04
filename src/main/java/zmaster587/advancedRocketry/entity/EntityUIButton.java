package zmaster587.advancedRocketry.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import zmaster587.advancedRocketry.api.AdvancedRocketryEntities;
import zmaster587.advancedRocketry.tile.station.TileHolographicPlanetSelector;

public class EntityUIButton extends EntityUIPlanet {

	String id;
	TileHolographicPlanetSelector tile;
	
	public EntityUIButton(World worldIn, ResourceLocation id, TileHolographicPlanetSelector tile) {
		this(AdvancedRocketryEntities.ENTITY_UIBUTTON, worldIn);
		this.id = id.toString();
		this.tile = tile;
	}
	
	public EntityUIButton(EntityType<?> type, World worldIn) {
		super(type, worldIn);
		setSize(0.2f, 0.2f);
	}
	
	@Override
	protected void registerData() {
		this.dataManager.register(planetID, id);
		this.dataManager.register(scale, 1f);
		this.dataManager.register(selected, false);
		
	}
	
	@Override
	public ActionResultType processInitialInteract(PlayerEntity player, Hand hand) {
		if(!world.isRemote && tile != null) {
			//tile.onInventoryButtonPressed(getPlanetID());
		}
		return ActionResultType.PASS;
	}
	
	public ResourceLocation getPlanetID() {
		return new ResourceLocation(this.dataManager.get(planetID));
	}
}
