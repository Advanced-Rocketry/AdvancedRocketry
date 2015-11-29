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
	
	//ModContainer Class adapted from SackCastellon
	public ModContainer()
	{		
		super(new ModMetadata());
		
		System.out.println("********* CoreDummyContainer. OK");
		
		ModMetadata meta = getMetadata();
		
		meta.modId = "AdvancedRocketry";
		meta.name = "Advanced Rocketry";
		meta.version = "1";
		meta.credits = "Created by Zmaster587";
		meta.authorList = Arrays.asList("Zmaster587");
		meta.description = "";
		meta.url = "";
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
