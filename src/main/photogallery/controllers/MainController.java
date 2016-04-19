package photogallery.controllers;

import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import io.syncframework.api.Action;
import io.syncframework.api.ApplicationContext;
import io.syncframework.api.Controller;
import io.syncframework.api.Parameter;
import io.syncframework.api.Result;
import io.syncframework.api.ResultFactory;
import photogallery.Photo;

@Controller(url="/*")
public class MainController {
	private static final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	private ApplicationContext application;
	@Parameter
	private List<String> dates;
	@Parameter
	private Map<String, List<Photo>> photos = new HashMap<String, List<Photo>>();
	@Parameter
	private String today;
	
	@Action
	public Result main() {
		dates = new LinkedList<String>();
		
		today = sdf.format(new Date());
		
		// obtains the @Application public folder.
		String publicFolder = (String)application.get(ApplicationContext.PUBLIC_FOLDER);
		
		// public/photos
		File photosFolder = new File(publicFolder, "photos");
		
		// check if exists the folder. If true, we need then look for photos
		if(photosFolder.exists()) {
		
			// photos are organized as:
			// public/photos/dd-mm-yyyy/#index.png or #index.jpg;
			// public/photos/dd-mm-yyyy/#index.label.txt;
			
			// reads the public/photos/&lt;date> directory
			String dates[] = photosFolder.list(new FilenameFilter() {
				public boolean accept(File file, String name) {
					if(file.isDirectory() && name.contains("-"))
						return true;
					return false;
				}
			});
			for(String date: dates) {
				//
				// replace the date from dd-MM-yyyy format to dd/MM/yyyy
				// uses the date dd/MM/yyyy as key... this will be used later on the template.
				//
				String datekey = date.replaceAll("-", "/");
				
				// stores the date in the dates list.
				this.dates.add(datekey);
				
				// now check the photos...
				File dateFolder = new File(photosFolder, date);
				String files[] = dateFolder.list(new FilenameFilter() {
					public boolean accept(File file, String name) {
						// only files with .jpg or .png
						if(name.endsWith(".jpg") || name.endsWith(".png"))
							return true;
						return false;
					}
				});
				for(String f: files) {
					// create the Photo object
					Photo photo = new Photo();
					photo.setLabel("unknown");
					photo.setPath("/photos/"+date+"/"+f); // path is relative to web path... so why / and not File.separator
					
					String index = null;
					if(f.endsWith(".jpg"))
						index = f.substring(0, f.length() - ".jpg".length()); // remove the suffix ".jpg"
					else
						index = f.substring(0, f.length() - ".png".length()); // remove the suffix ".png"
					
					// check for the metadata file... index.label.txt
					File labelFile = new File(dateFolder, index + ".label.txt");
					if(labelFile.exists()) {
						try {
							List<String> lines = Files.readAllLines(Paths.get(labelFile.getAbsolutePath()));
							if(lines.size() > 0)
								photo.setLabel(lines.get(0)); // set the label
						}
						catch(Exception ignore) {}
					}

					// add the Photos to the photos 
					List<Photo> list = photos.get(datekey);
					if(list == null)
						list = new LinkedList<Photo>();
					list.add(photo);
					photos.put(datekey, list);
				}
			}
		}
		
		return ResultFactory.render("/main.ftl");
	}

	public ApplicationContext getApplication() {
		return application;
	}

	public void setApplication(ApplicationContext application) {
		this.application = application;
	}

	public List<String> getDates() {
		return dates;
	}

	public void setDates(List<String> dates) {
		this.dates = dates;
	}

	public Map<String, List<Photo>> getPhotos() {
		return photos;
	}

	public void setPhotos(Map<String, List<Photo>> photos) {
		this.photos = photos;
	}

	public String getToday() {
		return today;
	}

	public void setToday(String today) {
		this.today = today;
	}
}