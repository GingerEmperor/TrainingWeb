// package org.example.services;
//
// import java.io.File;
// import java.io.FileWriter;
// import java.io.IOException;
// import java.util.List;
//
// import org.example.models.muscles.Muscle;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.stereotype.Service;
//
// import lombok.extern.slf4j.Slf4j;
//
// @Slf4j
// @Service
// public class CSVService {
//
//     @Value("${upload.csvPath}")
//     private String csvPath;
//
//     public File csvFile(String fileName){
//         File file = new File(csvPath+"/"+fileName);
//         return file;
//     }
//
//     public void writeToCsvFile(List<String[]> thingsToWrite, String separator) {
//         String fileName=csvFile("csvfile.csv").getName();
//         try (FileWriter writer = new FileWriter(fileName)) {
//             for (String[] strings : thingsToWrite) {
//                 for (int i = 0; i < strings.length; i++) {
//                     writer.append(strings[i]);
//                     if (i < (strings.length - 1)) {
//                         writer.append(separator);
//                     }
//                 }
//                 writer.append(System.lineSeparator());
//             }
//             writer.flush();
//         } catch (IOException e) {
//             e.printStackTrace();
//         }
//     }
//
//     public void wrightAllDataToConsole(List<Muscle> muscles){
//         muscles.forEach(muscle -> log.info(muscle.toString()));
//         log.info("innnnfffooo");
//
//     }
// }
