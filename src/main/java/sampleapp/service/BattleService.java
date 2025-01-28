package sampleapp.service;

import sampleapp.model.Battle;
import sampleapp.model.Card;
import sampleapp.model.CardInfo;
import sampleapp.model.User;
import sampleapp.persistence.UnitOfWork;
import sampleapp.persistence.repository.*;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;

public class BattleService {
    private final UserRepository userRepository;
    private final DeckRepository deckRepository;
    private final BattleRepository battleRepository;

    private User player1;
    private User player2;
    private final Object queue = new Object();
    private StringBuilder log = new StringBuilder();
    private LocalDateTime startTime = LocalDateTime.now();
    private final Random random = new Random();
    private boolean running = false;

    public BattleService(){
        this.userRepository = new UserRepositoryImpl(new UnitOfWork());
        this.deckRepository = new DeckRepositoryImpl(new UnitOfWork());
        this.battleRepository = new BattleRepositoryImpl(new UnitOfWork());
    }

    public void reset(){
        player1 = null;
        player2 = null;
        running = false;
        log = new StringBuilder();
        startTime = LocalDateTime.now();
    }

    public String startBattle(String username){
        Optional<User> optUser = null;
        try {
            optUser = this.userRepository.getUserByUsername(username);
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching user", e);
        }
        if(optUser.isEmpty()){
            throw new IllegalStateException("User not found");
        }

        User user = optUser.get();

        synchronized (queue){
            try {
                if ((player1 != null && Objects.equals(user.getUsername(), player1.getUsername())) || (player2 != null && Objects.equals(user.getUsername(), player2.getUsername()))) {
                    throw new IllegalStateException("User already started");
                }

                if (player1 == null) {
                    player1 = user;
                    System.out.println("Player 1 joined: " + player1.getUsername());
                    // Player 1 waits for Player 2
                    queue.wait();
                } else if (player2 == null) {
                    player2 = user;
                    System.out.println("Player 2 joined: " + player2.getUsername());
                    // Player 2 notifies Player 1
                    queue.notifyAll();
                }

                if (player2 == null){
                    while (player2 == null) {
                        System.out.println("Waiting for Player 2...");
                        queue.wait();
                    }
                }

                if (player2.getUsername().equals(user.getUsername())){
                    while(running) {
                        System.out.println("Waiting for Player 1 to finish battle...");
                        queue.wait();
                    }
                    System.out.println("Battle finished for Player 2");
                    return log.toString();
                }

                running = true;
                log.append(String.format("Player %s and Player %s battle started%n", player1.getUsername(), player2.getUsername()));

                this.executeBattle();

                running = false;
                queue.notifyAll();

                return log.toString();

            } catch(InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Interrupted", e);
            }
        }
    }

    public void executeBattle(){
        List<Card> deck1 = this.deckRepository.getDeck(player1.getUsername());
        List<Card> deck2 = this.deckRepository.getDeck(player2.getUsername());

        for(int round = 0; round < 100; round++){
            if(deck1.isEmpty() || deck2.isEmpty()){
                log.append("All rounds played%n");
                break;
            }

            Card card1 = deck1.get(random.nextInt(deck1.size()));
            Card card2 = deck2.get(random.nextInt(deck2.size()));

            User winner = this.getWinner(card1, card2);

            if(winner == null){
                log.append("Round is a draw%n");
                continue;
            }

            log.append("Round %s won by player: %s%n".formatted(round, winner.getUsername()));

            if(Objects.equals(winner.getUsername(), player1.getUsername())){
                deck2.remove(card2);
                deck1.add(card2);
            } else if(Objects.equals(winner.getUsername(), player2.getUsername())){
                deck1.remove(card1);
                deck2.add(card1);
            }
        }

        if (deck2.isEmpty()) {
            log.append("Player %s won!%n".formatted(player1.getUsername()));
            updateStats(player1);
        }
        if (deck1.isEmpty()) {
            log.append("Player %s won!%n".formatted(player2.getUsername()));
            updateStats(player2);
        }
    }

    private void updateStats(User winner){
        Battle battle = new Battle(0, player1.getUsername(), player2.getUsername(), winner.getUsername(), log.toString(), startTime);

        this.battleRepository.createBattle(battle);

        player1.setGames_played(player1.getGames_played() + 1);
        player2.setGames_played(player2.getGames_played() + 1);

        if (winner.getUsername().equals(player1.getUsername())){
            player1.setElo(player1.getElo() + 3);
            player2.setElo(player2.getElo() - 5);
            player1.setGames_won(player1.getGames_won() + 1);
            player2.setGames_lost(player2.getGames_lost() + 1);
            player1.setCoins(player1.getCoins() + 2);
            if (player2.getCoins() > 0) player2.setCoins(player2.getCoins() - 2);
        } else if (winner.getUsername().equals(player2.getUsername())){
            player2.setElo(player2.getElo() + 3);
            player1.setElo(player1.getElo() - 5);
            player2.setGames_won(player2.getGames_won() + 1);
            player1.setGames_lost(player1.getGames_lost() + 1);
            player2.setCoins(player2.getCoins() + 2);
            if (player1.getCoins() > 0) player1.setCoins(player1.getCoins() - 2);
        }

        try {
            this.userRepository.updateUser(player1);
            this.userRepository.updateUser(player2);
        } catch (SQLException e) {
            throw new RuntimeException("Error updating User", e);
        }
    }

    public User getWinner(Card card1, Card card2) {
        double damage1 = card1.getDamage();
        if (damageExceptions(card1, card2)) {
            return player1;
        }

        double damage2 = card2.getDamage();
        if (damageExceptions(card2, card1)) {
            return player2;
        }

        if (card1.getCardType() == "spell") {
            damage1 = this.spellDamage(card1, card2.getElementType());
        }

        if (card2.getCardType() == "spell") {
            damage2 = this.spellDamage(card2, card1.getElementType());
        }

        if (damage1 > damage2) {
            return player1;
        } else if (damage1 < damage2) {
            return player2;
        }

        return null;
    }

    public double spellDamage(Card card, String card2Element) {
        if (card.getElementType() == card2Element) {
            return card.getDamage();
        }

        if (card.getElementType() == "water" && card2Element == "fire") {
            return card.getDamage() * 2;
        }
        if (card.getElementType() == "water" && card2Element == "regular") {
            return (double) card.getDamage() / 2;
        }

        if (card.getElementType() == "fire" && card2Element == "regular") {
            return card.getDamage() * 2;
        }
        if (card.getElementType() == "fire" && card2Element == "water") {
            return (double) card.getDamage() / 2;
        }

        if (card.getElementType() == "regular" && card2Element == "water") {
            return card.getDamage() * 2;
        }
        if (card.getElementType() == "regular" && card2Element == "fire") {
            return (double) card.getDamage() / 2;
        }

        return 0;
    }

    public boolean damageExceptions(Card card1, Card card2) {
        if (card1.getName().contains("Goblin") && card2.getInfo() == CardInfo.DRAGON) {
            return false;
        }
        if (card1.getName() == "Ork" && card2.getInfo() == CardInfo.WIZARD) {
            return false;
        }
        if (card1.getCardType() == "spell" && card2.getInfo() == CardInfo.KNIGHT) {
            return true;
        }
        if (card1.getCardType() == "spell" && card2.getInfo() == CardInfo.KRAKEN) {
            return false;
        }
        if (card1.getInfo() == CardInfo.DRAGON && card2.getInfo() == CardInfo.FIRE_ELF) {
            return false;
        }

        return false;
    }


}
