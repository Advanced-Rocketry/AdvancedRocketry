package zmaster587.advancedRocketry.inventory.modules;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.AbstractButton;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.thread.EffectiveSide;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;

import zmaster587.advancedRocketry.api.ARConfiguration;
import zmaster587.advancedRocketry.api.Constants;
import zmaster587.advancedRocketry.api.dimension.IDimensionProperties;
import zmaster587.advancedRocketry.api.dimension.solar.IGalaxy;
import zmaster587.advancedRocketry.api.dimension.solar.StellarBody;
import zmaster587.advancedRocketry.dimension.DimensionManager;
import zmaster587.advancedRocketry.dimension.DimensionProperties;
import zmaster587.advancedRocketry.inventory.IPlanetDefiner;
import zmaster587.advancedRocketry.inventory.TextureResources;
import zmaster587.libVulpes.inventory.GuiModular;
import zmaster587.libVulpes.inventory.modules.*;
import zmaster587.libVulpes.render.RenderHelper;

import java.util.*;

public class ModulePlanetSelector extends ModuleContainerPan implements IButtonInventory {


	//Closest thing i can get to a struct :/
	private static class PlanetRenderProperties {
		int radius;
		int posX;
		int posY;

		public PlanetRenderProperties() {}

		public PlanetRenderProperties(int radius, int posX, int posY) {
			this.radius = radius;
			this.posX = posX;
			this.posY = posY;
		}
	}

	ModuleButton btnUpLevel, btnConfirm, btnPlanetList;

	private static final int size = 2000;
	private ResourceLocation topLevel;
	ISelectionNotify hostTile;
	private ResourceLocation currentSystem;
	ResourceLocation selectedSystem;
	private double zoom;
	private boolean currentSystemChanged = false;
	//If the current view is a starmap
	private boolean stellarView;
	private List<ModuleButton> planetList;
	private ModuleContainerPan clickablePlanetList;
	private boolean allowStarSelection;

	private HashMap<ResourceLocation, PlanetRenderProperties> renderPropertiesMap;
	PlanetRenderProperties currentlySelectedPlanet;
	IPlanetDefiner planetDefiner;

	public ModulePlanetSelector(ResourceLocation planetId, ResourceLocation backdrop, ISelectionNotify tile, boolean star) {
		this(planetId, backdrop, tile, null, star);
	}

	public ModulePlanetSelector(ResourceLocation planetId, ResourceLocation backdrop, ISelectionNotify tile, IPlanetDefiner definer, boolean star) {
		super(0, 0, null, null, backdrop, 0, 0, 0, 0, size,size);
		this.planetDefiner = definer;
		hostTile = tile;
		int center = size/2;
		zoom = 1.0;

		planetList = new ArrayList<>();
		moduleList = new ArrayList<>();
		staticModuleList = new ArrayList<>();
		renderPropertiesMap = new HashMap<>();
		currentlySelectedPlanet = new PlanetRenderProperties();
		currentSystem = Constants.INVALID_STAR;
		selectedSystem = Constants.INVALID_PLANET;
		stellarView = false;

		staticModuleList.add(btnUpLevel = new ModuleButton(0, 0, "<< Up", this, zmaster587.libVulpes.inventory.TextureResources.buttonBuild));
		staticModuleList.add(btnConfirm = new ModuleButton(0, 18, "Select", this, zmaster587.libVulpes.inventory.TextureResources.buttonBuild));
		staticModuleList.add(btnPlanetList = new ModuleButton(0, 36, "PlanetList", this, zmaster587.libVulpes.inventory.TextureResources.buttonBuild));

		ModuleDualProgressBar progressBar;
		staticModuleList.add(progressBar = new ModuleDualProgressBar(100, 0, 0, TextureResources.atmIndicator, (IProgressBar)tile, "%b -> %a Earth's atmospheric pressure"));
		progressBar.setTooltipValueMultiplier(.16f);

		staticModuleList.add(progressBar = new ModuleDualProgressBar(200, 0, 2, TextureResources.massIndicator, (IProgressBar)tile, "%b -> %a Earth's mass"));
		progressBar.setTooltipValueMultiplier(.02f);

		staticModuleList.add(progressBar = new ModuleDualProgressBar(300, 0, 1, TextureResources.distanceIndicator, (IProgressBar)tile, "%b -> %a Relative Distance units"));
		progressBar.setTooltipValueMultiplier(.16f);

		//renderPlanetarySystem(properties, center, center, 3f);
		if(EffectiveSide.get().isClient()) {

			//bgTexture = new ModuleImage(0, 54, zmaster587.libVulpes.inventory.TextureResources.buttonScan[0], 128,256);

			//staticModuleList.add(bgTexture);

			if(star) {
				topLevel = Constants.INVALID_PLANET;
				currentSystem = DimensionManager.getInstance().getStar(planetId).getId();
				renderStarSystem(DimensionManager.getInstance().getStar(planetId), center, center, 1f, 0.5f);
			}
			else {
				currentSystem = planetId;
				topLevel = planetId;
				renderPlanetarySystem(DimensionManager.getInstance().getDimensionProperties(planetId), center, center, 1f, 3f);
			}
			refreshSideBar(true, currentSystem);
		}


	}

