# JS Coremods Fabric

A "port" of [MinecraftForge's CoreMods](https://github.com/minecraftforge/coremods) to Fabric.

## Adding to your dependencies

Kotlin DSL: 
```kotlin
repositories {
    //[...]
    maven {
        name = "Jitpack"
        url = uri("https://jitpack.io/")
    }
    //[...]
}

dependencies {
    //[...]
    modImplementation(include("com.github.alkyaly:js-coremods-fabric:$version")!!)
    //[...]
}
```

Groovy DSL:
```groovy
repositories {
    //[...]
    maven {
        name 'Jitpack'
        url 'https://jitpack.io/'
    }
    //[...]
}

dependencies {
    //[...]
    modImplementation(include('com.github.alkyaly:js-coremods-fabric:${version}'))
    //[...]
}
```

## Usage

### Format
Your script file must have a top level function called `initializeCoreMod` that takes no parameters.

Example `myExample.js`:
```javascript
    function initializeCoreMod() {
        //...   
    }
```

This function must return an object, with some keys to other objects, those keys are the actual coremod name.<br>
Every named inner coremod object must have a `target` object and a `transformer` function.<br><br>

The target always has a `type` which indicates the target type (Must be: `"FIELD"`, `"METHOD"` or `"CLASS"`).<br>
Targets of type class have a `name` string, which indicates the targetting class.<br>
Targets of type field have a `class` string (The owner of the field), a `fieldName` (The field name) and a `fieldDesc` (The field descriptor).<br>
Targets of type method have a `class` string (The owner of the method), a `methodName` (The method name) and a `methodDesc` (The method descriptor).<br>

Those names must be in intermediary, otherwise will fail in different environments.<br>
`myExample.js`:
```javascript
function initializeCoreMod() {
    return {
        'name': {
            'target': {
                'type': 'FIELD',
                'class': 'net.minecraft.CoolClass_1984',
                'fieldName': 'COOL_NAME',
                'fieldDesc': 'Ljava/lang/Object;'
            }
        }
    }
    //...
}
```

The transformer function takes a single parameter, the selected type.<br>
For `FIELD`, a `FieldNode`; For `METHOD`, a `MethodNode`; For `CLASS`, a `ClassNode`.<br>
And at the end, you must return the parameter.<br>
Do all your transformations inside the function.
`myExample.js`:
```javascript
function initializeCoreMod() {
    return {
        'name': {
            'target': {
                'type': 'FIELD',
                'class': 'net.minecraft.CoolClass_1984',
                'fieldName': 'COOL_NAME',
                'fieldDesc': 'Ljava/lang/Object;'
            },
            'transformer': function(field) {
                var Opcodes = Java.type('org.objectweb.asm.Opcodes');
                var AnnotationNode = Java.type('org.objectweb.asm.tree.AnnotationNode');
           
                field.access &= ~Opcodes.ACC_FINAL; //Strips the final modifier from the field
                field.visibleAnnotations.add(new AnnotationNode('Lorg/jetbrains/annotations/Nullable;')); //Adds the nullable annotation
            
                //do thing
                //go crazy
            
                return field;       
            }
        }
    }
}
```

### Declaring Coremods
The coremods must be declared in a custom block inside your `fabric.mod.json`.<br>
A `"js"` array of strings:

```json
//...
"custom": {
    "js": [
        "myExample.js"
    ]
}
//...
```
