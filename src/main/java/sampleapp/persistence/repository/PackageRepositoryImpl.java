package sampleapp.persistence.repository;

import sampleapp.persistence.DataAccessException;
import sampleapp.persistence.UnitOfWork;
import sampleapp.model.Package;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class PackageRepositoryImpl implements PackageRepository {
    private UnitOfWork unitOfWork;

    public PackageRepositoryImpl(UnitOfWork unitOfWork) {
        this.unitOfWork = unitOfWork;
    }

    @Override
    public void save(Package packageToBeSaved, List<String> cardIds) {
        String packageSql = "INSERT INTO packages DEFAULT VALUES RETURNING id";
        String packageCardsSql = "INSERT INTO packages_cards (package_id, card_id) VALUES (?, ?)";

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
}