	@Override
	public void onScroll(double dwheel) {
		//TODO
		//zoom = Math.min(Math.max(zoom + dwheel/1000.0, 0.36), 2.0);
		//redrawSystem();

		if(clickablePlanetList != null)
			clickablePlanetList.onScroll(dwheel);
	}

	public void setAllowStarSelection(boolean allow) {
		this.allowStarSelection = allow;
	}

	public ResourceLocation getSelectedSystem() {
		return selectedSystem;
	}

	public void setSelectedSystem(ResourceLocation id) {
		selectedSystem = id;
	}

	@OnlyIn(value=Dist.CLIENT)
	private void renderGalaxyMap(IGalaxy galaxy, int posX, int posY, float distanceZoomMultiplier, float planetSizeMultiplier) {
		Collection<StellarBody> stars = galaxy.getStars();

		for(StellarBody star : stars) {

			if(planetDefiner != null && !planetDefiner.isStarKnown(star))
				continue;

			int displaySize = (int)(planetSizeMultiplier*star.getDisplayRadius());
			int offsetX = star.getPosX() + posX - displaySize/2; 
			int offsetY = star.getPosZ() + posY - displaySize/2;
			ModuleButton button;

			if(star.getSubStars() != null && !star.getSubStars().isEmpty()) {
				float phaseInc = 360f / star.getSubStars().size();
				float phase = 0;
				for(StellarBody star2 : star.getSubStars()) {
					displaySize = (int)(planetSizeMultiplier*star2.getDisplayRadius());

					int deltaX, deltaY;
					deltaX = (int)(star2.getStarSeparation()*MathHelper.cos(phase)*0.5);
					deltaY = (int)(star2.getStarSeparation()*MathHelper.sin(phase)*0.5);

					planetList.add(button = new ModuleButton(offsetX + deltaX, offsetY + deltaY, "", this, new ResourceLocation[] { star.isBlackHole() ? TextureResources.locationBlackHole_icon : TextureResources.locationSunNew }, String.format("Name: %s\nNumber of Planets: %d",star.getName(), star.getNumPlanets()), displaySize, displaySize));
					button.setAdditionalData(star.getId());
					button.setSound("buttonblipa");
					button.setBGColor(star2.getColorRGB8());
					phase += phaseInc;
				}
			}

			planetList.add(button = new ModuleButton(offsetX, offsetY, "", this, new ResourceLocation[] { star.isBlackHole() ? TextureResources.locationBlackHole_icon : TextureResources.locationSunNew }, String.format("Name: %s\nNumber of Planets: %d",star.getName(), star.getNumPlanets()), displaySize, displaySize));
			button.setAdditionalData(star.getId());
			button.setSound("buttonblipa");
			button.setBGColor(star.getColorRGB8());

			renderPropertiesMap.put(star.getId(), new PlanetRenderProperties(displaySize, offsetX, offsetY));
			//prevMultiplier *= 0.25f;

		}

		moduleList.addAll(planetList);
	}

