package ru.itis.batch.repository;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;
import ru.itis.batch.entity.BatchResult;

import java.util.List;
import java.util.UUID;

@Repository
public interface BatchResultRepository extends CassandraRepository<BatchResult, BatchResult.Key> {

    List<BatchResult> findByKeyJobId(UUID jobId);
}