package com.knowgate.io;

/**
 * This file is licensed under the Apache License version 2.0.
 * You may not use this file except in compliance with the license.
 * You may obtain a copy of the License at:
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.
 */

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Helper class for MIME Types
 * @author Sergio Montoro
 * @version 9.0
 */
public class MimeUtils {

	private static final Map<String,String> extToMime;
	
	private static final Map<String,String> mimeToExt;

	static {
		extToMime = new HashMap<String,String>(2617);
		mimeToExt = new HashMap<String,String>(2617);
		try (InputStream mimeTypes = MimeUtils.class.getResourceAsStream("mimetypes.csv")) {
			for (String line : IOUtils.readLines(mimeTypes)) {
				final String[] pair = line.split(",");
				extToMime.put(pair[0], pair[1]);
				mimeToExt.put(pair[1], pair[0]);
			}
		} catch (IOException neverthrown) { }
	}

	/**
	 * Get MIME Type for a file extension
	 * @param fileExtension String
	 * @return String MIME type or null if no MIME type was found for given file extension
	 */
	public static String getMimeTypeForFileExtension(final String fileExtension) {
		return null==fileExtension || fileExtension.length()==0 ? null : extToMime.get(fileExtension.toLowerCase().replace('.', ' ').trim());
	}

	/**
	 * Get file extension for a MIME Type
	 * @param fileExtension String
	 * @return String MIME type or null if no MIME type was found for given file extension
	 */
	public static String getFileExtensionForMimeType(final String mimeType) {
		return null==mimeType || mimeType.length()==0 ? null : mimeToExt.get(mimeType.toLowerCase().trim());
	}

}
