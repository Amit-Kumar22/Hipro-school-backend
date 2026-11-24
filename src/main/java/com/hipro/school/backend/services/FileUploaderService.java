package com.hipro.school.backend.services;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.hipro.school.backend.util.Utils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class FileUploaderService {
	@Value("${file.upload-dir}")
	private String BASE_UPLOAD_DIR;
	
	@Value("${file.public-url}")
	private String BASE_PUBLIC_URL;
	
	public String uploadFile(MultipartFile file, String folderName) throws IOException {
		if (file == null || file.isEmpty()) {
			throw new IllegalArgumentException("File is empty or null");
		}

		String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
		if (originalFilename.contains("..")) {
			throw new IllegalArgumentException("Invalid file name: " + originalFilename);
		}
		// Create unique file name
		String cleanedFilename = originalFilename.replaceAll("\\s+", "-"); // replaces all spaces
		String uniqueFilename = Utils.generateRandomString(12) + "_" + cleanedFilename;

		// Target path like: /var/www/travel-portal/images/
		Path targetDirectory = Paths.get(BASE_UPLOAD_DIR, folderName).normalize();
		Files.createDirectories(targetDirectory); // creates if not exists

		// Final path
		Path destinationPath = targetDirectory.resolve(uniqueFilename);
		try (InputStream inputStream = file.getInputStream()) {
			Files.copy(inputStream, destinationPath, StandardCopyOption.REPLACE_EXISTING);
		}

		// Public URL: http://ip/files/images/unique_file.jpg
		String publicUrl = BASE_PUBLIC_URL + folderName + "/" + uniqueFilename;
		log.info("✅ File uploaded to folder [{}]: {}", folderName, publicUrl);

		return publicUrl;
	}
	
	public boolean deleteFile(String publicUrl) {
	    if (publicUrl == null || !publicUrl.startsWith(BASE_PUBLIC_URL)) {
	        log.warn("⚠️ Cannot delete file. Invalid or external URL: {}", publicUrl);
	        return false;
	    }

	    try {
	        String relativePath = publicUrl.substring(BASE_PUBLIC_URL.length());

	        // Fix: ensure no leading slash
	        if (relativePath.startsWith("/")) {
	            relativePath = relativePath.substring(1);
	        }

	        Path filePath = Paths.get(BASE_UPLOAD_DIR, relativePath).normalize();

	        log.info("🛠️ Resolving delete request. \nPublic URL: {} \nRelative Path: {} \nFull Path: {}",
	                publicUrl, relativePath, filePath.toAbsolutePath());

	        if (Files.exists(filePath)) {
	            Files.delete(filePath);
	            log.info("🗑️ Successfully deleted file: {}", filePath.toAbsolutePath());
	            return true;
	        } else {
	            log.warn("⚠️ File not found, nothing to delete: {}", filePath.toAbsolutePath());
	            return false;
	        }
	    } catch (Exception e) {
	        log.error("❌ Failed to delete file for URL: {}", publicUrl, e);
	        return false;
	    }
	}

	
	public boolean deleteFile3(String publicUrl) {
	    if (publicUrl == null || !publicUrl.startsWith(BASE_PUBLIC_URL)) {
	        log.warn("⚠️ Cannot delete file. Invalid or external URL: {}", publicUrl);
	        return false;
	    }

	    try {
	        // Extract relative path
	        String relativePath = publicUrl.substring(BASE_PUBLIC_URL.length());
	        Path filePath = Paths.get(BASE_UPLOAD_DIR, relativePath).normalize();

	        log.info("🛠️ Resolving delete request. \nPublic URL: {} \nRelative Path: {} \nFull Path: {}",
	                publicUrl, relativePath, filePath.toAbsolutePath());

	        // Check before delete
	        boolean existsBefore = Files.exists(filePath);
	        log.debug("📂 File exists before delete? {}", existsBefore);

	        if (existsBefore) {
	            Files.delete(filePath);
	            boolean existsAfter = Files.exists(filePath);
	            if (!existsAfter) {
	                log.info("🗑️ Successfully deleted file: {}", filePath.toAbsolutePath());
	                return true;
	            } else {
	                log.error("❌ Tried to delete file, but it still exists: {}", filePath.toAbsolutePath());
	                return false;
	            }
	        } else {
	            log.warn("⚠️ File not found, nothing to delete: {}", filePath.toAbsolutePath());
	            return false;
	        }
	    } catch (Exception e) {
	        log.error("❌ Failed to delete file for URL: {}", publicUrl, e);
	        return false;
	    }
	}



	public boolean deleteFile2(String publicUrl) {
		if (publicUrl == null || !publicUrl.startsWith(BASE_PUBLIC_URL)) {
			log.warn("⚠️ Cannot delete file. Invalid or external URL: {}", publicUrl);
			return false;
		}

		// Extract path after base public URL
		String relativePath = publicUrl.replace(BASE_PUBLIC_URL, "");
		Path filePath = Paths.get(BASE_UPLOAD_DIR, relativePath).normalize();

		try {
			Files.deleteIfExists(filePath);
			log.info("🗑️ Deleted file: {}", filePath);
			return true;
		} catch (IOException e) {
			log.error("❌ Failed to delete file: {}", filePath, e);
			return false;
		}
	}

}
