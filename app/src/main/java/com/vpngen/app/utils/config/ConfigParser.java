package com.vpngen.app.utils.config;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Map;
import java.util.zip.InflaterInputStream;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class ConfigParser {

    public boolean importConnectionFromCode(String code) {
        code = code.replace("vpn://", "");
        byte[] ba;
        try {
            ba = Base64.getUrlDecoder().decode(code);
        } catch (IllegalArgumentException e) {
            System.out.println("Base64 decode error: " + e.getMessage());
            return false;
        }

        // Remove qCompress 4-byte header
        if (ba.length > 4) {
            byte[] temp = new byte[ba.length - 4];
            System.arraycopy(ba, 4, temp, 0, ba.length - 4);
            ba = temp;
        }

        // Print ba bytes
        StringBuilder hexString = new StringBuilder();
        for (byte b : ba) {
            String hex = Integer.toHexString(0xFF & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }

        // Decompression
        byte[] baUncompressed;
        try (InflaterInputStream iis = new InflaterInputStream(new ByteArrayInputStream(ba));
             ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int read;
            while ((read = iis.read(buffer)) != -1) {
                bos.write(buffer, 0, read);
            }
            baUncompressed = bos.toByteArray();
        } catch (IOException e) {
            System.out.println("Decompression failed: " + e.getMessage());
            return false;
        }

        // JSON unmarshalling
        Map<String, Object> o;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            o = objectMapper.readValue(baUncompressed, Map.class);
        } catch (IOException e) {
            System.out.println("JSON unmarshal error: " + e.getMessage());
            return false;
        }

        System.out.println("Resulting JSON object: " + o);

        // Pretty print JSON
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
            String prettyJson = objectMapper.writeValueAsString(o);
            System.out.println(prettyJson);
        } catch (IOException e) {
            System.out.println("JSON marshal error: " + e.getMessage());
            return false;
        }

        return true;
    }
}
