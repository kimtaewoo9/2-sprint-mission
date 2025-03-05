package com.sprint.mission.discodeit.service.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {
    public static void init(Path directory){
        if(!Files.exists(directory)){
            try{
                Files.createDirectories(directory);
            }catch (IOException e){
                throw new RuntimeException(e);
            }
        }
    }

    public static <T> void save(Path filePath, T data){
        try(
            FileOutputStream fos = new FileOutputStream(filePath.toFile());
            ObjectOutputStream oos = new ObjectOutputStream(fos);
        ) {
            oos.writeObject(data);
        } catch (IOException e){
            throw new RuntimeException("파일 저장 실패 : " + filePath ,e);
        }
    }

    public static <T> List<T> load(Path directory){
        if(Files.exists(directory)){
            try{
                List<T> list = Files.list(directory)
                        .map(path -> {
                            try(
                                    FileInputStream fis = new FileInputStream(path.toFile());
                                    ObjectInputStream ois = new ObjectInputStream(fis);
                            ){
                                Object data = ois.readObject();
                                return (T) data;
                            } catch (IOException | ClassNotFoundException e){
                                throw new RuntimeException(e);
                            }
                        })
                        .toList();
                return list;
            }catch (IOException e){
                throw new RuntimeException(e);
            }
        }else{
            return new ArrayList<>();
        }
    }

    public static boolean delete(Path filePath){
        try{
            if(Files.deleteIfExists(filePath)){
                System.out.println("delete success");
                return true;
            }
            return false;
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }
}