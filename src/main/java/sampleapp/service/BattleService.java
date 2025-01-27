package sampleapp.service;

import sampleapp.model.Battle;
import sampleapp.model.Card;
import sampleapp.persistence.UnitOfWork;
import sampleapp.persistence.repository.*;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

public class BattleService {
    private final BattleRepository battleRepository;
    private final UserRepository userRepository;
    private final DeckRepository deckRepository;

    private String player1;
    private String player2;
    private final Object queue = new Object();
    private boolean running = false;
    private final StringBuilder log = new StringBuilder();
    private final Random random = new Random();

    public BattleService() {
        UnitOfWork unitOfWork = new UnitOfWork();
        this.battleRepository = new BattleRepositoryImpl(unitOfWork);
        this.userRepository = new UserRepositoryImpl(unitOfWork);
        this.deckRepository = new DeckRepositoryImpl(unitOfWork);
    }

    public String startBattle(String username) {
        synchronized (queue) {
            try {
                if (player1 == null) {
                    player1 = username;
                    log.append("Player 1 joined: ").append(username).append("\n");
                    queue.wait(); // Player 1 wartet auf Player 2
                } else if (player2 == null) {
                    player2 = username;
                    log.append("Player 2 joined: ").append(username).append("\n");
                    queue.notifyAll(); // Player 2 informiert Player 1
                }

                while (player2 == null) {
                    queue.wait(); // Warte, bis Player 2 beitritt
                }

                if (running) {
                    log.append("Battle is already running\n");
                    return log.toString();
                }

                running = true;
                log.append(String.format("Battle started between %s and %s\n", player1, player2));
                executeBattle();

                running = false;
                queue.notifyAll(); // Beende Battle

                // Battle speichern
                Battle battle = new Battle(0, player1, player2, null, log.toString(), LocalDateTime.now());
                battleRepository.createBattle(battle);

                return log.toString();

            } catch (InterruptedException | SQLException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Battle interrupted", e);
            }
        }
    }

    private void executeBattle() {
        List<Card> deck1 = deckRepository.getDeck(player1);
        List<Card> deck2 = deckRepository.getDeck(player2);

        for (int round = 1; round <= 100; round++) {
            if (deck1.isEmpty() || deck2.isEmpty()) {
                log.append("Battle ended early, one deck is empty\n");
                break;
            }

            Card card1 = deck1.get(random.nextInt(deck1.size()));
            Card card2 = deck2.get(random.nextInt(deck2.size()));

            log.append("Round ").append(round).append(": ");
            if (card1.getDamage() > card2.getDamage()) {
                log.append(player1).append(" wins the round\n");
                deck2.remove(card2);
            } else if (card1.getDamage() < card2.getDamage()) {
                log.append(player2).append(" wins the round\n");
                deck1.remove(card1);
            } else {
                log.append("Draw\n");
            }
        }

        if (deck1.isEmpty()) {
            log.append(player2).append(" wins the battle!\n");
        } else if (deck2.isEmpty()) {
            log.append(player1).append(" wins the battle!\n");
        }
    }
}
