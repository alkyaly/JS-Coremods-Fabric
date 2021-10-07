package io.github.alkyaly.jscoremodsfabric.tf;

import io.github.alkyaly.jscoremodsfabric.Coremod;
import io.github.alkyaly.jscoremodsfabric.JsCoremodsFabric;
import io.github.alkyaly.jscoremodsfabric.Targets;
import org.objectweb.asm.tree.ClassNode;

import java.util.function.Function;

public class ClassTransformer extends Transformer<ClassNode> {
    public ClassTransformer(Coremod coremod, String coremodName, Targets targets, Function<ClassNode, ClassNode> func) {
        super(coremod, coremodName, targets, func);
    }

    @Override
    public ClassNode run(ClassNode input) {
        JsCoremodsFabric.LOGGER.debug(JsCoremodsFabric.COREMOD, "Transforming {}", input.name);
        return func.apply(input);
    }
}
