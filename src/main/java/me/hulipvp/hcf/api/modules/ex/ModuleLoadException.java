package me.hulipvp.hcf.api.modules.ex;

public class ModuleLoadException extends RuntimeException {

    public ModuleLoadException(String module, String message) {
        super("[Module] Failed to load Module '" + module + "', error: " + message);
    }
}
