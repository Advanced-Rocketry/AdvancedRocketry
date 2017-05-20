package zmaster587.advancedRocketry.asm;

import java.util.Arrays;

import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import com.google.common.eventbus.EventBus;


public class ModContainer extends DummyModContainer {
	
	//ModContainer Class adapted from SackCastellon
	public ModContainer()
	{		
		super(new ModMetadata());
		
		System.out.println("********* CoreDummyContainer. OK");
		
		ModMetadata meta = getMetadata();
		
		meta.modId = "advancedrocketrycore";
		meta.name = "Advanced Rocketry Core";
		meta.version = "1";
		meta.credits = "Created by Zmaster587";
		meta.authorList = Arrays.asList("Zmaster587");
		meta.description = "ASM handler for AR";
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
