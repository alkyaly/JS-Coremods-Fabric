package io.github.alkyaly.jscoremodsfabric;

import io.github.alkyaly.jscoremodsfabric.tf.ClassTransformer;
import io.github.alkyaly.jscoremodsfabric.tf.FieldTransformer;
import io.github.alkyaly.jscoremodsfabric.tf.MethodTransformer;
import io.github.alkyaly.jscoremodsfabric.tf.Transformer;
import org.openjdk.nashorn.api.scripting.ScriptObjectMirror;

import javax.script.Bindings;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class Coremod {

    private final InputStream stream;
    private final ScriptEngine engine;
    private Map<String, ? extends Bindings> invoked;

    public Coremod(InputStream stream, ScriptEngine engine) {
        this.stream = stream;
        this.engine = engine;
    }

    public List<? extends Transformer<?>> eval() {
        try {
            engine.eval(new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8)));
            invoked = (Map<String, ? extends Bindings>) ((Invocable) engine).invokeFunction(ScriptManager.INIT_COREMOD);
            return buildTransformers();
        } catch (ScriptException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public List<? extends Transformer<?>> buildTransformers() {
        return invoked.entrySet().stream().map(this::buildCore).toList();
    }

    public Transformer<?> buildCore(Map.Entry<String, ? extends Bindings> entry) {
        String name = entry.getKey();
        Bindings bindings = entry.getValue();

        Map<String, Object> target = (Map<String, Object>) bindings.get("target");
        TargetType type = TargetType.valueOf((String) target.get("type"));
        Targets targetName;
        Bindings transformer = (Bindings) bindings.get("transformer");

        switch (type) {
            case CLASS -> {
                String clsName = Targets.map(type, (String) target.get("name"), null, null);
                targetName = new Targets.Class(clsName);
                return new ClassTransformer(this, name, targetName, getFunction(transformer));
            }
            case METHOD -> {
                String clsMName = Targets.map(TargetType.CLASS, (String) target.get("class"), null, null);
                String mFullName = Targets.map(type, (String) target.get("class"), (String) target.get("methodName"), (String) target.get("methodDesc"));
                targetName = new Targets.Method(clsMName, mFullName);
                return new MethodTransformer(this, name, targetName, getFunction(transformer));
            }
            case FIELD -> {
                String clsFName = Targets.map(TargetType.CLASS, (String) target.get("class"), null, null);
                String fFullName = Targets.map(type, (String) target.get("class"), (String) target.get("fieldName"), (String) target.get("fieldDesc"));
                targetName = new Targets.Field(clsFName, fFullName);
                return new FieldTransformer(this, name, targetName, getFunction(transformer));
            }
        }
        throw new IllegalArgumentException("Type: " + target + "is unimplemented");
    }

    private static <A, R> Function<A, R> getFunction(Bindings obj) {
        return a -> (R) ((ScriptObjectMirror) obj).call(obj, a);
    }

    enum TargetType {
        CLASS,
        METHOD,
        FIELD
    }
}
