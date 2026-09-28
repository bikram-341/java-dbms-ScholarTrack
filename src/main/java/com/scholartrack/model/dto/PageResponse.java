package com.scholartrack.model.dto;

import org.springframework.data.domain.Page;
import java.util.List;

public class PageResponse<T> {
    private List<T> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean isFirst;
    private boolean isLast;
    private boolean hasNext;
    private boolean hasPrevious;

    public PageResponse() {}

    public PageResponse(Page<T> pageObj) {
        this.content = pageObj.getContent();
        this.page = pageObj.getNumber();
        this.size = pageObj.getSize();
        this.totalElements = pageObj.getTotalElements();
        this.totalPages = pageObj.getTotalPages();
        this.isFirst = pageObj.isFirst();
        this.isLast = pageObj.isLast();
        this.hasNext = pageObj.hasNext();
        this.hasPrevious = pageObj.hasPrevious();
    }

    public PageResponse(List<T> content, int page, int size, long totalElements, int totalPages, boolean isFirst, boolean isLast) {
        this.content = content;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.isFirst = isFirst;
        this.isLast = isLast;
        this.hasNext = page + 1 < totalPages;
        this.hasPrevious = page > 0;
    }

    public List<T> getContent() { return content; }
    public void setContent(List<T> content) { this.content = content; }
    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }
    public int getSize() { return size; }
    public void setSize(int size) { this.size = size; }
    public long getTotalElements() { return totalElements; }
    public void setTotalElements(long totalElements) { this.totalElements = totalElements; }
    public int getTotalPages() { return totalPages; }
    public void setTotalPages(int totalPages) { this.totalPages = totalPages; }
    public boolean isFirst() { return isFirst; }
    public void setFirst(boolean first) { isFirst = first; }
    public boolean isLast() { return isLast; }
    public void setLast(boolean last) { isLast = last; }
    public boolean isHasNext() { return hasNext; }
    public void setHasNext(boolean hasNext) { this.hasNext = hasNext; }
    public boolean isHasPrevious() { return hasPrevious; }
    public void setHasPrevious(boolean hasPrevious) { this.hasPrevious = hasPrevious; }
}
