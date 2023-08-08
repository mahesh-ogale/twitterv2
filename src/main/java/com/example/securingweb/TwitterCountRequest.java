package com.example.securingweb;

public class TwitterCountRequest {
    String queryName;
    String query;
    Boolean basicAccess;

    public String getQueryName() {
        return queryName;
    }

    public void setQueryName(String queryName) {
        this.queryName = queryName;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public Boolean getBasicAccess() {
        return basicAccess;
    }

    public void setBasicAccess(Boolean basicAccess) {
        this.basicAccess = basicAccess;
    }
}
