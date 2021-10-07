function initializeCoreMod() {
    return {
        'wooo': {
            'target': {
                'type': 'CLASS',
                'name': 'net.minecraft.client.renderer.LevelRenderer',
            },
            'transformer': function(clazz) {
                var Opcodes = Java.type('org.objectweb.asm.Opcodes');
                var MethodNode = Java.type('org.objectweb.asm.tree.MethodNode');
                var InsnNode = Java.type('org.objectweb.asm.tree.InsnNode');

                var newMtd = new MethodNode(
                    Opcodes.ACC_PRIVATE,
                    "clarence",
                    "()V",
                    null, null
                );
                newMtd.instructions.add(new InsnNode(Opcodes.RETURN));

                clazz.methods.add(newMtd);

                return clazz;
            }
        }
    }
}