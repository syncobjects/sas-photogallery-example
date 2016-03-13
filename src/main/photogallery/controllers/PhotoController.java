package photogallery.controllers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.syncobjects.as.api.Action;
import com.syncobjects.as.api.ApplicationContext;
import com.syncobjects.as.api.Controller;
import com.syncobjects.as.api.ErrorContext;
import com.syncobjects.as.api.FileUpload;
import com.syncobjects.as.api.Parameter;
import com.syncobjects.as.api.Result;
import com.syncobjects.as.api.ResultFactory;

@Controller(url="/photo/*")
public class PhotoController {
	private static final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	private ApplicationContext application;
	private ErrorContext errors;
	@Parameter
	private String date;
	@Parameter
	private FileUpload photo;
	@Parameter
	private String label;

	@Action
	public Result post() {
		Date d = new Date();
		if(date != null) {
			try { d = sdf.parse(date); }
			catch(ParseException e) {
				errors.put("message", "invalid date format");
			}
		}
		if(photo == null) {
			errors.put("message", "photo is required");
		}
		else {
			//
			// check file's content-type ... just JPEG and PNG images are allowed
			//
			if(!photo.getType().equals("image/jpeg") && !photo.getType().equals("image/png")) {
				errors.put("message", "invalid file format");
				// We still need to delete the file from the tmp folder. 
				// Remember that the file submitted is @Controller's responsibility!
				photo.getFile().delete();
			}
		}
		if(label == null) {
			errors.put("message", "label is required");
		}
		if(errors.size() > 0)
			return ResultFactory.render("/error.ftl");

		String publicFolder = (String)application.get(ApplicationContext.PUBLIC_FOLDER);
		
		File photosFolder = new File(publicFolder, "photos");
		if(!photosFolder.exists())
			photosFolder.mkdirs();
		
		File datedir = new File(photosFolder, sdf.format(d).replaceAll("/", "-"));
		if(!datedir.exists())
			datedir.mkdirs();

		//
		// count number of files existing to this directory
		// create the index for the next file using the #files + 1
		//
		int index = datedir.list().length + 1;

		String fileExtension = photo.getType().equals("image/jpeg") ? ".jpg": ".png";
		
		File destfile = new File(datedir, index+""+fileExtension);
		photo.getFile().renameTo(destfile);

		//
		// create the <index>.label.txt file.
		// this file will hold the label for the referred file.
		//
		File labelFile = new File(datedir, index+".label.txt");
		try {
			FileOutputStream fos = new FileOutputStream(labelFile);
			PrintWriter pw = new PrintWriter(fos);
			pw.write(label);
			pw.close();
			fos.close();
		}
		catch(Exception e) {
			errors.put("message", "failed to write label file: "+labelFile.getAbsolutePath());
			return ResultFactory.render("/error.ftl");
		}

		return ResultFactory.redirect("/main");
	}

	public ApplicationContext getApplication() {
		return application;
	}

	public void setApplication(ApplicationContext application) {
		this.application = application;
	}

	public ErrorContext getErrors() {
		return errors;
	}

	public void setErrors(ErrorContext errors) {
		this.errors = errors;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public FileUpload getPhoto() {
		return photo;
	}

	public void setPhoto(FileUpload photo) {
		this.photo = photo;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
}
