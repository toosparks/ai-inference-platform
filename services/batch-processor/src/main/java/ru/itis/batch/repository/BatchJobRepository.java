package ru.itis.batch.repository;

import org.springframework.data.cassandra.repository.CassandraRepository;
import ru.itis.batch.entity.BatchJob;

import java.util.UUID;

public interface BatchJobRepository extends CassandraRepository<BatchJob, UUID> {
}