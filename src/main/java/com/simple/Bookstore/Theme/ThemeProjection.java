package com.simple.Bookstore.Theme;

import java.time.LocalDateTime;

public interface ThemeProjection {
    Long getId();

    String getName();

    String getDescription();

    Boolean getPublished();

    LocalDateTime getDate();

    Long getUserId();

    String getUsername();

    String getUserDisplayName();

    String getBase00();

    String getBase01();

    String getBase02();

    String getBase03();

    String getBase04();

    String getBase05();

    String getBase06();

    String getBase07();
}