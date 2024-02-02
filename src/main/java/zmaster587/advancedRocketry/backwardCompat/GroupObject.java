package zmaster587.advancedRocketry.backwardCompat;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;

public class GroupObject {
    public String name;
    public ArrayList<Face> faces = new ArrayList<>();
    public int glDrawingMode;
    public VertexFormat drawMode;

    public GroupObject() {
        this("");
    }

    public GroupObject(String name) {
        this(name, -1);
    }

    public GroupObject(String name, int glDrawingMode) {
        this.name = name;
        this.glDrawingMode = glDrawingMode;
    }

    @SideOnly(Side.CLIENT)
    public void render() {
        if (faces.size() > 0) {
            BufferBuilder buffer = Tessellator.getInstance().getBuffer();
            buffer.begin(glDrawingMode, drawMode);
            render(buffer);
            Tessellator.getInstance().draw();
        }
    }

    @SideOnly(Side.CLIENT)
    public void render(BufferBuilder tessellator) {
        if (faces.size() > 0) {
            for (Face face : faces) {
                face.addFaceForRender(tessellator);
            }
        }
    }
}