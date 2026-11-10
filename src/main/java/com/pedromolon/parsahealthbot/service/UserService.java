package com.pedromolon.parsahealthbot.service;

import com.pedromolon.parsahealthbot.exception.UserNotFoundException;
import com.pedromolon.parsahealthbot.model.UserAccount;
import com.pedromolon.parsahealthbot.model.enums.ActivityLevel;
import com.pedromolon.parsahealthbot.repository.UserAccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserAccountRepository userAccountRepository;

    public UserService(UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
    }

    @Transactional
    public UserAccount getOrCreate(Long chatId, String username) {
        return userAccountRepository.findById(chatId)
                .orElseGet(() -> {
                    String name = username != null ? username : "Usuário";
                    UserAccount newUser = new UserAccount();
                    newUser.setChatId(chatId);
                    newUser.setName(name);
                    return userAccountRepository.save(newUser);
                });
    }

    public UserAccount getUser(Long chatId) {
        return userAccountRepository.findById(chatId)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado"));
    }

    @Transactional
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

        userAccountRepository.save(user);

        return user;
    }

}
