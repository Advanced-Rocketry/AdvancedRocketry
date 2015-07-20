package zmaster587.advancedRocketry.Inventory.multiblock;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import zmaster587.advancedRocketry.Inventory.GuiPowerStorage;
import zmaster587.advancedRocketry.network.PacketHandler;
import zmaster587.advancedRocketry.network.PacketMachine;
import zmaster587.advancedRocketry.tile.multiblock.TileMultiBlockMachine;
import zmaster587.libVulpes.api.IUniversalEnergy;
import zmaster587.libVulpes.gui.GuiToggleButtonImage;

public class GuiMultiblock extends GuiPowerStorage {
	
	TileMultiBlockMachine tile;
	GuiToggleButtonImage enabledButton;
	ResourceLocation[] buttonToggleImage = new ResourceLocation[] {new ResourceLocation("advancedrocketry:textures/gui/buttons/switchOn.png"), new ResourceLocation("advancedrocketry:textures/gui/buttons/switchOff.png")};
	
	public GuiMultiblock(InventoryPlayer player, TileMultiBlockMachine multiblock) {
		super(new ContainerMultiblock(player, multiblock), player, (IUniversalEnergy)multiblock.getBatteries());
		tile = multiblock;
	}
	
	public GuiMultiblock(ContainerMultiblock container ,InventoryPlayer player, TileMultiBlockMachine multiblock) {
		super(container, player, (IUniversalEnergy)multiblock.getBatteries());
		tile = multiblock;
	}
	
	@Override
	public void initGui() {
		super.initGui();
		
		int x = (width - xSize) / 2;
		int y = (height - ySize) / 2;
		
		enabledButton = new GuiToggleButtonImage(0, x + 160, y + 5, 11, 26, buttonToggleImage);
		enabledButton.setState(tile.getMachineEnabled());
		buttonList.add(enabledButton);
	}
	
	@Override
	protected void actionPerformed(GuiButton button) {
		super.actionPerformed(button);
		
		if(button == enabledButton) {
			boolean enabled;
			tile.setMachineEnabled((enabled = !tile.getMachineEnabled()));
			enabledButton.setState(enabled);
			PacketHandler.sendToServer(new PacketMachine(tile,(byte)TileMultiBlockMachine.NetworkPackets.TOGGLE.ordinal()));
		}
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int a, int b) {
		super.drawGuiContainerForegroundLayer(a, b);
		enabledButton.setState(tile.getMachineEnabled());
		this.fontRendererObj.drawString(I18n.format(tile.getMachineName(), new Object[0]), 8, 6, 4210752);
	}
}
