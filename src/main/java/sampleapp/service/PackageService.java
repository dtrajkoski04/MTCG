package sampleapp.service;

import httpserver.server.Response;
import sampleapp.model.Card;
import sampleapp.persistence.UnitOfWork;
import sampleapp.persistence.repository.PackageRepository;
import sampleapp.model.Package;
import sampleapp.persistence.repository.PackageRepositoryImpl;
import sampleapp.persistence.repository.UserRepository;
import sampleapp.persistence.repository.UserRepositoryImpl;

import java.sql.SQLException;
import java.util.List;

public class PackageService {
    private PackageRepository packageRepository;
    private UserRepository userRepository;

    public PackageService() {
        this.packageRepository = new PackageRepositoryImpl(new UnitOfWork());
        this.userRepository = new UserRepositoryImpl(new UnitOfWork());
    }

    public void save(Package pkg, List<String> cardIds) throws SQLException {
        if (cardIds == null || cardIds.size() != 5) {
            throw new IllegalArgumentException("cardIds must contain exactly 5 elements");
        }
        packageRepository.save(pkg, cardIds);
    }


    public void acquirePackages(String username) throws SQLException {
        var user = userRepository.getUserByUsername(username)
                .orElseThrow(() -> new SQLException("User not found"));
        if(user.getCoins() < 5){
            throw new SQLException("You need at least 5 coins");
        }

        Package pkg = packageRepository.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new SQLException("Package not found"));
        user.setCoins(user.getCoins() - 5);
        userRepository.updateUser(user);

        List<Card> cards = packageRepository.findCardsByPackageId(pkg.getId());

        cards.forEach(card -> {
            try {
                userRepository.addCardToUser(user.getUsername(), card.getId());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        packageRepository.delete(pkg.getId());
    }
}