package me.hulipvp.hcf.api.modules.ex;

public class InvalidModuleException extends Exception {

    public InvalidModuleException(String message) {
        super("[Module] " + message);
    }

    public InvalidModuleException(Throwable throwable) {
        super(throwable);
    }

    public InvalidModuleException(String message, Throwable throwable) {
        super("[Module] " + message, throwable);
    }
}
