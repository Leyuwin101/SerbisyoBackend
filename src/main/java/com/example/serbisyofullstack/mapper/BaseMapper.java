package com.example.serbisyofullstack.mapper;

/**
 * Contract every mapper in the project follows so the service layer can rely on
 * a uniform mapping API regardless of the entity/DTO pair:
 *
 * <ul>
 * <li>{@link #toDto(Object)} — entity to its DTO representation (for API
 * responses)</li>
 * <li>{@link #toEntity(Object)} — create-request DTO to a new entity (before
 * save)</li>
 * <li>{@link #toUpdate(Object, Object)} — apply an update-request DTO onto an
 * existing (already managed) entity; only supplied fields overwrite state</li>
 * </ul>
 *
 * @param <E> the JPA entity type
 * @param <C> the "create" request DTO type
 * @param <U> the "update" request DTO type
 * @param <D> the DTO/response representation type
 */
public interface BaseMapper<E, C, U, D> {

    /**
     * Map an entity to its DTO representation.
     */
    D toDto(E entity);

    /**
     * Map a create-request DTO to a new, unpersisted entity.
     */
    E toEntity(C request);

    /**
     * Apply an update-request DTO onto an existing entity. Only the fields
     * present in the request overwrite the entity; the entity instance is
     * mutated in place and returned for convenience.
     */
    E toUpdate(U request, E entity);
}
