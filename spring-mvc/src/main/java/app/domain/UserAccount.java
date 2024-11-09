package app.domain;

import app.exceptions.InsuficientBalance;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Entity
@Table(name = "USER_ACCOUNT")
@NoArgsConstructor
public class UserAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    private long balance;

    public UserAccount(User user, long balance) {
        this.user = user;
        this.balance = balance;
    }

    public void withdraw(long amount) {
        log.info("User {} withdrawing amount {} and balance {}", user.getName(), amount, balance);
        if (amount > balance) {
            throw new InsuficientBalance();
        }
        this.balance -= amount;
    }

    public void deposit(long amount) {
        this.balance += amount;
    }
}
