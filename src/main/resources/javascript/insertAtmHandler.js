function initializeCoreMod() {
    return {
        'coremodmethod': {
            'target': {
                'type': 'METHOD',
                'class': 'net.minecraft.world.World',
                'methodName': 'func_241211_a_',
                'methodDesc': '(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;II)Z'
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
		print("Attempting to transform setBlockState!");
                for (var i = arrayLength-1; i >0 ; --i) {
                    var instruction = method.instructions.get(i);
                    if (instruction.getOpcode() == Opcodes.ICONST_1) {

                        var instArray = new Array(new VarInsnNode(Opcodes.ALOAD, 0), new VarInsnNode(Opcodes.ALOAD, 1));
			
			var invoke = ASMAPI.listOf(ASMAPI.buildMethodCall('zmaster587/advancedRocketry/atmosphere/AtmosphereHandler', 'onBlockChange', "(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)V", ASMAPI.MethodType.STATIC));
			
			instArray = instArray.concat(invoke);
			
			for (var j = 0; j < instArray.length; ++j) {
			    method.instructions.insertBefore(instruction, instArray[j]);
			}
                        print("Transformed setBlockState!");
                        break;
                    }
                }
                return method;
            }
        }
    }
}
