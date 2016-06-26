package com.customize.reporter.model;

import org.testng.Reporter;

/**
 * This class serves as the base for all logging with respect to UI operations being done either on the browser or on a
 * native app in a simulator/device. Any functionality that intends to provide a reporting capability similar to
 * {@link com.customize.reporter.WebReporter} should leverage this class for the basic functionalities and add up only customizations as and
 * where required.
 * 
 */
public abstract class AbstractLog {

    private String type;
    private String msg;
    private String screen;
    private String location;

    /**
     * Add custom parsing of a String that represents a line in the log.
     * 
     * @param part
     */
    abstract protected void parse(String part);

    /**
     * @return - <code>true</code> if there are logs that need to be dumped into the TestNG reports via
     *         {@link Reporter#log(String)}.
     */
    abstract public boolean hasLogs();

    protected AbstractLog() {
    }

    protected AbstractLog(String s) {

        if (s == null) {
            return;
        }
        String[] parts = s.split("\\|\\|");
        for (int i = 0; i < parts.length; i++) {
            baseParse(parts[i]);
        }
    }

    protected void baseParse(String part) {
        if (part.startsWith("TYPE=")) {
            type = part.replace("TYPE=", "");
            if ("".equals(type)) {
                type = null;
            }
        } else if (part.startsWith("MSG=")) {
            msg = part.replace("MSG=", "");
        } else if (part.startsWith("SCREEN=")) {
            screen = part.replace("SCREEN=", "");
            if ("".equals(screen)) {
                screen = null;
            }
        } else {
            parse(part);
        }
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getScreen() {
        return screen;
    }

    public String getLocation() {
        return location;
    }

    public String toString() {
        StringBuffer buff = new StringBuffer();
        buff.append("TYPE=");
        if (type != null) {
            buff.append(type);
        }
        buff.append("||MSG=");
        if (msg != null) {
            buff.append(msg);
        }
        buff.append("||SCREEN=");
        if (screen != null && !screen.trim().isEmpty()) {
            buff.append(screen);
        }

        buff.append("||LOCATION=");
        if (location != null) {
            buff.append(location);
        }
        return buff.toString();

    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getScreenURL() {
        String returnValue = "no screen";
        if (screen != null) {
            returnValue = screen.replaceAll("\\\\", "/");
        }
        return returnValue;
    }

    public void setScreen(String screen) {
        this.screen = screen;
    }
}
