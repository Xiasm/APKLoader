package com.xiashengming.plugin;

public class PluginException extends RuntimeException {

    public PluginException() {
    }

    public PluginException(String message) {
        super(message);
    }

    public PluginException(String message, Throwable cause) {
        super(message, cause);
    }
}
