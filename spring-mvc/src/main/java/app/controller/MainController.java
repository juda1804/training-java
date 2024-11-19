package app.controller;

import app.facade.BookingFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class MainController {

    @Autowired
    private BookingFacade bookingFacade;

    @GetMapping("/")
    public String index(Model model) {
        var tickets = bookingFacade.getAllTickets();
        ModelAndView mav = new ModelAndView("index");
        mav.addObject("tickets", tickets);
        return "index";
    }
}