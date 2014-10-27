package com.way.chat.servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

public class UploadFileServlet extends HttpServlet
{
	private static final long serialVersionUID = 1L;

	public static final String IMAGE_PATH = "images";

	public UploadFileServlet()
	{
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		DiskFileItemFactory factory = new DiskFileItemFactory();
		factory.setSizeThreshold(4 * 1024);
		ServletFileUpload upload = new ServletFileUpload(factory);
		upload.setSizeMax(4 * 1024 * 1024);

		response.setContentType("text/plain;charset=utf-8");
		PrintWriter pw = response.getWriter();
		try
		{
			List<?> fileItems = upload.parseRequest(request);
			Iterator<?> iter = fileItems.iterator();
			while (iter.hasNext())
			{
				FileItem item = (FileItem) iter.next();
				if (item.isFormField())
				{
					continue;
				}
				try
				{
					File fName = new File(getWebappsPath() + "/" + IMAGE_PATH + "/" + getRandFileName(""));
					if (fName.getParentFile().exists() == false)
					{
						fName.getParentFile().mkdirs();
					}
					item.write(fName);
				}
				catch (Exception ex)
				{
					ex.printStackTrace();
				}
			}
		}
		catch (FileUploadException ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			pw.close();
		}
	}

	protected String getWebappsPath()
	{
		String rtn = System.getProperty("catalina.base");
		return rtn;
	}

	protected String getFileNameExtension(String fileName)
	{
		String rtn = "jpg";
		int pos = fileName.lastIndexOf('.');
		if (pos > 0)
		{
			rtn = fileName.substring(pos + 1);
		}
		return rtn;
	}

	protected String getRandFileName(String fileName)
	{
		String name = "";
		Random rand = new Random(999);
		int val = Math.abs(rand.nextInt()) % 1000;
		name = "" + System.currentTimeMillis() + "_" + val + "." + getFileNameExtension(fileName);
		return name;
	}
}
