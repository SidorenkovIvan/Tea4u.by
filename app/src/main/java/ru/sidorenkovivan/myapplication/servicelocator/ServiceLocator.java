package ru.sidorenkovivan.myapplication.servicelocator;

public class ServiceLocator {

    private final Cache cache = new Cache();

    public ConnectionService getService(String serviceName) {
        final ConnectionService service = cache.getService(serviceName);
        if (service != null) {
            return service;
        }

        final InitialContext initialContext = new InitialContext();
        final ConnectionService connectionService = (ConnectionService) initialContext.lookup(serviceName);
        cache.addService(serviceName, connectionService);

        return connectionService;
    }
}

