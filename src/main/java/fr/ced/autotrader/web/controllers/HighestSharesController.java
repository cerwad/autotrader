package fr.ced.autotrader.web.controllers;


import fr.ced.autotrader.algo.SharePicker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
@Slf4j
@Controller
public class HighestSharesController {

  @Autowired
  private SharePicker sharePicker;

  @RequestMapping("/highShares")
  public String getHighShares( Model model) {
    log.info("Calling /highShares");
    model.addAttribute("results", sharePicker.getTopFive());
    String view = "highShares";
    model.addAttribute("view", view);
    return view;
  }

}
