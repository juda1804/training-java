package com.epam.homework;

import com.epam.homework.config.AppConfig;
import com.epam.homework.repository.UserRepository;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class App {
    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        var userRepository = context.getBean(UserRepository.class);
        var minimumFriends = 100;
        var minimumLikes = 20;
        var users = userRepository.findUsersWithMoreThanFriendsAndLikes();
        System.out.printf(
                "Users with more than %d friends and more than %d likes in March 2025:\n",
                minimumFriends,
                minimumLikes
        );
        users.forEach(user -> {
            System.out.printf("%s %s\n", user.getName(), user.getSurname());
        });

    }
}
