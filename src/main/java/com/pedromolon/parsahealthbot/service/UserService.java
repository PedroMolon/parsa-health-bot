package com.pedromolon.parsahealthbot.service;

import com.pedromolon.parsahealthbot.model.UserAccount;
import com.pedromolon.parsahealthbot.repository.UserAccountRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserAccountRepository userAccountRepository;

    public UserService(UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
    }

    public UserAccount getOrCreateUser(Long chatId, String name) {
        String userName = name != null ? name : "Usuário";
        Optional<UserAccount> existingUser = userAccountRepository.findById(chatId);
        if (existingUser.isPresent()) {
            return existingUser.get();
        }

        UserAccount newUser = new UserAccount(chatId, userName);
        return userAccountRepository.save(newUser);
    }

}
