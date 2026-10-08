package com.vit.ordertracker.web;

import com.vit.ordertracker.service.ItemService;
import com.vit.ordertracker.service.OrderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {

    private final ItemService items;
    private final OrderService orders;

    public HomeController(ItemService items, OrderService orders) {
        this.items = items;
        this.orders = orders;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("itemCount", items.count());
        model.addAttribute("orderCount", orders.count());
        return "index";
    }

    @GetMapping("/track")
    public String track(@RequestParam(required = false) Long id, Model model) {
        if (id != null) {
            orders.find(id).ifPresentOrElse(
                    o -> model.addAttribute("order", o),
                    () -> model.addAttribute("error", "Order not found"));
        }
        return "track";
    }
}
