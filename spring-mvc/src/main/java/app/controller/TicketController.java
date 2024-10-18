package app.controller;

import app.dao.TicketRepository;
import app.domain.Event;
import app.domain.Ticket;
import app.domain.TicketCategory;
import app.domain.User;
import app.facade.BookingFacade;
import app.service.TicketService;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class TicketController {

    @Autowired
    private BookingFacade bookingFacade;

    @GetMapping("/tickets/view")
    public ModelAndView viewTickets(@RequestParam(defaultValue = "10") int pageNum,
                                    @RequestParam(defaultValue = "30") int pageSize,
                                    @RequestParam() Long userId) {
        User userById = bookingFacade.getUserById(userId).get();

        Page<Ticket> page = bookingFacade.getBookedTickets(userById,pageNum, pageSize);

        // Add tickets and pagination metadata to the model
        ModelAndView mav = new ModelAndView("ticketList");  // "ticketList" is the Thymeleaf template
        mav.addObject("tickets", page.getContent());
        mav.addObject("currentPage", pageNum);
        mav.addObject("totalPages", page.getTotalPages());
        mav.addObject("pageSize", pageSize);
        return mav;
    }

    @GetMapping("/tickets/all")
    public ModelAndView viewTickets() {

        List<Ticket> tickets = bookingFacade.getAllTickets();
        // Add tickets and pagination metadata to the model
        ModelAndView mav = new ModelAndView("allTickets");
        mav.addObject("tickets", tickets);
        return mav;
    }

    @GetMapping("/tickets/preload")
    public ResponseEntity<String> preloadTickets(@RequestParam String filePath) {
        try {
            // Call the method from BookingFacade, passing the file path
            bookingFacade.preloadTickets(filePath);
            return ResponseEntity.ok("Tickets loaded successfully from file: " + filePath);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error loading tickets: " + e.getMessage());
        }
    }
}
