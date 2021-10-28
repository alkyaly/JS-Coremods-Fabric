package io.github.alkyaly.jscoremodsfabric;

import com.google.common.collect.ImmutableSet;
import org.openjdk.nashorn.api.scripting.NashornScriptEngineFactory;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import java.util.Set;

public final class ScriptManager {

    public static final String INIT_COREMOD = "initializeCoreMod";

    public static ScriptEngine newEngine() {
        ScriptEngine engine = new NashornScriptEngineFactory().getScriptEngine(ScriptManager::checkClass);
        ScriptContext ctx = engine.getContext();

        ctx.removeAttribute("load", ctx.getAttributesScope("load"));
        ctx.removeAttribute("quit", ctx.getAttributesScope("quit"));
        ctx.removeAttribute("loadWithNewGlobal", ctx.getAttributesScope("loadWithNewGlobal"));
        ctx.removeAttribute("exit", ctx.getAttributesScope("exit"));

        return engine;
    }

    public static boolean checkClass(String s) {
        return ALLOWED_CLASSES.contains(s) || (s.lastIndexOf('.') != -1 && ALLOWED_PACKAGES.contains(s.substring(0, s.lastIndexOf('.'))));
    }

    public static final Set<String> ALLOWED_PACKAGES = ImmutableSet.of(
            "java.util",
            "java.util.function",
            "org.objectweb.asm.util"
    );

    public static final Set<String> ALLOWED_CLASSES = ImmutableSet.of(
            "org.objectweb.asm.Opcodes",
            "io.github.alkyaly.jscoremodsfabric.api.ASMAPI",

            "org.objectweb.asm.tree.AbstractInsnNode",
            "org.objectweb.asm.tree.FieldInsnNode",
            "org.objectweb.asm.tree.FrameNode",
            "org.objectweb.asm.tree.IincInsnNode",
            "org.objectweb.asm.tree.InsnNode",
            "org.objectweb.asm.tree.IntInsnNode",
            "org.objectweb.asm.tree.InsnList",
            "org.objectweb.asm.tree.InvokeDynamicInsnNode",
            "org.objectweb.asm.tree.JumpInsnNode",
            "org.objectweb.asm.tree.LabelNode",
            "org.objectweb.asm.tree.LdcInsnNode",
            "org.objectweb.asm.tree.LineNumberNode",
            "org.objectweb.asm.tree.LocalVariableAnnotationNode",
            "org.objectweb.asm.tree.LocalVariableNode",
            "org.objectweb.asm.tree.LookupSwitchInsnNode",
            "org.objectweb.asm.tree.MethodInsnNode",
            "org.objectweb.asm.tree.MultiANewArrayInsnNode",
            "org.objectweb.asm.tree.TableSwitchInsnNode",
            "org.objectweb.asm.tree.TryCatchBlockNode",
            "org.objectweb.asm.tree.TypeAnnotationNode",
            "org.objectweb.asm.tree.AnnotationNode",
            "org.objectweb.asm.tree.TypeInsnNode",
            "org.objectweb.asm.tree.VarInsnNode",

            "org.objectweb.asm.tree.FieldNode",

            "org.objectweb.asm.tree.MethodNode",
            "org.objectweb.asm.tree.ParameterNode",

            "org.objectweb.asm.Attribute",
            "org.objectweb.asm.Handle",
            "org.objectweb.asm.Label",
            "org.objectweb.asm.Type",
            "org.objectweb.asm.TypePath",
            "org.objectweb.asm.TypeReference"
    );
}
