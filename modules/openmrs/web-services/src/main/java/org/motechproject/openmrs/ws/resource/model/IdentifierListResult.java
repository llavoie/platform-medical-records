package org.motechproject.openmrs.ws.resource.model;

import java.util.List;

public class IdentifierListResult {
    private List<Identifier> results;

    public List<Identifier> getResults() {
        return results;
    }

    public void setResults(List<Identifier> results) {
        this.results = results;
    }
}
