package com.vit.ordertracker.web;

import com.vit.ordertracker.service.AlertService;
import com.vit.ordertracker.service.ItemService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final ItemService items;
    private final AlertService alerts;

    public HomeController(ItemService items, AlertService alerts) {
        this.items = items;
        this.alerts = alerts;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("itemCount", items.count());
        model.addAttribute("lowStockCount", alerts.lowStockItems().size());
        return "index";
    }

    @GetMapping("/alerts")
    public String alertsPage(Model model) {
        model.addAttribute("lowStock", alerts.lowStockItems());
        return "alerts";
    }
}
