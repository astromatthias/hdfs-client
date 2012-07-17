package org.fiz.hoop.main;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

import org.apache.hadoop.fs.Path;

public class MainClient {

	/**
	 * Main class to run the Hoop client as  java app. 
	 * 
	 * @author mhn 
	 * @param input: hdfs.properties file (contains hdfs.uri und hdfs.dest und hdfs.operation (ingest, retrieve)  and file to copy
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException, IOException {
		
		
		if (args.length == 2) { 
			
		// Get HDFS configuration: Please move this out of the class ! 
		String propertiesFilename = args[0]; 
		String files = args[1]; 

		// Configuration 
		Properties prop = properties(propertiesFilename); 
		String uri = prop.getProperty("hdfs.uri"); 
		Path dst = new Path(prop.getProperty("hdfs.dest"));
		String resultFilename = prop.getProperty("result.filename"); 
		String fileOperation = prop.getProperty("hdfs.operation");
		short replication = Short.valueOf(prop.getProperty("hdfs.replication")); 
		
		
		Long startTime1 = null; 
		Long endTime1 = null;
		Long operationTime = null; 
		Long startTime = System.currentTimeMillis();
		
		// HDFS operation
		HDFSConnector hdfs = new HDFSConnector(uri);
		
    	BufferedReader input =  new BufferedReader(new FileReader(files));
    	
    	try { 
    	String line = null;
		
		while (( line = input.readLine()) != null) { 
			Path src = new Path(line);
			
			if (fileOperation.equalsIgnoreCase("ingest")) { 
				
				System.out.println("INGEST " + line + " to " + dst);
				 
				operationTime = hdfs.ingest(src, dst, replication);
				
			} else if (fileOperation.equalsIgnoreCase("retrieve") || fileOperation.equalsIgnoreCase("get")) { 
				
				System.out.println("RETRIEVE " + src+ " to " + dst);
				startTime1 = System.currentTimeMillis();
				hdfs.get(src, dst);
				endTime1 = System.currentTimeMillis();
				operationTime = endTime1 - startTime1;
				
			}
			
			
				
			Long endTime = System.currentTimeMillis();
			Long totalTime = endTime - startTime; 
			 
			
			// Output the result to a file
			print(totalTime, operationTime, resultFilename);
		} 
		
		} finally { 
			input.close(); 
		}
		
		hdfs.close();
		
		
		} else { 
			System.out.println("Please provide two parameters: first: " +
					"the properties file with the properties " +
					"hdfs.uri and " +
					"hdfs.dest " +
					"result filename " +
					"hdfs.operation" +
					"hdfs.replication " +
					"and the inputifles as a list");			
		}
		
		
	}
	
	/**
	 * print the result in a file
	 * @throws IOException 
	 */
	private static void print(Long totalTime, Long copyTime, String resultFilename) throws IOException { 
		String result = "201,"+totalTime.toString() + "," + copyTime.toString() + "\n";
		//System.out.println(result);
		FileOutputStream fos = new FileOutputStream( resultFilename,true );
	    fos.write( result.getBytes() );
	    fos.close();

	}
	
	
	/**
	 * get the time in milliseconds 
	 * @return
	 */
	private static Long mytime() { 
		Date date = new Date(); 
		Long time = date.getTime();
		return time; 
		
	}
	
	/**
	 * read the Properties file, containg the hdfs connection details. 
	 * @param propertiesFilename
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static Properties properties(String propertiesFilename) throws FileNotFoundException, IOException { 
		
		Properties prop = new Properties(); 
		prop.load(new FileInputStream(propertiesFilename)); 
		
		return prop;
		
	}

}
