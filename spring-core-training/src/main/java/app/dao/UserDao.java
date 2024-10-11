package app.dao;

import app.domain.User;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class UserDao {
    private final StorageBean storageBean;

    public UserDao(StorageBean storageBean) {
        this.storageBean = storageBean;
    }

    public User findUserById(Long id) {
        return (User) storageBean.getStorage("user").get(id);
    }

    public void saveUser(User user) {
        storageBean.addToStorage("user", user.getId(), user);
    }
}

