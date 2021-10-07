function initializeCoreMod() {
    return {
        'clear': {
            'target': {
                'type': 'METHOD',
                'class': 'net.minecraft.client.renderer.LevelRenderer',
                'methodName': 'setLevel',
                'methodDesc': '(Lnet/minecraft/client/multiplayer/ClientLevel;)V'
            },
            'transformer': function(method) {
                var TypeInsnNode = Java.type('org.objectweb.asm.tree.TypeInsnNode');
                var LdcInsnNode = Java.type('org.objectweb.asm.tree.LdcInsnNode');
                var MethodInsnNode = Java.type('org.objectweb.asm.tree.MethodInsnNode');
                var InsnNode = Java.type('org.objectweb.asm.tree.InsnNode');
                var InsnList = Java.type('org.objectweb.asm.tree.InsnList');
                var Opcodes = Java.type('org.objectweb.asm.Opcodes');

                var list = new InsnList();
                list.add(new TypeInsnNode(Opcodes.NEW, "java/lang/IllegalStateException"));
                list.add(new InsnNode(Opcodes.DUP));
                list.add(new LdcInsnNode("hewwo?"));
                list.add(new MethodInsnNode(
                    Opcodes.INVOKESPECIAL,
                    "java/lang/IllegalStateException",
                    "<init>",
                    "(Ljava/lang/String;)V",
                    false
                ));
                list.add(new InsnNode(Opcodes.ATHROW));
                method.instructions = list;


                return method;
            }
        }
    }
}
/*
NEW java/lang/IllegalStateException
    DUP
    LDC "Pose stack not empty"
    INVOKESPECIAL java/lang/IllegalStateException.<init> (Ljava/lang/String;)V
    ATHROW
    */