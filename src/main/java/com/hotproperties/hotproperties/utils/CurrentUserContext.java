package com.hotproperties.hotproperties.utils;

import com.hotproperties.hotproperties.entities.User;
import org.springframework.security.core.Authentication;

public record CurrentUserContext(User user, Authentication auth) {}
