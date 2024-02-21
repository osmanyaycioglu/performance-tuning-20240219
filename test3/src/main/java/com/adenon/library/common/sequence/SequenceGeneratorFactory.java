package com.adenon.library.common.sequence;

import java.util.HashMap;


public class SequenceGeneratorFactory implements ISequenceGeneratorFactory {

    private final HashMap<String, IntegerSequenceGenerator> integerSequenceGeneratorMap = new HashMap<String, IntegerSequenceGenerator>();
    private final HashMap<String, LongSequenceGenerator>    longSequenceGeneratorMap    = new HashMap<String, LongSequenceGenerator>();
    private static SequenceGeneratorFactory                 singleSequenceGeneratorFactory;

    private SequenceGeneratorFactory() {
    }


    public static final synchronized SequenceGeneratorFactory getSequenceGeneratorFactory() {
        if (SequenceGeneratorFactory.singleSequenceGeneratorFactory == null) {
            SequenceGeneratorFactory.singleSequenceGeneratorFactory = new SequenceGeneratorFactory();
        }
        return SequenceGeneratorFactory.singleSequenceGeneratorFactory;
    }

    @Override
    public IntegerSequenceGenerator getIntegerSequenceGenerator(final String name) {
        IntegerSequenceGenerator sequenceGenerator = this.integerSequenceGeneratorMap.get(name);
        if (sequenceGenerator == null) {
            sequenceGenerator = new IntegerSequenceGenerator();
            this.integerSequenceGeneratorMap.put(name, sequenceGenerator);
        }
        return sequenceGenerator;
    }

    @Override
    public LongSequenceGenerator getLongSequenceGenerator(final String name) {
        LongSequenceGenerator sequenceGenerator = this.longSequenceGeneratorMap.get(name);
        if (sequenceGenerator == null) {
            sequenceGenerator = new LongSequenceGenerator();
            this.longSequenceGeneratorMap.put(name, sequenceGenerator);
        }
        return sequenceGenerator;
    }

}
