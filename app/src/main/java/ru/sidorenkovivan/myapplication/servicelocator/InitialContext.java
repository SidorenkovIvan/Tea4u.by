package ru.sidorenkovivan.myapplication.servicelocator;

public class InitialContext {
    public Object lookup(String serviceName) {
        if (serviceName.equalsIgnoreCase("WifiService")) {
            return new WifiService();
        } else if (serviceName.equalsIgnoreCase("LteService")) {
            return new LteService();
        }

        return null;
    }
}
