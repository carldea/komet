package dev.ikm.komet.layout.component.version.field;

import dev.ikm.tinkar.entity.Entity;
import dev.ikm.tinkar.entity.EntityVersion;

import java.util.Set;

/**
 * Represents a field whose value is a set of entity components.
 *
 * This interface extends the KlField interface, parameterized with a set of entities
 * and their corresponding versions.
 *
 * @param <E> The type of the entities in the set.
 * @param <V> The type of the entity versions.
 *
 *  * @TODO should we have separate list and set types, or should they be combined into one component that can be constrained to prohibit addition of duplicates?
 */
public interface KlComponentSetField<E extends Entity<V>, V extends EntityVersion> extends KlField<Set<E>> {
}
