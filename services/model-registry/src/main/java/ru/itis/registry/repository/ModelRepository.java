package ru.itis.registry.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.itis.registry.entity.Model;

import java.util.List;
import java.util.Optional;

@Repository
public interface ModelRepository extends JpaRepository<Model, Long> {

    Optional<Model> findByNameAndVersion(String name, String version);

    List<Model> findByName(String name);

    List<Model> findByCategory(String category);
}