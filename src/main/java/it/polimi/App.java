package it.polimi;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.vocabulary.VCARD;

/**
 * Hello world!
 *
 */
public class App {
	
	private final static String FILE_SRC = "/home/damian/Estudios/Polimi/Courses/Semester 4/Thesis/Colombetti/Protege/vacation.owl";
	
    public static void main( String[] args ) {
    	String personURI = "http://somewhere/JohnSmith";
    	String fullName = "John Smith";
    	
    	Model model = ModelFactory.createDefaultModel();
    	
    	Resource johnSmith = model.createResource(personURI);
    	
    	johnSmith.addProperty(VCARD.FN, fullName);
    	
    	Resource damianSoriano = model.createResource("http://person/DamianSoriano")
    			.addProperty(VCARD.FN, "Damián Soriano");
    	
    	StmtIterator listStatements = model.listStatements();
    	while (listStatements.hasNext()) {
    		Statement statement = listStatements.next();
    		System.out.println(statement);
    	}

    	System.out.println("OWL");
    	
    	Model owlModel = FileManager.get().loadModel(FILE_SRC);
    	
    	OntModel ontologyModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM_TRANS_INF, owlModel);
    	
    	listStatements = ontologyModel.listStatements();
    	while (listStatements.hasNext()) {
    		Statement statement = listStatements.next();
    		System.out.println(statement);
    	}
    	
    	ontologyModel.write(System.out);
    	
    }
}
