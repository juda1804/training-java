package app.controller;

import app.domain.User;
import app.facade.BookingFacade;
import java.util.List;
import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
@Controller
@RequestMapping("/users")
public class UserController {

    private final BookingFacade bookingFacade;

    public UserController(BookingFacade bookingFacade) {
        this.bookingFacade = bookingFacade;
    }

    @PostMapping("/")
    public String createUser(
                             @RequestParam String name,
                             @RequestParam String email, Model model) {
        User user = bookingFacade.createUser(name, email);
        model.addAttribute("user", user);
        return "userDetails";
    }

    @GetMapping("/all")
    public ModelAndView getAllUsers() {
        List<User> users = bookingFacade.getAllUsers();
        ModelAndView mav = new ModelAndView("allUsers");
        mav.addObject("users", users);
        return mav;
    }

    @GetMapping("/{id}")
    public String getUserById(@PathVariable Long id, Model model) {
        Optional<User> user = bookingFacade.getUserById(id);
        user.ifPresent(value -> model.addAttribute("user", value));
        return "userDetails";  // Thymeleaf template
    }
}
