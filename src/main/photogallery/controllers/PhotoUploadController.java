package photogallery.controllers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.syncframework.api.Action;
import io.syncframework.api.ApplicationContext;
import io.syncframework.api.Controller;
import io.syncframework.api.ErrorContext;
import io.syncframework.api.FileUpload;
import io.syncframework.api.Parameter;
import io.syncframework.api.Result;
import io.syncframework.api.ResultFactory;

@Controller(url="/photoupload")
public class PhotoUploadController {
	private static final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	private ApplicationContext application;
	private ErrorContext errors;
	/**
	 * date from the form
	 */
	@Parameter
	private String date;
	/**
	 * receive the photo uploaded by the user
	 */
	@Parameter
	private FileUpload photo;
	/**
	 * label from the form
	 */
	@Parameter
	private String label;

	/**
	 * This is our @Action to handle the photoupload.
	 * @return redirect to /main
	 */
	@Action
	public Result photoupload() {
		// validates the form
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

		// retrieves the public folder
		String publicFolder = (String)application.get(ApplicationContext.PUBLIC_FOLDER);
		
		// creates the public/photos folder if does not exist
		File photosFolder = new File(publicFolder, "photos");
		if(!photosFolder.exists())
			photosFolder.mkdirs();
		
		// creates the public/photos/<date> folder if does not exist. Note that the from treats the date
		// as dd/MM/yyy, so we replace it to dd-MM-yyyy.
		File datedir = new File(photosFolder, sdf.format(d).replaceAll("/", "-"));
		if(!datedir.exists())
			datedir.mkdirs();

		//
		// count number of image files existing to this directory.
		// create the index for the next file using the #files + 1
		// remember that since we have the .txt files to hold the metadata... only the images
		// is what we shall consider for this count.
		//
		int index = datedir.list(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				if(name.endsWith(".jpg") || name.endsWith(".png"))
					return true;
				return false;
			}
		}).length + 1;

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
