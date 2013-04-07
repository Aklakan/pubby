package de.fuberlin.wiwiss.pubby;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


/**
 * A registry for datasources. Enables referencing custom data sources by URIs:
 * 
 * The dataset will check the registry first before creating a new datasource.
 * Note that these datasources should be thread safe.
 * 
 * @author Claus Stadler <cstadler@informatik.uni-leipzig.de>
 *
 */
public class DataSourceRegistry {
	private Map<String, DataSource> idToDataSource = Collections.synchronizedMap(new HashMap<String, DataSource>());
	
	private Map<String, DataSource> immutIdToDataSource = Collections.unmodifiableMap(idToDataSource); 
	
	/*
	private DataSourceRegistry() {
	}
	*/
	
	public void put(String id, DataSource dataSource) {
		this.idToDataSource.put(id, dataSource);
	}
	
	public DataSource get(String id) {
		return idToDataSource.get(id);
	}
	
	public Map<String, DataSource> getMap() {
		return immutIdToDataSource;
	}
	
	private static DataSourceRegistry instance = null;
	
	public static DataSourceRegistry getInstance() {
		if(instance == null) {
			synchronized(DataSourceRegistry.class) {
				if(instance == null) {
					instance = new DataSourceRegistry();
				}
			}
		}
		
		return instance;
	}
}
