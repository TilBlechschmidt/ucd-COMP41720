package dev.blechschmidt.quocoactor.service;

import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.ActorRef;
import dev.blechschmidt.quocoactor.service.actor.Quoter;
import dev.blechschmidt.quocoactor.service.messages.QuoterInit;

public class Main {
    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create();

        ActorRef ref = system.actorOf(Props.create(Quoter.class), "dodgydrivers");
        ref.tell(new QuoterInit(new DDQService()), null);

        Quoter.registerWithBroker(args, system, ref);
    }
}
