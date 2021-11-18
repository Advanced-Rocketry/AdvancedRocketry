package zmaster587.advancedRocketry.backwardCompat;

import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
public class GroupObject
{
    public String name;
    public ArrayList<Face> faces = new ArrayList<>();
    public int glDrawingMode;
    public VertexFormat drawMode;

    public GroupObject()
    {
        this("");
    }

    public GroupObject(String name)
    {
        this(name, -1);
    }

    public GroupObject(String name, int glDrawingMode)
    {
        this.name = name;
        this.glDrawingMode = glDrawingMode;
    }

    @OnlyIn(value=Dist.CLIENT)
    public void render(MatrixStack matrix, int lighting, int lightingOverlay, IVertexBuilder tessellator)
    {
    	render(matrix, lighting, lightingOverlay, tessellator, 1f, 1f, 1f, 1f);
    }
    
    @OnlyIn(value=Dist.CLIENT)
    public void render(MatrixStack matrix, int lighting, int lightingOverlay, IVertexBuilder tessellator, float r, float g, float b, float a)
    {
        if (faces.size() > 0)
        {
            for (Face face : faces)
            {
                face.addFaceForRender(matrix, lighting, lightingOverlay, tessellator, r, g, b, a);
            }
        }
    }
    
    @OnlyIn(value=Dist.CLIENT)
    public void render(MatrixStack matrix, IVertexBuilder tessellator)
    {
        if (faces.size() > 0)
        {
            for (Face face : faces)
            {
                face.addFaceForRender(matrix, tessellator);
            }
        }
    }
    
    @OnlyIn(value=Dist.CLIENT)
    public void render(MatrixStack matrix, IVertexBuilder tessellator, float r, float g, float b, float a)
    {
        if (faces.size() > 0)
        {
            for (Face face : faces)
            {
                face.addFaceForRender(matrix, tessellator, r, g, b, a);
            }
        }
    }
}