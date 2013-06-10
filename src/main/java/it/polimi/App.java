package it.polimi;

import java.util.Iterator;

import lombok.extern.log4j.Log4j;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.ObjectProperty;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.reasoner.ValidityReport;
import com.hp.hpl.jena.reasoner.ValidityReport.Report;
import com.hp.hpl.jena.reasoner.rulesys.GenericRuleReasoner;
import com.hp.hpl.jena.reasoner.rulesys.Rule;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.vocabulary.VCARD;

@Log4j
public class App {
	
	private final static String FILE_SRC = "/home/damian/Estudios/Polimi/Courses/Semester 4/Thesis/Colombetti/Protege/inc.owl";
	
	public static void main( String[] args ) {
		String terranURI = "http://somewhere/Terran";
		String alienURI = "http://somewhere/Alien";
		String terralienURI = "http://somewhere/Terraline";
		String terralien_01URI = "http://somewhere/terraline 01";
		
		OntModel ontologyModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_RULE_INF);
		
		OntClass terranClass = ontologyModel.createClass(terranURI);
		OntClass alienClass = ontologyModel.createClass(alienURI);
		OntClass terralienClass = ontologyModel.createClass(terralienURI);
		
		Individual terralien = terranClass.createIndividual(terralien_01URI);
		terralien.addOntClass(alienClass);
		
		String rules = "[rule1: (?a rdf:type http://somewhere/Terran) (?a rdf:type http://somewhere/Alien) -> (?a rdf:type http://somewhere/Terraline)]";
		GenericRuleReasoner genericRuleReasoner = new GenericRuleReasoner(Rule.parseRules(rules));
		
		InfModel inf = ModelFactory.createInfModel(genericRuleReasoner, ontologyModel);

		Model union = inf.union(ontologyModel);
		union.write(System.out);
	}
	
	public static void main4( String[] args ) {
		Model model = FileManager.get().loadModel(FILE_SRC);
		
		OntModel ontologyModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_RULE_INF);
		ontologyModel.add(model);
		
		ontologyModel.write(System.out);
		
		ValidityReport validateReport = ontologyModel.validate();
		log.info("is valid: " + validateReport.isValid());
		log.info("is clean: " + validateReport.isClean());
		Iterator<Report> reports = validateReport.getReports();
		while(reports.hasNext()) {
			Report report = reports.next();
			log.info(report);
		}
	}
	
	public static void main3( String[] args ) {
		String personURI = "http://somewhere/Person";
		String manURI = "http://somewhere/Man";
		String womanURI = "http://somewhere/Woman";
		
		String animalURI = "http://somewhere/Animal";
		String dogURI = "http://somewhere/Dog";
		
		String damianURI = "http://somewhere/damianSoriano";
		String mercedesURI = "http://somewhere/mercedesSarua";
		String androgenoURI = "http://somewhere/androgeno";
		
		String humaURI = "http://somewhere/huma";
		
		String ownsURI = "http://somewhere/owns";
		
		OntModel ontologyModel = ModelFactory.createOntologyModel();
		
		OntClass person = ontologyModel.createClass(personURI);
		OntClass man = ontologyModel.createClass(manURI);
		OntClass woman = ontologyModel.createClass(womanURI);
		person.addSubClass(man);
		person.addSubClass(woman);
		man.addDisjointWith(woman);
		
		OntClass animal = ontologyModel.createClass(animalURI);
		OntClass dog = ontologyModel.createClass(dogURI);
		animal.addSubClass(dog);
		animal.addDisjointWith(person);
		animal.addDisjointWith(man);
		animal.addDisjointWith(woman);
		animal.addSubClass(man);

		ObjectProperty owns = ontologyModel.createObjectProperty(ownsURI);
		
//		AllValuesFromRestriction avf = ontologyModel.createAllValuesFromRestriction(null, owns, animal);

		owns.addDomain(person);
		owns.addRange(animal);
		owns.addLabel("owns", "en");
		
		Individual damian = man.createIndividual(damianURI);
		Individual mercedes = woman.createIndividual(mercedesURI);
		Individual androgeno = man.createIndividual(androgenoURI);
		androgeno.addOntClass(woman);
		androgeno.addOntClass(man);
		Individual huma = dog.createIndividual(humaURI);
		
		mercedes.addProperty(owns, huma);
		mercedes.addProperty(owns, damian);
		
//		log.info("Full Ontology");
//		ontologyModel.write(System.out);
		
		ValidityReport validateReport = ontologyModel.validate();
		log.info("is valid: " + validateReport.isValid());
		log.info("is clean: " + validateReport.isClean());
		Iterator<Report> reports = validateReport.getReports();
		while(reports.hasNext()) {
			Report report = reports.next();
			log.info(report);
		}
	}
	
    public static void main2( String[] args ) {
    	String personURI = "http://somewhere/JohnSmith";
    	String fullName = "John Smith";
    	
    	Model model = ModelFactory.createDefaultModel();
    	
    	Resource johnSmith = model.createResource(personURI);
    	
    	johnSmith.addProperty(VCARD.FN, fullName);
    	
    	model.createResource("http://person/DamianSoriano")
    			.addProperty(VCARD.FN, "Damián Soriano");
    	
    	StmtIterator listStatements = model.listStatements();
    	while (listStatements.hasNext()) {
    		Statement statement = listStatements.next();
    		System.out.println(statement);
    	}

    	System.out.println("OWL");
    	
    	Model owlModel = FileManager.get().loadModel(FILE_SRC);
    	
    	OntModel ontologyModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM_TRANS_INF, owlModel);
    	
    	ResIterator listSubjects = ontologyModel.listSubjects();
    	while (listSubjects.hasNext()) {
    		Resource resource = listSubjects.next();
    		System.out.println(resource);
    	}
    	
//    	listStatements = ontologyModel.listStatements();
//    	while (listStatements.hasNext()) {
//    		Statement statement = listStatements.next();
//    		System.out.println(statement);
//    	}
    	
//    	ontologyModel.write(System.out);
    	
    }
}
