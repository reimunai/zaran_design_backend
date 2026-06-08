package com.example.zaran_design_backend.dto;

import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;

/** 通用分页响应，对应文档「成功响应（列表数据）」结构 */
public class PageResponse<T> {

    private List<T> list;
    private long total;
    private int page;
    private int size;
    private int pages;

    public PageResponse() {
    }

    public PageResponse(List<T> list, long total, int page, int size, int pages) {
        this.list = list;
        this.total = total;
        this.page = page;
        this.size = size;
        this.pages = pages;
    }

    /** 由 Spring Data Page 构建（page 入参为 1 起始的页码） */
    public static <E, T> PageResponse<T> of(Page<E> source, int page, int size, Function<E, T> mapper) {
        List<T> list = source.getContent().stream().map(mapper).toList();
        return new PageResponse<>(list, source.getTotalElements(), page, size, source.getTotalPages());
    }

    public List<T> getList() { return list; }
    public void setList(List<T> list) { this.list = list; }

    public long getTotal() { return total; }
    public void setTotal(long total) { this.total = total; }

    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }

    public int getSize() { return size; }
    public void setSize(int size) { this.size = size; }

    public int getPages() { return pages; }
    public void setPages(int pages) { this.pages = pages; }
}
