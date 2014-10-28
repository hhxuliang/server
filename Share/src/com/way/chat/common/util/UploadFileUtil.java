package com.way.chat.common.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.UUID;

public class UploadFileUtil
{
	private static final int BUFFER_SIZE = 4 * 1024;
	private static final String BOUNDARY = UUID.randomUUID().toString();
	private static final String BEGIN = "--";
	private static final String LINE_END = "\r\n";
	private static final String CONTENT_TYPE = "multipart/form-data";
	private static final String END = LINE_END + BEGIN + BOUNDARY + BEGIN + LINE_END;

	public String doUploadFile(String fileName)
	{
		String rtnVal = "";
		File fileTemp = new File(fileName);
		if (fileTemp.exists())
		{
			try
			{
				URL url = new URL(Constants.FILE_UPLOAD_URL);
				URLConnection urlConn = url.openConnection();
				try
				{
					if (HttpURLConnection.class.isAssignableFrom(urlConn.getClass()))
					{
						HttpURLConnection httpUrlConn = (HttpURLConnection) urlConn;
						httpUrlConn.setDoInput(true);
						httpUrlConn.setDoOutput(true);
						httpUrlConn.setUseCaches(false);
						httpUrlConn.setRequestMethod("POST");
						httpUrlConn.setRequestProperty("Connection", "Keep-Alive");
						httpUrlConn.setRequestProperty("Charset", "UTF-8");
						httpUrlConn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);

						StringBuffer sb = new StringBuffer();
						sb.append(BEGIN).append(BOUNDARY).append(LINE_END);
						sb.append("Content-Disposition:form-data; name=\"" + fileName + "\"; filename=\"" + fileName + "\"" + LINE_END);
						sb.append("Content-Type:image/pjpeg" + LINE_END);
						sb.append(LINE_END);
						byte bytes[] = new byte[BUFFER_SIZE];
						try
						{
							OutputStream os = httpUrlConn.getOutputStream();
							os.write(sb.toString().getBytes());

							FileInputStream fis = new FileInputStream(fileTemp);
							while (true)
							{
								int iRead = fis.read(bytes);
								os.write(bytes, 0, iRead);
								if (iRead < BUFFER_SIZE)
								{
									break;
								}
							}
							os.write(END.getBytes());
							os.flush();
							os.close();
							fis.close();
						}
						catch (Exception ex)
						{
							ex.printStackTrace();
						}
						try
						{
							if (httpUrlConn.getResponseCode() == 200)
							{
								ByteArrayOutputStream baos = new ByteArrayOutputStream();
								InputStream is = httpUrlConn.getInputStream();
								while (true)
								{
									int iRead = is.read(bytes);
									baos.write(bytes, 0, iRead);
									if (iRead < BUFFER_SIZE)
									{
										break;
									}
								}
								is.close();
								rtnVal = baos.toString();
							}
						}
						catch (Exception ex)
						{
							ex.printStackTrace();
						}
					}
				}
				catch (Exception ex)
				{
					ex.printStackTrace();
				}
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
		}
		return rtnVal;
	}

	public static void main(String[] args)
	{
		UploadFileUtil ufu = new UploadFileUtil();
		String sss = ufu.doUploadFile("F:/08ÕÕÆ¬/IMG_8839.jpg");
		System.out.println(sss);
	}
}
