package application;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.NotDirectoryException;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Calendar;
import java.util.LinkedList;

public class SearchEngine 
{
	private Calendar compareDate;
	
	private long yearInMs = Long.valueOf("31536000000");
	
	SearchEngine()
	{
	}
	
	public LinkedList<FileItems> search(Path path, int date)
	{
		compareDate = Calendar.getInstance();	
		compareDate.setTimeInMillis(compareDate.getTimeInMillis() - yearInMs * date);
		
		LinkedList<FileItems> fi = new LinkedList<FileItems>();
		
		DirectoryStream.Filter<Path> how = 
        		new DirectoryStream.Filter<Path>()
        		{
        			public boolean accept(Path filename) throws IOException
        			{
        				if(Files.isReadable(filename))
        					return true;
        		
        				return false;
        			}
        		};
        		
        try(DirectoryStream<Path> dirstrm = Files.newDirectoryStream(path, how))
        {
        	for(Path entry : dirstrm)
        	{
        		BasicFileAttributes attribs = Files.readAttributes(entry, BasicFileAttributes.class);
        		
        		if(attribs.isDirectory())
        		{
        			fi.addAll(search(entry, date));
        		}
        		else
        		{
        			if(attribs.lastModifiedTime().toMillis() < compareDate.getTimeInMillis())
        				fi.add(new FileItems(entry.getName(entry.getNameCount() - 1).toString(), entry.getParent(), 
        					entry.getName(entry.getNameCount() - 1).toString().substring(entry.getName(entry.getNameCount() - 1).toString().lastIndexOf("."), entry.getName(entry.getNameCount() - 1).toString().length()),
        						attribs.lastModifiedTime().toMillis(), attribs.lastAccessTime().toMillis(), attribs.creationTime().toMillis()));
        		}	
        	}
        }
        catch(InvalidPathException e)
        {
        	InfoWindow.showError("Некорректный путь " + e);
        }
        catch(NotDirectoryException e)
        {
        	InfoWindow.showError("Дирректония отсутствует " + e);
        }
        catch(SecurityException e)
        {
        	InfoWindow.showError("Нет доступа к директории " + e);
        }
        catch(IOException e)
        {
        	InfoWindow.showError("Ошибка ввода/вывода " + e);
        }
        catch(Exception e)
        {
        	InfoWindow.showError("Ошибка : " + e);
        }
        
        return fi;
	}
}
