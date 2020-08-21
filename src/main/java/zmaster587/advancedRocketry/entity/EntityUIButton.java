package zmaster587.advancedRocketry.entity;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import zmaster587.advancedRocketry.api.AdvancedRocketryEntities;
import zmaster587.advancedRocketry.tile.station.TilePlanetaryHologram;

public class EntityUIButton extends EntityUIPlanet {

	String id;
	TilePlanetaryHologram tile;
	
	public EntityUIButton(World worldIn, ResourceLocation id, TilePlanetaryHologram tile) {
		this(worldIn);
		this.id = id.toString();
		this.tile = tile;
	}
	
	public EntityUIButton(World worldIn) {
		super(AdvancedRocketryEntities.ENTITY_UIBUTTON,worldIn);
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
