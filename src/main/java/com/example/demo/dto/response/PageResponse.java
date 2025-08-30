package com.example.demo.dto.response;

import java.util.List;

public class PageResponse<T> {
  private List<T> content;
  private int page;
  private int size;
  private long total;

  public PageResponse() {}
  public PageResponse(List<T> content, int page, int size, long total) {
    this.content = content; this.page = page; this.size = size; this.total = total;
  }
  public List<T> getContent() { return content; }
  public void setContent(List<T> content) { this.content = content; }
  public int getPage() { return page; }
  public void setPage(int page) { this.page = page; }
  public int getSize() { return size; }
  public void setSize(int size) { this.size = size; }
  public long getTotal() { return total; }
  public void setTotal(long total) { this.total = total; }
}
