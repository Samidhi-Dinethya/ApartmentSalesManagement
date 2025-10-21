package com.example.apartmentsalesmanagementsystem.controller;

import com.example.apartmentsalesmanagementsystem.service.ApartmentService;
import com.example.apartmentsalesmanagementsystem.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/apartments")
public class ApartmentController {
    
    @Autowired
    private ApartmentService apartmentService;
    
    @Autowired
    private ReviewService reviewService;
    
    @GetMapping
    public String listApartments(Model model) {
        model.addAttribute("apartments", apartmentService.findAvailableApartments());
        return "apartments/list";
    }
    
    @GetMapping("/{id}")
    public String viewApartment(@PathVariable Long id, Model model) {
        apartmentService.findById(id).ifPresent(apartment -> {
            model.addAttribute("apartment", apartment);
            
            // Add review data
            model.addAttribute("reviews", reviewService.getApprovedReviewsByApartment(apartment));
            model.addAttribute("averageRating", reviewService.getAverageRatingForApartment(apartment));
            model.addAttribute("totalReviews", reviewService.getTotalReviewCountForApartment(apartment));
        });
        return "apartments/detail";
    }
    
    @GetMapping("/search")
    public String searchApartments(@RequestParam String query, Model model) {
        model.addAttribute("apartments", apartmentService.searchApartments(query));
        model.addAttribute("searchQuery", query);
        return "apartments/search";
    }
    
    @GetMapping("/filter")
    public String filterApartments(@RequestParam(required = false) String city,
                                  @RequestParam(required = false) Integer minBedrooms,
                                  @RequestParam(required = false) Double minPrice,
                                  @RequestParam(required = false) Double maxPrice,
                                  Model model) {
        // This would need to be implemented with more complex filtering logic
        model.addAttribute("apartments", apartmentService.findAvailableApartments());
        return "apartments/filter";
    }
}
