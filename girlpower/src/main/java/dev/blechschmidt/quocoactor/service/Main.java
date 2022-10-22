package dev.blechschmidt.quocoactor.service;

import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.ActorRef;
import dev.blechschmidt.quocoactor.service.actor.Quoter;
import dev.blechschmidt.quocoactor.service.messages.QuoterInit;

public class Main {
    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create();

        ActorRef ref = system.actorOf(Props.create(Quoter.class), "auldfellas");
        ref.tell(new QuoterInit(new GPQService()), null);

        ActorSelection selection = system.actorSelection("akka.tcp://default@127.0.0.1:2551/user/broker");
        selection.tell("register", ref);
    }
}
