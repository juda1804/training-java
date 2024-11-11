package org.booking.data.repository;

import org.booking.model.UserAccount;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Optional;

import static org.booking.util.IdentifierGenerator.generateId;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"/applicationContext.xml"})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class UserAccountRepositoryTest {

    @Autowired
    private UserAccountRepository userAccountRepository;

    @BeforeEach
    public void resetDb() {
        userAccountRepository.deleteAll();
    }

    @Test
    public void testCreateUserAccount() {
        var userAccount = new UserAccount();
        userAccount.setId(generateId());
        userAccount.setUserId(generateId());
        userAccount.setAmount(10);

        var storedUserAccount = userAccountRepository.save(userAccount);

        Assertions.assertEquals(userAccount.getId(), storedUserAccount.getId());
        Assertions.assertEquals(userAccount.getUserId(), storedUserAccount.getUserId());
        Assertions.assertEquals(userAccount.getAmount(), storedUserAccount.getAmount());
    }

    @Test
    public void testFinById() {
        var userAccount = new UserAccount();
        userAccount.setId(generateId());
        userAccount.setUserId(generateId());
        userAccount.setAmount(10);

        userAccountRepository.save(userAccount);

        var account = userAccountRepository.findById(userAccount.getId());

        Assertions.assertEquals(Optional.of(userAccount.getId()), account.map(UserAccount::getId));
    }

    @Test
    public void testFinByIdIfItHasNotBeenStored() {
        var userId = generateId();

        var account = userAccountRepository.findById(userId);

        Assertions.assertEquals(Optional.empty(), account);
    }

    @Test
    public void testFindUserAccountByUsedId() {
        var userId = generateId();

        var userAccount1 = new UserAccount();
        userAccount1.setId(generateId());
        userAccount1.setUserId(userId);
        userAccount1.setAmount(10.0);

        var userAccount2 = new UserAccount();
        userAccount2.setId(generateId());
        userAccount2.setUserId(userId);
        userAccount2.setAmount(25.0);

        var userAccount3 = new UserAccount();
        userAccount3.setId(generateId());
        userAccount3.setUserId(userId);
        userAccount3.setAmount(25.0);

        List.of(userAccount1, userAccount2, userAccount3).forEach(userAccount -> userAccountRepository.save(userAccount));

        var userAccountResults = userAccountRepository.findByUserId(userId);

        Assertions.assertEquals(3, userAccountResults.size());
    }

    @Test
    public void testDeleteUserAccount() {
        var userAccount = new UserAccount();
        userAccount.setId(generateId());
        userAccount.setUserId(generateId());
        userAccount.setAmount(10);

        var storedUserAccount = userAccountRepository.save(userAccount);

        Assertions.assertEquals(userAccount.getId(), storedUserAccount.getId());
        Assertions.assertEquals(userAccount.getUserId(), storedUserAccount.getUserId());
        Assertions.assertEquals(userAccount.getAmount(), storedUserAccount.getAmount());

        userAccountRepository.deleteById(userAccount.getId());

        var account = userAccountRepository.findById(userAccount.getId());

        Assertions.assertEquals(Optional.empty(), account);
    }

}
