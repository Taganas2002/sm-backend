package com.example.demo.util;

import java.security.SecureRandom;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public final class Tokens {
  private static final SecureRandom RNG = new SecureRandom();
  private Tokens(){}

  public static String randomUrlToken(int bytes) {
    byte[] b = new byte[bytes]; RNG.nextBytes(b);
    return java.util.Base64.getUrlEncoder().withoutPadding().encodeToString(b);
  }
  public static String sha256Hex(String s) {
    try {
      MessageDigest md = MessageDigest.getInstance("SHA-256");
      byte[] h = md.digest(s.getBytes(StandardCharsets.UTF_8));
      StringBuilder sb = new StringBuilder(h.length*2);
      for(byte x: h) sb.append(String.format("%02x", x));
      return sb.toString();
    } catch (Exception e) { throw new RuntimeException(e); }
  }
}
