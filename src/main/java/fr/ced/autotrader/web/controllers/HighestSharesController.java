package fr.ced.autotrader.web.controllers;


import fr.ced.autotrader.algo.SharePicker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HighestSharesController {

  @Autowired
  private SharePicker sharePicker;

  @RequestMapping("/highShares")
  public String getHighShares( Model model) {

    model.addAttribute("results", sharePicker.getTopFive());
    String view = "highShares";
    model.addAttribute("view", view);
    return view;
  }

}
