package ir.secondhand.backend.service;

import ir.secondhand.backend.exception.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

/**
 * ذخیره‌سازی ساده تصاویر آگهی روی سیستم فایل. برای این پروژه آموزشی، سرویس
 * ابری یا آپلود پیشرفته پیاده‌سازی نمی‌شود؛ فقط مسیر نسبی فایل نگه‌داری می‌شود.
 */
@Service
public class ImageStorageService {

    private static final List<String> ALLOWED_EXTENSIONS = List.of("jpg", "jpeg", "png", "webp");

    @Value("${app.upload.dir}")
    private String uploadDir;

    private static final String ADVERTISEMENT_SUBDIR = "advertisements";

    public String store(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("فایل تصویر ارسال نشده است.");
        }

        String originalName = file.getOriginalFilename() == null ? "" : file.getOriginalFilename();
        String extension = getExtension(originalName);
        if (!ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new BadRequestException("فرمت تصویر باید jpg، png یا webp باشد.");
        }

        try {
            Path targetDir = Paths.get(uploadDir, ADVERTISEMENT_SUBDIR);
            Files.createDirectories(targetDir);

            String storedFileName = UUID.randomUUID() + "." + extension;
            Path targetPath = targetDir.resolve(storedFileName);

            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
            }

            return ADVERTISEMENT_SUBDIR + "/" + storedFileName;
        } catch (IOException ex) {
            throw new BadRequestException("ذخیره تصویر با خطا مواجه شد.");
        }
    }

    private String getExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex == -1 || dotIndex == fileName.length() - 1) {
            return "";
        }
        return fileName.substring(dotIndex + 1);
    }
}
