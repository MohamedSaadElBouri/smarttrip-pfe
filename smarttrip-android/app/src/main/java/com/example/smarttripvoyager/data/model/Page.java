package com.example.smarttripvoyager.data.model;

import java.util.List;

public class Page<T> {
    public List<T> content;
    public int totalPages;
    public long totalElements;
}
