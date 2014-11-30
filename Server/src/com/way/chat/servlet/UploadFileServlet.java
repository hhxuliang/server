package com.way.chat.servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
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
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd/HH/mm-ss-SSS");
	private static final Random random = new Random();

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
		ServletFileUpload upload = new ServletFileUpload(factory);
		upload.setSizeMax(4L * 1024L * 1024L * 1024L);
		String extName = request.getParameter("ExtName");
		if (extName == null)
		{
			extName = request.getHeader("ExtName");
		}
		if (extName == null)
		{
			extName = "";
		}
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
					String urlPath = request.getRequestURL().toString().replace("UploadFile", "");
					String realPath = request.getServletContext().getRealPath("/");
					String fileName = IMAGE_PATH + "/" + getRandFileName(extName);
					File fileTemp = new File(realPath + "/" + fileName);
					if (fileTemp.getParentFile().exists() == false)
					{
						fileTemp.getParentFile().mkdirs();
					}
					item.write(fileTemp);
					pw.write(urlPath + fileName);
					pw.write("\n");
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

	protected String getFileNameExtension(String extName)
	{
		String rtnVal = extName;
		if (rtnVal == null || rtnVal.trim().equals(""))
		{
			rtnVal = ".jpg";
		}
		if (rtnVal.startsWith(".") == false)
		{
			rtnVal = "." + rtnVal;
		}
		return rtnVal;
	}

	protected String getRandFileName(String extName)
	{
		String rtnVal = String.format("%s-%d%s", sdf.format(new Date()), random.nextInt(999), getFileNameExtension(extName));
		return rtnVal;
	}
}
