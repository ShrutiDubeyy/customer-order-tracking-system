package com.vit.ordertracker.web;

import com.vit.ordertracker.service.AlertService;
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
    private final AlertService alerts;

    public HomeController(ItemService items, OrderService orders, AlertService alerts) {
        this.items = items;
        this.orders = orders;
        this.alerts = alerts;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("itemCount", items.count());
        model.addAttribute("orderCount", orders.count());
        model.addAttribute("lowStockCount", alerts.lowStockItems().size());
        model.addAttribute("delayedCount", alerts.delayedOrders().size());
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

    @GetMapping("/alerts")
    public String alertsPage(Model model) {
        model.addAttribute("lowStock", alerts.lowStockItems());
        model.addAttribute("delayed", alerts.delayedOrders());
        model.addAttribute("delayHours", alerts.getDelayHours());
        return "alerts";
    }
}
