package zmaster587.advancedRocketry.api;

import net.minecraft.entity.EntityType;
import zmaster587.advancedRocketry.entity.EntityDummy;
import zmaster587.advancedRocketry.entity.EntityElevatorCapsule;
import zmaster587.advancedRocketry.entity.EntityHoverCraft;
import zmaster587.advancedRocketry.entity.EntityItemAbducted;
import zmaster587.advancedRocketry.entity.EntityLaserNode;
import zmaster587.advancedRocketry.entity.EntityRocket;
import zmaster587.advancedRocketry.entity.EntityUIButton;
import zmaster587.advancedRocketry.entity.EntityUIPlanet;
import zmaster587.advancedRocketry.entity.EntityUIStar;

public class AdvancedRocketryEntities {

	public static final EntityType<EntityDummy> ENTITY_DUMMY;
	public static final EntityType<EntityElevatorCapsule> ENTITY_ELEVATOR_CAPSULE; // setSize(3,3);
	public static final EntityType<EntityHoverCraft> ENTITY_HOVER_CRAFT;  //setSize(2.5f, 1f);
	public static final EntityType<EntityItemAbducted> ENTITY_ITEM_ABDUCTED;  //this.setSize(0.25F, 0.25F);
	public static final EntityType<EntityLaserNode> ENTITY_LASER_NODE;  //this.setSize(0.25F, 0.25F);
	
	public static final EntityType<EntityRocket> ENTITY_ROCKET;  //this.setSize(0.25F, 0.25F);
	public static final EntityType<EntityUIPlanet> ENTITY_UIPLANET;  //this.setSize(0.25F, 0.25F);
	public static final EntityType<EntityUIStar> ENTITY_UISTAR;  //this.setSize(0.25F, 0.25F);
	public static final EntityType<EntityUIButton> ENTITY_UIBUTTON = null;
}
