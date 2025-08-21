package com.example.demo.dto.response;

import java.util.List;

public class PermissionsSnapshotDto {
  private Long userId;
  private Integer roleId;
  private List<String> baseline;
  private List<OverrideItem> overrides;
  private List<String> effective;

  public static class OverrideItem {
    private String code;
    private String effect; // ALLOW or DENY
    public OverrideItem() {}
    public OverrideItem(String code, String effect) { this.code = code; this.effect = effect; }
    public String getCode() { return code; } public void setCode(String code) { this.code = code; }
    public String getEffect() { return effect; } public void setEffect(String effect) { this.effect = effect; }
  }

  public Long getUserId() { return userId; } public void setUserId(Long userId) { this.userId = userId; }
  public Integer getRoleId() { return roleId; } public void setRoleId(Integer roleId) { this.roleId = roleId; }
  public List<String> getBaseline() { return baseline; } public void setBaseline(List<String> baseline) { this.baseline = baseline; }
  public List<OverrideItem> getOverrides() { return overrides; } public void setOverrides(List<OverrideItem> overrides) { this.overrides = overrides; }
  public List<String> getEffective() { return effective; } public void setEffective(List<String> effective) { this.effective = effective; }
}
