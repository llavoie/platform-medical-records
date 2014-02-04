package org.motechproject.couch.mrs.repository;

import org.motechproject.couch.mrs.model.CouchConcept;

import java.util.List;

public interface AllCouchConcepts {
    List<CouchConcept> findByConceptId(String conceptId);

    void addConcept(CouchConcept concept);

    void update(CouchConcept concept);

    void remove(CouchConcept concept);

    List<CouchConcept> getAll();

    List<CouchConcept> findByConceptName(String name);
}
