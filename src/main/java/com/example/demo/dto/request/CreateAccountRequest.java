package com.example.demo.dto.request;

import java.util.List;

public class CreateAccountRequest {
  private String name;
  private String email;
  private String phone;
  private String password;
  private Integer roleId;
  private List<MenuSelection> initialMenuSelections;

  public static class MenuSelection {
    private String code;
    private boolean checked;
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public boolean isChecked() { return checked; }
    public void setChecked(boolean checked) { this.checked = checked; }
  }

  public String getName() { return name; } public void setName(String name) { this.name = name; }
  public String getEmail() { return email; } public void setEmail(String email) { this.email = email; }
  public String getPhone() { return phone; } public void setPhone(String phone) { this.phone = phone; }
  public String getPassword() { return password; } public void setPassword(String password) { this.password = password; }
  public Integer getRoleId() { return roleId; } public void setRoleId(Integer roleId) { this.roleId = roleId; }
  public List<MenuSelection> getInitialMenuSelections() { return initialMenuSelections; }
  public void setInitialMenuSelections(List<MenuSelection> initialMenuSelections) { this.initialMenuSelections = initialMenuSelections; }
}
