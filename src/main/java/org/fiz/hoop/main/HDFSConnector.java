package org.fiz.hoop.main;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;


/**
 * This is a simple HDFs Connector to cop and retrieve files from HDFS
 * @author mhn
 *
 */
public class HDFSConnector {
	
	private FileSystem fs; 
	private Configuration conf;
	
	public HDFSConnector(String uri) { 
		
		try {
			
			conf = new Configuration(); 
			fs = FileSystem.get(new URI(uri), conf);
			
			/* Path dstfile = new Path(dst +"/plan_overview.html" );
			FileStatus status = fs.getFileStatus(dstfile);
			System.out.println("Replication: " + status.getReplication());
			System.out.println("Blocksize: " + status.getBlockSize());
			*/
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	/**
	 * Copy a file to HDFS
	 * @param src
	 * @param dst
	 * @throws IOException
	 */
	public Long ingest(Path src, Path dst, short replication) throws IOException { 
		
		// Set the replication factor
		fs.setReplication(src, replication);
		String filename = src.getName(); 
		String filenames[] = filename.split("/");
		String xcv = dst.toString() + "/" + filenames[filenames.length-1];
		Path finaldst = new Path(xcv);
		
		//start copying the file
		Long starttime = System.currentTimeMillis();
		fs.copyFromLocalFile(src,finaldst); 
		Long endtime = System.currentTimeMillis();
		
		FileStatus status = fs.getFileStatus(finaldst);
		System.out.println("Replication: " + status.getReplication());
		System.out.println("Blocksize: " + status.getBlockSize());
		
		return endtime-starttime; 
		
			
	}
	
	/**
	 * retrieve a file from HDFS
	 * @param src
	 * @param dst
	 * @throws IOException
	 */
	public void get(Path src, Path dst) throws IOException { 
		
		fs.copyToLocalFile(src, dst); 
		
	}
	
	
	public void close() throws IOException { 
		fs.closeAll(); 
	}
	

}
