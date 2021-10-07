package io.github.alkyaly.jscoremodsfabric.mixin;

import io.github.alkyaly.jscoremodsfabric.Coremod;
import io.github.alkyaly.jscoremodsfabric.JsCoremodsFabric;
import io.github.alkyaly.jscoremodsfabric.ScriptManager;
import io.github.alkyaly.jscoremodsfabric.Target;
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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MixinPlugin implements IMixinConfigPlugin {

    private final Map<String, Set<Transformer<?>>> transformers = new HashMap<>();
    private final Map<String, String> targetToDummyMixin = new HashMap<>();
    private final Map<String, byte[]> dummyClassToBytes = new HashMap<>();
    private int i = 0;

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
                        String clazz = fixName(tf.getTarget().clazz());
                        String js = "JS_" + i;
                        String dummy = fixName(mixinPackage) + "/" + js;

                        dummyClassToBytes.put('/' + dummy + ".class", fakeMixin(dummy, clazz));

                        if (!targetToDummyMixin.containsKey(clazz)) {
                            targetToDummyMixin.put(clazz, js);
                            i++;
                        }
                        transformers.computeIfAbsent(clazz, k -> new HashSet<>())
                                .add(tf);
                    }

                    JsCoremodsFabric.LOGGER.debug(JsCoremodsFabric.COREMOD, "CoreMod loaded successfully");
                } catch (IOException e) {
                    JsCoremodsFabric.LOGGER.error(JsCoremodsFabric.COREMOD, "Error initializing CoreMod", e);
                }
            }
        }
    }

    @Override
    public List<String> getMixins() {
        addClassUrl(url(dummyClassToBytes));
        return targetToDummyMixin.values().stream().toList();
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
        String tgClass = fixName(targetClassName);
        Set<Transformer<?>> tfs = transformers.get(tgClass);

        if (tfs != null) {
            for (Transformer<?> tf : tfs) {
                Target target = tf.getTarget();

                if (target instanceof Target.Class) {
                    ((ClassTransformer) tf).run(targetClass);
                } else if (target instanceof Target.Method tm) {
                    MethodNode mtd = targetClass.methods.stream()
                            .filter(m -> m.name.equals(tm.name()))
                            .findAny()
                            .orElseThrow(() -> new IllegalArgumentException(
                                    String.format("Could not find method %s in target class %s referenced by coremod: %s",
                                            tm.name(), tm.clazz(), tf.getCoremodName())));

                    ((MethodTransformer) tf).run(mtd);
                } else if (target instanceof Target.Field fi) {
                    FieldNode field = targetClass.fields.stream()
                            .filter(f -> f.name.equals(fi.name()))
                            .findAny()
                            .orElseThrow(() -> new IllegalArgumentException(
                                    String.format("Could not find field %s in target class %s referenced by coremod: %s",
                                            fi.name(), fi.clazz(), tf.getCoremodName())));
                    ((FieldTransformer) tf).run(field);
                }
            }
        }
    }

    //taken from Fabric-Asm (https://github.com/Chocohead/Fabric-Asm)
    //which is licensed under the Mozilla Public License version 2.0
    //All credits go to Chocohead.
    private static byte[] fakeMixin(String name, String target) {
        ClassWriter cw = new ClassWriter(0);
        cw.visit(60, Opcodes.ACC_PUBLIC | Opcodes.ACC_ABSTRACT | Opcodes.ACC_INTERFACE, name, null, "java/lang/Object", null);

        AnnotationVisitor mixinAnnotation = cw.visitAnnotation("Lorg/spongepowered/asm/mixin/Mixin;", false);
        AnnotationVisitor targetAnnotation = mixinAnnotation.visitArray("value");
        targetAnnotation.visit(null, Type.getType('L' + target + ';'));
        targetAnnotation.visitEnd();
        mixinAnnotation.visitEnd();

        cw.visitEnd();
        return cw.toByteArray();
    }

    private static void addClassUrl(URL url) {
        if (url == null) return;
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
            addUrl.invoke(loader, url);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Could not invoke URLClassLoader#addURL", e);
        }
    }

    private static URL url(Map<String, byte[]> nameToBytes) {
        try {
            return new URL("", "", -1, "/", new URLStreamHandler() {
                @Override
                protected URLConnection openConnection(URL u) {
                    return new URLConnection(u) {
                        @Override
                        public void connect() {
                            throw new UnsupportedOperationException("noop");
                        }

                        @Override
                        public InputStream getInputStream() {
                            return new ByteArrayInputStream(nameToBytes.get(u.getPath()));
                        }
                    };
                }
            }
            );
        } catch (MalformedURLException ignored) {
            return null;
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
