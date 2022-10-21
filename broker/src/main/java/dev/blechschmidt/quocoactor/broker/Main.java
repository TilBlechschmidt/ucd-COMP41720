package dev.blechschmidt.quocoactor.broker;

import akka.actor.ActorSystem;
import akka.actor.Props;

public class Main {
    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create();
        system.actorOf(Props.create(Broker.class), "broker");
    }
}
