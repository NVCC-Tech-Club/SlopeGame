package com.slope.game;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ResourceLoader {
    public static List<String> readAllLines(String fileName) {
        List<String> list = new ArrayList<>();

        try(BufferedReader br = new BufferedReader(new InputStreamReader(Class.forName(ResourceLoader.class.getName()).getResourceAsStream(fileName)))) {
            String line;
            while((line = br.readLine()) != null) {
                list.add(line);
            }
        } catch(IOException | ClassNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        }

        return list;
    }

    public static String loadFile(String fileName) {
        InputStream is = ResourceLoader.class.getClassLoader().getResourceAsStream(fileName);
        StringBuilder fileSource = new StringBuilder();

        if(useReader(is, fileSource) == 0) {
            throw new IllegalStateException("Was not able to read this file!");
        }

        return fileSource.toString();
    }

    private static int useReader(InputStream is, StringBuilder shaderSource) {
        try {
            BufferedReader shaderReader = new BufferedReader(new InputStreamReader(is));

            String line;
            while ((line = shaderReader.readLine()) != null) {
                shaderSource.append(line).append("\n");
            }

            shaderReader.close();
            return 1;
        }catch (Exception e) {
            return 0;
        }
    }
}