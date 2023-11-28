package com.example.securingweb;

public class TwitterCountRequest {
    String queryName;
    String query;
    boolean basicAccess;
    String startTime;
    String endTime;
    String expansions;
    String mediaFields;
    String placeFields;
    String pollFields;
    String tweetFields;
    String userFields;
    String sortOrder;

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

    public boolean getBasicAccess() {
        return basicAccess;
    }

    public void setBasicAccess(boolean basicAccess) {
        this.basicAccess = basicAccess;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getExpansions() {
        return expansions;
    }

    public void setExpansions(String expansions) {
        this.expansions = expansions;
    }

    public String getMediaFields() {
        return mediaFields;
    }

    public void setMediaFields(String mediaFields) {
        this.mediaFields = mediaFields;
    }

    public String getPlaceFields() {
        return placeFields;
    }

    public void setPlaceFields(String placeFields) {
        this.placeFields = placeFields;
    }

    public String getPollFields() {
        return pollFields;
    }

    public void setPollFields(String pollFields) {
        this.pollFields = pollFields;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }

    public String getTweetFields() {
        return tweetFields;
    }

    public void setTweetFields(String tweetFields) {
        this.tweetFields = tweetFields;
    }

    public String getUserFields() {
        return userFields;
    }

    public void setUserFields(String userFields) {
        this.userFields = userFields;
    }
}
