package com.example.demo.security;

public final class TenantContext {
  private static final ThreadLocal<Long> CURRENT_SCHOOL = new ThreadLocal<>();
  private TenantContext() {}
  public static void setSchoolId(Long id) { CURRENT_SCHOOL.set(id); }
  public static Long getSchoolId() { return CURRENT_SCHOOL.get(); }
  public static Long requireSchoolId() {
    Long v = CURRENT_SCHOOL.get();
    if (v == null) throw new IllegalStateException("No active school in context");
    return v;
  }
  public static void clear() { CURRENT_SCHOOL.remove(); }
}
