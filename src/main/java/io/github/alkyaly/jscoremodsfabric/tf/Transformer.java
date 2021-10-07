package io.github.alkyaly.jscoremodsfabric.tf;

import io.github.alkyaly.jscoremodsfabric.Coremod;
import io.github.alkyaly.jscoremodsfabric.Targets;

import java.util.function.Function;

public abstract class Transformer<T> {

    private final Coremod coremod;
    private final String coremodName;
    private final Targets targets;
    protected final Function<T, T> func;

    public Transformer(Coremod coremod, String coremodName, Targets targets, Function<T, T> func) {
        this.coremod = coremod;
        this.coremodName = coremodName;
        this.targets = targets;
        this.func = func;
    }

    public abstract T run(T input);

    public Targets getTargets() {
        return targets;
    }
}