	@OnlyIn(value=Dist.CLIENT)
	private void renderStarSystem(StellarBody star, int posX, int posY, float distanceZoomMultiplier, float planetSizeMultiplier) {

		int displaySize = (int)(planetSizeMultiplier*star.getDisplayRadius());

		int offsetX = posX - displaySize/2; 
		int offsetY = posY - displaySize/2; 

		ModuleButton button;

		if(star.getSubStars() != null && !star.getSubStars().isEmpty()) {
			float phaseInc = 360f / star.getSubStars().size();
			float phase = 0;
			for(StellarBody star2 : star.getSubStars()) {
				displaySize = (int)(planetSizeMultiplier*star2.getDisplayRadius());

				int deltaX, deltaY;
				deltaX = (int)(star2.getStarSeparation()*MathHelper.cos(phase)*0.5);
				deltaY = (int)(star2.getStarSeparation()*MathHelper.sin(phase)*0.5);

				planetList.add(button = new ModuleButton(offsetX + deltaX, offsetY + deltaY, "", this, new ResourceLocation[] { star.isBlackHole() ? TextureResources.locationBlackHole_icon : TextureResources.locationSunNew }, String.format("Name: %s\nNumber of Planets: %d",star.getName(), star.getNumPlanets()), displaySize, displaySize));
				button.setAdditionalData(star.getId());
				button.setSound("buttonblipa");
				button.setBGColor(star2.getColorRGB8());
				phase += phaseInc;
			}
		}
		displaySize = (int)(planetSizeMultiplier*star.getDisplayRadius());
		offsetX = posX - displaySize/2; 
		offsetY = posY - displaySize/2; 

		planetList.add(button = new ModuleButton(offsetX, offsetY, "", this, new ResourceLocation[] { star.isBlackHole() ? TextureResources.locationBlackHole_icon : TextureResources.locationSunNew }, String.format("Name: %s\nNumber of Planets: %d",star.getName(), star.getNumPlanets()), displaySize, displaySize));
		button.setAdditionalData(star.getId());
		button.setSound("buttonblipa");
		button.setBGColor(star.getColorRGB8());
		renderPropertiesMap.put(star.getId(), new PlanetRenderProperties(displaySize, offsetX, offsetY));


		//prevMultiplier *= 0.25f;
		displaySize = (int)(planetSizeMultiplier*100);
		offsetX = posX - displaySize/2; 
		offsetY = posY - displaySize/2;

		for(IDimensionProperties properties : star.getPlanets()) {

			if(planetDefiner != null && !planetDefiner.isPlanetKnown(properties))
				continue;

			if(!properties.isMoon())
				renderPlanets((DimensionProperties)properties, offsetX + displaySize/2, offsetY + displaySize/2, displaySize, distanceZoomMultiplier,planetSizeMultiplier);
		}

		moduleList.addAll(planetList);
	}

	@OnlyIn(value=Dist.CLIENT)
	private void renderPlanetarySystem(DimensionProperties planet, int posX, int posY, float distanceZoomMultiplier, float planetSizeMultiplier) {

		int displaySize = Math.max((int)(planetSizeMultiplier*planet.gravitationalMultiplier/.02f), 7);

		int offsetX = (int)(distanceZoomMultiplier*posX) - displaySize/2; 
		int offsetY = (int)(distanceZoomMultiplier*posY) - displaySize/2; 
		displaySize *=distanceZoomMultiplier;

		ModuleButton button;
		planetList.add(button = new ModuleButtonPlanet(offsetX, offsetY, "", this, planet, planet.getName(), displaySize, displaySize));
		button.setAdditionalData(planet.getId());
		button.setSound("buttonblipa");

		renderPropertiesMap.put(planet.getId(), new PlanetRenderProperties(displaySize, offsetX, offsetY));

		//prevMultiplier *= 0.25f;

		for(ResourceLocation childId : planet.getChildPlanets()) {
			DimensionProperties properties = DimensionManager.getInstance().getDimensionProperties(childId);

			if(planetDefiner != null && !planetDefiner.isPlanetKnown(properties))
				continue;

			renderPlanets(properties, offsetX + displaySize/2, offsetY + displaySize/2, displaySize, distanceZoomMultiplier, planetSizeMultiplier);
		}

		moduleList.addAll(planetList);
	}

