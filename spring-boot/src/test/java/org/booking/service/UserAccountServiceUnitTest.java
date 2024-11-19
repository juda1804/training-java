package org.booking.service;

import org.booking.TestWebApplication;
import org.booking.data.repository.UserAccountRepository;
import org.booking.model.UserAccount;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.booking.util.IdentifierGenerator.generateId;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = TestWebApplication.class)
public class UserAccountServiceUnitTest {

    @Mock
    private UserAccountRepository userAccountRepository;

    @InjectMocks
    private UserAccountService userAccountService;

    @Test
    public void testCreateUserAccount() {
        var userAccount = new UserAccount();
        userAccount.setId(generateId());
        userAccount.setUserId(generateId());
        userAccount.setAmount(10);

        when(userAccountRepository.save(userAccount)).thenReturn(userAccount);

        var storedUserAccount = userAccountService.createUserAccount(userAccount);

        Assertions.assertEquals(userAccount.getId(), storedUserAccount.getId());
        Assertions.assertEquals(userAccount.getUserId(), storedUserAccount.getUserId());
        Assertions.assertEquals(userAccount.getAmount(), storedUserAccount.getAmount());
    }

    @Test
    public void testRefillUserAccount() {
        var accountId = generateId();
        var userId = generateId();
        var userAccount = new UserAccount();
        userAccount.setId(accountId);
        userAccount.setUserId(userId);
        userAccount.setAmount(10.0);

        when(userAccountRepository.findById(userAccount.getId())).thenReturn(Optional.of(userAccount));
        when(userAccountRepository.save(userAccount)).thenReturn(userAccount);

        var wasRefilled = userAccountService.refillUserAccount(accountId, 20.0);

        Assertions.assertTrue(wasRefilled);
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

        when(userAccountRepository.findByUserId(userId)).thenReturn(List.of(userAccount1, userAccount2, userAccount3));

        var userAccountResults = userAccountService.findUserAccountByUserId(userId);

        Assertions.assertEquals(3, userAccountResults.size());
    }

    @Test
    public void testDeleteUserAccount() {
        var accountId = generateId();
        var userAccount = new UserAccount();
        userAccount.setId(accountId);
        userAccount.setUserId(generateId());
        userAccount.setAmount(10);

        var wasDeleted = userAccountService.delete(userAccount.getId());

        Assertions.assertTrue(wasDeleted);
    }
}
