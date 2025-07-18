/*
 * Copyright © 2015 Integrated Knowledge Management (support@ikm.dev)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dev.ikm.komet.kview.klwindows.concept;

import dev.ikm.komet.framework.KometNodeFactory;
import dev.ikm.komet.framework.activity.ActivityStream;
import dev.ikm.komet.framework.activity.ActivityStreamOption;
import dev.ikm.komet.framework.activity.ActivityStreams;
import dev.ikm.komet.framework.view.ViewProperties;
import dev.ikm.komet.kview.klwindows.AbstractEntityChapterKlWindow;
import dev.ikm.komet.kview.klwindows.EntityKlWindowType;
import dev.ikm.komet.kview.klwindows.EntityKlWindowTypes;
import dev.ikm.komet.kview.mvvm.view.details.DetailsNode;
import dev.ikm.komet.kview.mvvm.view.details.DetailsNodeFactory;
import dev.ikm.komet.preferences.KometPreferences;
import dev.ikm.tinkar.common.alert.AlertStreams;
import dev.ikm.tinkar.common.id.PublicIdStringKey;
import dev.ikm.tinkar.common.id.PublicIds;
import dev.ikm.tinkar.common.util.uuid.UuidT5Generator;
import dev.ikm.tinkar.terms.EntityFacade;
import javafx.scene.layout.Pane;
import org.eclipse.collections.api.factory.Lists;

import java.util.UUID;

import static dev.ikm.komet.kview.mvvm.viewmodel.FormViewModel.CREATE;
import static dev.ikm.komet.kview.mvvm.viewmodel.FormViewModel.MODE;

/**
 * A specialized Komet window for creating or editing concept entities.
 * <p>
 * This class extends {@link AbstractEntityChapterKlWindow} and incorporates a {@link DetailsNode}
 * for viewing or modifying concept details. It leverages the activity stream framework
 * to broadcast and receive updates about concept changes.
 */
public class ConceptKlWindow extends AbstractEntityChapterKlWindow {

    private final DetailsNode detailsNode;
    private final PublicIdStringKey<ActivityStream> detailsActivityStreamKey;

    /**
     * Constructs a new {@code ConceptKlWindow}.
     *
     * @param journalTopic   the UUID representing the journal topic the owning Journal Window uses to communicate events.
     * @param entityFacade   entity facade when not null usually this will load and display the current details.
     * @param viewProperties view properties is access to view calculators to query data.
     * @param preferences    komet preferences assists on reading and writing data to preferences user.home/Solor/database_folder/preferences
     */
    public ConceptKlWindow(UUID journalTopic, EntityFacade entityFacade,
                           ViewProperties viewProperties, KometPreferences preferences) {
        super(journalTopic, entityFacade, viewProperties, preferences);

        final boolean isCreateMode = (entityFacade == null);

        String uniqueDetailsTopic = isCreateMode
                ? "details-%s".formatted(getWindowTopic())
                : "details-%s".formatted(entityFacade.nid());
        UUID uuid = UuidT5Generator.get(uniqueDetailsTopic);

        // Create a unique key for the details activity stream.
        this.detailsActivityStreamKey = new PublicIdStringKey<>(PublicIds.of(uuid.toString()), uniqueDetailsTopic);
        ActivityStreams.create(detailsActivityStreamKey);

        // Initialize the DetailsNode with a factory.
        KometNodeFactory detailsNodeFactory = new DetailsNodeFactory();
        this.detailsNode = (DetailsNode) detailsNodeFactory.create(viewProperties.parentView(),
                detailsActivityStreamKey,
                ActivityStreamOption.PUBLISH.keyForOption(),
                AlertStreams.ROOT_ALERT_STREAM_KEY,
                true,
                journalTopic);

        // Configure the details node if we are in create mode.
        if (isCreateMode) {
            detailsNode.getDetailsViewController()
                    .getConceptViewModel()
                    .setPropertyValue(MODE, CREATE);
            detailsNode.getDetailsViewController().updateView();
        }

        // This will refresh the Concept details, history, timeline
        detailsNode.handleActivity(Lists.immutable.of(entityFacade));

        // Getting the concept window pane
        this.paneWindow = (Pane) detailsNode.getNode();

        // Set the onClose callback for the details window.
        detailsNode.getDetailsViewController().setOnCloseConceptWindow(detailsController -> {
            ActivityStreams.delete(detailsActivityStreamKey);
            getOnClose().ifPresent(Runnable::run);
            // TODO more clean up such as view models and listeners just in case (memory).
        });
    }

    /**
     * Returns the key that identifies the activity stream for concept details.
     *
     * @return a {@link PublicIdStringKey} keyed to the {@link ActivityStream} for concept details
     */
    public PublicIdStringKey<ActivityStream> getDetailsActivityStreamKey() {
        return detailsActivityStreamKey;
    }

    /**
     * Returns the {@link DetailsNode} associated with this window.
     *
     * @return the {@link DetailsNode} used for concept viewing or editing
     */
    public DetailsNode getDetailsNode() {
        return detailsNode;
    }

    @Override
    public EntityKlWindowType getWindowType() {
        return EntityKlWindowTypes.CONCEPT;
    }

    @Override
    protected boolean isPropertyPanelOpen() {
        return detailsNode.getDetailsViewController().isPropertiesPanelOpen();
    }

    @Override
    protected void setPropertyPanelOpen(boolean isOpen) {
        detailsNode.getDetailsViewController().setPropertiesPanelOpen(isOpen);
    }
}
