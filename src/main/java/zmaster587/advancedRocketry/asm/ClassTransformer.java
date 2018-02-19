package zmaster587.advancedRocketry.asm;

import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import zmaster587.advancedRocketry.AdvancedRocketry;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.entity.Entity;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.launchwrapper.Launch;

public class ClassTransformer implements IClassTransformer {


	private static final String CLASS_KEY_ENTITYRENDERER = "net.minecraft.client.renderer.EntityRenderer";
	private static final String CLASS_KEY_ENTITYLIVEINGBASE = "net.minecraft.entity.EntityLivingBase";
	private static final String CLASS_KEY_ENTITYLIVINGRENDERER = "net.minecraft.client.renderer.entity.RenderLivingEntity";
	private static final String CLASS_KEY_ENTITY = "net.minecraft.entity.Entity";
	private static final String CLASS_KEY_ENTITY_PLAYER_SP = "net.minecraft.client.entity.EntityPlayerSP";
	private static final String CLASS_KEY_ENTITY_PLAYER_MP = "net.minecraft.client.entity.EntityPlayerMP";
	private static final String CLASS_KEY_ENTITY_PLAYER = "net.minecraft.entity.player.EntityPlayer";
	private static final String CLASS_KEY_ENTITY_ITEM = "net.minecraft.entity.EntityItem";
	private static final String CLASS_KEY_NETHANDLERPLAYSERVER = "net.minecraft.network.NetHandlerPlayServer";
	private static final String CLASS_KEY_C03PACKETPLAYER = "net.minecraft.network.play.client.C03PacketPlayer";
	private static final String CLASS_KEY_WORLD = "net.minecraft.world.World";
	private static final String CLASS_KEY_BLOCK = "net.minecraft.block.Block";
	private static final String CLASS_KEY_BLOCKPOS = "net.minecraft.util.math.BlockPos";
	private static final String CLASS_KEY_IBLOCKSTATE = "net.minecraft.block.state.IBlockState";
	private static final String CLASS_KEY_RENDER_GLOBAL = "net.minecraft.client.renderer.RenderGlobal";
	private static final String CLASS_KEY_ICAMERA = "net.minecraft.client.renderer.culling.ICamera";
	private static final String CLASS_KEY_BLOCK_BED = "net.minecraft.block.BlockBed";
	private static final String CLASS_KEY_WORLDPROVIDER = "net.minecraft.world.WorldProvider";
	private static final String CLASS_KEY_ENUMHAND = "net.minecraft.util.EnumHand";
	private static final String CLASS_KEY_ITEMSTACK = "net.minecraft.item.ItemStack";
	private static final String CLASS_KEY_ENUMFACING = "net.minecraft.util.EnumFacing";
	
	private static final String METHOD_KEY_PROCESSPLAYER = "processPlayer";
	private static final String METHOD_KEY_JUMP = "jump";
	private static final String METHOD_KEY_MOVEENTITY = "moveEntity";
	private static final String METHOD_KEY_SETPOSITION = "setPosition";
	private static final String METHOD_KEY_MOUNTENTITY = "mountEntity";
	private static final String METHOD_KEY_ONLIVINGUPDATE = "net.minecraft.client.entity.EntityPlayerSP.onLivingUpdate";
	private static final String METHOD_KEY_ONUPDATE = "net.minecraft.client.entity.Entity.onUpdate";
	private static final String METHOD_KEY_GETLOOKVEC = "net.minecraft.entity.EntityLivingBase.getLookVec";
	private static final String METHOD_KEY_DORENDER  = "net.minecraft.client.renderer.entity.RenderLivingEntity.doRender";
	private static final String METHOD_KEY_MOVEENTITYWITHHEADING = "net.minecraft.entity.EntityLivingBase.moveEntityWithHeading";
	private static final String METHOD_KEY_MOVEFLYING = "net.minecraft.entity.Entity.moveFlying";
	private static final String METHOD_KEY_SETBLOCKSTATE = CLASS_KEY_WORLD + ".setBlockState";
	private static final String METHOD_KEY_SETBLOCKMETADATAWITHNOTIFY = CLASS_KEY_WORLD + ".setBlockMetadataWithNotify";
	private static final String METHOD_KEY_SETUPTERRAIN = "setupTerrain";
	private static final String METHOD_KEY_ONBLOCKACTIVATED = CLASS_KEY_BLOCK_BED  + "onBlockActivated";
	
	private static final String FIELD_YAW = "net.minecraft.client.renderer.EntityRenderer.rotationYaw";
	private static final String FIELD_PITCH = "net.minecraft.client.renderer.EntityRenderer.rotationPitch";
	private static final String FIELD_PREV_YAW = "net.minecraft.client.renderer.EntityRenderer.prevRotationYaw";
	private static final String FIELD_PREV_PITCH = "net.minecraft.client.renderer.EntityRenderer.prevRotationPitch";
	private static final String FIELD_PLAYERENTITY = "net.minecraft.network.NetHandlerPlayServer.playerEntity";
	private static final String FIELD_HASMOVED = "net.minecraft.network.NetHandlerPlayServer.hasMoved";
	private static final String FIELD_RIDINGENTITY = "net.minecraft.entity.Entity.ridingEntity";
	private static final String FIELD_PROVIDER = CLASS_KEY_WORLD + "provider";
	
	private static final HashMap<String, SimpleEntry<String, String>> entryMap = new HashMap<String, SimpleEntry<String, String>>();
	


	private boolean obf;

	/*private class ClassEntry {
		String name, obfName, desc;

		public ClassEntry(String name, String obfName, String desc) {
			this.name = name;
			this.obfName = obfName;
			this.desc = desc;
		}

		public String getObfName() {return obfName; }
		public String getDeobfName() {return name; }
		public String getDesc() {return desc;}
	}*/

