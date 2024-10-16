package com.epam.homework;

import com.epam.homework.config.AppConfig;
import com.epam.homework.model.File;
import com.epam.homework.repository.DatabaseInit;
import com.epam.homework.repository.FileRepository;
import com.epam.homework.service.FileService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;

public class App {
    final static long MAX_SIZE = 200 * 1024 * 1024;

    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        var databaseInit = context.getBean(DatabaseInit.class);

        databaseInit.createTable();

        var filePath = "file.txt";
        var file = createFileObject(filePath);

        File fileWithId = upload(file, context);
        System.out.printf("Generated file with name %s and id %d\n", fileWithId.name(), fileWithId.id());


        File valueRetrieved = getFileById(fileWithId.id().longValue(), context);
        System.out.printf("Successfully retrieved file with id %d and name [%s]\n",
                valueRetrieved.id(), valueRetrieved.name());

        try {
            var originalChecksum = file.calculateChecksum();
            var storedChecksum = fileWithId.calculateChecksum();

            var retrievedChecksum = valueRetrieved.calculateChecksum();
            var allEqual = originalChecksum.equals(storedChecksum) && originalChecksum.equals(retrievedChecksum);

            System.out.printf("""
                            Original file checksum: [%s]
                            File with id checksum: [%s]
                            Retrieved from database checksum: [%s]
                            Are values equals? %s
                            """,
                    originalChecksum,
                    storedChecksum,
                    retrievedChecksum,
                    allEqual
            );
        } catch (NoSuchAlgorithmException ex) {
            System.out.println("Something went wrong calculating checksums...");
        }

    }

    private static File createFileObject(String filePath) {
        var fileContent = createFile(filePath);

        var file = new File(null, filePath, "text", (long) fileContent.length, fileContent);
        return file;
    }

    public static byte[] createFile(String path) {
        byte[] buffer = TEXT.getBytes(StandardCharsets.UTF_8);
        byte[] content = new byte[(int) MAX_SIZE + 1000];
        try (FileOutputStream fos = new FileOutputStream(path)) {
            long written = 0;
            while (written < MAX_SIZE) {
                fos.write(buffer);
                written += buffer.length;
                System.arraycopy(buffer, 0, content, (int) written, buffer.length);
            }

            System.out.printf("Successfully wrote file %s\n", path);

            return content;
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new RuntimeException("Could not write file...", ex);
        }
    }


    public static File upload(File file, ApplicationContext context) {
        var fileRepository = context.getBean(FileRepository.class);
        return fileRepository.save(file);
    }

    public static File getFileById(Long id, ApplicationContext context) {
        var fileRepository = context.getBean(FileRepository.class);
        return fileRepository.getById(id.intValue());
    }

    private static final String TEXT = """
            In the quiet town of Willowbrook, where the air had the scent of blooming lilac trees,
            there lived a peculiar man named Mr. Jenkins. His gray hair was always meticulously combed,
            and his spectacles rested on the tip of his nose as if held by invisible fingers.
            His eyes, a faded shade of blue, sparkled with a curiosity that belied his seventy-four years.
            Every morning, Mr. Jenkins could be seen tending to his garden, a patch of green that was the envy of his 
            neighbors. His routine was as reliable as the tick of a clock.
            """;
}
