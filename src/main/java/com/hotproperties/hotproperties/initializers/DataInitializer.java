package com.hotproperties.hotproperties.initializers;

import com.hotproperties.hotproperties.entities.Role;
import com.hotproperties.hotproperties.entities.User;
import com.hotproperties.hotproperties.repositories.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class DataInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void init() {
        if (userRepository.count() == 0 ) {

            User u1 = new User(
                    "Mason", "Lee", "mason.lee@email.com", passwordEncoder.encode("ml.123"),
                    Role.ROLE_ADMIN

            );



            userRepository.saveAll(List.of(u1));

            System.out.println("🟢 Admin with email:'mason.lee@email.com' and pw: 'ml.123'  created .");
        } else {
            System.out.println("🟡 Admin already exists, skipping initialization.");
        }
    }
}
