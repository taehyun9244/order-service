package com.example.orderservice.controller;

import com.example.orderservice.dto.OrderDto;
import com.example.orderservice.model.OrderEntity;
import com.example.orderservice.service.OrderService;
import com.example.orderservice.vo.RequestOrder;
import com.example.orderservice.vo.ResponseOrder;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/order-service")
public class OrderController {

    private final Environment env;
    private final OrderService orderService;

    @Autowired
    public OrderController(Environment env, OrderService orderService) {
        this.env = env;
        this.orderService = orderService;
    }

    //작동 체크
    @GetMapping("/health_check")
    public String status(){
        return String.format("Working Order Service on PORT %s", env.getProperty("local.server.port"));
    }

    //주문 생성
    @PostMapping("/{userId}/orders")
    public ResponseEntity<ResponseOrder> creatOrder(@RequestBody RequestOrder orderDetails,
                                                    @PathVariable("userId") String userId){
        log.info("orderDetails={}", orderDetails);
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        OrderDto orderDto = mapper.map(orderDetails, OrderDto.class);
        orderDto.setUserId(userId);
        OrderDto creatOrder = orderService.creatOrder(orderDto);

        ResponseOrder responseOrder = mapper.map(creatOrder, ResponseOrder.class);
        log.info("responseOrder={}", responseOrder);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseOrder);
    }

    //유저별 주문전체 조회
    @GetMapping("/{userId}/orders")
    public ResponseEntity<List<ResponseOrder>> getOrder(@PathVariable String userId){
        Iterable<OrderEntity> orderList = orderService.getOrdersByUserId(userId);
        log.info("orderList={}", orderList);

        List<ResponseOrder> result = new ArrayList<>();
        orderList.forEach(v -> {
            result.add(new ModelMapper().map(v, ResponseOrder.class));
        });

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}
