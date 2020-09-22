package zmaster587.advancedRocketry.client.render;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.NativeImage;
import zmaster587.advancedRocketry.AdvancedRocketry;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class ClientDynamicTexture extends DynamicTexture {

	private static final byte BYTES_PER_PIXEL = 4;
	int textureId;
	NativeImage nativeImage;
	
	
	/**
	 * Creates a texture with the default 512x512 pixels
	 */
	public ClientDynamicTexture() {
		this(512,512);
	}
	
	/**
	 * 
	 * @param x x size of the image
	 * @param y y size of the image
	 */
	public ClientDynamicTexture(int x, int y) {
		super(x,y, true);
		nativeImage = new NativeImage(x, y, true);
		textureId = -1;
		init();
	}
	
	/**
	 * @return this buffered image
	 */
	public int getHeight()
	{
		return 512;
	}
	
	public int getWidth()
	{
		return 512;
	}
	
	/**
	 * 
	 * @param x x location of the pixel
	 * @param y y location of the pixel
	 * @param color color in RGBA8
	 */
	public void setPixel(int x, int y, int color) {
		super.getTextureData().setPixelRGBA(x, y, color);
	}
	
	/**
	 * @return IntBuffer containing the pixels for the image
	 */
	public IntBuffer getByteBuffer() {
		return IntBuffer.wrap(getTextureData().makePixelArray());
	}

	public void setByteBuffer(IntBuffer buffer) {
		
		//Just clamp to edge
		//GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP);
		//GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP);
		
		//Scale linearly
		//GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		//GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		
		//GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
		
		//GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, image.getWidth(), image.getHeight(), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
		
		ByteBuffer bb = ByteBuffer.allocate(buffer.capacity());
		bb.asIntBuffer().put(buffer);
		try {
			getTextureData().read(bb);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void init() {
		//create array, every single pixel
		super.setBlurMipmap(false, false);
	}
	
	/**
	 * Returns the GL texture ID of this image, if it doesnt exist, then creates it
	 * @return the GL texture ID of this image
	 */
	public int getTextureId() {
		return super.getGlTextureId();
	}
}
