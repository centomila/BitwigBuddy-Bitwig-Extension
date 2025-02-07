package com.centomila;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class ReadFile {
    private String filePath;

    // Constructor to initialize with the file path
    public ReadFile(String filePath) {
        this.filePath = filePath;
    }

    // Method to read file content and return as a String
    public String readFileAsString() {
        StringBuilder content = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                content.append(line).append("\n");
            }
        } catch (IOException e) {
            return "Error reading file: " + e.getMessage();
        }
        return content.toString().trim();
    }
}
