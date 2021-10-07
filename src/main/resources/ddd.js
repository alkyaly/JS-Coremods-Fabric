function initializeCoreMod() {
    return {
        'clear': {
            'target': {
                'type': 'METHOD',
                'class': 'net.minecraft.class_310',
                'methodName': 'method_29606',
                'methodDesc': '(Ljava/lang/String;)V'
            },
            'transformer': function(method) {
                var FieldInsnNode = Java.type('org.objectweb.asm.tree.FieldInsnNode');
                var Opcodes = Java.type('org.objectweb.asm.Opcodes');

                var found = null;
                for (var i = 0; i < method.instructions.size(); i++) {
                    var node = method.instructions.get(i);
                    if (node.opcode == Opcodes.GETSTATIC) {
                        found = node;
                        break;
                    }
                }
                method.instructions.set(
                    found,
                    new FieldInsnNode(
                        Opcodes.GETSTATIC,
                        "net/minecraft/client/Minecraft$ExperimentalDialogType",
                        "NONE",
                        "Lnet/minecraft/client/Minecraft$ExperimentalDialogType;"

                    )
                );

                return method;
            }
        }
    }
}