package sampleapp.persistence.repository;

import sampleapp.model.Card;
import sampleapp.persistence.DataAccessException;
import sampleapp.persistence.UnitOfWork;
import sampleapp.model.Package;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static sampleapp.persistence.repository.CardRepositoryImpl.mapResultSetToCard;

public class PackageRepositoryImpl implements PackageRepository {
    private UnitOfWork unitOfWork;

    public PackageRepositoryImpl(UnitOfWork unitOfWork) {
        this.unitOfWork = unitOfWork;
    }

    @Override
    public void save(Package packageToBeSaved, List<String> cardIds) {
        String packageSql = "INSERT INTO packages DEFAULT VALUES RETURNING id";
        String packageCardsSql = "INSERT INTO package_cards (package_id, card_id) VALUES (?, ?)";

        try (PreparedStatement stmt = unitOfWork.prepareStatement(packageSql); PreparedStatement stmt2 = unitOfWork.prepareStatement(packageCardsSql)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                packageToBeSaved.setId(rs.getInt(1));
            }
            for(String cardId : cardIds) {
                stmt2.setInt(1, packageToBeSaved.getId());
                stmt2.setString(2, cardId);
                stmt2.addBatch();
            }
            stmt2.executeBatch();
            unitOfWork.commitTransaction();
        } catch (SQLException e) {
            unitOfWork.rollbackTransaction();
            throw new DataAccessException("Failed to create Package", e);
        }

    }

   @Override
    public List<Package> findAll() {
        String packageSql = "SELECT * FROM packages";
        List<Package> packages = new ArrayList<>();

        try(PreparedStatement stmt = unitOfWork.prepareStatement(packageSql)){
            ResultSet rs = stmt.executeQuery();
            unitOfWork.commitTransaction();
            while(rs.next()) {
                packages.add(new Package(rs.getInt("id")));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return packages;
    }

    @Override
    public List<Card> findCardsByPackageId(int packageId) {
        String cardsSql = "SELECT c.id, c.name, c.damage, c.element_type, c.card_type " +
                "FROM package_cards pc " +
                "JOIN cards c ON pc.card_id = c.id " +
                "WHERE pc.package_id = ?";
        List<Card> cards = new ArrayList<>();

        try(PreparedStatement stmt = unitOfWork.prepareStatement(cardsSql)){
            stmt.setInt(1, packageId);
            unitOfWork.commitTransaction();
            try(ResultSet rs = stmt.executeQuery()){
                while(rs.next()) {
                    cards.add(mapResultSetToCard(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return cards;
    }

    @Override
    public void delete(int packageId) {
        String deleteSql = "DELETE FROM packages WHERE id = ?";

        try(PreparedStatement stmt = unitOfWork.prepareStatement(deleteSql)){
            stmt.setInt(1, packageId);
            stmt.executeUpdate();
            unitOfWork.commitTransaction();
        }catch(SQLException e) {
            e.printStackTrace();
        }
    }
}