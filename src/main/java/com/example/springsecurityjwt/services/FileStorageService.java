package com.example.springsecurityjwt.services;

import com.example.springsecurityjwt.Repository.FilesStorageRepository;
import com.example.springsecurityjwt.controllers.MountainController;
import com.example.springsecurityjwt.util.FilesUtil;
import com.example.springsecurityjwt.util.Status;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.stream.Stream;

@Service
public class FileStorageService implements FilesStorageRepository {

//    private final Path root = Paths.get(FilesUtil.folderPath);
    private final Path root = Paths.get("/opt/files/");

    private final Path nginx = Paths.get("http://35.194.165.190:80/opt/files");

    @Override
    public void init() {
        try{
            Files.createDirectory(root);
        }catch(IOException e){
            throw new RuntimeException("Could not initialize folder for upload");
        }
    }

    @Override
    public Status save(MultipartFile file, String filePath) {
        try{
            String newFileName = System.currentTimeMillis() + file.getOriginalFilename();
            Files.copy(file.getInputStream(), this.root.resolve(filePath).resolve(newFileName));
            System.out.println(this.root.resolve(filePath).resolve(newFileName));
            return new Status(200, newFileName);
        }catch(Exception e){
            //throw new RuntimeException("Could not store the file. Error: " + e.getMessage());
            e.printStackTrace();
            return new Status(404, e.getMessage());
        }
    }

    @Override
    public Resource load(String filename) {
        try{
            Path file = root.resolve(filename);
            Resource resource = new UrlResource(file.toUri());

            if(resource.exists() || resource.isReadable()){
                return resource;
            }else{
                throw new RuntimeException("Could not read the file!");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }

    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(root.toFile());
    }

    @Override
    public Status delete(String fileName) {
        File file = new File(root+fileName);
        if(file.delete()){
            return new Status(200, "File deleted");
        }
        return new Status (404, "Could not delete file!!!!");

    }

    @Override
    public Stream<Path> loadAll() {
        try{
            return Files.walk(this.root, 1).filter(path -> !path.equals(this.root)).map(this.root::relativize);
        } catch (IOException e) {
            throw new RuntimeException("Could not load the files!");
        }
    }
}
