package com.sprint.mission.discodeit;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {

    public static void main(String[] args) {
        Path file = Path.of("temp/example.dat");

        try {
            Files.createFile(file);
        } catch (FileAlreadyExistsException e) {
            System.out.println("파일이 이미 존재합니다.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