	@OnlyIn(value=Dist.CLIENT)
	private void renderPlanets(DimensionProperties planet, int parentOffsetX, int parentOffsetY, int parentRadius, float distanceMultiplier, float planetSizeMultiplier) {

		int displaySize = Math.max((int)(planetSizeMultiplier*planet.gravitationalMultiplier/.02f),7);

		int offsetX = parentOffsetX + (int)(Math.cos(planet.orbitTheta)*((planet.orbitalDist*distanceMultiplier) + parentRadius)) - displaySize/2;
		int offsetY = parentOffsetY + (int)(Math.sin(planet.orbitTheta)*((planet.orbitalDist*distanceMultiplier) + parentRadius)) - displaySize/2;

		ModuleButton button;

		planetList.add(button = new ModuleButtonPlanet(offsetX, offsetY, "", this, planet, planet.getName() + "\nMoons: " + planet.getChildPlanets().size(), displaySize, displaySize));
		button.setSound("buttonblipa");
		button.setAdditionalData(planet.getId());

		renderPropertiesMap.put(planet.getId(), new PlanetRenderProperties(displaySize, offsetX, offsetY));

	}


	@OnlyIn(value=Dist.CLIENT)
	public void setPlanetAsKnown(ResourceLocation id) {
		for(ModuleBase module : moduleList) {
			if(module instanceof ModuleButton && ((ModuleButton)module).getAdditionalData() == id) {
				((ModuleButton)module).setImage( new ResourceLocation[] {DimensionManager.getInstance().getDimensionProperties(id).getPlanetIcon()});
			}
		}
	}

	@Override
	@OnlyIn(value=Dist.CLIENT)
	public List<AbstractButton> addButtons(int x, int y) {

		int screenSizeX = Minecraft.getInstance().getMainWindow().getWidth();
		int screenSizeY = Minecraft.getInstance().getMainWindow().getHeight();

		setOffset2(internalOffsetX - screenSizeX/4, internalOffsetY - screenSizeY/4);

		List <AbstractButton> list = super.addButtons(x, y);

		if(clickablePlanetList != null)
			list.addAll(clickablePlanetList.addButtons(x, y));

		return list;
	}

	@OnlyIn(value=Dist.CLIENT)
	private void redrawSystem() {

		int offsetX = -currentPosX;
		int offsetY = -currentPosY;
		setOffset2(0,0);
		for(int i = 0; i< planetList.size(); i++) {
			ModuleButton module = planetList.get(i);
			if(planetList.contains(module))
				this.buttonList.remove(module.button);
		}

		this.moduleList.removeAll(planetList);

		planetList.clear();
		if(!stellarView) {
			if(!DimensionManager.getInstance().isStar(currentSystem)) {
				DimensionProperties properties = DimensionManager.getInstance().getDimensionProperties(currentSystem);
				renderPlanetarySystem(properties, size/2, size/2, 1f,3f*properties.getPathLengthToStar());
			}
			else
				renderStarSystem(DimensionManager.getInstance().getStar(currentSystem), size/2, size/2, (float) zoom, (float)zoom*.5f);
		}
		else
			renderGalaxyMap(DimensionManager.getInstance(), size/2, size/2, (float) zoom, (float)zoom*.25f);


		int x = currentPosX - size/2, y = currentPosY - size/2;

		this.screenSizeX = Minecraft.getInstance().getMainWindow().getWidth();
		this.screenSizeY = Minecraft.getInstance().getMainWindow().getHeight();
		for(ModuleBase module : this.planetList) {
			for(AbstractButton module2 : module.addButtons(currentPosX, currentPosY)) {
				if(module2.x /* x */ > 128 + offsetX || clickablePlanetList == null || !clickablePlanetList.isEnabled())
					buttonList.add( module2 );
			}
		}

		setOffset2(offsetX, offsetY);
	}