	public ClassTransformer() {

		obf = !(boolean)Launch.blackboard.get("fml.deobfuscatedEnvironment");
		//TODO: obf names
		//entryMap.put(CLASS_KEY_ENTITYRENDERER, new SimpleEntry<String, String>("net/minecraft/client/renderer/EntityRenderer", "blt"));
		entryMap.put(CLASS_KEY_ENTITYLIVEINGBASE, new SimpleEntry<String, String>("net/minecraft/entity/EntityLivingBase", "sf"));
		//entryMap.put(CLASS_KEY_ENTITYLIVINGRENDERER, new SimpleEntry<String, String>("net/minecraft/client/renderer/entity/RendererLivingEntity", ""));
		entryMap.put(CLASS_KEY_ENTITY, new SimpleEntry<String, String>("net/minecraft/entity/Entity","rw"));
		//entryMap.put(CLASS_KEY_ENTITY_PLAYER_SP, new SimpleEntry<String, String>("net/minecraft/client/entity/EntityPlayerSP",""));
		entryMap.put(CLASS_KEY_ENTITY_PLAYER_MP, new SimpleEntry<String, String>("net/minecraft/entity/player/EntityPlayerMP","lu"));
		entryMap.put(CLASS_KEY_ENTITY_PLAYER, new SimpleEntry<String, String>("net/minecraft/entity/player/EntityPlayer","zs"));
		entryMap.put(CLASS_KEY_ENTITY_ITEM, new SimpleEntry<String, String>("net/minecraft/entity/item/EntityItem","yk"));
		//entryMap.put(CLASS_KEY_NETHANDLERPLAYSERVER, new SimpleEntry<String, String>("net/minecraft/network/NetHandlerPlayServer",""));
		//entryMap.put(CLASS_KEY_C03PACKETPLAYER, new SimpleEntry<String, String>("net/minecraft/network/play/client/C03PacketPlayer",""));
		entryMap.put(CLASS_KEY_WORLD, new SimpleEntry<String, String>("net/minecraft/world/World","aid"));
		entryMap.put(CLASS_KEY_BLOCK, new SimpleEntry<String, String>("net/minecraft/block/Block","akf"));
		entryMap.put(CLASS_KEY_BLOCKPOS, new SimpleEntry<String, String>("net/minecraft/util/math/BlockPos","cm"));
		entryMap.put(CLASS_KEY_IBLOCKSTATE, new SimpleEntry<String, String>("net/minecraft/block/state/IBlockState","ars"));
		entryMap.put(CLASS_KEY_RENDER_GLOBAL, new SimpleEntry<String, String>("net/minecraft/client/renderer/RenderGlobal","boh"));
		entryMap.put(CLASS_KEY_ICAMERA, new SimpleEntry<String, String>("net/minecraft/client/renderer/culling/ICamera","brf"));
		entryMap.put(CLASS_KEY_BLOCK_BED, new SimpleEntry<String, String>("net/minecraft/block/BlockBed","akd"));
		entryMap.put(CLASS_KEY_WORLDPROVIDER, new SimpleEntry<String, String>("net/minecraft/world/WorldProvider","atl"));
		entryMap.put(CLASS_KEY_ENUMHAND, new SimpleEntry<String, String>("net/minecraft/util/EnumHand","qr"));
		entryMap.put(CLASS_KEY_ITEMSTACK, new SimpleEntry<String, String>("net/minecraft/item/ItemStack","adz"));
		entryMap.put(CLASS_KEY_ENUMFACING, new SimpleEntry<String, String>("net/minecraft/util/EnumFacing","ct"));
		
		
		//entryMap.put(METHOD_KEY_PROCESSPLAYER, new SimpleEntry<String, String>("processPlayer",""));
		//entryMap.put(METHOD_KEY_MOVEENTITY, new SimpleEntry<String, String>("moveEntity",""));
		//entryMap.put(METHOD_KEY_SETPOSITION, new SimpleEntry<String, String>("setPosition",""));
		//entryMap.put(METHOD_KEY_GETLOOKVEC, new SimpleEntry<String, String>("getLook", ""));
		//entryMap.put(METHOD_KEY_DORENDER, new SimpleEntry<String, String>("doRender",""));
		entryMap.put(METHOD_KEY_MOVEENTITYWITHHEADING, new SimpleEntry<String, String>("moveEntityWithHeading","g"));
		//entryMap.put(METHOD_KEY_MOVEFLYING, new SimpleEntry<String, String>("moveFlying",""));
		//entryMap.put(METHOD_KEY_ONLIVINGUPDATE, new SimpleEntry<String, String>("onLivingUpdate","e"));
		entryMap.put(METHOD_KEY_ONUPDATE, new SimpleEntry<String, String>("onUpdate","m"));
		//entryMap.put(METHOD_KEY_MOUNTENTITY, new SimpleEntry<String, String>("mountEntity", "a"));
		//entryMap.put(METHOD_KEY_JUMP, new SimpleEntry<String, String>("jump",""));
		entryMap.put(METHOD_KEY_SETBLOCKSTATE, new SimpleEntry<String, String>("setBlockState", "a"));
		//entryMap.put(METHOD_KEY_SETBLOCKMETADATAWITHNOTIFY, new SimpleEntry<String, String>("setBlockMetadataWithNotify", "a"));
		entryMap.put(METHOD_KEY_SETUPTERRAIN, new SimpleEntry<String, String>("setupTerrain", "a"));
		entryMap.put(METHOD_KEY_ONBLOCKACTIVATED, new SimpleEntry<String, String>("onBlockActivated", "a"));
		
		//entryMap.put(FIELD_YAW, new SimpleEntry<String, String>("rotationYaw", "blt"));
		//entryMap.put(FIELD_PITCH, new SimpleEntry <String, String>("rotationPitch", "blt"));
		//entryMap.put(FIELD_PREV_YAW, new SimpleEntry<String, String>("prevRotationYaw", "blt"));
		//entryMap.put(FIELD_PREV_PITCH, new SimpleEntry<String, String>("prevRotationPitch", "blt"));
		//entryMap.put(FIELD_PLAYERENTITY, new SimpleEntry<String, String>("playerEntity", ""));
		//entryMap.put(FIELD_HASMOVED, new SimpleEntry<String, String>("hasMoved", ""));
		//entryMap.put(FIELD_RIDINGENTITY, new SimpleEntry<String,String>("ridingEntity", "m"));
		entryMap.put(FIELD_PROVIDER, new SimpleEntry<String,String>("provider", "s"));
	}

