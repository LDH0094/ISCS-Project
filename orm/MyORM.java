package orm;

import annotations.Column;
import annotations.Entity;
import annotations.MappedClass;
import dao.BasicMapper;
import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;
import io.github.lukehutch.fastclasspathscanner.scanner.ScanResult;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;

public class MyORM 
{	
	
	HashMap<Class, Class> entityToMapperMap = new HashMap<Class, Class>();
	
	
	public void init() throws Exception
	{
		// scan all mappers -- @MappedClass
		scanMappers();		
		
		// scan all the entities -- @Entity
		scanEntities();
				
		// create all entity tables
		createTables();

	}


	private void scanMappers() throws ClassNotFoundException 
	{

		// use FastClasspathScanner to scan the dao package for @MappedClass
		ScanResult scanResult = new FastClasspathScanner("dao").scan();
		// check if the clazz has the @Entity annotation
		// this returns List<String>
		List<String> results = scanResult.getNamesOfClassesWithAnnotation(MappedClass.class);

		// iterate over to results
		for (String result : results) {
			Class aClass = Class.forName(result);
			MappedClass mappedClass = (MappedClass) aClass.getAnnotation(MappedClass.class);

			if (mappedClass != null) {
				Class clazz = mappedClass.clazz();
				entityToMapperMap.put(clazz, aClass);

				// if annotation is present on this...
				if (clazz.isAnnotationPresent(Entity.class)) {
					// map the clazz to the mapper class
					BasicMapper<?> mapper = (BasicMapper<?>) getMapper(clazz);
					mapper.createTable();
				} else {
					// if not throw new RuntimeException("No @Entity")
					throw new RuntimeException("No @Entity");
				}

			}

		}

	}
	

	private void scanEntities() throws ClassNotFoundException 
	{
		// use FastClasspathScanner to scan the entity package for @Entity
		ScanResult scanResult = new FastClasspathScanner("entity").scan();

		List<String> results = scanResult.getNamesOfClassesWithAnnotation(Entity.class);

		// iterate over to results
		for (String result : results) {
			Class aClass = Class.forName(result);
			Entity entity = (Entity) aClass.getAnnotation(Entity.class);

			int id = 0;
			// go through each of the fields
			for (Field f : aClass.getDeclaredFields()) {
				Column columnField = f.getAnnotation(Column.class);

				if (columnField != null) {
					if (columnField.id()) {
						id++;
					}
					// check if there is only 1 field with a Column id attribute
					// if more than one field has id throw new RuntimeException("duplicate id=true")
					if (id > 1) {
						throw new RuntimeException("duplicate id=true");
					}
				}
			}

		}


		
		
	}
	
	
	public Object getMapper(Class clazz)
	{
		// create the proxy object for the mapper class supplied in clazz parameter
		// all proxies will use the supplied DaoInvocationHandler as the InvocationHandler

		return null;
	}
	

	private void createTables()
	{
		// go through all the Mapper classes in the map
			// create a proxy instance for each
			// all these proxies can be casted to BasicMapper
			// run the createTable() method on each of the proxies
		
	}

	

	
	
}
