package ru.itis.registry.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "models",
        uniqueConstraints = @UniqueConstraint(columnNames = {"name", "version"}))
@Getter
@Setter
public class Model {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String version;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false, length = 512)
    private String url;

    @Column(nullable = false)
    private String license;

    private long sizeMb;

    @Column(length = 1024)
    private String description;

    @Column(nullable = false)
    private Instant createdAt;

    public Model() {}

    public Model(String name, String version, String category, String url,
                 String license, long sizeMb, String description) {
        this.name = name;
        this.version = version;
        this.category = category;
        this.url = url;
        this.license = license;
        this.sizeMb = sizeMb;
        this.description = description;
        this.createdAt = Instant.now();
    }
}