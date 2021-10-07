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
                var Opcodes = Java.type('org.objectweb.asm.Opcodes');

                field.access &= ~Opcodes.ACC_FINAL;


                return field;
            }
        }
    }
}