	@Override
	@OnlyIn(value=Dist.CLIENT)
	public void onMouseClicked(GuiModular gui, double x, double y, int button) {

		if(clickablePlanetList != null)
			clickablePlanetList.onMouseClicked(gui, x, y, button);

		super.onMouseClicked(gui, x, y, button);

		int screenSizeX = Minecraft.getInstance().getMainWindow().getWidth();
		int screenSizeY = Minecraft.getInstance().getMainWindow().getHeight();

		//CME workaround
		if(currentSystemChanged) {
			currentPosX = 0;
			currentPosY = 0;
			zoom = 1;
			redrawSystem();
			setOffset2(internalOffsetX - screenSizeX/4 , internalOffsetY - screenSizeY /4);
			//redrawSystem();

			//selectedSystem = Constants.INVALID_PLANET;

			currentSystemChanged = false;

			hostTile.onSystemFocusChanged(this);
			refreshSideBar(true, selectedSystem);
		}
	}

	@Override
	protected void moveContainerInterior(int deltaX, int deltaY) {
		super.moveContainerInterior(deltaX, deltaY);
	}

	@Override
	@OnlyIn(value=Dist.CLIENT)
	public void renderBackground(ContainerScreen<? extends Container> gui, MatrixStack matrix, int x, int y, int mouseX,
			int mouseY, FontRenderer font) {

		if(!stellarView && System.currentTimeMillis() % 5 == 0)
			redrawSystem();
		super.renderBackground(gui, matrix, x, y, mouseX, mouseY, font);

		int center = size/2;
		int numSegments = 50;

		float theta = (float) (2 * Math.PI / (float)(numSegments));
		float cos = (float) Math.cos(theta);
		float sin = (float) Math.sin(theta);

		BufferBuilder buffer = Tessellator.getInstance().getBuffer();
		GL11.glPushMatrix();

		//Render orbits
		if(!stellarView) {
			for(int ii = 1; ii < 10; ii++) {
				float x2 /*aka radius*/ = ii*80;
				float y2 = 0;
				float t;
				GL11.glPushMatrix();
				GL11.glTranslatef(center + currentPosX, center + currentPosY, 0);
				GlStateManager.disableTexture();
				GlStateManager.enableBlend();
				GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
				GL11.glColor4f(0.8f, .8f, 1f, .2f);
				GL11.glEnable(GL11.GL_LINE_STIPPLE);
				GL11.glLineStipple(5, (short)0x5555);


				buffer.begin(GL11.GL_LINE_LOOP, DefaultVertexFormats.POSITION);
				for(int i = 0; i < numSegments; i++)	{
					buffer.pos(x2, y2, 200).endVertex();
					t = x2;
					x2 = cos*x2 - sin*y2;
					y2 = sin*t + cos*y2;
				}
				Tessellator.getInstance().draw();
				//buffer.finishDrawing();
				//Reset GL info
				GlStateManager.enableTexture();
				GlStateManager.disableBlend();
				GL11.glColor4f(1f, 1f, 1f, 1f);
				GL11.glPopMatrix();
				GL11.glLineStipple(5, (short)0xFFFF);
			}
		}

		//Render Selection
		if(!Constants.INVALID_PLANET.equals(selectedSystem)) {

			Minecraft.getInstance().getTextureManager().bindTexture(TextureResources.selectionCircle);
			GL11.glPushMatrix();
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			float radius = currentlySelectedPlanet.radius / 2f;

			if(renderPropertiesMap.containsKey(selectedSystem)) {
				PlanetRenderProperties base = renderPropertiesMap.get(selectedSystem);
				GL11.glTranslatef(base.posX + currentPosX + base.radius/ 2f, base.posY + currentPosY + base.radius/ 2f, 0);
			}
			else 
				GL11.glTranslatef(currentlySelectedPlanet.posX + currentPosX + radius, currentlySelectedPlanet.posY  + currentPosY + radius, 0);

			double progress = System.currentTimeMillis() % 20000 / 50f;

			GL11.glPushMatrix();
			GL11.glRotated(progress, 0, 0, 1);

			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
			RenderHelper.renderNorthFaceWithUVNoNormal(matrix, buffer, 1, -radius, -radius, radius, radius, 0, 1, 0, 1,1,1,1,1);
			Tessellator.getInstance().draw();
			GL11.glPopMatrix();

			GL11.glPushMatrix();
			//GL11.glRotatef(-Minecraft.getInstance().theWorld.getGameTime(), 0, 0, 1);
			radius *= (1.2 + 0.1*Math.sin(progress/10f));
			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
			RenderHelper.renderNorthFaceWithUVNoNormal(matrix, buffer, 1, -radius, -radius, radius, radius, 0, 1, 0, 1,1,1,1,1);
			Tessellator.getInstance().draw();
			GL11.glPopMatrix();

			GlStateManager.disableBlend();
			GL11.glPopMatrix();
		}
		GL11.glPopMatrix();
	}

