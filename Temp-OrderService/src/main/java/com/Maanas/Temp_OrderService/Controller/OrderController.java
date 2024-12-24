package com.Maanas.Temp_OrderService.Controller;

import org.springframework.web.bind.annotation.*;

@RestController
public class OrderController {

    @GetMapping("/order/{id}")
    public String getOrder(@PathVariable String id) {
        return "Order details for order ID: " + id;
    }

    @PostMapping("/orders")
    public String postOrder(@RequestBody String id){
        return "The order you added is" + id;
    }
}
