package zmaster587.advancedRocketry.client.render;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import zmaster587.advancedRocketry.AdvancedRocketry;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class ClientDynamicTexture {

    private static final byte BYTES_PER_PIXEL = 4;
    int textureId;
    private BufferedImage image;

    /**
     * Creates a texture with the default 512x512 pixels
     */
    public ClientDynamicTexture() {
        this(512, 512);
    }

    /**
     * @param x x size of the image
     * @param y y size of the image
     */
    public ClientDynamicTexture(int x, int y) {
        image = new BufferedImage(x, y, BufferedImage.TYPE_INT_ARGB);
        textureId = -1;
        init();
    }

    /**
     * @return this buffered image
     */
    public BufferedImage getImage() {
        return image;
    }

    /**
     * @param x     x location of the pixel
     * @param y     y location of the pixel
     * @param color color in RGBA8
     */
    public void setPixel(int x, int y, int color) {

        ByteBuffer buffer = BufferUtils.createByteBuffer(image.getHeight() * image.getWidth() * BYTES_PER_PIXEL);

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, getTextureId());
        GL11.glGetTexImage(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
        buffer.putInt(x + (y * image.getHeight()), color);

        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, image.getWidth(), image.getHeight(), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
    }

    /**
     * @return IntBuffer containing the pixels for the image
     */
    public IntBuffer getByteBuffer() {
        ByteBuffer buffer = BufferUtils.createByteBuffer(image.getHeight() * image.getWidth() * BYTES_PER_PIXEL);

        //GL11.glBindTexture(GL11.GL_TEXTURE_2D, getTextureId());
        //GL11.glGetTexImage(GL11.GL_TEXTURE_2D,0 , GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
        int[] pixels = new int[image.getWidth() * image.getHeight()];
        image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());

        IntBuffer ret = buffer.asIntBuffer();
        ret.put(pixels);
        return ret;
    }

    public void setByteBuffer(IntBuffer buffer) {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, getTextureId());

        //Just clamp to edge
        //GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP);
        //GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP);

        //Scale linearly
        //GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        //GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);

        //GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);

        //GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, image.getWidth(), image.getHeight(), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
        try {
            GL11.glTexSubImage2D(GL11.GL_TEXTURE_2D, 0, 0, 0, image.getWidth(), image.getHeight(), GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            AdvancedRocketry.logger.warn("Planet image generation FX failed!");
        }
    }

    private void init() {
        //create array, every single pixel

        ByteBuffer buffer = BufferUtils.createByteBuffer(image.getHeight() * image.getWidth() * BYTES_PER_PIXEL);

        for (int i = 0; i < image.getHeight() * image.getWidth(); i++) {
            buffer.putInt(0x00000000);
        }
        buffer.flip();

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, getTextureId());

        //Just clamp to edge
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP);

        //Scale linearly
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);

        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);

        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, image.getWidth(), image.getHeight(), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
    }

    /**
     * Returns the GL texture ID of this image, if it doesnt exist, then creates it
     *
     * @return the GL texture ID of this image
     */
    public int getTextureId() {
        if (textureId != -1)
            return textureId;

        textureId = GL11.glGenTextures();
        return textureId;
    }
}
