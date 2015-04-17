package yachmu.interfaces;

import yachmu.annotations.Controller;
import yachmu.annotations.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class MainController {

    @RequestMapping("/")
    public String hello(
            HttpServletRequest request, HttpServletResponse response) {
        return "Hello, " + request.getParameter("name");
    }

    @RequestMapping("/hi")
    public String hi(HttpServletRequest request, HttpServletResponse response) {
        return "Hi, " + request.getParameter("name");
    }

}
