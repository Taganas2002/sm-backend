// src/main/java/com/example/demo/dto/response/GroupOption.java
package com.example.demo.dto.response;

public class GroupOption {
  private Long id;
  private String name;

  public GroupOption(Long id, String name) {
    this.id = id;
    this.name = name;
  }
  public Long getId() { return id; }
  public String getName() { return name; }
}
