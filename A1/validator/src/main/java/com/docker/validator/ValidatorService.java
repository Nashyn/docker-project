package com.docker.validator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.Resource;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;


@SpringBootApplication
public class ValidatorService {

    public static void main(String[] args) {
        SpringApplication.run(ValidatorService.class, args);
    }

    @RestController
    public static class ValidatorController {

        private static final String DATA_PATH = "/app/data";

        @Autowired
        private ResourceLoader resourceLoader;

        @PostMapping("/calculateAmount")
        public ResponseEntity<Map<String, Object>> calculateAmount(@RequestBody Map<String, String> json_data) {
            Map<String, Object> response = new HashMap<>();
            String fileName = json_data.get("file");
            String product = json_data.get("product");

            if (!fileName.toLowerCase().endsWith(".dat") && !fileName.toLowerCase().endsWith(".csv") && !fileName.toLowerCase().endsWith(".yml")) {
                response.put("file", fileName);
                response.put("error", "Input file not in CSV format.");
                return ResponseEntity.badRequest().body(response);
            }

            Resource resource = resourceLoader.getResource("file:" + DATA_PATH + "/" + fileName);
            if (!resource.exists()) {
                response.put("file", fileName);
                response.put("error", "File not found.");
                return ResponseEntity.badRequest().body(response);
            }

            try {
                boolean isHeaderValid = Files.lines(Paths.get(resource.getURI())).findFirst().map(line -> line.equals("product,amount")).orElse(false);
                if (!isHeaderValid) {
                    response.put("file", fileName);
                    response.put("error", "Input file not in CSV format.");
                    return ResponseEntity.ok(response);
                }
                
                long sum = Files.lines(Paths.get(resource.getURI()))
                        .skip(1)
                        .map(line -> line.split(","))
                        .filter(parts -> parts[0].equals(product))
                        .mapToLong(parts -> Long.parseLong(parts[1]))
                        .sum();

                response.put("file", fileName);
                response.put("sum", sum);
                return ResponseEntity.ok(response);
           
            } catch (Exception e) {
                response.put("file", fileName);
                response.put("error", "Input file not in CSV format.");
                return ResponseEntity.ok(response);
            }
        }
    }
}
