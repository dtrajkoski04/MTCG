package sampleapp.service;

import sampleapp.model.Card;
import sampleapp.persistence.UnitOfWork;
import sampleapp.persistence.repository.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BattleService {
    private DeckRepository deckRepository;
    private BattleRepository battleRepository;
    private UserRepository userRepository;
    private List<String> queue;
    private String result;
    private String player1;
    private String player2;
    private Object lock;
    boolean running;

    public BattleService() {
        this.deckRepository = new DeckRepositoryImpl(new UnitOfWork());
        this.battleRepository = new BattleRepositoryImpl(new UnitOfWork());
        this.userRepository = new UserRepositoryImpl(new UnitOfWork());
        this.queue = new ArrayList<>();
        this.result = null;
        this.player1 = null;
        this.player2 = null;
        this.lock = new Object();
        this.running = false;
    }

    public String startBattle(String username){
        try {
            this.userRepository.getUserByUsername(username);
        } catch (SQLException e) {
            throw new RuntimeException("Error finding the username", e);
        }

        try {
            if(queue.contains(username)){
                throw new RuntimeException("Username already in queue");
            }

            queue.add(username);
            running = true;

            if(queue.size() < 2){
                synchronized (lock){
                    while(running){
                        lock.wait();
                    }
                }
                return result;
            }

            if(queue.size() >= 2){
                this.player1 = queue.get(0);
                this.player2 = queue.get(1);

                this.result = executeBattle(this.player1, this.player2);
                running = false;
                return result;
            }
        } catch(InterruptedException e){
            throw new RuntimeException("Interrupted Exception", e);
        }
        return null;
    }

    public String executeBattle(String player1, String player2){
        List<Card> deck1 = this.deckRepository.getDeck(player1);
        List<Card> deck2 = this.deckRepository.getDeck(player2);

        if(deck1.isEmpty() || deck2.isEmpty()){
            return "One or more decks are empty";
        }

        StringBuilder log = new StringBuilder();
        log.append("Battle Begins!\n");
        int round = 0;

        while(!deck1.isEmpty() && !deck2.isEmpty() && round < 100){
            round++;
            Card card1 = deck1.get(new Random().nextInt(deck1.size()));
            Card card2 = deck2.get(new Random().nextInt(deck2.size()));

            log.append("Round ").append(round).append(": ")
                    .append(card1.getName()).append(" ("+player1+") vs ")
                    .append(card2.getName()).append(" ("+player2+")\n");

            double damage1 = damageCalculator(card1, card2);
            double damage2 = damageCalculator(card2, card1);

            if(damage1 > damage2){
                log.append("\t"+player1+" wins the round! ").append(card1.getName()).append(" "+damage1).append(" defeats ").append(card2.getName()).append(" "+damage2).append("\n");
                deck2.remove(card2);
                deck1.add(card2);
            } else if(damage2 > damage1){
                log.append("\t"+player2+" wins the round! ").append(card2.getName()).append(" "+damage2).append(" defeats ").append(card1.getName()).append(" "+damage1).append("\n");
                deck1.remove(card1);
                deck2.add(card1);
            } else {
                log.append("\tIts a Draw! No cards are exchanged.\n");
            }
        }
        log.append("Battle Ends, ");

        if(deck1.isEmpty()){
            log.append(player2+" Wins!\n");
            this.battleRepository.updateStats(player1, false);
            this.battleRepository.updateStats(player2, true);
            this.battleRepository.updateCoins(player2);
        } else if(deck2.isEmpty()){
            log.append(player1+" Wins!\n");
            this.battleRepository.updateStats(player1, true);
            this.battleRepository.updateStats(player2, false);
            this.battleRepository.updateCoins(player1);
        } else {
            log.append("It is a draw after 100 rounds!\n");
        }
        return log.toString();
    }

    public double damageCalculator(Card card1, Card card2) {
        double damage = specialDamage(card1, card2);

        if(card1.getCardType().equals("spell") && !card2.getCardType().equals("spell")){
            damage = elementDamage(card1, card2);
        }

        return damage;
    }

    public double specialDamage(Card card1, Card card2) {
        double baseDamage = card1.getDamage();

        if (card1.getCardType().equals("goblin") && card2.getCardType().equals("dragon")) {
            return 0; // Goblins can't attack Dragons
        }
        if (card2.getCardType().equals("wizzard") && card1.getCardType().equals("ork")) {
            return 0; // Wizzards control Orks
        }
        if (card1.getInfo().getDisplayName().equals("WaterSpell") && card2.getInfo().getDisplayName().equals("Knight")) {
            return Double.MAX_VALUE; // WaterSpells instantly defeat Knights
        }
        if (card2.getInfo().getDisplayName().equals("Kraken") && card1.getCardType().equals("spell")) {
            return 0; // Krakens are immune to spells
        }
        if (card2.getInfo().getDisplayName().equals("FireElve") && card1.getCardType().equals("dragon")) {
            return 0; // FireElves evade Dragons' attacks
        }

        return baseDamage;
    }

    public double elementDamage(Card card1, Card card2) {
        double baseDamage = card1.getDamage();
        String element1 = card1.getElementType();
        String element2 = card2.getElementType();

        if (element1.equals("water") && element2.equals("fire")) {
            return baseDamage * 2; // Water is effective against Fire
        }
        if (element1.equals("fire") && element2.equals("normal")) {
            return baseDamage * 2; // Fire is effective against Normal
        }
        if (element1.equals("normal") && element2.equals("water")) {
            return baseDamage * 2; // Normal is effective against Water
        }
        if (element2.equals("water") && element1.equals("fire")) {
            return baseDamage / 2; // Fire is weak against Water
        }
        if (element2.equals("normal") && element1.equals("water")) {
            return baseDamage / 2; // Water is weak against Normal
        }
        if (element2.equals("fire") && element1.equals("normal")) {
            return baseDamage / 2; // Normal is weak against Fire
        }

        return baseDamage;
    }

}
