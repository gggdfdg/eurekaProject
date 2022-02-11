package com.ming.eureka.business;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ming.eureka.FileUtil;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.io.FileUtils;
import org.apache.http.util.Asserts;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.AbstractView;

/**
 * 文件下载view
 * 		支持断点续传（请求头包含 Range:bytes=12-34 or Range:bytes=12-）
 * 		model 必须包含 file(File)
 * 		
 * 		TODO:	If-Range：对应响应头ETag的值；
 *				Unless-Modified-Since：对应响应头Last-Modified的值。
 * 		
 * @author lll
 */
@Slf4j
@Component("fileView")
public class FileView extends AbstractView {

	public static final String VIEW_NAME = "fileView";
	
	protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest req,
			HttpServletResponse res) throws Exception {
		Asserts.notNull(model, "Model must not be null or empty.");
		
		Object o = model.get("file");
		if (o == null) {
			res.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		if (!(o instanceof File))
			throw new IllegalArgumentException(
					"Object mapped by \"file\" key  must be a File.");
		File file = (File) o;

		if (!file.isFile()) {
			res.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		// content type
		String contentType = null;
		if (model.containsKey("contentType")) {
			o = model.get("contentType");
			if (o instanceof String)
				contentType = (String) o;
		}

		// content type
		String characterEncoding = null;
		if (model.containsKey("characterEncoding")) {
			o = model.get("characterEncoding");
			if (o instanceof String)
				characterEncoding = (String) o;
		}

		String filename = file.getPath();
		if (null == contentType) {
			if (filename.endsWith(".html"))
				contentType = "text/html; charset=utf-8";
			else if (filename.endsWith(".xml"))
				contentType = "application/xml; charset=utf-8";
			else if (filename.endsWith(".txt") || filename.endsWith(".log")
					|| filename.endsWith(".out"))
				contentType = "text/plain; charset=utf-8";
			else if (filename.indexOf(".log.") > 0)
				contentType = "text/plain; charset=utf-8";
			else if (filename.endsWith(".zip"))
				contentType = "application/x-zip-compressed";
			else
				contentType = this.getServletContext().getMimeType(filename);

			if (contentType == null)
				contentType = "application/octet-stream";
		}

		// ToDo Do I need/want to do this?
		if (characterEncoding == null) {
			if ((!contentType.contains("charset="))
					&& (contentType.startsWith("text/") || contentType
							.startsWith("application/xml"))) {
				characterEncoding = "utf-8";
			}
		}

		// Set content type and character encoding as given/determined.
		res.setContentType(contentType);
		if (characterEncoding != null)
			res.setCharacterEncoding(characterEncoding);

		boolean isRangeRequest = false;
		long startPos = 0, endPos = Integer.MAX_VALUE;
		String rangeRequest = req.getHeader("Range");
		if (rangeRequest != null) { // bytes=12-34 or bytes=12-
			int pos = rangeRequest.indexOf("=");
			if (pos > 0) {
				int pos2 = rangeRequest.indexOf("-");
				if (pos2 > 0) {
					String startString = rangeRequest.substring(pos + 1, pos2);
					String endString = rangeRequest.substring(pos2 + 1);
					startPos = Long.parseLong(startString);
					if (endString.length() > 0)
						endPos = Long.parseLong(endString) + 1;
					isRangeRequest = true;
				}
			}
		}

		// set content length
		long fileSize = file.length();
		long contentLength = fileSize;
		if (isRangeRequest) {
			endPos = Math.min(endPos, fileSize);
			contentLength = endPos - startPos;
		}
		res.setContentLength((int) contentLength);

		// indicate we allow Range Requests
		if (!isRangeRequest)
			res.addHeader("Accept-Ranges", "bytes");

		if (req.getMethod().equals("HEAD")) {
			return;
		}

		try
	    {
	      if ( isRangeRequest )
	      {
	        // set before content is sent
	        res.addHeader( "Content-Range", "bytes " + startPos + "-" + ( endPos - 1 ) + "/" + fileSize );
	        res.setStatus( HttpServletResponse.SC_PARTIAL_CONTENT );
	        
	        byte[] data = FileUtil.readFile(file, startPos, (int)contentLength);
	        res.getOutputStream().write(data);
	        
	      } else {
	    	  FileUtils.copyFile(file, res.getOutputStream());
	      }
	    }
		catch (FileNotFoundException e) {
			log.error("returnFile(): FileNotFoundException= " + filename);
			if (!res.isCommitted())
				res.sendError(HttpServletResponse.SC_NOT_FOUND);
		} catch (java.net.SocketException e) {
			log.info("returnFile(): SocketException sending file: " + filename
					+ " " + e.getMessage());
		} catch (IOException e) {
			String eName = e.getClass().getName(); // dont want compile time
													// dependency on
													// ClientAbortException
			if (eName
					.equals("org.apache.catalina.connector.ClientAbortException")) {
				log.info("returnFile(): ClientAbortException while sending file: "
						+ filename + " " + e.getMessage());
				return;
			}

			log.error("returnFile(): IOException (" + e.getClass().getName()
					+ ") sending file ", e);
			res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					"Problem sending file");
		}
	}

}
