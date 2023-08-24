package com.bestshop.admin.user;

import com.bestshop.common.entity.Role;
import com.bestshop.common.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class UserController {

    @Autowired
    private UserService service;

    @GetMapping("/users")
    public String listAll(Model model){
        List<User> listUsers = service.listAll();
        model.addAttribute("listUsers", listUsers);
        return "users";
    }

    @GetMapping("/users/new")
    public String newUser(Model model){
        List<Role> listRoles = service.listRoles();

        User user = new User();
        user.setEnabled(true);
        model.addAttribute("user", user);
        model.addAttribute("listRoles", listRoles);
        model.addAttribute("pageTitle", "Create New User");

        return "user_form";
    }

    @PostMapping("users/save")
    public String saveUser(User user, RedirectAttributes redirectAttributes){
        service.save(user);

        redirectAttributes.addFlashAttribute("message", "The user has been saved successfully.");
        return "redirect:/users";
    }

    @GetMapping("users/delete/{id}")
    public String deleteUser(@PathVariable(name = "id") Integer id, Model model, RedirectAttributes redirectAttributes) throws UserNotFoundException {
        try{
            service.deleteById(id);
            redirectAttributes.addFlashAttribute("message", "The user ID " + id + " has been deleted successfully");
        }catch (UserNotFoundException ex){
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
        }
        return "redirect:/users";
    }

    @GetMapping("/users/edit/{id}")
    public String editUser(@PathVariable(name = "id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        try {
            User user = service.get(id);
            List<Role> listRoles = service.listRoles();

            model.addAttribute("user", user);
            model.addAttribute("pageTitle", "Edit User (ID:" + id +")");
            model.addAttribute("listRoles", listRoles);

            return "user_form";
        } catch (UserNotFoundException ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
            return "redirect:/users";
        }
    }

    @GetMapping("users/{id}/enabled/{enabled}")
    public String updateUserEnabledStatus(@PathVariable(name = "id") Integer id,
                                        @PathVariable(name = "enabled") boolean enabled,
                                        RedirectAttributes redirectAttributes){
        service.updateEnabledStatu(id, enabled);
        String status = enabled ? "Enabled" : "Disabled";
        redirectAttributes.addFlashAttribute("message", "User iD " + id + " has been " + status);
        return "redirect:/users";
    }


}
