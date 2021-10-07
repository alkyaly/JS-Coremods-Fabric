function initializeCoreMod() {
    return {
        'clear': {
            'target': {
                'type': 'FIELD',
                'class': 'net.minecraft.class_310',
                'fieldName': 'field_1762',
                'fieldDesc': 'Lorg/apache/logging/log4j/Logger;'
            },
            'transformer': function(field) {
                var TypeInsnNode = Java.type('org.objectweb.asm.tree.TypeInsnNode');
                var LdcInsnNode = Java.type('org.objectweb.asm.tree.LdcInsnNode');
                var MethodInsnNode = Java.type('org.objectweb.asm.tree.MethodInsnNode');
                var InsnNode = Java.type('org.objectweb.asm.tree.InsnNode');
                var InsnList = Java.type('org.objectweb.asm.tree.InsnList');
                var Opcodes = Java.type('org.objectweb.asm.Opcodes');

                field.access &= ~Opcodes.ACC_FINAL;


                return field;
            }
        }
    }
}