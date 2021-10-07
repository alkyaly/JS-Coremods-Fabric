package io.github.alkyaly.jscoremodsfabric.mixin;

import io.github.alkyaly.jscoremodsfabric.Coremod;
import io.github.alkyaly.jscoremodsfabric.JsCoremodsFabric;
import io.github.alkyaly.jscoremodsfabric.ScriptManager;
import io.github.alkyaly.jscoremodsfabric.Targets;
import io.github.alkyaly.jscoremodsfabric.tf.ClassTransformer;
import io.github.alkyaly.jscoremodsfabric.tf.FieldTransformer;
import io.github.alkyaly.jscoremodsfabric.tf.MethodTransformer;
import io.github.alkyaly.jscoremodsfabric.tf.Transformer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.CustomValue;
import net.fabricmc.loader.api.metadata.ModMetadata;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MixinPlugin implements IMixinConfigPlugin {

    private static final Map<String, Set<Transformer<?>>> TRANSFORMERS = new HashMap<>();
    private static final List<String> DUMMY_MIXINS = new ArrayList<>();
    public static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();
    private static int i = 0;

    @Override
    public void onLoad(String mixinPackage) {
        for (ModContainer mod : FabricLoader.getInstance().getAllMods()) {
            ModMetadata meta = mod.getMetadata();

            if (!meta.containsCustomValue("js")) continue;

            for (CustomValue value : meta.getCustomValue("js").getAsArray()) {
                String file = value.getAsString();
                try (InputStream st = getClass().getClassLoader().getResourceAsStream(file)) {
                    JsCoremodsFabric.LOGGER.debug(JsCoremodsFabric.COREMOD, "Loading CoreMod from {}", file);
                    Coremod coremod = new Coremod(st, ScriptManager.newEngine());
                    List<? extends Transformer<?>> tfs = coremod.eval();

                    for (Transformer<?> tf : tfs) {
                        String clazz = fixName(tf.getTargets().clazz());
                        String dummy = fixName(mixinPackage) + "/JS_" + i;

                        //DUMMY_MIXINS.add("JS_" + i);
                        LOOKUP.defineClass(fakeMixin(dummy, clazz));
                        addClassUrl(dummy);
                        TRANSFORMERS.computeIfAbsent(clazz, k -> new HashSet<>())
                                .add(tf);
                        i++;
                    }

                    JsCoremodsFabric.LOGGER.debug(JsCoremodsFabric.COREMOD, "CoreMod loaded successfully");
                } catch (IOException | IllegalAccessException e) {
                    JsCoremodsFabric.LOGGER.error(JsCoremodsFabric.COREMOD, "Error initializing CoreMod", e);
                }
            }
        }
    }

    @Override
    public List<String> getMixins() {
        return null;
        //return DUMMY_MIXINS;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
        String tgClass = fixName(targetClassName);
        Set<Transformer<?>> tfs = TRANSFORMERS.get(tgClass);

        if (tfs != null) {
            for (Transformer<?> tf : tfs) {
                Targets target = tf.getTargets();

                if (target instanceof Targets.Class) {
                    ((ClassTransformer) tf).run(targetClass);
                } else if (target instanceof Targets.Method tm) {
                    MethodNode mtd = targetClass.methods.stream()
                            .filter(m -> m.name.equals(tm.name()))
                            .filter(m -> m.desc.equals(tm.desc()))
                            .findAny()
                            .orElseThrow(() -> new IllegalArgumentException(
                                    String.format("Could not find method %s with desc %s in target class %s",
                                            tm.name(), tm.desc(), tm.clazz())));

                    ((MethodTransformer) tf).run(mtd);
                } else if (target instanceof Targets.Field fi) {
                    FieldNode field = targetClass.fields.stream()
                            .filter(f -> f.name.equals(fi.name()))
                            .filter(f -> f.desc.equals(fi.desc()))
                            .findAny()
                            .orElseThrow(() -> new IllegalArgumentException(
                                    String.format("Could not find field %s with desc %s in target class %s",
                                            fi.name(), fi.desc(), fi.clazz())));
                    ((FieldTransformer) tf).run(field);
                }
            }
        }
    }


    public static byte[] fakeMixin(String name, String target) {
        ClassWriter cw = new ClassWriter(0);
        cw.visit(52, Opcodes.ACC_PUBLIC | Opcodes.ACC_ABSTRACT | Opcodes.ACC_INTERFACE, name, null, "java/lang/Object", null);

        AnnotationVisitor mixinAnnotation = cw.visitAnnotation("Lorg/spongepowered/asm/mixin/Mixin;", false);
        AnnotationVisitor targetAnnotation = mixinAnnotation.visitArray("value");
        targetAnnotation.visit(null, Type.getType('L' + target + ';'));
        targetAnnotation.visitEnd();
        mixinAnnotation.visitEnd();

        cw.visitEnd();
        return cw.toByteArray();
    }

    private static void addClassUrl(String clazz) {
        ClassLoader loader = MixinPlugin.class.getClassLoader();
        Method addUrl = null;

        for (Method method : loader.getClass().getMethods()) {
            if (method.getParameterCount() != 1) continue;
            if (method.getReturnType() != void.class) continue;
            if (method.getParameters()[0].getType() != URL.class) continue;
            addUrl = method;
            break;
        }

        if (addUrl == null) throw new NoSuchMethodError("Couldn't find URLClassLoader#addURL");

        try {
            addUrl.setAccessible(true);
            addUrl.invoke(loader, MixinPlugin.class.getResource("/" + clazz));
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Could not invoke URLClassLoader#addURL", e);
        }
    }

    private static String fixName(String str) {
        return str.replace('.', '/');
    }

    @Override public String getRefMapperConfig() { return null; }
    @Override public boolean shouldApplyMixin(String targetClassName, String mixinClassName) { return true; }
    @Override public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) { }
    @Override public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) { }
}
