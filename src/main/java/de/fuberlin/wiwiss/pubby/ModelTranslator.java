package de.fuberlin.wiwiss.pubby;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;

/**
 * Translates an RDF model from the dataset into one fit for publication
 * on the server by replacing URIs, adding the correct prefixes etc. 
 * 
 * @author Richard Cyganiak (richard@cyganiak.de)
 * @version $Id$
 */
public class ModelTranslator {
	private final Model model;
	private final Dataset dataset;
	private final Configuration serverConfig;
	
	public ModelTranslator(Model model, Dataset dataset, Configuration configuration) {
		this.model = model;
		this.dataset = dataset;
		this.serverConfig = configuration;
	}
	
	public Model getTranslated() {
		Model result = ModelFactory.createDefaultModel();
		result.setNsPrefixes(model);
		for (String prefix: serverConfig.getPrefixes().getNsPrefixMap().keySet()) {
			result.setNsPrefix(prefix, serverConfig.getPrefixes().getNsPrefixURI(prefix));
		}
		StmtIterator it = model.listStatements();
		while (it.hasNext()) {
			Statement stmt = it.nextStatement();
			Resource s = stmt.getSubject();
			if (s.isURIResource()) {
				s = result.createResource(getPublicURI(s.getURI()));
			}
			Property p = result.createProperty(
					getPublicURI(stmt.getPredicate().getURI()));
			RDFNode o = stmt.getObject();
			if (o.isURIResource()) {
				o = result.createResource(
						getPublicURI(((Resource) o).getURI()));
			}
			result.add(s, p, o);
		}
		return result;
	}
	
	private String getPublicURI(String datasetURI) {
		MappedResource resource = dataset.getMappedResourceFromDatasetURI(datasetURI, serverConfig);
		if (resource == null) return datasetURI;
		return resource.getController().getAbsoluteIRI();
	}
}
