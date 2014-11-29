package com.way.chat.common.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.Serializable;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class UploadFileByHttpClient implements Serializable
{
	/** */
	private static final long serialVersionUID = -6151358452387434841L;
	private static final int BUFFER_SIZE = 4 * 1024;

	public String doUploadFile(String fileName, String extName)
	{
		String rtnVal = "";
		File fileTemp = new File(fileName);
		if (fileTemp.exists())
		{
			try
			{
				CloseableHttpClient client = HttpClients.createDefault();
				try
				{
					HttpPost post = new HttpPost(Constants.FILE_UPLOAD_URL);
					MultipartEntityBuilder meb = MultipartEntityBuilder.create();
					meb.addTextBody("Connection", "Keep-Alive");
					meb.addTextBody("Charset", "UTF-8");
					meb.addBinaryBody(fileName, fileTemp);
					post.setEntity(meb.build());
					post.setHeader("ExtName", extName);
					CloseableHttpResponse response = client.execute(post);
					if (response.getStatusLine().getStatusCode() == 200)
					{
						byte bytes[] = new byte[BUFFER_SIZE];
						ByteArrayOutputStream baos = new ByteArrayOutputStream();
						InputStream is = response.getEntity().getContent();
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
					response.close();
				}
				catch (Exception ex)
				{
					ex.printStackTrace();
				}
				client.close();
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
		UploadFileByHttpClient ufu = new UploadFileByHttpClient();
		String sss = ufu.doUploadFile("F:/08ÕÕÆ¬/IMG_8839.jpg", ".jpg");
		System.out.println(sss);
	}

}
