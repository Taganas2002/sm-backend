package com.example.demo.dto.request;

import java.util.List;

public class SaveUserMenuPermissionsRequest {
	private Long roleId; // optional
  private List<MenuSelection> menuSelections;

  public static class MenuSelection {
    private String code;
    private boolean checked;
    public String getCode() { return code; } public void setCode(String code) { this.code = code; }
    public boolean isChecked() { return checked; } public void setChecked(boolean checked) { this.checked = checked; }
  }

  public Long getRoleId() { return roleId; } public void setRoleId(Long roleId) { this.roleId = roleId; }
  public List<MenuSelection> getMenuSelections() { return menuSelections; }
  public void setMenuSelections(List<MenuSelection> menuSelections) { this.menuSelections = menuSelections; }
}