	@Override
	public byte[] transform(String name, String transformedName,
			byte[] bytes) {


		//Vanilla deobf
		String changedName = name.replace('.','/');

		//Need to override setPosition to fix bounding boxes

		/*if(changedName.equals(getName(CLASS_KEY_NETHANDLERPLAYSERVER))) {
			ClassNode cn = startInjection(bytes);
			MethodNode processPlayer = getMethod(cn, "setPlayerLocation", "(DDDFF)V");

			if(processPlayer != null ) {
				final InsnList nodeAdd = new InsnList();

				AbstractInsnNode pos = null;


				for (int i = processPlayer.instructions.size()-1; i > 0; i--) {
					AbstractInsnNode ain = processPlayer.instructions.get(i);

					if(ain.getOpcode() == Opcodes.ALOAD) {
						pos = ain;
					}
				} 


				nodeAdd.add(new VarInsnNode(Opcodes.ALOAD, 0));
				nodeAdd.add(new FieldInsnNode(Opcodes.GETFIELD, getName(CLASS_KEY_NETHANDLERPLAYSERVER), getName(FIELD_PLAYERENTITY), "L" + getName(CLASS_KEY_ENTITY_PLAYER_MP) + ";"));
				nodeAdd.add(new VarInsnNode(Opcodes.DLOAD, 1));
				nodeAdd.add(new VarInsnNode(Opcodes.DLOAD, 3));
				nodeAdd.add(new VarInsnNode(Opcodes.DLOAD, 5));
				nodeAdd.add(new VarInsnNode(Opcodes.FLOAD, 7));
				nodeAdd.add(new VarInsnNode(Opcodes.FLOAD, 8));
				nodeAdd.add(new VarInsnNode(Opcodes.ALOAD, 0));
				nodeAdd.add(new FieldInsnNode(Opcodes.GETFIELD, getName(CLASS_KEY_NETHANDLERPLAYSERVER), getName(FIELD_PLAYERENTITY), "L" + getName(CLASS_KEY_ENTITY_PLAYER_MP) + ";"));
				nodeAdd.add(new TypeInsnNode(Opcodes.CHECKCAST, getName(CLASS_KEY_ENTITYLIVEINGBASE)));
				nodeAdd.add(new FieldInsnNode(Opcodes.GETFIELD, getName(CLASS_KEY_ENTITYLIVEINGBASE), "gravRotation", "I"));

				nodeAdd.add(new MethodInsnNode(Opcodes.INVOKESTATIC,  "zmaster587/advancedRocketry/client/ClientHelper", "netHandlerSetPlayerLocation", "(L" + getName(CLASS_KEY_ENTITY_PLAYER_MP) + ";DDDFFI)V", false));
				nodeAdd.add(new InsnNode(Opcodes.RETURN));

				processPlayer.instructions.insertBefore(processPlayer.instructions.getFirst(), nodeAdd);
			}

			return finishInjection(cn);
		}
		if(changedName.equals(getName(CLASS_KEY_ENTITY))) {
			ClassNode cn = startInjection(bytes);
			MethodNode setPosition = getMethod(cn, getName(METHOD_KEY_SETPOSITION), "(DDD)V");

			if(setPosition != null) {
				final InsnList nodeAdd = new InsnList();
				final LabelNode jumpNode = new LabelNode();

				nodeAdd.add(new VarInsnNode(Opcodes.ALOAD,0));
				nodeAdd.add(new TypeInsnNode(Opcodes.INSTANCEOF, getName(CLASS_KEY_ENTITYLIVEINGBASE)));
				nodeAdd.add(new JumpInsnNode(Opcodes.IFEQ, jumpNode));

				nodeAdd.add(new VarInsnNode(Opcodes.ALOAD,0));
				nodeAdd.add(new TypeInsnNode(Opcodes.CHECKCAST, getName(CLASS_KEY_ENTITYLIVEINGBASE)));
				nodeAdd.add(new VarInsnNode(Opcodes.ALOAD,0));
				nodeAdd.add(new TypeInsnNode(Opcodes.CHECKCAST, getName(CLASS_KEY_ENTITYLIVEINGBASE)));
				nodeAdd.add(new FieldInsnNode(Opcodes.GETFIELD, getName(CLASS_KEY_ENTITYLIVEINGBASE), "gravRotation", "I"));
				nodeAdd.add(new VarInsnNode(Opcodes.DLOAD, 1));
				nodeAdd.add(new VarInsnNode(Opcodes.DLOAD, 3));
				nodeAdd.add(new VarInsnNode(Opcodes.DLOAD, 5));
				nodeAdd.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "zmaster587/advancedRocketry/client/ClientHelper", "setPosition", "(L" + getName(CLASS_KEY_ENTITYLIVEINGBASE) + ";IDDD)V", false));
				nodeAdd.add(jumpNode);

				setPosition.instructions.insertBefore(setPosition.instructions.getLast().getPrevious(), nodeAdd);
			}

			return finishInjection(cn);
		}
		if(changedName.equals(getDeobfName(CLASS_KEY_ENTITY_PLAYER_SP))) {
			ClassNode cn = startInjection(bytes);
			MethodNode onLivingUpdate = getMethod(cn, getName(METHOD_KEY_ONLIVINGUPDATE), "()V");

			if(onLivingUpdate != null) 
			{
				final InsnList nodeAdd = new InsnList();
				final LabelNode label = new LabelNode();
				final LabelNode endOfInvokeLabel = new LabelNode();

				AbstractInsnNode pos = null;
				AbstractInsnNode gotoPos = null;
				int eqnum = 13;


				for (int i = 0; i < onLivingUpdate.instructions.size(); i++) {
					AbstractInsnNode ain = onLivingUpdate.instructions.get(i);

					if(ain.getOpcode() == Opcodes.IFEQ && eqnum-- == 0) {
						pos = ain;
						for(i=i+1, eqnum = 4; i < onLivingUpdate.instructions.size(); i++) {
							//eqnum = 2;
							ain = onLivingUpdate.instructions.get(i);
							if(ain.getOpcode() == Opcodes.ALOAD && eqnum-- == 0) {
								gotoPos = ain;
								break;
							}
						}
						break;
					}
				}

				nodeAdd.add(new VarInsnNode(Opcodes.ALOAD,0));
				nodeAdd.add(new FieldInsnNode(Opcodes.GETFIELD, getName(CLASS_KEY_ENTITYLIVEINGBASE), "gravRotation", "I"));
				nodeAdd.add(new JumpInsnNode(Opcodes.IFEQ, endOfInvokeLabel));
				nodeAdd.add(new VarInsnNode(Opcodes.ALOAD, 0));
				nodeAdd.add(new VarInsnNode(Opcodes.ALOAD, 0));
				nodeAdd.add(new FieldInsnNode(Opcodes.GETFIELD, getName(CLASS_KEY_ENTITYLIVEINGBASE), "gravRotation", "I"));
				nodeAdd.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "zmaster587/advancedRocketry/client/ClientHelper", "moveFlyingVerticalOverride", "(L" + getName(CLASS_KEY_ENTITY_PLAYER_SP) + ";I)V", false));
				nodeAdd.add(new JumpInsnNode(Opcodes.GOTO, label));
				nodeAdd.add(endOfInvokeLabel);

				onLivingUpdate.instructions.insert(pos, nodeAdd);

				onLivingUpdate.instructions.insertBefore(gotoPos, label);

			}

			return finishInjection(cn);
		}
		if(changedName.equals(getDeobfName(CLASS_KEY_ENTITYLIVINGRENDERER))) {
			ClassNode cn = startInjection(bytes);

			MethodNode doRender = getMethod(cn, getName(METHOD_KEY_DORENDER), "(L" + getName(CLASS_KEY_ENTITYLIVEINGBASE)+";DDDFF)V");

			if(doRender != null) {
				final InsnList nodeAdd = new InsnList();
				AbstractInsnNode pos = null;
				int eqnum = 2;

				for (int i = 0; i < doRender.instructions.size(); i++) {
					AbstractInsnNode ain = doRender.instructions.get(i);

					if(ain.getOpcode() == Opcodes.IFEQ && eqnum-- == 0) {
						for(i = i - 1; i > 0; i--) {
							ain = doRender.instructions.get(i);
							if(ain.getOpcode() == Opcodes.FSTORE) {
								pos = ain;
								break;
							}
						}
						break;
					}
				}


				nodeAdd.add(new VarInsnNode(Opcodes.ALOAD, 1));
				nodeAdd.add(new FieldInsnNode(Opcodes.GETFIELD, getName(CLASS_KEY_ENTITYLIVEINGBASE), "gravRotation", "I"));

				nodeAdd.add(new VarInsnNode(Opcodes.ALOAD, 1));

				nodeAdd.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "zmaster587/advancedRocketry/client/ClientHelper", "transformEntity", "(IL" + getName(CLASS_KEY_ENTITYLIVEINGBASE) + ";)V", false));

				doRender.instructions.insert(pos, nodeAdd);
			}

			return finishInjection(cn);
		}
		if(changedName.equals(getDeobfName(CLASS_KEY_ENTITYLIVEINGBASE))) {
			ClassNode cn = startInjection(bytes);

			MethodNode moveFlying = new MethodNode(Opcodes.ACC_PUBLIC, getName(METHOD_KEY_MOVEFLYING), "(FFF)V", null, null);
			MethodNode moveEntity = new MethodNode(Opcodes.ACC_PUBLIC, getName(METHOD_KEY_MOVEENTITY), "(DDD)V", null, null);
			MethodNode setPosition = new MethodNode(Opcodes.ACC_PUBLIC, getName(METHOD_KEY_SETPOSITION), "(DDD)V", null, null);


			//Add need to override setPosition in entitybase to fix collision boxes
			final InsnList moveEntityNode = new InsnList();
			final LabelNode jumpToMoveEntity = new LabelNode();
			final LabelNode jumpToEndEntity = new LabelNode();

			moveEntityNode.add(new VarInsnNode(Opcodes.ALOAD, 0));
			moveEntityNode.add(new FieldInsnNode(Opcodes.GETFIELD, getName(CLASS_KEY_ENTITYLIVEINGBASE), "gravRotation", "I"));
			moveEntityNode.add(new JumpInsnNode(Opcodes.IFNE, jumpToMoveEntity));
			moveEntityNode.add(new VarInsnNode(Opcodes.ALOAD, 0));
			moveEntityNode.add(new VarInsnNode(Opcodes.DLOAD, 1));
			moveEntityNode.add(new VarInsnNode(Opcodes.DLOAD, 3));
			moveEntityNode.add(new VarInsnNode(Opcodes.DLOAD, 5));
			moveEntityNode.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, getName(CLASS_KEY_ENTITY), getName(METHOD_KEY_MOVEENTITY), "(DDD)V", false));
			moveEntityNode.add(new JumpInsnNode(Opcodes.GOTO, jumpToEndEntity));

			moveEntityNode.add(jumpToMoveEntity);
			moveEntityNode.add(new VarInsnNode(Opcodes.ALOAD, 0));
			moveEntityNode.add(new VarInsnNode(Opcodes.ALOAD, 0));
			moveEntityNode.add(new FieldInsnNode(Opcodes.GETFIELD, getName(CLASS_KEY_ENTITYLIVEINGBASE), "gravRotation", "I"));
			moveEntityNode.add(new VarInsnNode(Opcodes.DLOAD, 1));
			moveEntityNode.add(new VarInsnNode(Opcodes.DLOAD, 3));
			moveEntityNode.add(new VarInsnNode(Opcodes.DLOAD, 5));
			moveEntityNode.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "zmaster587/advancedRocketry/client/ClientHelper", "moveEntity", "(L" + getName(CLASS_KEY_ENTITYLIVEINGBASE) + ";IDDD)V", false));
			moveEntityNode.add(jumpToEndEntity);

			moveEntityNode.add(new InsnNode(Opcodes.RETURN));

			moveEntity.instructions.insert(moveEntityNode);
			cn.methods.add(moveEntity);

			//Add moveFlying methods nodes
			final InsnList moveFlyingNode = new InsnList();
			final LabelNode jumpTo = new LabelNode();
			final LabelNode endJump = new LabelNode();

			moveFlyingNode.add(new VarInsnNode(Opcodes.ALOAD, 0));
			moveFlyingNode.add(new FieldInsnNode(Opcodes.GETFIELD, getName(CLASS_KEY_ENTITYLIVEINGBASE), "gravRotation", "I"));
			moveFlyingNode.add(new JumpInsnNode(Opcodes.IFNE, jumpTo));
			moveFlyingNode.add(new VarInsnNode(Opcodes.ALOAD, 0));
			moveFlyingNode.add(new VarInsnNode(Opcodes.FLOAD, 1));
			moveFlyingNode.add(new VarInsnNode(Opcodes.FLOAD, 2));
			moveFlyingNode.add(new VarInsnNode(Opcodes.FLOAD, 3));
			moveFlyingNode.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, getName(CLASS_KEY_ENTITY), getName(METHOD_KEY_MOVEFLYING), "(FFF)V", false));
			moveFlyingNode.add(new JumpInsnNode(Opcodes.GOTO, endJump));
			moveFlyingNode.add(jumpTo);
			moveFlyingNode.add(new VarInsnNode(Opcodes.ALOAD, 0));
			moveFlyingNode.add(new VarInsnNode(Opcodes.ALOAD, 0));
			moveFlyingNode.add(new FieldInsnNode(Opcodes.GETFIELD, getName(CLASS_KEY_ENTITYLIVEINGBASE), "gravRotation", "I"));
			moveFlyingNode.add(new VarInsnNode(Opcodes.FLOAD, 1));
			moveFlyingNode.add(new VarInsnNode(Opcodes.FLOAD, 2));
			moveFlyingNode.add(new VarInsnNode(Opcodes.FLOAD, 3));
			moveFlyingNode.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "zmaster587/advancedRocketry/client/ClientHelper", "moveFlying", "(L" + getName(CLASS_KEY_ENTITYLIVEINGBASE) + ";IFFF)V", false));
			moveFlyingNode.add(endJump);
			moveFlyingNode.add(new InsnNode(Opcodes.RETURN));

			moveFlying.instructions.insert(moveFlyingNode);
			cn.methods.add(moveFlying);
			//End add moveFlying method nodes

			cn.fields.add(new FieldNode(Opcodes.ACC_PUBLIC, "gravRotation", Type.INT_TYPE.getDescriptor(), "I", new Integer(1)));

			//TODO: might break in obf
			MethodNode constructor = getMethod(cn, "<init>", "(Lnet/minecraft/world/World;)V");
			MethodNode getLook = getMethod(cn, getName(METHOD_KEY_GETLOOKVEC), "(F)Lnet/minecraft/util/Vec3;");
			MethodNode moveEntityWithHeading = getMethod(cn, getName(METHOD_KEY_MOVEENTITYWITHHEADING), "(FF)V");
			MethodNode jump = getMethod(cn, getName(METHOD_KEY_JUMP), "()V");
			if(constructor != null) {
				final InsnList nodeAdd = new InsnList();

				nodeAdd.add(new VarInsnNode(Opcodes.ALOAD, 0));
				nodeAdd.add(new InsnNode(Opcodes.ICONST_1));
				nodeAdd.add(new FieldInsnNode(Opcodes.PUTFIELD, getName(CLASS_KEY_ENTITYLIVEINGBASE), "gravRotation", "I"));

				constructor.instructions.insertBefore(constructor.instructions.getLast(), nodeAdd);
			}
			if(jump != null) {
				final InsnList nodeAdd = new InsnList();
				LabelNode skipAddLabel = new LabelNode();

				nodeAdd.add(new VarInsnNode(Opcodes.ALOAD, 0));
				nodeAdd.add(new FieldInsnNode(Opcodes.GETFIELD, getName(CLASS_KEY_ENTITYLIVEINGBASE), "gravRotation", "I"));
				nodeAdd.add(new JumpInsnNode(Opcodes.IFEQ, skipAddLabel));
				nodeAdd.add(new VarInsnNode(Opcodes.ALOAD,0));
				nodeAdd.add(new FieldInsnNode(Opcodes.GETFIELD, getName(CLASS_KEY_ENTITYLIVEINGBASE), "gravRotation", "I"));
				nodeAdd.add(new VarInsnNode(Opcodes.ALOAD, 0));
				nodeAdd.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "zmaster587/advancedRocketry/client/ClientHelper", "livingEntityJump", "(IL" + getName(CLASS_KEY_ENTITYLIVEINGBASE) + ";)V", false));
				nodeAdd.add(new InsnNode(Opcodes.RETURN));
				nodeAdd.add(skipAddLabel);

				jump.instructions.insertBefore(jump.instructions.getFirst(), nodeAdd);
			}
			if(moveEntityWithHeading != null) {
				final InsnList nodeAdd = new InsnList();
				AbstractInsnNode pos = null, endAssign = null;
				LabelNode skipAddLabel = new LabelNode();
				LabelNode endEdit = new LabelNode();

				for (int i = moveEntityWithHeading.instructions.size()-1; i > 0; i--) {
					AbstractInsnNode ain = moveEntityWithHeading.instructions.get(i);

					if(ain.getOpcode() == Opcodes.GOTO) {
						for( i = i + 1; i < moveEntityWithHeading.instructions.size(); i++) {
							ain = moveEntityWithHeading.instructions.get(i);
							if(ain.getOpcode() == Opcodes.ALOAD && pos == null) {
								pos = ain;
							}
							if(ain.getOpcode() == Opcodes.PUTFIELD) {
								endAssign = ain;
								break;
							}
						}
						break;
					}
				}


				nodeAdd.add(new VarInsnNode(Opcodes.ALOAD,0));
				nodeAdd.add(new FieldInsnNode(Opcodes.GETFIELD, getName(CLASS_KEY_ENTITYLIVEINGBASE), "gravRotation", "I"));
				nodeAdd.add(new JumpInsnNode(Opcodes.IFEQ, skipAddLabel));
				nodeAdd.add(new VarInsnNode(Opcodes.ALOAD,0));
				nodeAdd.add(new FieldInsnNode(Opcodes.GETFIELD, getName(CLASS_KEY_ENTITYLIVEINGBASE), "gravRotation", "I"));
				nodeAdd.add(new VarInsnNode(Opcodes.ALOAD, 0));
				nodeAdd.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "zmaster587/advancedRocketry/client/ClientHelper", "transformGravity", "(IL" + getName(CLASS_KEY_ENTITYLIVEINGBASE) + ";)V", false));
				nodeAdd.add(new JumpInsnNode(Opcodes.GOTO, endEdit));
				nodeAdd.add(skipAddLabel);

				moveEntityWithHeading.instructions.insertBefore(pos, nodeAdd);
				moveEntityWithHeading.instructions.insert(endAssign, endEdit);
			}
			//Make look direction consistent with transformed camera
			if(getLook != null) {
				final InsnList nodeAdd = new InsnList();
				nodeAdd.add(new VarInsnNode(Opcodes.ALOAD, 0));
				nodeAdd.add(new VarInsnNode(Opcodes.ALOAD, 0));
				nodeAdd.add(new FieldInsnNode(Opcodes.GETFIELD, getName(CLASS_KEY_ENTITYLIVEINGBASE), "gravRotation", "I"));
				nodeAdd.add(new VarInsnNode(Opcodes.FLOAD, 1));

				//TODO: might break in obf
				nodeAdd.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "zmaster587/advancedRocketry/client/ClientHelper", "createModifiedLookVector", "(L" + getName(CLASS_KEY_ENTITYLIVEINGBASE) +";IF)Lnet/minecraft/util/Vec3;", false));
				nodeAdd.add(new InsnNode(Opcodes.ARETURN));

				getLook.instructions.insertBefore(getLook.instructions.getFirst(), nodeAdd);

			}

			return finishInjection(cn);
		}

		//Transform Camera as needed
		if(changedName.equals(getDeobfName(CLASS_KEY_ENTITYRENDERER))) {
			ClassNode cn = startInjection(bytes);
			//TODO: obfuscated names
			MethodNode orientCamera = getMethod(cn, "orientCamera", "(F)V");
			MethodNode updateCameraAndRender = getMethod(cn, "updateCameraAndRender", "(F)V");

			if(orientCamera != null) {
				final InsnList nodeAdd = new InsnList();
				final InsnList nodeAdd2 = new InsnList();
				final InsnList nodeAdd3 = new InsnList();
				final InsnList nodeAdd4 = new InsnList();

				AbstractInsnNode pos = null;
				AbstractInsnNode pos2 = null;
				int ifneNum = 1;
				int invokeNum = 1;

				AbstractInsnNode pos3 = null;
				int gotoNum = 3;

				for (int i = 0; i < orientCamera.instructions.size(); i++) {
					AbstractInsnNode ain = orientCamera.instructions.get(i);
					if (ain.getOpcode() == Opcodes.IFNE && ifneNum-- == 0) {

						pos = ain;
					}

					if(pos != null && ain.getOpcode() == Opcodes.INVOKESTATIC && invokeNum-- == 0) {
						pos2 = ain;
					}
					if (ain.getOpcode() == Opcodes.GOTO && gotoNum-- == 0) {

						pos3 = ain;
					}
				}

				LabelNode gotoLabel = new LabelNode();
				LabelNode gotoLabel2 = new LabelNode();

				nodeAdd.add(new FieldInsnNode(Opcodes.GETSTATIC, "zmaster587/advancedRocketry/client/ClientHelper", "rotate", "Z"));
				//nodeAdd.add(new VarInsnNode(Opcodes.ALOAD, 2));
				//nodeAdd.add(new FieldInsnNode(Opcodes.GETFIELD, getName(CLASS_KEY_ENTITYLIVEINGBASE), "gravRotation", "I"));
				nodeAdd.add(new JumpInsnNode(Opcodes.IFEQ, gotoLabel2));

				nodeAdd.add(new VarInsnNode(Opcodes.ALOAD, 2));
				nodeAdd.add(new FieldInsnNode(Opcodes.GETFIELD, getName(CLASS_KEY_ENTITYLIVEINGBASE), "gravRotation", "I"));

				nodeAdd.add(new VarInsnNode(Opcodes.ALOAD, 2));


				nodeAdd.add(new VarInsnNode(Opcodes.FLOAD,1));

				nodeAdd.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "zmaster587/advancedRocketry/client/ClientHelper", "transformCamera2", "(IL" + getName(CLASS_KEY_ENTITYLIVEINGBASE) + ";F)V", false));
				nodeAdd.add(new JumpInsnNode(Opcodes.GOTO, gotoLabel));
				nodeAdd.add(gotoLabel2);


				nodeAdd3.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "zmaster587/advancedRocketry/client/ClientHelper", "transformCamera", "()V", false));
				nodeAdd4.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "zmaster587/advancedRocketry/client/ClientHelper", "transformCamera", "()V", false));

				orientCamera.instructions.insertBefore(orientCamera.instructions.get(orientCamera.instructions.indexOf(pos3)), nodeAdd3);
				orientCamera.instructions.insertBefore(orientCamera.instructions.get(orientCamera.instructions.indexOf(pos3)+8), nodeAdd4);

				orientCamera.instructions.insertBefore(orientCamera.instructions.get(orientCamera.instructions.indexOf(pos)-4), nodeAdd);
				orientCamera.instructions.insertBefore(orientCamera.instructions.get(orientCamera.instructions.indexOf(pos2)+1), gotoLabel);
				//TODO: OVerride Entity.setAngles
				//639
			}
			/*if(orientCamera != null) {
				final InsnList nodeAdd = new InsnList();
				final InsnList nodeAdd2 = new InsnList();

				AbstractInsnNode pos = null;
				int gotoNum = 3;

				for (int i = 0; i < orientCamera.instructions.size(); i++) {
					AbstractInsnNode ain = orientCamera.instructions.get(i);
					if (ain.getOpcode() == Opcodes.GOTO && gotoNum-- == 0) {

						pos = ain;
						break;
					}
				}

				nodeAdd.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "zmaster587/advancedRocketry/client/ClientHelper", "transformCamera", "()V", false));
				nodeAdd2.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "zmaster587/advancedRocketry/client/ClientHelper", "transformCamera", "()V", false));

				orientCamera.instructions.insertBefore(orientCamera.instructions.get(orientCamera.instructions.indexOf(pos)), nodeAdd);
				orientCamera.instructions.insertBefore(orientCamera.instructions.get(orientCamera.instructions.indexOf(pos)+8), nodeAdd2);
				//TODO: OVerride Entity.setAngles

			}
			if(updateCameraAndRender != null) {
				final InsnList nodeAdd = new InsnList();

				AbstractInsnNode pos = null;
				int fmulNum = 4;

				for (int i = 0; i < updateCameraAndRender.instructions.size(); i++) {
					AbstractInsnNode ain = updateCameraAndRender.instructions.get(i);
					if (ain.getOpcode() == Opcodes.INVOKEVIRTUAL && fmulNum-- == 0) {

						pos = ain;
						break;
					}
				}
				nodeAdd.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "zmaster587/advancedRocketry/client/ClientHelper", "transformMouse", "()V", false));

				updateCameraAndRender.instructions.insertBefore(updateCameraAndRender.instructions.get(updateCameraAndRender.instructions.indexOf(pos)+1), nodeAdd);

			}

			return finishInjection(cn);
		}*/

		/*
		 * from  net.minecraft.entity.player.EntityPlayer
		 *  
		 * public void mountEntity(Entity p_70078_1_)
		 * {
		 *     if (this.ridingEntity != null && p_70078_1_ == null)
		 *     {
		 *         if (!this.worldObj.isRemote)
		 *         {
		 *             this.dismountEntity(this.ridingEntity);
		 *         }
		 * 
		 *         if (this.ridingEntity != null)
		 *         {
		 *         	//Begin insert
		 *         	if(this.ridingEntity instanceof IDismountHandler)
		 *         			this.ridingEntity.onDismount(this)
		 *         			return;
		 *         	//End Insert
		 *             	this.ridingEntity.riddenByEntity = null;
		 *         }
		 * 
		 *         this.ridingEntity = null;
		 *     }
		 *     else
		 *     {
		 *         super.mountEntity(p_70078_1_);
		 *     }
		 * }
		 * */
		
		if(changedName.equals(getName(CLASS_KEY_RENDER_GLOBAL))) {
			ClassNode cn = startInjection(bytes);
			MethodNode setupTerrain = getMethod(cn, getName(METHOD_KEY_SETUPTERRAIN), "(L"+ getName(CLASS_KEY_ENTITY) + ";DL" + getName(CLASS_KEY_ICAMERA) + ";IZ)V");
			if(setupTerrain != null) {
				final InsnList nodeAdd = new InsnList();
				
				AbstractInsnNode pos1 = null;
				LabelNode pos2 = null;
				
				int ifnull = 3;
				int aload = 3;
				int indexPos1 = 0;;
				
				for(int i = setupTerrain.instructions.size() - 1; i >= 0; i--) {
					AbstractInsnNode ain = setupTerrain.instructions.get(i);
					if(ain.getOpcode() == Opcodes.IFNULL && --ifnull == 0) {
						pos1 = ain;
						indexPos1 = i;
						break;
					}
				}
				
				for(int i = indexPos1; i < setupTerrain.instructions.size(); i++) {
					AbstractInsnNode ain = setupTerrain.instructions.get(i);
					if(ain.getOpcode() == Opcodes.ALOAD && --aload == 0) {
						pos2 = (LabelNode)setupTerrain.instructions.get(i-2);;
						break;
					}
				}
				
				
				//Lack of robustness, this could go really wrong. To future me: told you so!
				//pos2 = setupTerrain.instructions.get(914);
				
				//nodeAdd.add(new VarInsnNode(Opcodes.ILOAD, 25));
				nodeAdd.add(new JumpInsnNode(Opcodes.GOTO, pos2));
				//nodeAdd.add(new VarInsnNode(Opcodes.ILOAD, 27));
				//nodeAdd.add(new JumpInsnNode(Opcodes.IFEQ, pos2));
				
				setupTerrain.instructions.insert(pos1, nodeAdd);
				
			}
			else
				AdvancedRocketry.logger.fatal("ASM injection into RenderGlobal.setupTerrain FAILED!");

			return finishInjection(cn);
		}
		
		/*if(changedName.equals(getName(CLASS_KEY_ENTITY_PLAYER))) {
			ClassNode cn = startInjection(bytes);
			MethodNode mountEntityMethod = getMethod(cn, getName(METHOD_KEY_MOUNTENTITY), "(L"+ getName(CLASS_KEY_ENTITY) + ";)V");

			if(mountEntityMethod != null) {
				final InsnList nodeAdd = new InsnList();
				AbstractInsnNode pos = null;

				for(int i = mountEntityMethod.instructions.size() - 1; i >= 0; i--) {
					AbstractInsnNode ain = mountEntityMethod.instructions.get(i);
					if(ain.getOpcode() == Opcodes.IFNULL) {
						pos = ain;
						break;
					}
				}

				LabelNode jumpLabel = new LabelNode();
				nodeAdd.add(new VarInsnNode(Opcodes.ALOAD, 0));
				nodeAdd.add(new FieldInsnNode(Opcodes.GETFIELD, getName(CLASS_KEY_ENTITY), getName(FIELD_RIDINGENTITY), "L" + getName(CLASS_KEY_ENTITY) + ";"));
				nodeAdd.add(new TypeInsnNode(Opcodes.INSTANCEOF, "zmaster587/libVulpes/api/IDismountHandler"));
				nodeAdd.add(new JumpInsnNode(Opcodes.IFEQ, jumpLabel));


				nodeAdd.add(new VarInsnNode(Opcodes.ALOAD, 0));
				nodeAdd.add(new FieldInsnNode(Opcodes.GETFIELD, getName(CLASS_KEY_ENTITY), getName(FIELD_RIDINGENTITY), "L" + getName(CLASS_KEY_ENTITY) + ";"));
				nodeAdd.add(new VarInsnNode(Opcodes.ALOAD, 0));
				nodeAdd.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, "zmaster587/libVulpes/api/IDismountHandler", "handleDismount", "(L" + getName(CLASS_KEY_ENTITY) + ";)V", true));
				nodeAdd.add(new InsnNode(Opcodes.RETURN));
				nodeAdd.add(jumpLabel);

				mountEntityMethod.instructions.insert(pos, nodeAdd);
			}
			else
				AdvancedRocketry.logger.severe("ASM injection into EntityPlayer.mountEntity FAILED!");

			return finishInjection(cn);
		}*/
		
		if(changedName.equals(getName(CLASS_KEY_ENTITY_PLAYER_MP))) {
			ClassNode cn = startInjection(bytes);
			MethodNode onUpdate = getMethod(cn, getName(METHOD_KEY_ONUPDATE), "()V");

			if(onUpdate != null) {
				final InsnList nodeAdd = new InsnList();
				LabelNode label = new LabelNode();
				AbstractInsnNode pos = null;
				AbstractInsnNode ain = null;
				int numSpec = 1;
				int numAload = 7;
				
				for(int i = 0; i < onUpdate.instructions.size(); i++) {
					ain = onUpdate.instructions.get(i);
					if(ain.getOpcode() == Opcodes.INVOKEVIRTUAL && numSpec-- == 0) {
						
						while( i < onUpdate.instructions.size() ) {
							pos = onUpdate.instructions.get(i++);
							if( pos.getOpcode()  == Opcodes.ALOAD && numAload-- == 0 ) {
								label = (LabelNode)pos.getPrevious().getPrevious().getPrevious();
								break;
							}
						}
						break;
					}
				}
				
				
				nodeAdd.add(new VarInsnNode(Opcodes.ALOAD, 0));
				nodeAdd.add(new TypeInsnNode(Opcodes.CHECKCAST, "java/lang/Object"));
				nodeAdd.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "zmaster587/advancedRocketry/util/RocketInventoryHelper", "allowAccess", "(Ljava/lang/Object;)Z", false));
				nodeAdd.add(new JumpInsnNode(Opcodes.IFNE, label));
				
				onUpdate.instructions.insert(ain, nodeAdd);
				
				//onUpdate.instructions.insertBefore(pos, label);
				
			}
			
