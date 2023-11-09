package com.game.copycat.view;

import com.game.copycat.domain.MemberRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.BindingResultUtils;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {
    @GetMapping("/login")
    public String login(Model model) {
//        MemberRequest build = MemberRequest.builder().build();
        MemberRequest memberRequest = new MemberRequest();
        model.addAttribute("bindingResult", new BeanPropertyBindingResult(new Object(), "bindingResult"));
        model.addAttribute("memberRequest", memberRequest);
        return "login";
    }
}