	@Override
	@OnlyIn(value=Dist.CLIENT)
	public void onInventoryButtonPressed(ModuleButton buttonId) {

		//Go Up a level
		if(buttonId == btnUpLevel) {
			DimensionProperties properties =  DimensionManager.getInstance().getDimensionProperties(currentSystem);

			if(Constants.INVALID_PLANET.equals(topLevel) || currentSystem != topLevel) {
				if(!DimensionManager.getInstance().isStar(currentSystem) && properties.isMoon())
					currentSystem = properties.getParentPlanet();
				else {
					if(DimensionManager.getInstance().isStar(currentSystem)) {
						//if the star was the current system then go to stellar view
						stellarView = true;
					}
					currentSystem = properties.getStar().getId();
				}

				currentSystemChanged=true;

				selectedSystem = Constants.INVALID_PLANET;
			}
		}
		//Confirm selection
		else if(buttonId == btnConfirm) {
			DimensionProperties properties =  DimensionManager.getInstance().getDimensionProperties(selectedSystem);
			if(!Constants.INVALID_PLANET.equals(selectedSystem) && !DimensionManager.getInstance().isStar(selectedSystem) || (this.allowStarSelection && properties.getStar().isBlackHole())) {
				hostTile.onSelectionConfirmed(this);
				Minecraft.getInstance().player.closeScreen();
			}
		}
		else if(buttonId == btnPlanetList) {
			if(clickablePlanetList != null) {
				boolean flag = !clickablePlanetList.isEnabled();
				clickablePlanetList.setEnabled(flag);
				//bgTexture.setEnabled(flag);
			}
		}
		else {
			//Zoom into selected system
			if(selectedSystem == buttonId.getAdditionalData()) {
				currentSystem = (ResourceLocation) buttonId.getAdditionalData();
				currentSystemChanged=true;
				//Go back to planetary mapping
				stellarView = false;
				//selectedSystem = Constants.INVALID_PLANET;
			}
			else {
				//Make clicked planet selected
				selectedSystem = (ResourceLocation)buttonId.getAdditionalData();
				currentlySelectedPlanet = renderPropertiesMap.get(buttonId.getAdditionalData());
				hostTile.onSelected(this);
				refreshSideBar(currentSystemChanged, selectedSystem);
			}
		}
	}

