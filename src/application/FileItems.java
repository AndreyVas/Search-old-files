package application;

import java.nio.file.Path;

public class FileItems 
{
	private String name;
	private Path path;
	private String ext;
	private long lastModifiedDate;
	private long lastAccessDate;
	private long creationDate;
	
	FileItems()
	{
		
	}
	
	FileItems(String name, Path path, String ext, long lastModifiedDate, long lastAccessDate, long creationDate)
	{
		this.name = name;
		this.path = path;
		this.ext = ext;
		this.lastAccessDate = lastAccessDate;
		this.lastModifiedDate = lastModifiedDate;
		this.creationDate = creationDate;
	}
	
	public long getLastAccessDate()
	{
		return this.lastAccessDate;
	}
	
	public long getLastModifiedDate()
	{
		return this.lastModifiedDate;
	}
	
	public long getCreationDate()
	{
		return this.creationDate;
	}
	
	public Path getPath()
	{
		return path;
	}
	
	public String getName()
	{
		return name;
	}
	
	public String getExt()
	{
		return ext;
	}
}
