package com.dcmc.apps.taskmanager.service;

import com.dcmc.apps.taskmanager.domain.Priority;
import com.dcmc.apps.taskmanager.repository.PriorityRepository;
import com.dcmc.apps.taskmanager.service.dto.CreatePriorityDTO;
import com.dcmc.apps.taskmanager.service.dto.PriorityDTO;
import com.dcmc.apps.taskmanager.service.dto.VisibilityPriorityDTO;
import com.dcmc.apps.taskmanager.service.mapper.PriorityMapper;

import java.time.Instant;
import java.util.Optional;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.dcmc.apps.taskmanager.domain.Priority}.
 */
@Service
@Transactional
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class PriorityService {

    private static final Logger LOG = LoggerFactory.getLogger(PriorityService.class);

    private final PriorityRepository priorityRepository;

    private final PriorityMapper priorityMapper;

    public PriorityService(PriorityRepository priorityRepository, PriorityMapper priorityMapper) {
        this.priorityRepository = priorityRepository;
        this.priorityMapper = priorityMapper;
    }

    /**
     * Save a priority.
     *
     * @param priorityDTO the entity to save.
     * @return the persisted entity.
     */
    public PriorityDTO save(CreatePriorityDTO priorityDTO) {
        LOG.debug("Request to save Priority : {}", priorityDTO);

        Priority priority = new Priority()
            .name(priorityDTO.getName())
            .description(priorityDTO.getDescription())
            .visible(true) // Default visibility set to true
            .createdAt(java.time.Instant.now());

        priority = priorityRepository.save(priority);
        return priorityMapper.toDto(priority);
    }

    /**
     * Update a priority.
     *
     * @param priorityDTO the entity to save.
     * @return the persisted entity.
     */
    public PriorityDTO update(Long id, CreatePriorityDTO priorityDTO) {
        LOG.debug("Request to update Priority ID {} with data: {}", id, priorityDTO);

        // 1. Buscar la entidad existente
        Priority existingPriority = priorityRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Priority not found with id: " + id));

        // 2. Actualizar solo los campos permitidos
        existingPriority
            .name(priorityDTO.getName())
            .description(priorityDTO.getDescription())
            .updatedAt(Instant.now());


        // 4. Guardar los cambios
        Priority updatedPriority = priorityRepository.save(existingPriority);
        return priorityMapper.toDto(updatedPriority);
    }

    /**
     * Partially update a priority.
     *
     * @param priorityDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<PriorityDTO> partialUpdate(PriorityDTO priorityDTO) {
        LOG.debug("Request to partially update Priority : {}", priorityDTO);

        return priorityRepository
            .findById(priorityDTO.getId())
            .map(existingPriority -> {
                priorityMapper.partialUpdate(existingPriority, priorityDTO);

                return existingPriority;
            })
            .map(priorityRepository::save)
            .map(priorityMapper::toDto);
    }

    /**
     * Get one priority by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<PriorityDTO> findOne(Long id) {
        LOG.debug("Request to get Priority : {}", id);
        return priorityRepository.findById(id).map(priorityMapper::toDto);
    }

    /**
     * Delete the priority by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Priority : {}", id);
        priorityRepository.deleteById(id);
    }

    public PriorityDTO updateVisibility(Long id, VisibilityPriorityDTO dto) {
        Priority priority = priorityRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Priority not found"));

        priority.setVisible(dto.getVisible());

        return priorityMapper.toDto(priorityRepository.save(priority));
    }
}
