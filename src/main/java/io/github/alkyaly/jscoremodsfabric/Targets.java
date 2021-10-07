package io.github.alkyaly.jscoremodsfabric;

public interface Targets {

    record Class(String name) implements Targets {
        @Override
        public String format() {
            return name;
        }

        @Override
        public String clazz() {
            return name;
        }
    }

    record Field(String clazz, String name, String desc) implements Targets {
        @Override
        public String format() {
            return clazz + ";" + name + ":" + desc;
        }

        @Override
        public String clazz() {
            return clazz;
        }
    }

    record Method(String clazz, String name, String desc) implements Targets {
        @Override
        public String format() {
            return clazz + ";" + name + desc;
        }

        @Override
        public String clazz() {
            return clazz;
        }
    }

    String format();

    String clazz();
}
