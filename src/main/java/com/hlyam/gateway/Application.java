package com.hlyam.gateway;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import com.hlyam.consumer.grpc.ConsumerServiceGrpc;
import com.hlyam.gateway.dto.OrderRequestDTO;
import com.hlyam.waiter.grpc.CreateOrderRequest;
import com.hlyam.waiter.grpc.OrderResponse;
import com.hlyam.waiter.grpc.WaiterServiceGrpc;

import io.quarkus.grpc.GrpcClient;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import io.smallrye.common.annotation.Blocking;

@Path("/orders")
public class Application {

	@GrpcClient("waiter")
	WaiterServiceGrpc.WaiterServiceBlockingStub waiterService;

	@GrpcClient("consumer")
	ConsumerServiceGrpc.ConsumerServiceBlockingStub consumerServiceBlocking;

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/waiter")
	public String routeOrderByWaiter(OrderRequestDTO request) {
		System.out.println("Received order for waiter: " + request.order());

		CreateOrderRequest grpcReq = CreateOrderRequest.newBuilder().setOrder(request.order()).build();

		OrderResponse resp = waiterService.createOrder(grpcReq);

		return resp.getMessage() + " direct call to waiter.";
	}

	@POST
	@Path("/consumer")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Blocking
	public String routeOrderByConsumer(OrderRequestDTO request) {
		CreateOrderRequest grpcReq = CreateOrderRequest.newBuilder()
	            .setOrder(request.order())
	            .build();
	            
	    // 2. Execute the blocking call safely
	    OrderResponse resp = consumerServiceBlocking.createOrder(grpcReq);
	    
	    // 3. Return a synchronous String
	    return resp.getMessage() + " received by consumer.";
	}

}
