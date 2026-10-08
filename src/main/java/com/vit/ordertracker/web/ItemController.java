package com.vit.ordertracker.web;

import com.vit.ordertracker.model.Item;
import com.vit.ordertracker.service.ItemService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/items")
public class ItemController {

    private final ItemService service;

    public ItemController(ItemService service) {
        this.service = service;
    }

    @GetMapping
    public String list(@RequestParam(required = false) String q, Model model) {
        model.addAttribute("items", service.search(q));
        model.addAttribute("q", q);
        return "items/list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("item", new Item());
        model.addAttribute("title", "Add item");
        return "items/form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("item", service.get(id));
        model.addAttribute("title", "Edit item");
        return "items/form";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("item") Item item, BindingResult result,
                       Model model, RedirectAttributes redirect) {
        model.addAttribute("title", item.getId() == null ? "Add item" : "Edit item");
        if (result.hasErrors()) {
            return "items/form";
        }
        try {
            service.save(item);
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            return "items/form";
        }
        redirect.addFlashAttribute("message", "Item saved");
        return "redirect:/items";
    }
}
