package com.hlyam.gateway;

import com.hlyam.gateway.dto.OrderRequestDTO;
import com.hlyam.waiter.grpc.CreateOrderRequest;
import com.hlyam.waiter.grpc.OrderResponse;
import com.hlyam.waiter.grpc.WaiterServiceGrpc;

import io.quarkus.grpc.GrpcClient;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/orders")
public class Application {

    @GrpcClient("waiter")
    WaiterServiceGrpc.WaiterServiceBlockingStub waiterService;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String routeOrder(OrderRequestDTO request) {
    	System.out.println("Received order: " + request.order());

        CreateOrderRequest grpcReq = CreateOrderRequest.newBuilder()
                .setOrder(request.order())
                .build();

        OrderResponse resp = waiterService.createOrder(grpcReq);

        return resp.getMessage();
    }
    
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String test() {
        System.out.println("GET /orders hit!");
        return "OK";
    }
}
