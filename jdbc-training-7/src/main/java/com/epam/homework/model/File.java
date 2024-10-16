package com.epam.homework.model;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public record File(
        Integer id,
        String name,
        String type,
        Long size,
        byte[] content
) {
    public String calculateChecksum() throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        digest.update(content);
        var hashBytes = digest.digest();
        var sb = new StringBuilder();
        for(byte b: hashBytes)
            sb.append(String.format("%02x", b));
        return sb.toString();
    }
}
