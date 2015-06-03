package zmaster587.advancedRocketry.Inventory;

import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import zmaster587.advancedRocketry.entity.EntityRocket;
import zmaster587.advancedRocketry.entity.EntityRocket.PacketType;
import zmaster587.advancedRocketry.network.PacketEntity;
import zmaster587.advancedRocketry.network.PacketHandler;
import zmaster587.advancedRocketry.network.PacketMachine;
import zmaster587.advancedRocketry.util.Configuration;
import zmaster587.libVulpes.gui.CommonResources;
import zmaster587.libVulpes.gui.GuiImageButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class GuiRocket extends GuiProgressBarContainer {

	private EntityRocket rocket;
	private ResourceLocation background = new ResourceLocation("advancedrocketry:textures/gui/rocketHUD.png");
	
	GuiImageButton disassemble;
	GuiImageButton nextInv;
	GuiImageButton prevInv;
	
	int startSlot, endSlot;
	public GuiRocket(EntityPlayer player, EntityRocket rocket, int min, int max) {
		super(new ContainerRocket(player, rocket, min, max));
		
		this.rocket = rocket;
		
		startSlot = min;
		endSlot = max;
	}

	@Override
	public void initGui() {
		super.initGui();
		
		int x = (width - xSize) / 2;
		int y = (height - ySize) / 2;
		
		disassemble =  new GuiImageButton(0, x + 56, y + 64, 64, 20, TextureResources.buttonBuild);
		
		prevInv = new GuiImageButton(1, x + 8, y + 65, 10, 16, new ResourceLocation[] { CommonResources.leftArrow, CommonResources.leftArrow_hover, CommonResources.leftArrow_pressed, null });
		
		nextInv = new GuiImageButton(2, x + 158, y + 65, 10, 16, new ResourceLocation[] { CommonResources.rightArrow, CommonResources.rightArrow_hover, CommonResources.rightArrow_pressed, null });
		
		buttonList.add(disassemble);
		buttonList.add(prevInv);
		buttonList.add(nextInv);
	}
	
	@Override
	public void drawBackground(int p_146278_1_) {
	}

	@Override
	public void handleInput() {
		// TODO Auto-generated method stub
		super.handleInput();
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int a,
			int b) {
		super.drawGuiContainerForegroundLayer(a, b);
		
		//Draw Button Text
				this.drawString(fontRendererObj, "Dissassemble", 56, 70, 0x22FFFF22);
	}
	
	@Override
	protected void actionPerformed(GuiButton button) {
		super.actionPerformed(button);
		
		if(button == disassemble) {
			PacketHandler.sendToServer(new PacketEntity(rocket, (byte)EntityRocket.PacketType.DECONSTRUCT.ordinal()));
		} else if(button == prevInv) {
			rocket.storage.decrementInvPos();
			PacketHandler.sendToServer(new PacketEntity(rocket, (byte)PacketType.OPENGUI.ordinal()));
		}
		else if (button == nextInv) {
			rocket.storage.incrementInvPos();
			PacketHandler.sendToServer(new PacketEntity(rocket, (byte)PacketType.OPENGUI.ordinal()));
		}
		
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float p_146976_1_,
			int p_146976_2_, int p_146976_3_) {
		
		this.mc.renderEngine.bindTexture(CommonResources.genericBackground);
		
		int x = (width - xSize) / 2, y = (height - ySize) / 2;
		this.drawTexturedModalRect(x, y, 0, 0, 176, 171);
		
		for(int i = 0; i + startSlot < endSlot; i++) {
			this.drawTexturedModalRect(x + 7 + (18* (i % 9)), y + 7 + (18*(i/9)), 176, 0, 18, 18);
		}
		
		//Extra background features
		this.drawTexturedModalRect(x + 173, y, 128, 0, 48, 86);
		this.drawTexturedModalRect(x + 173, y+86, 128, 168, 48, 3);
		
		//Fuel bar background
		this.drawTexturedModalRect(x + 192, y+ 21, 184, 18, 4, 40);
		this.drawTexturedModalRect(x + 192, y+ 7, 184, 18, 4, 40);
		
		//Fuel Bar
		this.drawProgressBarVertical(x + 193, y + 8, 253, 187, 2, 52, rocket.getFuelAmount()/(float)rocket.getFuelCapacity());
		
		this.drawString(this.fontRendererObj, "Fuel", x + 184, y+ 72, 0x22FFFF22);
		
	}
}
