package sampleapp.persistence.repository;

import java.sql.SQLException;
import java.util.List;

import sampleapp.model.Card;
import sampleapp.model.Package;

public interface PackageRepository {
    void save(Package packageToBeSaved, List<String> cardIds) throws SQLException;
    List<Package> findAll();
    List<Card> findCardsByPackageId(int packageId);
    void delete(int packageId);
}