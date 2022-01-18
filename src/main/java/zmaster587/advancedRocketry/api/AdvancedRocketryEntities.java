package zmaster587.advancedRocketry.api;

import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
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

	public static EntityType<EntityDummy> ENTITY_DUMMY;
	public static EntityType<EntityElevatorCapsule> ENTITY_ELEVATOR_CAPSULE; // setSize(3,3);
	public static EntityType<EntityHoverCraft> ENTITY_HOVER_CRAFT;  //setSize(2.5f, 1f);
	public static EntityType<EntityItemAbducted> ENTITY_ITEM_ABDUCTED;  //this.setSize(0.25F, 0.25F);
	public static EntityType<EntityLaserNode> ENTITY_LASER_NODE;  //this.setSize(0.25F, 0.25F);
	
	public static EntityType<EntityRocket> ENTITY_ROCKET;  //this.setSize(0.25F, 0.25F);
	public static EntityType<EntityUIPlanet> ENTITY_UIPLANET;  //this.setSize(0.25F, 0.25F);
	public static EntityType<EntityUIStar> ENTITY_UISTAR;  //this.setSize(0.25F, 0.25F);
	public static EntityType<EntityUIButton> ENTITY_UIBUTTON;
	
	@SubscribeEvent
	public static void registerEntities(RegistryEvent.Register<EntityType<?>> event)
	{
		ENTITY_DUMMY = EntityType.Builder.<EntityDummy>create(EntityDummy::new, EntityClassification.MISC).disableSummoning().build("dummy");
		ENTITY_ELEVATOR_CAPSULE = EntityType.Builder.<EntityElevatorCapsule>create(EntityElevatorCapsule::new, EntityClassification.MISC).size(3,0.1f).immuneToFire().setTrackingRange(64).setShouldReceiveVelocityUpdates(true).setUpdateInterval(20).disableSummoning().build("spaceelevator");
		ENTITY_HOVER_CRAFT = EntityType.Builder.<EntityHoverCraft>create(EntityHoverCraft::new, EntityClassification.MISC).size(2.5f, 1f).immuneToFire().setTrackingRange(64).setShouldReceiveVelocityUpdates(true).setUpdateInterval(3).build("hovercraft");
		ENTITY_ITEM_ABDUCTED = EntityType.Builder.<EntityItemAbducted>create(EntityItemAbducted::new, EntityClassification.MISC).size(0.25F, 0.25F).setTrackingRange(127).setShouldReceiveVelocityUpdates(true).setUpdateInterval(400).build("itemabducted");
		ENTITY_LASER_NODE = EntityType.Builder.<EntityLaserNode>create(EntityLaserNode::new, EntityClassification.MISC).size(0.25F, 0.25F).setTrackingRange(256).setUpdateInterval(20).build("lasernode");
		ENTITY_ROCKET = EntityType.Builder.<EntityRocket>create(EntityRocket::new, EntityClassification.MISC).size(0.25F, 0.25F).setTrackingRange(256).setUpdateInterval(3).build("rocket");
		ENTITY_UIPLANET = EntityType.Builder.<EntityUIPlanet>create(EntityUIPlanet::new, EntityClassification.MISC).size(0.25F, 0.25F).setTrackingRange(64).setUpdateInterval(20).build("uiplanet");
		ENTITY_UISTAR = EntityType.Builder.<EntityUIStar>create(EntityUIStar::new, EntityClassification.MISC).size(0.25F, 0.25F).setTrackingRange(64).setUpdateInterval(20).build("uistar");
		ENTITY_UIBUTTON = EntityType.Builder.<EntityUIButton>create(EntityUIButton::new, EntityClassification.MISC).size(0.25F, 0.25F).setTrackingRange(64).setUpdateInterval(20).build("uibutton");
		
		event.getRegistry().registerAll(
				ENTITY_DUMMY.setRegistryName(Constants.modId, "dummy"),
				ENTITY_ELEVATOR_CAPSULE.setRegistryName(Constants.modId, "spaceelevator"),
				ENTITY_HOVER_CRAFT.setRegistryName(Constants.modId, "hovercraft"),
				ENTITY_ITEM_ABDUCTED.setRegistryName(Constants.modId, "itemabducted"),
				ENTITY_LASER_NODE.setRegistryName(Constants.modId, "lasernode"),
				ENTITY_ROCKET.setRegistryName(Constants.modId, "rocket"),
				ENTITY_UIPLANET.setRegistryName(Constants.modId, "uiplanet"),
				ENTITY_UISTAR.setRegistryName(Constants.modId, "uistar"),
				ENTITY_UIBUTTON.setRegistryName(Constants.modId, "uibutton"));
	}
}
