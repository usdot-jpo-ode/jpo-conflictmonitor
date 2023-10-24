package us.dot.its.jpo.ode.messagesender;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;

@Controller
@RequestMapping("/")
public class GuiController {
    
    final static Logger logger = LoggerFactory.getLogger(GuiController.class);

    
    @Autowired TestMessageSenderConfiguration config;
    
    @GetMapping("/")
    public String viewMap(Model model) {
        logger.info("viewMap");
        model.addAttribute("basemapUrl", config.getBasemapUrl());
        model.addAttribute("basemapAttribution", config.getBasemapAttribution());
        return "index";
    }

    @RequestMapping("/help")
    public String help() {
        return "help";
    }
}
