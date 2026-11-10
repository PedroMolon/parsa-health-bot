package com.pedromolon.parsahealthbot.service;

import com.pedromolon.parsahealthbot.exception.UserNotFoundException;
import com.pedromolon.parsahealthbot.model.UserAccount;
import com.pedromolon.parsahealthbot.model.enums.ActivityLevel;
import com.pedromolon.parsahealthbot.repository.UserAccountRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserAccountRepository userAccountRepository;

    public UserService(UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
    }

    public UserAccount createUser(Long chatId, String username) {
        String name = username != null ? username : "Usuário";

        UserAccount newUser = new UserAccount(chatId, name);

        return userAccountRepository.save(newUser);
    }

    public UserAccount getUser(Long chatId) {
        Optional<UserAccount> existingUser = Optional.of(userAccountRepository.findById(chatId)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado")));

        return existingUser.get();
    }

    public UserAccount updateUserProfile(Long chatId, Double weight, Integer height, Integer age, ActivityLevel activityLevel) {
        UserAccount user = userAccountRepository.findById(chatId)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado"));

        user.setWeight(weight);
        user.setHeight(height);
        user.setAge(age);
        user.setActivityLevel(activityLevel);

        int baseWaterMl = (int) (weight * 35);
        if (activityLevel == ActivityLevel.MODERATE || activityLevel == ActivityLevel.ACTIVE || activityLevel == ActivityLevel.VERY_ACTIVE) {
            baseWaterMl += 500;
        }
        user.setDailyWaterGoalMl(baseWaterMl);

        return userAccountRepository.save(user);
    }

}
