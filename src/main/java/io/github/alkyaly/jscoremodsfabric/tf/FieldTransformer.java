package io.github.alkyaly.jscoremodsfabric.tf;

import io.github.alkyaly.jscoremodsfabric.Coremod;
import io.github.alkyaly.jscoremodsfabric.JsCoremodsFabric;
import io.github.alkyaly.jscoremodsfabric.Target;
import org.objectweb.asm.tree.FieldNode;

import java.util.function.Function;

public class FieldTransformer extends Transformer<FieldNode> {
    public FieldTransformer(Coremod coremod, String coremodName, Target targets, Function<FieldNode, FieldNode> func) {
        super(coremod, coremodName, targets, func);
    }

    @Override
    public FieldNode run(FieldNode input) {
        JsCoremodsFabric.LOGGER.debug(JsCoremodsFabric.COREMOD, "Transforming {} with desc {}", input.name, input.desc);
        return func.apply(input);
    }
}
