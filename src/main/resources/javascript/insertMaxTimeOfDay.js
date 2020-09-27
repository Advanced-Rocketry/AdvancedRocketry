function initializeCoreMod() {
    return {
        'coremodmethod': {
            'target': {
                'type': 'METHOD',
                'class': 'net.minecraft.world.IDayTimeReader',
                'methodName': 'func_242415_f',
                'methodDesc': '(F)F'
            },
            'transformer': function(method) {
		var ASMAPI = Java.type('net.minecraftforge.coremod.api.ASMAPI');
		var Opcodes = Java.type('org.objectweb.asm.Opcodes');
                var arrayLength = method.instructions.size();
		var InsnNode = Java.type('org.objectweb.asm.tree.InsnNode');
		var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode');
		var MethodInsnNode = Java.type('org.objectweb.asm.tree.MethodInsnNode');
		var JumpInsnNode = Java.type('org.objectweb.asm.tree.JumpInsnNode');
		var LabelNode = Java.type('org.objectweb.asm.tree.LabelNode');
		print("Attempting to transform Time of day!");
                for (var i = 0; i < arrayLength; ++i) {
                    var instruction = method.instructions.get(i);
                    if (instruction.getOpcode() == Opcodes.FRETURN) {

                        var instArray = new Array(new VarInsnNode(Opcodes.ALOAD, 0));
			
			var invoke = ASMAPI.listOf(ASMAPI.buildMethodCall('zmaster587/advancedRocketry/client/ClientHelper', 'callTimeOfDay', "(FLnet/minecraft/world/IDayTimeReader;)F", ASMAPI.MethodType.STATIC));
			
			instArray = instArray.concat(invoke);
			
			for (var j = 0; j < instArray.length; ++j) {
			    method.instructions.insertBefore(instruction, instArray[j]);
			}
                        print("Transformed Time of day!");
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
