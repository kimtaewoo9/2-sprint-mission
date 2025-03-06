package com.sprint.mission.discodeit.repository.file;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

public class FileUtils {
    @Getter
    public static final Path baseDirectory = Paths.get(System.getProperty("user.dir")).resolve("data");

    public static void init(Path directory){
        if(!Files.exists(directory)){
            try{
                Files.createDirectories(directory);
            }catch (IOException e){
                throw new RuntimeException("파일 생성 중 오류가 발생했습니다.");
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

    public static <T> T loadById(Path filePath){
        if(Files.exists(filePath)){
            try(
                FileInputStream fis = new FileInputStream(filePath.toFile());
                ObjectInputStream ois = new ObjectInputStream(fis)
            ){
                Object data = ois.readObject();
                return (T) data;
            }catch(IOException | ClassNotFoundException e){
                throw new RuntimeException("파일 로드 중 오류 발생");
            }
        }
        return null;
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
                                throw new RuntimeException("파일 불러오기 중 오류가 발생했습니다.");
                            }
                        })
                        .toList();
                return list;
            }catch (IOException e){
                throw new RuntimeException("파일 불러오기 중 오류가 발생했습니다.");
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
            throw new RuntimeException("파일 삭제 중 오류가 발생했습니다.", e);
        }
    }
}
