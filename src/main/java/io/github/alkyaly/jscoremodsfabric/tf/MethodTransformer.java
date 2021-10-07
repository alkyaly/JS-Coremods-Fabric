package io.github.alkyaly.jscoremodsfabric.tf;

import io.github.alkyaly.jscoremodsfabric.Coremod;
import io.github.alkyaly.jscoremodsfabric.JsCoremodsFabric;
import io.github.alkyaly.jscoremodsfabric.Targets;
import org.objectweb.asm.tree.MethodNode;

import java.util.function.Function;

public class MethodTransformer extends Transformer<MethodNode> {
    public MethodTransformer(Coremod coremod, String coremodName, Targets targets, Function<MethodNode, MethodNode> func) {
        super(coremod, coremodName, targets, func);
    }

    @Override
    public MethodNode run(MethodNode input) {
        JsCoremodsFabric.LOGGER.debug(JsCoremodsFabric.COREMOD, "Transforming {} with desc {}", input.name, input.desc);
        return func.apply(input);
    }
}
