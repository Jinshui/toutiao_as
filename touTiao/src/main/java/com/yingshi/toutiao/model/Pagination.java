package com.yingshi.toutiao.model;

import java.util.ArrayList;
import java.util.List;

public class Pagination<T> {
    private int currentPage;
    private int totalCounts;
    private int pageSize;
    private List<T> items = new ArrayList<T>();

    public int getCurrentPage() {
        return currentPage;
    }
    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }
    public int getTotalCounts() {
        return totalCounts;
    }
    public void setTotalCounts(int totalCounts) {
        this.totalCounts = totalCounts;
    }
    public int getPageSize() {
        return pageSize;
    }
    public void setPageSize(int numPerPage) {
        this.pageSize = numPerPage;
    }
    public List<T> getItems() {
        return items;
    }
    public void setItems(List<T> items) {
        this.items = items;
    }
}
