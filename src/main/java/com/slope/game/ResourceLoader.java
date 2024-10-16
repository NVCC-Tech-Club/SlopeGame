package com.slope.game;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ResourceLoader {
    public static List<String> readAllLines(String fileName) {
        List<String> list = new ArrayList<>();
        InputStream is = ResourceLoader.class.getClassLoader().getResourceAsStream(fileName);

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            String line;
            while ((line = br.readLine()) != null) {
                list.add(line);
            }
        } catch(Exception e) {
            System.out.println("Unable to find file!");
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

    public static String loadShader(String filePath) {
        StringBuilder fileSource = new StringBuilder();

        if(processShaderIncludes(filePath, fileSource) == 0) {
            throw new IllegalStateException("Was not able to read this file!");
        }

        return fileSource.toString();
    }

    public static int processShaderIncludes(String filePath, StringBuilder shaderSource) {
        InputStream is = ResourceLoader.class.getClassLoader().getResourceAsStream(filePath);
        Pattern includePattern = Pattern.compile("#include\\s+\"(.*)\"");
        String line;

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            while ((line = reader.readLine()) != null) {
                Matcher matcher = includePattern.matcher(line.trim());

                if (matcher.matches()) {
                    String includeFilePath = matcher.group(1);
                    processShaderIncludes(includeFilePath, shaderSource); // Recursively process includes
                } else {
                    shaderSource.append(line).append("\n");
                }
            }

            reader.close();
            return 1;
        }catch (Exception e) {
            return 0;
        }
    }

    private static int useReader(InputStream is, StringBuilder shaderSource) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            String line;
            while ((line = reader.readLine()) != null) {
                shaderSource.append(line).append("\n");
            }

            reader.close();
            return 1;
        }catch (Exception e) {
            return 0;
        }
    }
}