package zmaster587.advancedRocketry.inventory.modules;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import zmaster587.advancedRocketry.api.DataStorage;
import zmaster587.advancedRocketry.util.IDataInventory;
import zmaster587.libVulpes.inventory.modules.ModuleBase;
import zmaster587.libVulpes.util.IconResource;

import java.util.LinkedList;
import java.util.List;

public class ModuleAutoData extends ModuleBase {

    static final int barYSize = 38;
    static final int barXSize = 6;
    static final int textureOffsetX = 0;
    static final int textureOffsetY = 215;

    DataStorage[] data;
    int[] prevData;
    int prevDataType;
    int slot1;
    int slot2;
    IDataInventory chipInput;
    IDataInventory chipOutput;
    IconResource icon = zmaster587.advancedRocketry.inventory.TextureResources.ioSlot;

    public ModuleAutoData(int offsetX, int offsetY, int slot1, int slot2, IDataInventory chipInput, IDataInventory chipOutput, DataStorage... data) {
        super(offsetX, offsetY);
        this.data = data;
        prevData = new int[data.length];
        this.chipInput = chipInput;
        this.chipOutput = chipOutput;
        this.slot1 = slot1;
        this.slot2 = slot2;
    }

    @Override
    public List<Slot> getSlots(Container container) {
        slotList.add(new SlotData(chipInput, slot1, offsetX + 10, offsetY));
        slotList.add(new SlotData(chipOutput, slot2, offsetX + 10, offsetY + 24));
        return slotList;
    }

    @Override
    public int numberOfChangesToSend() {
        return data.length + 1;
    }

    @Override
    public boolean needsUpdate(int localId) {
        if (localId < data.length)
            return data[localId].getData() != prevData[localId];
        return data[0].getDataType().ordinal() != prevDataType;
    }

    @Override
    protected void updatePreviousState(int localId) {
        if (localId < data.length)
            prevData[localId] = data[localId].getData();

        else
            prevDataType = data[0].getDataType().ordinal();
    }

    @Override
    public void sendChanges(Container container, IContainerListener crafter,
                            int variableId, int localId) {
        if (localId < data.length)
            crafter.sendWindowProperty(container, variableId, data[localId].getData());
        else
            crafter.sendWindowProperty(container, variableId, data[0].getDataType().ordinal());
    }

    @Override
    public void onChangeRecieved(int slot, int value) {
        if (slot < data.length)
            data[slot].setData(value, DataStorage.DataType.UNDEFINED);
        else
            data[0].setDataType(DataStorage.DataType.values()[value]);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void renderForeground(int guiOffsetX, int guiOffsetY, int mouseX, int mouseY, float zLevel, GuiContainer gui, FontRenderer font) {

        int relativeX = mouseX - offsetX;
        int relativeY = mouseY - offsetY;

        //Handles data tooltip
        if (relativeX > 0 && relativeX < barXSize && relativeY > 0 && relativeY < barYSize) {
            int totalData = 0, totalMaxData = 0;

            for (DataStorage datum : data) {
                totalData += datum.getData();
                totalMaxData += datum.getMaxData();
            }

            List<String> list = new LinkedList<>();
            list.add(totalData + " / " + totalMaxData + " Data");
            list.add("Type: " + I18n.format(data[0].getDataType().toString()));

            this.drawTooltip(gui, list, mouseX, mouseY, zLevel, font);
        }

    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderBackground(GuiContainer gui, int x, int y, int mouseX, int mouseY, FontRenderer font) {

        for (Slot slot : slotList) {
            gui.drawTexturedModalRect(x + slot.xPos - 1, y + slot.yPos - 1, icon.getxLoc(), icon.getyLoc(), icon.getxSize(), icon.getySize());
        }

        int totalData = 0, totalMaxData = 0;

        for (DataStorage datum : data) {
            totalData += datum.getData();
            totalMaxData += datum.getMaxData();
        }

        float percent = totalData / (float) totalMaxData;

        gui.drawTexturedModalRect(offsetX + x, offsetY + y, 176, 18, 8, 40);
        gui.drawTexturedModalRect(offsetX + x - 1, offsetY + y + barYSize + 4, 19, 171, 10, 10);

        gui.drawTexturedModalRect(offsetX + x + 1, 1 + offsetY + y + (barYSize - (int) (percent * barYSize)), textureOffsetX, barYSize - (int) (percent * barYSize) + textureOffsetY, barXSize, (int) (percent * barYSize));
    }
}
