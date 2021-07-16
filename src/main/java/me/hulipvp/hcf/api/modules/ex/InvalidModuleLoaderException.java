package me.hulipvp.hcf.api.modules.ex;

public class InvalidModuleLoaderException extends RuntimeException {

    public InvalidModuleLoaderException(String module) {
        super("[Module] Module '" + module + "' failed to be instated due to an invalid class loader");
    }
}
