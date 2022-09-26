package dev.blechschmidt.quocormi.core;

import java.rmi.AlreadyBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;

/**
 * Interface to register services with the registry.
 * 
 * Security notice: This service does not perform authentication and will allow
 * anyone to register services! Only use in a private network or if you know
 * what you are doing.
 * 
 * @author Til Blechschmidt <til.blechschmidt@ucdconnect.ie>
 *
 */
public class UnauthenticatedRegistryService implements RegistryService {
    private final Registry registry;

    public UnauthenticatedRegistryService(Registry registry) {
        this.registry = registry;
    }

    public void bind(String name, Remote remote) throws RemoteException, AlreadyBoundException {
        registry.bind(name, remote);
    }
}
