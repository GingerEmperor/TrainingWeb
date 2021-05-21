package org.example.services;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.example.exeptions.CanNotAccessException;
import org.example.exeptions.FileCanNotSaveException;
import org.example.exeptions.InvalidNameException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class GlobalService {

    public String saveImgToPathWithPrefixName(
            MultipartFile file,
            String path,
            String prefixName
    ) throws IOException, FileCanNotSaveException {
        if (file != null && !file.getOriginalFilename().isEmpty()) {
            File uploadDir = new File(path);

            if (!uploadDir.exists()) {
                uploadDir.mkdir();
            }
            final String uuidFile = UUID.randomUUID().toString();
            final String resultFileName = prefixName + "-" + uuidFile + "." + file.getOriginalFilename();

            file.transferTo(new File(path + "/" + resultFileName));

            return resultFileName;
        }
        throw new FileCanNotSaveException();
    }

    public boolean checkIfNameIsValid(String name) {
        if (name != null) {
            name = name.trim();
            if (!name.isEmpty()) {
                return true;
            }
        }

        throw new InvalidNameException("Не валидное имя");
    }

    public boolean checkAccessByUsername(String username) {
        if (SecurityContextHolder.getContext().getAuthentication().getName().equals(username)) {
            return true;
        }
        throw new CanNotAccessException("Could not access with this Username");
    }
}
