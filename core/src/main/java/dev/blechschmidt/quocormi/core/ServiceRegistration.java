package dev.blechschmidt.quocormi.core;

import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 * Registers a class implementing the Remote interface with a registry.
 * The registry can either be passed in directly, built from CLI args, or
 * from the REGISTRY environment variable.
 * 
 * NOTE: This class uses the UnauthenticatedRegistryService class to allow
 * remote binding to the registry. As the class does not have any
 * authentication, this is a potential security hazard. DO NOT expose the
 * service hosting the registry to the internet!
 * 
 * @author Til Blechschmidt <til.blechschmidt@ucdconnect.ie>
 *
 */
public class ServiceRegistration {
    public static final int REGISTRY_PORT = 1099;

    // Prevent the registry service from deallocating in case we are hosting a
    // registry, otherwise it is null.
    private static UnauthenticatedRegistryService registryService;

    String name;
    Remote remote;

    Registry registry;

    /**
     * Creates a new instance from raw parts
     * 
     * @param name     Name with which the service is registered in the registry
     * @param remote   Implementation of the service
     * @param registry Registry at which to register
     */
    public ServiceRegistration(String name, Remote remote, Registry registry) {
        this.name = name;
        this.remote = remote;
        this.registry = registry;
    }

    /**
     * Creates a new instance by deriving the registry host from the CLI args where
     * the first argument is the hostname. If no hostname is provided, it will try
     * to read the host from the REGISTRY environment variable. If that is not
     * present either, it will host a new registry.
     * 
     * @param name   Name with which the service is registered in the registry
     * @param remote Implementation of the service
     * @param args   CLI arguments from main class
     * @throws RemoteException
     * @throws AlreadyBoundException
     * @throws NotBoundException
     */
    public ServiceRegistration(String name, Remote remote, String[] args)
            throws RemoteException, AlreadyBoundException, NotBoundException {
        this.name = name;
        this.remote = remote;
        this.registry = buildRegistry(args);
    }

    /**
     * Creates a new instance by reading the host from the REGISTRY environment
     * variable. If that is not present either, it will host a new registry.
     * 
     * @param name   Name with which the service is registered in the registry
     * @param remote Implementation of the service
     * @throws RemoteException
     * @throws AlreadyBoundException
     * @throws NotBoundException
     */
    public ServiceRegistration(String name, Remote remote)
            throws RemoteException, AlreadyBoundException, NotBoundException {
        this.name = name;
        this.remote = remote;
        this.registry = buildRegistry(null);
    }

    /**
     * Registers the service with the remote registry using a RegistryService
     * 
     * @throws RemoteException
     * @throws AlreadyBoundException
     * @throws NotBoundException
     */
    public void register() throws RemoteException, AlreadyBoundException, NotBoundException {
        RegistryService registryService = (RegistryService) registry
                .lookup(Constants.REGISTRY_SERVICE);
        Remote service = UnicastRemoteObject.exportObject(this.remote, 0);
        registryService.bind(name, service);
    }

    /**
     * Registry hosting/connection logic used for the different constructor
     * variants. If you need access to the registry for instantiating your service,
     * use this method in combination with the "raw parts" constructor.
     * 
     * @param args CLI args from main class, can be null
     * @return Remote or locally hosted registry
     * @throws RemoteException
     * @throws AlreadyBoundException
     * @throws NotBoundException
     */
    public static Registry buildRegistry(String[] args)
            throws RemoteException, AlreadyBoundException, NotBoundException {
        String host_env = System.getenv("REGISTRY");
        String host_arg = args != null ? (args.length > 0 ? args[0] : null) : null;

        if (host_arg != null) {
            System.out.println("Connecting to registry @ '" + host_arg + ":" + REGISTRY_PORT + "'");
            return LocateRegistry.getRegistry(host_arg, REGISTRY_PORT);
        } else if (host_env != null) {
            System.out.println("Connecting to registry @ '" + host_env + ":" + REGISTRY_PORT + "'");
            return LocateRegistry.getRegistry(host_env, REGISTRY_PORT);
        } else {
            System.out.println("Hosting registry on port " + REGISTRY_PORT);

            // Create the registry
            Registry registry = LocateRegistry.createRegistry(REGISTRY_PORT);

            // Bind the RegistryService so remote instances of the ServiceRegistration can
            // actually register services
            registryService = new UnauthenticatedRegistryService(registry);
            registry.bind(Constants.REGISTRY_SERVICE,
                    UnicastRemoteObject.exportObject(registryService, 0));

            return registry;
        }
    }
}
