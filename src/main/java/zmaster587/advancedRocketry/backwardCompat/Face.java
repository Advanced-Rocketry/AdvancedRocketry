package zmaster587.advancedRocketry.backwardCompat;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.util.math.vector.Matrix3f;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.math.vector.Vector4f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class Face
{
    public Vertex[] vertices;
    public Vertex[] vertexNormals;
    public Vertex faceNormal;
    public TextureCoordinate[] textureCoordinates;

    @OnlyIn(value=Dist.CLIENT)
    public void addFaceForRender(MatrixStack matrix, int lighting, int lightingOverlay, IVertexBuilder tessellator, float r, float g, float b, float a)
    {
        addFaceForRender(matrix, lighting, lightingOverlay, tessellator, 0.0005F,r ,g, b, a);
    }
    
    @OnlyIn(value=Dist.CLIENT)
    public void addFaceForRender(MatrixStack matrix, IVertexBuilder tessellator)
    {
        addFaceForRender(matrix, tessellator, 0.0005F);
    }
    
    @OnlyIn(value=Dist.CLIENT)
    public void addFaceForRender(MatrixStack matrix, IVertexBuilder tessellator, float textureOffset)
    {
        if (faceNormal == null)
        {
            faceNormal = this.calculateFaceNormal();
        }

        //tessellator.setNormal(faceNormal.x, faceNormal.y, faceNormal.z);

        float averageU = 0F;
        float averageV = 0F;

        if ((textureCoordinates != null) && (textureCoordinates.length > 0))
        {
            for (int i = 0; i < textureCoordinates.length; ++i)
            {
                averageU += textureCoordinates[i].u;
                averageV += textureCoordinates[i].v;
            }

            averageU = averageU / textureCoordinates.length;
            averageV = averageV / textureCoordinates.length;
        }

        float offsetU, offsetV;
        Matrix4f matrix4f = matrix.getLast().getMatrix();
        Matrix3f matrix3f = matrix.getLast().getNormal();
        for (int i = 0; i < vertices.length; ++i)
        {

            if ((textureCoordinates != null) && (textureCoordinates.length > 0))
            {
                offsetU = textureOffset;
                offsetV = textureOffset;

                if (textureCoordinates[i].u > averageU)
                {
                    offsetU = -offsetU;
                }
                if (textureCoordinates[i].v > averageV)
                {
                    offsetV = -offsetV;
                }
                
                Vector3f vector3f = new Vector3f(faceNormal.x, faceNormal.y, faceNormal.z);
                vector3f.transform(matrix3f);
                
                Vector4f vector4f = new Vector4f(vertices[i].x, vertices[i].y, vertices[i].z, 1.0F);
                vector4f.transform(matrix4f);
                tessellator.pos(vector4f.getX(), vector4f.getY(),vector4f.getZ()).tex(textureCoordinates[i].u + offsetU, textureCoordinates[i].v + offsetV).normal(vector3f.getX(), vector3f.getY(), vector3f.getZ()).endVertex();

            }
            else
            {

                
                Vector3f vector3f = new Vector3f(faceNormal.x, faceNormal.y, faceNormal.z);
                vector3f.transform(matrix3f);
                
                Vector4f vector4f = new Vector4f(vertices[i].x, vertices[i].y, vertices[i].z, 1.0F);
                vector4f.transform(matrix4f);
            	tessellator.pos(vector4f.getX(), vector4f.getY(),vector4f.getZ()).color(1, 1, 1, 1).lightmap(-1, -1).normal(vector3f.getX(), vector3f.getY(), vector3f.getZ()).endVertex();
            }
        }
    }
    
    @OnlyIn(value=Dist.CLIENT)
    public void addFaceForRender(MatrixStack matrix, int lighting, int lightingOverlay, IVertexBuilder tessellator, float textureOffset)
    {
    	addFaceForRender(matrix, lighting, lightingOverlay, tessellator, textureOffset,1f,1f,1f,1f);
    }
    
    @OnlyIn(value=Dist.CLIENT)
    public void addFaceForRender(MatrixStack matrix, int lighting, int lightingOverlay, IVertexBuilder tessellator, float textureOffset, float r, float g, float b, float a)
    {
        if (faceNormal == null)
        {
            faceNormal = this.calculateFaceNormal();
        }

        //tessellator.setNormal(faceNormal.x, faceNormal.y, faceNormal.z);

        float averageU = 0F;
        float averageV = 0F;

        if ((textureCoordinates != null) && (textureCoordinates.length > 0))
        {
            for (int i = 0; i < textureCoordinates.length; ++i)
            {
                averageU += textureCoordinates[i].u;
                averageV += textureCoordinates[i].v;
            }

            averageU = averageU / textureCoordinates.length;
            averageV = averageV / textureCoordinates.length;
        }

        float offsetU, offsetV;
        Matrix4f matrix4f = matrix.getLast().getMatrix();
        Matrix3f matrix3f = matrix.getLast().getNormal();
        for (int i = 0; i < vertices.length; ++i)
        {

            if ((textureCoordinates != null) && (textureCoordinates.length > 0))
            {
                offsetU = textureOffset;
                offsetV = textureOffset;

                if (textureCoordinates[i].u > averageU)
                {
                    offsetU = -offsetU;
                }
                if (textureCoordinates[i].v > averageV)
                {
                    offsetV = -offsetV;
                }
                
                Vector3f vector3f = new Vector3f(faceNormal.x, faceNormal.y, faceNormal.z);
                vector3f.transform(matrix3f);
                
                Vector4f vector4f = new Vector4f(vertices[i].x, vertices[i].y, vertices[i].z, 1.0F);
                vector4f.transform(matrix4f);
                tessellator.addVertex(vector4f.getX(), vector4f.getY(),vector4f.getZ(), r, g, b, a, textureCoordinates[i].u + offsetU, textureCoordinates[i].v + offsetV, lightingOverlay, lighting, vector3f.getX(), vector3f.getY(), vector3f.getZ());
            }
            else
            {

                
                Vector3f vector3f = new Vector3f(faceNormal.x, faceNormal.y, faceNormal.z);
                vector3f.transform(matrix3f);
                
                Vector4f vector4f = new Vector4f(vertices[i].x, vertices[i].y, vertices[i].z, 1.0F);
                vector4f.transform(matrix4f);
                tessellator.addVertex(vector4f.getX(), vector4f.getY(),vector4f.getZ(), r, g, b, a, 0, 0, lightingOverlay, lighting, vector3f.getX(), vector3f.getY(), vector3f.getZ());
            }
        }
    }

    public Vertex calculateFaceNormal()
    {
    	
        Vector3d v1 = new Vector3d(vertices[1].x - vertices[0].x, vertices[1].y - vertices[0].y, vertices[1].z - vertices[0].z);
        Vector3d v2 = new Vector3d(vertices[2].x - vertices[0].x, vertices[2].y - vertices[0].y, vertices[2].z - vertices[0].z);
        Vector3d normalVector = null;

        normalVector = v1.crossProduct(v2).normalize();

        return new Vertex((float) normalVector.x, (float) normalVector.y, (float) normalVector.z);
    }
}