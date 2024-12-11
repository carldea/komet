package dev.ikm.komet.layout.component.version.field;

import dev.ikm.tinkar.entity.ConceptEntity;
import dev.ikm.tinkar.entity.ConceptEntityVersion;

/**
 * Represents a field that holds a concept entity.
 *
 * This interface extends KlField, and it is parameterized with a concept entity type
 * and its corresponding version type.
 *
 * @param <C> The type of the concept entity.
 * @param <V> The type of the concept entity version.
 */
public interface KlConceptField<C extends ConceptEntity<V> , V extends ConceptEntityVersion> extends KlField<C> {
}
