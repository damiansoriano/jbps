package it.polimi.jbps.model;

import com.hp.hpl.jena.ontology.OntModel;

public class OntologyModelFacadeTest extends ModelFacadeTest {

	@Override
	protected ModelFacade getModeFacade(OntModel ontologyModel) {
		return new OntologyModelFacade(ontologyModel);
	}

}
