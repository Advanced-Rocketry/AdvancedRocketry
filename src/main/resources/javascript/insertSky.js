function initializeCoreMod() {
    return {
        'coremodmethod': {
            'target': {
                'type': 'METHOD',
                'class': 'net.minecraft.client.renderer.WorldRenderer',
                'methodName': 'renderSky',
                'methodDesc': '(Lcom/mojang/blaze3d/matrix/MatrixStack;F)V'
            },
            'transformer': function(method) {
		var ASMAPI = Java.type('net.minecraftforge.coremod.api.ASMAPI');
		var Opcodes = Java.type('org.objectweb.asm.Opcodes');
                var arrayLength = method.instructions.size();
		print("Attempting to transform sky!");
                for (var i = 0; i < arrayLength; ++i) {
                    var instruction = method.instructions.get(i);
                    if (instruction.getOpcode() == Opcodes.ALOAD) {
                        var InsnNode = Java.type('org.objectweb.asm.tree.InsnNode');
			var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode');
			var MethodInsnNode = Java.type('org.objectweb.asm.tree.MethodInsnNode');
			var JumpInsnNode = Java.type('org.objectweb.asm.tree.JumpInsnNode');
			var LabelNode = Java.type('org.objectweb.asm.tree.LabelNode');
                        var loadMatrix = new VarInsnNode(Opcodes.ALOAD, 1);
			var loadFloat = new VarInsnNode(Opcodes.FLOAD, 2);
			var labelNode = new LabelNode();
			var ifNode = new JumpInsnNode(Opcodes.IFNE, labelNode);
			var returnNode = new InsnNode(Opcodes.RETURN);
			
			var instArray = new Array(loadMatrix, loadFloat);
			
			var invoke = ASMAPI.listOf(ASMAPI.buildMethodCall('zmaster587/advancedRocketry/client/ClientHelper', 'callCustomSkyRenderer', "(Lcom/mojang/blaze3d/matrix/MatrixStack;F)Z", ASMAPI.MethodType.STATIC));
			
			instArray = instArray.concat(invoke);
			instArray = instArray.concat([ifNode, returnNode, labelNode]);
			
			for (var j = 0; j < instArray.length; ++j) {
			    method.instructions.insertBefore(instruction, instArray[j]);
			}
                        print("Transformed Sky!");
                        break;
                    }
                }
                arrayLength = method.instructions.size();
                for (var i = 0; i < arrayLength; ++i) {
		    print( method.instructions.get(i) + "  opcode " + method.instructions.get(i).getOpcode());
		}
                return method;
            }
        }
    }
}
