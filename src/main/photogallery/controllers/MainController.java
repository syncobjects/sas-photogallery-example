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

import com.syncobjects.as.api.Action;
import com.syncobjects.as.api.ApplicationContext;
import com.syncobjects.as.api.Controller;
import com.syncobjects.as.api.Parameter;
import com.syncobjects.as.api.Result;
import com.syncobjects.as.api.ResultFactory;

import photogallery.Photo;

@Controller(url="/*")
public class MainController {
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
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		today = sdf.format(new Date());
		
		String publicFolder = (String)application.get(ApplicationContext.PUBLIC_FOLDER);
		File photosFolder = new File(publicFolder, "photos");
		if(photosFolder.exists()) {
			String dates[] = photosFolder.list(new FilenameFilter() {
				public boolean accept(File file, String name) {
					if(file.isDirectory() && name.contains("-"))
						return true;
					return false;
				}
			});
			
			for(String date: dates) {
				String datekey = date.replaceAll("-", "/");
				
				this.dates.add(datekey);
				
				// now check the photos...
				File dateFolder = new File(photosFolder, date);
				String files[] = dateFolder.list(new FilenameFilter() {
					public boolean accept(File file, String name) {
						if(name.endsWith(".jpg") || name.endsWith(".png"))
							return true;
						return false;
					}
				});
				
				for(String f: files) {
					Photo photo = new Photo();
					photo.setLabel("unknown");
					photo.setPath("/photos/"+date+"/"+f);
					
					String index = f.substring(0, f.length() - ".png".length());
					
					File labelFile = new File(dateFolder, index + ".label.txt");
					if(labelFile.exists()) {
						try {
							List<String> lines = Files.readAllLines(Paths.get(labelFile.getAbsolutePath()));
							if(lines.size() > 0)
								photo.setLabel(lines.get(0));
						}
						catch(Exception ignore) {}
					}

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
