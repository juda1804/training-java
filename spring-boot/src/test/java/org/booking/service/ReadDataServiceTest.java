package org.booking.service;

import org.booking.model.Events;
import org.booking.model.User;
import org.booking.model.Users;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"/applicationContext.xml"})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class ReadDataServiceTest {

    @Autowired
    private ReadDataService readDataService;

    @Test
    public void testReadFile() {
        var users = readDataService.readFile("data/users.xml", Users::getUser);

        Assertions.assertEquals(List.of(1565650L, 5650120L), users.stream().map(User::getId).toList());
    }

    @Test
    public void testFailedReadFileIfTypeIsDifferent() {
        var users = readDataService.readFile("data/users.xml", Events::getEvent);

        Assertions.assertEquals(List.of(), users);
    }
}
