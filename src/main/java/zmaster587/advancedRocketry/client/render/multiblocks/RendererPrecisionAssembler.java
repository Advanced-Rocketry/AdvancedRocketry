package zmaster587.advancedRocketry.client.render.multiblocks;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import zmaster587.advancedRocketry.backwardCompat.ModelFormatException;
import zmaster587.advancedRocketry.backwardCompat.WavefrontObject;
import zmaster587.libVulpes.block.RotatableBlock;
import zmaster587.libVulpes.render.RenderHelper;
import zmaster587.libVulpes.tile.multiblock.TileMultiblockMachine;

import java.util.List;

public class RendererPrecisionAssembler extends TileEntitySpecialRenderer {
	WavefrontObject model;

	ResourceLocation texture = new ResourceLocation("advancedrocketry:textures/models/precAssembler.png");

	//private final RenderItem dummyItem = Minecraft.getInstance().getRenderItem();
	
	//Model Names:
	// Tray
	// Hull
	// ProcessA
	// ProcessB
	// ProcessC
	
	public RendererPrecisionAssembler() {
		try {
			model = new WavefrontObject(new ResourceLocation("advancedrocketry:models/precAssembler.obj"));
		} catch (ModelFormatException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void render(TileEntity tile, double x,
			double y, double z, float f, int damage, float a) {

		TileMultiblockMachine multiBlockTile = (TileMultiblockMachine)tile;

		if(!multiBlockTile.canRender())
			return;
		
		matrix.push();

		//Rotate and move the model into position
		matrix.translate(x+.5f, y, z + .5f);
		Direction front = RotatableBlock.getFront(tile.getWorld().getBlockState(tile.getPos())); //tile.getWorldObj().getBlockMetadata(tile.xCoord, tile.yCoord, tile.zCoord));
		GL11.glRotatef((front.getXOffset() == 1 ? 180 : 0) + front.getZOffset()*90f, 0, 1, 0);
		matrix.translate(-.5f, 0, -.5f);
		
		if(multiBlockTile.isRunning()) {

			float progress = multiBlockTile.getProgress(0)/(float)multiBlockTile.getTotalProgress(0);
			float process,tray;
			tray = process = 3*progress;



			List<ItemStack> outputList = multiBlockTile.getOutputs();
			if(outputList != null && !outputList.isEmpty()) {
				ItemStack stack = outputList.get(0);
				ItemEntity entity = new ItemEntity(tile.getWorld());
				
				entity.setItem(stack);
				entity.hoverStart = 0;
				
				matrix.push();
				GL11.glRotatef(90, 1, 0, 0);
				matrix.translate(1, tray + .75, -1.2f);
				RenderHelper.renderItem(multiBlockTile, entity,  Minecraft.getInstance().getRenderItem());
				matrix.pop();
			}
			
			bindTexture(texture);
			model.renderPart("Hull");
			
			matrix.translate(0, 0, tray);
			model.renderPart("Tray"); // 0-> 3
			matrix.translate(0, 0, -tray);
			
			process *= 6;
			if(process > 2 && process < 4){
				if(process < 3) {
					process-=2;
					GL11.glTranslatef(0, -.25f*process, 0); // 0 -> -.25
					model.renderPart("ProcessA");
					GL11.glTranslatef(0, .25f*process, 0);
				}
				else if(process < 4) {
					process = -process + 4;
					GL11.glTranslatef(0, -.25f*process, 0); // 0 -> -.25
					model.renderPart("ProcessA");
					GL11.glTranslatef(0, .25f*process, 0);
				}
			}
			else
				model.renderPart("ProcessA");
			
			
			
			process -= 6;
			if(process > 2 && process < 4){
				if(process < 3) {
					process-=2;
					GL11.glTranslatef(0, -.25f*process, 0); // 0 -> -.25
					model.renderPart("ProcessB");
					GL11.glTranslatef(0, .25f*process, 0);
				}
				else if(process < 4) {
					process = -process + 4;
					GL11.glTranslatef(0, -.25f*process, 0); // 0 -> -.25
					model.renderPart("ProcessB");
					GL11.glTranslatef(0, .25f*process, 0);
				}
			}
			else
				model.renderPart("ProcessB");
			
			process -= 6;
			if(process > 1 && process < 3){
				if(process < 2) {
					process-=1;
					
					matrix.translate(1.55, 1.47, 0);
					GL11.glRotatef(90*process, 0, 0, 1);
					matrix.translate(-1.55, -1.47, 0);
					
					model.renderPart("ProcessC");
				}
				else if(process < 3) {
					process = -process + 3;
					
					matrix.translate(1.55, 1.47, 0);
					GL11.glRotatef(90*process, 0, 0, 1);
					matrix.translate(-1.55, -1.47, 0);
					
					model.renderPart("ProcessC");
				}
			}
			else
				model.renderPart("ProcessC");



			
		}
		else {
			bindTexture(texture);
			model.renderAll();
		}
		
		
		matrix.pop();

	}

}
