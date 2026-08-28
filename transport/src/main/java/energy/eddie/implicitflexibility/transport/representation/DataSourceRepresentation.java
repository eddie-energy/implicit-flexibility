package energy.eddie.implicitflexibility.transport.representation;

import org.springframework.hateoas.RepresentationModel;

public class DataSourceRepresentation extends RepresentationModel<DataSourceRepresentation> {

    private final String id;

    public DataSourceRepresentation(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
