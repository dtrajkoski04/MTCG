package sampleapp.persistence.repository;

import java.util.List;
import sampleapp.model.Package;

public interface PackageRepository {
    void save(Package packageToBeSaved, List<String> cardIds);
}