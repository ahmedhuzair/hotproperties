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
                    Role.ADMIN
            );


            User u2 = new User(
                    "Isabella", "Carter", "isabella.carter@email.com", passwordEncoder.encode("ic.123"),
                    Role.BUYER
            );

            User u3 = new User(
                    "Ethan", "Brooks", "ethan.brooks@email.com", passwordEncoder.encode("eb.123"),
                    Role.BUYER
            );


            User u4 = new User(
                    "Henry", "Wallace", "henry.wallace@email.com", passwordEncoder.encode("hw.123"),
                    Role.AGENT
            );



            userRepository.saveAll(List.of(u1, u2, u3, u4));

            System.out.println("🟢 Initial users and roles inserted.");
        } else {
            System.out.println("🟡 Users and roles already exist, skipping initialization.");
        }
    }
}
