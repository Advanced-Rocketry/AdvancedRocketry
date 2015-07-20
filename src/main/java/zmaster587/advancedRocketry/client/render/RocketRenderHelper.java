package zmaster587.advancedRocketry.client.render;

import org.lwjgl.opengl.GL11;

public class RocketRenderHelper {
	public static void renderOrbit(double x, double y, double z, double xRadius, double yRadius, double phase) {
		renderOrbit(x, y, z, xRadius, yRadius, 0, 0);
	}
	
	public static void renderOrbit(double x, double y, double z, double xRadius, double yRadius, double xOffset, double yOffset) {
		
		GL11.glLineWidth(20/(float)(Math.pow(x - 0.5 - xOffset, 2) + Math.pow(y - 0.5 - yOffset, 2) + Math.pow(z - 0.5, 2)));
		GL11.glBegin(GL11.GL_LINE_STRIP);
		for(int i = 0; i < 13; i++)
			GL11.glVertex3d(xOffset + 0.5 + xRadius*Math.cos((Math.PI*i)/6)/2.2, yOffset - 0.5 + yRadius*Math.sin((Math.PI*i)/6)/2.2, 0);
		GL11.glEnd();
	}
	
	public static void renderPositionAlongOrbit(double x, double y, double z, double xRadius, double yRadius, double phase, double xOffset, double yOffset) {
		GL11.glPointSize(200/(float)(Math.pow(x - 0.5 - xOffset, 2) + Math.pow(y - yOffset - 0.5, 2) + Math.pow(z - 0.5, 2)));
		GL11.glBegin(GL11.GL_POINTS);
		
		GL11.glVertex3d(xOffset + 0.5 + xRadius*Math.cos((Math.PI*phase)/180)/2.2, yOffset - 0.5 + yRadius*Math.sin((Math.PI*phase)/180)/2.2, 0);
		GL11.glEnd();
	}
}
