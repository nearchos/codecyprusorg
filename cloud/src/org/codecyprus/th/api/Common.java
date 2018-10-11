package org.codecyprus.th.api;

public class Common {

    public static boolean checkUrlBooleanParameter(final String parameter) {
        if(parameter == null) return false;
        else if(parameter.isEmpty()) return true;
        else if("true".equalsIgnoreCase(parameter)) return true;
        else if("on".equalsIgnoreCase(parameter)) return true;
        else return false;
    }
}