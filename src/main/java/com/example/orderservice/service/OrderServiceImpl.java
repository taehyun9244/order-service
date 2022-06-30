package com.example.orderservice.service;

import com.example.orderservice.dto.OrderDto;
import com.example.orderservice.model.OrderEntity;
import com.example.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService{
    private final OrderRepository orderRepository;

    //주문 생성
    @Override
    public OrderDto creatOrder(OrderDto orderDto) {
        if (orderDto.getProductId() == null && orderDto.getUserId() == null){
            throw new RuntimeException("주문번호가 없거나 회원이 아닙니다");
        }
        orderDto.setOrderId(UUID.randomUUID().toString());
        orderDto.setTotalPrice(orderDto.getQty() * orderDto.getUnitPrice());

        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        OrderEntity orderEntity = mapper.map(orderDto, OrderEntity.class);
        orderRepository.save(orderEntity);

        OrderDto result = mapper.map(orderEntity, OrderDto.class);
        log.info("result={}", result);

        return result;
    }

    //단일
    @Override
    public OrderDto getOrderByOrderId(String orderId) {
        OrderEntity orderEntity = orderRepository.findByOrderId(orderId);
        OrderDto orderDto = new ModelMapper().map(orderEntity, OrderDto.class);

        return orderDto;
    }

    //전체 목록
    @Override
    public Iterable<OrderEntity> getOrdersByUserId(String userId) {
        return orderRepository.findByUserId(userId);
    }
}
