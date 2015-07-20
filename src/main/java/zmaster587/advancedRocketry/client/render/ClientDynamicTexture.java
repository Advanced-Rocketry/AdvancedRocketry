package zmaster587.advancedRocketry.client.render;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

public class ClientDynamicTexture {

	private BufferedImage image;
	private static final byte BYTES_PER_PIXEL = 4;
	int textureId;
	
	public ClientDynamicTexture() {
		image = new BufferedImage(512, 512, BufferedImage.TYPE_INT_ARGB);
		textureId = -1;
		init();
	}
	
	public ClientDynamicTexture(int x, int y) {
		image = new BufferedImage(x, y, BufferedImage.TYPE_INT_ARGB);
		textureId = -1;
		init();
	}
	
	public BufferedImage getImage() {
		return image;
	}
	
	public void setPixel(int x, int y, int color) {
		
		ByteBuffer buffer = BufferUtils.createByteBuffer(image.getHeight() * image.getWidth() * BYTES_PER_PIXEL);;
		
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, getTextureId());
		GL11.glGetTexImage(GL11.GL_TEXTURE_2D,0 , GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
		buffer.putInt(x + (y * image.getHeight()), color);
		
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, image.getWidth(), image.getHeight(), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
	}
	
	public IntBuffer getByteBuffer() {
		ByteBuffer buffer = BufferUtils.createByteBuffer(image.getHeight() * image.getWidth() * BYTES_PER_PIXEL);;
		
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
		GL11.glTexSubImage2D(GL11.GL_TEXTURE_2D, 0, 0, 0, image.getWidth(), image.getHeight(), GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
	}
	
	private void init() {
		//create array, every single pixel

		ByteBuffer buffer = BufferUtils.createByteBuffer(image.getHeight() * image.getWidth() * BYTES_PER_PIXEL);

		for(int i = 0; i < image.getHeight() * image.getWidth(); i++) {
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
	
	public int getTextureId() {
		if(textureId != -1)
			return textureId;
		
		textureId = GL11.glGenTextures();
		return textureId;
	}
}
