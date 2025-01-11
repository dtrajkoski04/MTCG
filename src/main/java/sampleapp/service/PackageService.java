package sampleapp.service;

import httpserver.server.Response;
import sampleapp.persistence.UnitOfWork;
import sampleapp.persistence.repository.PackageRepository;
import sampleapp.model.Package;
import sampleapp.persistence.repository.PackageRepositoryImpl;
import sampleapp.persistence.repository.UserRepositoryImpl;

import java.util.List;

public class PackageService {
    private PackageRepository packageRepository;

    public PackageService() {
        this.packageRepository = new PackageRepositoryImpl(new UnitOfWork());
    }

    public void save(Package pkg, List<String> cardIds) {
        if(cardIds == null || cardIds.size() != 5) {
            throw new IllegalArgumentException("cardIds must contain exactly 5 elements");
        }
        packageRepository.save(pkg, cardIds);
    }
}