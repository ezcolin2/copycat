package com.game.copycat.view;

import com.game.copycat.domain.JoinRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {
    @GetMapping("/login")
    public String login(Model model) {
        JoinRequest memberRequest = new JoinRequest();
        // 타임리프에서 오류를 방지하기 위해서 비어있는 BindingResult와 JoinRequest를 넣어줌.
        model.addAttribute("bindingResult", new BeanPropertyBindingResult(new Object(), "bindingResult"));
        model.addAttribute("memberRequest", memberRequest);
        return "login";
    }

    @GetMapping("/rooms")
    public String rooms(Model model) {
        return "rooms";
    }
}
