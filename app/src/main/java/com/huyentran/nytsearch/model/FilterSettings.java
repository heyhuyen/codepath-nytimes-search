package com.huyentran.nytsearch.model;

import java.io.Serializable;
import java.util.HashSet;

import static com.huyentran.nytsearch.model.FilterSettings.SortOrder.NONE;
import static com.huyentran.nytsearch.utils.Constants.EMPTY;

/**
 * Model for search filter settings.
 */
public class FilterSettings implements Serializable {
    public enum SortOrder {
        NONE,
        NEWEST,
        OLDEST
    };

    private String beginDate;
    private SortOrder sortOrder;
    private HashSet<String> newsDeskValues;

    public FilterSettings() {
        this.beginDate = EMPTY;
        this.sortOrder = NONE;
        this.newsDeskValues = new HashSet<>();
    }

    public FilterSettings(FilterSettings settings) {
        this.beginDate = settings.getBeginDate();
        this.sortOrder = settings.getSortOrder();
        this.newsDeskValues = settings.getNewsDeskValues();
    }

    public String getBeginDate() {
        return this.beginDate;
    }

    public SortOrder getSortOrder() {
        return this.sortOrder;
    }

    public HashSet<String> getNewsDeskValues() {
        return this.newsDeskValues;
    }

    public void setBeginDate(String beginDate) {
        this.beginDate = beginDate;
    }

    public void setSortOrder(SortOrder sortOrder) {
        this.sortOrder = sortOrder;
    }

    public void setNewsDeskValues(HashSet<String> newsDeskValues) {
        this.newsDeskValues = newsDeskValues;
    }

    public void addNewsDeskValue(String newsDesk) {
        this.newsDeskValues.add(newsDesk);
    }

    public void removeNewsDeskValue(String newsDesk) {
        this.newsDeskValues.remove(newsDesk);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        FilterSettings that = (FilterSettings) o;
        return this.beginDate.equals(that.getBeginDate())
                && this.sortOrder.equals(that.getSortOrder())
                && this.newsDeskValues.equals(that.newsDeskValues);
    }
}
