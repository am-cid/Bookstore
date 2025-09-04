package com.simple.Bookstore.views.Profile;

import lombok.Getter;

@Getter
public enum ProfileViewType {
    THEMES("Themes"),
    REVIEWS("Reviews"),
    COMMENTS("Comments");

    private final String displayName;

    ProfileViewType(String displayName) {
        this.displayName = displayName;
    }
}
