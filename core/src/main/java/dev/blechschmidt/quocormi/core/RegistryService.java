package dev.blechschmidt.quocormi.core;

import java.rmi.AlreadyBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RegistryService extends Remote {
    /**
     * Registers a service with the registry
     * 
     * @param name   Name with which to register the service
     * @param remote Implementation of the service
     * @throws RemoteException
     */
    public void bind(String name, Remote remote) throws RemoteException, AlreadyBoundException;
}
