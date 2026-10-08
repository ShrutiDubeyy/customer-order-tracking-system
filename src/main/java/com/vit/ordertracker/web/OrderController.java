package com.vit.ordertracker.web;

import com.vit.ordertracker.model.CustomerOrder;
import com.vit.ordertracker.model.OrderForm;
import com.vit.ordertracker.model.OrderStatus;
import com.vit.ordertracker.service.ItemService;
import com.vit.ordertracker.service.NotFoundException;
import com.vit.ordertracker.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orders;
    private final ItemService items;

    public OrderController(OrderService orders, ItemService items) {
        this.orders = orders;
        this.items = items;
    }

    @GetMapping
    public String list(@RequestParam(required = false) OrderStatus status,
                       @RequestParam(required = false) String customer,
                       @RequestParam(required = false) String item,
                       Model model) {
        model.addAttribute("orders", orders.search(status, customer, item));
        model.addAttribute("statuses", OrderStatus.values());
        model.addAttribute("fStatus", status);
        model.addAttribute("fCustomer", customer);
        model.addAttribute("fItem", item);
        return "orders/list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("orderForm", new OrderForm());
        model.addAttribute("items", items.search(null));
        return "orders/form";
    }

    @PostMapping("/new")
    public String create(@Valid @ModelAttribute("orderForm") OrderForm form, BindingResult result,
                         Model model, RedirectAttributes redirect) {
        if (!result.hasErrors()) {
            try {
                CustomerOrder order = orders.create(form.getCustomerName(), form.getItemId(), form.getQuantity());
                redirect.addFlashAttribute("message", "Order #" + order.getId() + " placed");
                return "redirect:/orders/" + order.getId();
            } catch (IllegalArgumentException | NotFoundException ex) {
                model.addAttribute("error", ex.getMessage());
            }
        }
        model.addAttribute("items", items.search(null));
        return "orders/form";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        CustomerOrder order = orders.get(id);
        model.addAttribute("order", order);
        model.addAttribute("nextStatuses", order.getStatus().nextStatuses());
        return "orders/detail";
    }

    @PostMapping("/{id}/status")
    public String updateStatus(@PathVariable Long id, @RequestParam OrderStatus status,
                               RedirectAttributes redirect) {
        try {
            orders.updateStatus(id, status);
            redirect.addFlashAttribute("message", "Status updated to " + status);
        } catch (IllegalStateException ex) {
            redirect.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/orders/" + id;
    }
}
