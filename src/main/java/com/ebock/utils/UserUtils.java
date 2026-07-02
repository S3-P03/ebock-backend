package com.ebock.utils;

public class UserUtils {

    /**
     * Check if a cip is valid
     * @param cip to validate
     * @return if its valid
     */
    public static boolean isCipValid(String cip) {
        if (cip == null) {
            return false;
        }

        return cip.matches("(?i)[a-zA-Z]{4}[0-9]{4}");
    }
}
