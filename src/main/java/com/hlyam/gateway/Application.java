package com.hlyam.gateway;

import com.hlyam.chief.grpc.CookOrderRequest;
import com.hlyam.chief.grpc.CookResponse;
import com.hlyam.chief.grpc.MutinyChiefServiceGrpc;          // Mutiny stub import
import com.hlyam.gateway.dto.OrderRequestDTO;
import com.hlyam.waiter.grpc.CreateOrderRequest;
import com.hlyam.waiter.grpc.OrderResponse;
import com.hlyam.waiter.grpc.WaiterServiceGrpc;

import io.quarkus.grpc.GrpcClient;
import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/orders")
public class Application {

    @GrpcClient("waiter")
    WaiterServiceGrpc.WaiterServiceBlockingStub waiterService;

    @GrpcClient("chief")
    MutinyChiefServiceGrpc.MutinyChiefServiceStub chiefService;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/waiter")
    public String routeOrderByWaiter(OrderRequestDTO request) {
        System.out.println("Received order for waiter: " + request.order());
        CreateOrderRequest grpcReq = CreateOrderRequest.newBuilder().setOrder(request.order()).build();
        OrderResponse resp = waiterService.createOrder(grpcReq);
        return resp.getMessage()+" order received by waiter.";
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/chief")
    public Uni<String> routeOrderByConsumer(OrderRequestDTO request) {
        System.out.println("Received order for chief: " + request.order());

        CookOrderRequest grpcReq = CookOrderRequest.newBuilder()
                .setOrder(request.order())
                .build();

        return chiefService.cookOrder(grpcReq)
                          .onItem().transform(CookResponse::getMessage);
    }
}
