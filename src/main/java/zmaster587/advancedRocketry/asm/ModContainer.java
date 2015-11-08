package zmaster587.advancedRocketry.asm;

import java.util.Arrays;

import com.google.common.eventbus.EventBus;

import cpw.mods.fml.common.DummyModContainer;
import cpw.mods.fml.common.LoadController;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.event.FMLConstructionEvent;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class ModContainer extends DummyModContainer {
	
	public ModContainer()
	{		
		super(new ModMetadata());
		
		System.out.println("********* CoreDummyContainer. OK");
		
		ModMetadata meta = getMetadata();
		
		meta.modId = "CoreMod";
		meta.name = "Core Mod";
		meta.version = "1";
		meta.credits = "Created by SackCastellon";
		meta.authorList = Arrays.asList("SackCastellon");
		meta.description = "An API that contain some common classes for all my mods";
		meta.url = "http://www.minecraftforum.net/topic/1909056-/#core";
		meta.updateUrl = "";
		meta.screenshots = new String[0];
		meta.logoFile = "";
	}

	@Override
	public boolean registerBus(EventBus bus, LoadController controller)
	{
		System.out.println("********* registerBus. OK");
		bus.register(this);
		return true;
	}
	
	@SubscribeEvent
	public void modConstruction(FMLConstructionEvent event) {}
	
	@SubscribeEvent
	public void preInit(FMLPreInitializationEvent event) {}
	
	@SubscribeEvent
	public void load(FMLInitializationEvent event) {}
	
	@SubscribeEvent
	public void postInit(FMLPostInitializationEvent event) {}
}
