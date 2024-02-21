package com.adenon.api.smpp.common;

public interface ParamInterface {

    public static final int PARAM_TYPE_STRING = 1;
    public static final int PARAM_TYPE_INT    = 2;

    public int getIntegerVal();

    public String getStringVal();

    public int getValType();

    public int getParamId();

    public void setIntegerVal(int _val);

    public void setStringVal(String _val);

    public void setValType(int _val);

    public void setParamId(int _val);
}
