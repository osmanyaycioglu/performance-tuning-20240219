package com.adenon.library.common.sequence;


public interface ISequenceGeneratorFactory {


    public IntegerSequenceGenerator getIntegerSequenceGenerator(String name);

    public LongSequenceGenerator getLongSequenceGenerator(String name);

}