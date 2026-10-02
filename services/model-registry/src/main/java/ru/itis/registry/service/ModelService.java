package ru.itis.registry.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itis.registry.dto.ModelRequest;
import ru.itis.registry.dto.ModelResponse;
import ru.itis.registry.entity.Model;
import ru.itis.registry.repository.ModelRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ModelService {

    private final ModelRepository repository;

    @Transactional
    public ModelResponse register(ModelRequest request) {
        Model model = repository
                .findByNameAndVersion(request.name(), request.version())
                .orElseGet(() -> new Model(
                        request.name(),
                        request.version(),
                        request.category(),
                        request.url(),
                        request.license(),
                        request.sizeMb(),
                        request.description()
                ));

        model.setCategory(request.category());
        model.setUrl(request.url());
        model.setLicense(request.license());
        model.setSizeMb(request.sizeMb());
        model.setDescription(request.description());

        Model saved = repository.save(model);
        log.info("Registered model: {}:{} ({})", saved.getName(), saved.getVersion(), saved.getCategory());
        return toResponse(saved);
    }

    public List<ModelResponse> getAll() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    public List<ModelResponse> getByName(String name) {
        return repository.findByName(name).stream().map(this::toResponse).toList();
    }

    public ModelResponse getByNameAndVersion(String name, String version) {
        return repository.findByNameAndVersion(name, version)
                .map(this::toResponse)
                .orElseThrow(() -> new RuntimeException("Model not found: " + name + ":" + version));
    }

    @Transactional
    public void delete(Long id) {
        repository.deleteById(id);
        log.info("Deleted model id={}", id);
    }

    private ModelResponse toResponse(Model m) {
        return new ModelResponse(
                m.getId(), m.getName(), m.getVersion(), m.getCategory(),
                m.getUrl(), m.getLicense(), m.getSizeMb(),
                m.getDescription(), m.getCreatedAt()
        );
    }
}