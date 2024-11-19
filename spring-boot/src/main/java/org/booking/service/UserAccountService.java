package org.booking.service;

import org.booking.data.repository.UserAccountRepository;
import org.booking.model.UserAccount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserAccountService {
    private final Logger LOGGER = LoggerFactory.getLogger(UserAccountService.class);

    private final UserAccountRepository userAccountRepository;

    @Autowired
    public UserAccountService(UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
    }

    /**
     * create user account.
     * @param userAccount User account.
     * @return created user account.
     */
    public UserAccount createUserAccount(UserAccount userAccount) {
        return userAccountRepository.save(userAccount);
    }

    /**
     * find user account by the user id.
     * @param userId User id.
     * @return List of user accounts.
     */
    public List<UserAccount> findUserAccountByUserId(long userId) {
        return userAccountRepository.findByUserId(userId);
    }

    /**
     * refill user account by its id.
     * @param id User account id.
     * @param amount amount to refill.
     * @return Flag that shows whether user account has been updated with the new amount.
     */
    @Transactional()
    public boolean refillUserAccount(long id, double amount) {
        LOGGER.info("Refilling account with id: {}", id);
        return userAccountRepository.findById(id).map(account -> {
            var newAmount = calculateNewAmount(account.getAmount(), amount);
            account.setAmount(newAmount);
            userAccountRepository.save(account);
            LOGGER.info("User account with id: {} was refilled. new amount: {}", account.getId(), newAmount);
            return true;
        }).orElse(false);
    }

    /**
     * Update user account.
     * @param userAccounts User accounts.
     * @return Flag that shows whether user account has been updated.
     */
    public List<UserAccount> update(List<UserAccount> userAccounts) {
        userAccounts.forEach(userAccount -> LOGGER.info("Updating user account with id: {}", userAccount.getId()));
        return userAccountRepository.saveAll(userAccounts);
    }

    /**
     * Deletes user account by its id.
     * @param accountId User id.
     * @return Flag that shows whether user account has been deleted.
     */
    public boolean delete(long accountId) {
        LOGGER.info("Deleting user account with id: {}", accountId);
        try {
            userAccountRepository.deleteById(accountId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private double calculateNewAmount(double currentAmount, double amount) {
        return currentAmount + amount;
    }
}
