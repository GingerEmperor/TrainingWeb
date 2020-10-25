package org.example.services;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.example.exeptions.FileCanNotSaveException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class GlobalService {

    public String saveImgToPathWithPrefixName(
            MultipartFile file,
            String path,
            String prefixName
    ) throws IOException {
        if(file!=null && !file.getOriginalFilename().isEmpty()) {
            File uploadDir = new File(path);

            if (!uploadDir.exists()) {
                uploadDir.mkdir();
            }
            final String uuidFile = UUID.randomUUID().toString();
            final String resultFileName =prefixName+"-"+ uuidFile + "." + file.getOriginalFilename();

            file.transferTo(new File(path + "/" + resultFileName));

            return resultFileName;
        }
        throw new FileCanNotSaveException();
    }

    public int compareStringsByFirstCharacter(String s1,String s2){
        return s1.toLowerCase().charAt(0)-s2.toLowerCase().charAt(0);
    }
}