	@OnlyIn(value=Dist.CLIENT)
	private void refreshSideBar(boolean planetChanged, ResourceLocation selectedPlanet) {
		List<ModuleBase> list2 = new LinkedList<>();

		if(!stellarView) {
			if(!DimensionManager.getInstance().isStar(currentSystem)) {
				DimensionProperties parent = DimensionManager.getInstance().getDimensionProperties(currentSystem);

				List<ResourceLocation> propertyList = new LinkedList<>(parent.getChildPlanets());
				propertyList.add(parent.getId());
				int i = 0;
				for( ResourceLocation childId :  propertyList) 
				{
					DimensionProperties properties = DimensionManager.getInstance().getDimensionProperties(childId);

					if(planetDefiner != null && !planetDefiner.isPlanetKnown(properties))
						continue;

					ModuleButton button = new ModuleButton(0, i*18, properties.getName(), this, zmaster587.advancedRocketry.inventory.TextureResources.buttonGeneric, 128, 18);
					button.setAdditionalData(properties.getId());
					list2.add(button);

					if(properties.getId() == selectedPlanet)
						button.setColor(0xFFFF2222);

					i++;
				}
			}
			//Get planets around a star
			else {
				int i = 0;
				for( IDimensionProperties properties : DimensionManager.getInstance().getStar(currentSystem).getPlanets() ) 
				{

					if(planetDefiner != null && !planetDefiner.isPlanetKnown(properties))
						continue;

					if(!properties.isMoon() && !DimensionManager.spaceId.equals(properties.getId())) {
						ModuleButton button = new ModuleButton(0, i*18, properties.getName(), this, zmaster587.advancedRocketry.inventory.TextureResources.buttonGeneric, 128, 18);
						button.setAdditionalData(properties.getId());
						list2.add(button);

						if(properties.getId() == selectedPlanet)
							button.setColor(0xFFFF2222);
						i++;
					}
					
				}
			}
		}
		else {
			int i = 0;
			for( StellarBody properties : DimensionManager.getInstance().getStars() ) 
			{

				if(planetDefiner != null && !planetDefiner.isStarKnown(properties))
					continue;

				ModuleButton button = new ModuleButton(0, i*18, properties.getName(), this, zmaster587.advancedRocketry.inventory.TextureResources.buttonGeneric, 128, 18);
				button.setAdditionalData(properties.getId());
				list2.add(button);

				if(properties.getId() == selectedPlanet)
					button.setColor(0xFFFF2222);
				i++;
			}
		}

		boolean enabled = clickablePlanetList != null && clickablePlanetList.isEnabled();

		int offX = 0, offY = 0;

		if(clickablePlanetList != null) {
			staticModuleList.remove(clickablePlanetList);
			offX = clickablePlanetList.getScrollX();
			offY = clickablePlanetList.getScrollY();
		}

		clickablePlanetList = new ModuleContainerPan(0, 128, list2, new LinkedList<>(), null, 512, 256, 0, 0, 258, 8192);
		staticModuleList.add(clickablePlanetList);
		clickablePlanetList.addButtons(0, 0);

		//Hacky fix for bug in containerPan
		if(!planetChanged)
			clickablePlanetList.setOffset2(-offX, -offY);
		else
			clickablePlanetList.setOffset2(0, 64);

		clickablePlanetList.setEnabled(enabled);
		//bgTexture.setEnabled(enabled);
	}

	@Override
	public boolean needsUpdate(int localId) {
		for(ModuleBase module : staticModuleList) {
			if(localId >= 0 && localId < module.numberOfChangesToSend())
				return module.needsUpdate(localId);

			localId -= module.numberOfChangesToSend();
		}
		return false;
	}

	@Override
	public void sendChanges(Container container, IContainerListener crafter,
			int variableId, int localId) {
		for(ModuleBase module : staticModuleList) {
			if(localId >= 0 && localId < module.numberOfChangesToSend()) {
				module.sendChanges(container, crafter, variableId, localId);
				return;
			}

			localId -= module.numberOfChangesToSend();
		}
	}

	@Override
	public void onChangeRecieved(int slot, int value) {
		for(ModuleBase module : staticModuleList) {
			if(slot >= 0 && slot < module.numberOfChangesToSend()) {
				module.onChangeRecieved(slot, value);
				return;
			}

			slot -= module.numberOfChangesToSend();
		}
	}

	@Override
	public int numberOfChangesToSend() {
		int numChanges = 0;
		for(ModuleBase module : staticModuleList) {
			numChanges += module.numberOfChangesToSend();
		}

		return numChanges;
	}
}