			return finishInjection(cn);
		}
		if(changedName.equals(getName(CLASS_KEY_ENTITY_PLAYER))) {
			ClassNode cn = startInjection(bytes);
			MethodNode onUpdate = getMethod(cn, getName(METHOD_KEY_ONUPDATE), "()V");
			if(onUpdate != null) {
				final InsnList nodeAdd = new InsnList();
				LabelNode label = new LabelNode();
				AbstractInsnNode pos = null;
				AbstractInsnNode ain = null;
				int numSpec = 1;
				int numAload = 7;
				
				for(int i = 0; i < onUpdate.instructions.size(); i++) {
					ain = onUpdate.instructions.get(i);
					if(ain.getOpcode() == Opcodes.INVOKESPECIAL && numSpec-- == 0) {
						
						while( i < onUpdate.instructions.size() ) {
							pos = onUpdate.instructions.get(i++);
							if( pos.getOpcode()  == Opcodes.ALOAD && numAload-- == 0 ) {
								label = (LabelNode)pos.getPrevious().getPrevious().getPrevious();
								break;
							}
								
						}
						break;
					}
				}
				
				
				nodeAdd.add(new VarInsnNode(Opcodes.ALOAD, 0));
				nodeAdd.add(new TypeInsnNode(Opcodes.CHECKCAST, "java/lang/Object"));
				nodeAdd.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "zmaster587/advancedRocketry/util/RocketInventoryHelper", "allowAccess", "(Ljava/lang/Object;)Z", false));
				nodeAdd.add(new JumpInsnNode(Opcodes.IFNE, label));
				
				onUpdate.instructions.insert(ain, nodeAdd);
				
				//onUpdate.instructions.insertBefore(pos, label);
				
			}
			else
				AdvancedRocketry.logger.fatal("ASM injection into EntityPlayer.onupdate FAILED!");
			
			
			return finishInjection(cn);
		}
		if(changedName.equals(getName(CLASS_KEY_ENTITY_ITEM))) {
			ClassNode cn = startInjection(bytes);

			MethodNode onUpdate = getMethod(cn, getName(METHOD_KEY_ONUPDATE), "()V");

			if(onUpdate != null) {
				final InsnList nodeAdd = new InsnList();
				AbstractInsnNode pos = null;
				List<AbstractInsnNode> removeNodes = new LinkedList<AbstractInsnNode>();
				int numALoadsInARow = 0;
				int firstALoadIndex = 0;
				AbstractInsnNode ain;

				for(int i = onUpdate.instructions.size() - 1; i >= 0; i--) {
					ain = onUpdate.instructions.get(i);
					if(ain.getOpcode() == Opcodes.ALOAD) {
						if(numALoadsInARow == 0)
							firstALoadIndex = i;
						numALoadsInARow++;
						if(numALoadsInARow == 3) {
							pos = ain;
							break;
						}
					}
					else
						numALoadsInARow = 0;
				}
				int i = firstALoadIndex;
				while((ain = onUpdate.instructions.get(--i)).getOpcode() != Opcodes.PUTFIELD);


				while((ain = onUpdate.instructions.get(i--)).getOpcode() != Opcodes.ALOAD) {
					removeNodes.add(ain);
				}
				removeNodes.add(ain);

				pos = onUpdate.instructions.get(i);

				for(AbstractInsnNode node : removeNodes)
					onUpdate.instructions.remove(node);

				nodeAdd.add(new VarInsnNode(Opcodes.ALOAD, 0));
				nodeAdd.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "zmaster587/advancedRocketry/util/GravityHandler", "applyGravity", "(L" + getName(CLASS_KEY_ENTITY) + ";)V", false));
				onUpdate.instructions.insert(pos, nodeAdd);
			}

			return finishInjection(cn);
		}

		if(changedName.equals(getName(CLASS_KEY_ENTITYLIVEINGBASE))) {
			ClassNode cn = startInjection(bytes);

			MethodNode moveEntityWithHeading = getMethod(cn, getName(METHOD_KEY_MOVEENTITYWITHHEADING), "(FF)V");

			if(moveEntityWithHeading != null) {
				final InsnList nodeAdd = new InsnList();
				AbstractInsnNode pos = null;
				List<AbstractInsnNode> removeNodes = new LinkedList<AbstractInsnNode>();
				int numGoto = 4;

				for(int i = moveEntityWithHeading.instructions.size() - 1; i >= 0; i--) {
					AbstractInsnNode ain = moveEntityWithHeading.instructions.get(i);
					if(ain.getOpcode() == Opcodes.GOTO && --numGoto == 0) {
						pos = ain;


						while(moveEntityWithHeading.instructions.get(--i).getOpcode() != Opcodes.ALOAD);

						pos = moveEntityWithHeading.instructions.get(i-1);
						ain = moveEntityWithHeading.instructions.get(i);
						do {
							moveEntityWithHeading.instructions.remove(ain);
							removeNodes.add(ain);
							ain = moveEntityWithHeading.instructions.get(i);
						} while(ain.getOpcode() != Opcodes.PUTFIELD);

						moveEntityWithHeading.instructions.remove(ain);

						break;
					}
				}

				nodeAdd.add(new VarInsnNode(Opcodes.ALOAD, 0));
				nodeAdd.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "zmaster587/advancedRocketry/util/GravityHandler", "applyGravity", "(L" + getName(CLASS_KEY_ENTITY) + ";)V", false));
				moveEntityWithHeading.instructions.insert(pos, nodeAdd);
			}

			return finishInjection(cn);
		}

		//On block change insert a call to the atmosphere handler
		if(changedName.equals(getName(CLASS_KEY_WORLD))) {
			ClassNode cn = startInjection(bytes);
			MethodNode setBlockStateMethod = getMethod(cn, getName(METHOD_KEY_SETBLOCKSTATE), "(L" + getName(CLASS_KEY_BLOCKPOS) + ";L" + getName(CLASS_KEY_IBLOCKSTATE) +";I)Z");
			//MethodNode setBlockMetaMethod = getMethod(cn, getName(METHOD_KEY_SETBLOCKMETADATAWITHNOTIFY), "(IIIII)Z");

			if(setBlockStateMethod != null) {

				final InsnList nodeAdd = new InsnList();
				AbstractInsnNode pos = null;
				//int fmulNum = 2;

				for (int i = setBlockStateMethod.instructions.size()-1; i >= 0 ; i--) {
					AbstractInsnNode ain = setBlockStateMethod.instructions.get(i);
					if (ain.getOpcode() == Opcodes.IRETURN) {
						pos = ain;
						break;
					}
				}


				nodeAdd.add(new VarInsnNode(Opcodes.ALOAD, 0));
				nodeAdd.add(new VarInsnNode(Opcodes.ALOAD, 1));
				nodeAdd.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "zmaster587/advancedRocketry/atmosphere/AtmosphereHandler", "onBlockChange", "(L" + getName(CLASS_KEY_WORLD) + ";L" + getName(CLASS_KEY_BLOCKPOS) + ";)V", false));

				setBlockStateMethod.instructions.insertBefore(pos, nodeAdd);
			}
			else
				AdvancedRocketry.logger.fatal("ASM injection into World.setBlock FAILED!");

			/*if(setBlockMetaMethod != null) {

				final InsnList nodeAdd = new InsnList();
				AbstractInsnNode pos = null;
				int fmulNum = 2;

				for (int i = setBlockMetaMethod.instructions.size()-1; i >= 0 ; i--) {
					AbstractInsnNode ain = setBlockMetaMethod.instructions.get(i);
					if (ain.getOpcode() == Opcodes.IRETURN && --fmulNum == 0) {
						pos = ain;
						break;
					}
				}


				nodeAdd.add(new VarInsnNode(Opcodes.ALOAD, 0)); //this
				nodeAdd.add(new VarInsnNode(Opcodes.ILOAD, 1)); //x
				nodeAdd.add(new VarInsnNode(Opcodes.ILOAD, 2)); //y
				nodeAdd.add(new VarInsnNode(Opcodes.ILOAD, 3)); //z
				nodeAdd.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "zmaster587/advancedRocketry/atmosphere/AtmosphereHandler", "onBlockMetaChange", "(L" + getName(CLASS_KEY_WORLD) + ";III)V", false));

				setBlockMetaMethod.instructions.insertBefore(pos, nodeAdd);
			}			
			else
				AdvancedRocketry.logger.severe("ASM injection into World.setBlockMeta FAILED!");*/

			return finishInjection(cn);
		}


		return bytes;
	}

	private ClassNode startInjection(byte[] bytes) {
		final ClassNode node = new ClassNode();
		final ClassReader reader = new ClassReader(bytes);
		reader.accept(node, 0);

		return node;
	}

	private byte[] finishInjection(ClassNode node) {
		final ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		node.accept(writer);
		return writer.toByteArray();
	}

	private MethodNode getMethod(ClassNode node, String name, String sig) {
		for(MethodNode methodNode : node.methods) {
			if(methodNode.name.equals(name) && methodNode.desc.equals(sig))
				return methodNode;
		}
		return null;
	}

	private String getName(String key) {
		SimpleEntry<String, String> entry = entryMap.get(key);
		if(entry == null)
			return "";
		else
			if(obf)
				return entry.getValue();
			else
				return entry.getKey();
	}


	private String getDeobfName(String key) {
		SimpleEntry<String, String> entry = entryMap.get(key);
		if(entry == null)
			return "";
		else
			return entry.getKey();
	}

}
