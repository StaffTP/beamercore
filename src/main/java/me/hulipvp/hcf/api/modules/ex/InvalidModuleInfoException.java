package me.hulipvp.hcf.api.modules.ex;

public class InvalidModuleInfoException extends Exception {

    public InvalidModuleInfoException(String module) {
        super("[Module] Module '" + module + "' has an invalid info.yml file.");
    }

    public InvalidModuleInfoException(Throwable throwable) {
        super(throwable);
    }

    public InvalidModuleInfoException(String message, Throwable throwable) {
        super("[Module] " + message, throwable);
    }
}
