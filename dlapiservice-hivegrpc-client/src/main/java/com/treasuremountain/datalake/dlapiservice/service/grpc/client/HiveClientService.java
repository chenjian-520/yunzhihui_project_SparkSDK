package com.treasuremountain.datalake.dlapiservice.service.grpc.client;

import com.google.common.util.concurrent.Uninterruptibles;
import com.treasuremountain.datalake.dlapiservice.service.grpc.*;
import io.grpc.ManagedChannel;
import io.grpc.stub.StreamObserver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.*;

/**
 * Description:
 * <p>
 * Created by ref.tian on 2019/5/13.
 * Company: Foxconn
 * Project: TreasureMountain
 */
@Service
public class HiveClientService {

    @Autowired
    private GrpcClientMananer grpcClientMananer;
    private ScheduledExecutorService excuter = Executors.newScheduledThreadPool(20);
    @PostConstruct
    public void threadpool(){
//        call();
        excuter.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                call();
            }
        },0,5000,TimeUnit.MILLISECONDS);
    }

    public void call() {
        ManagedChannel channel = grpcClientMananer.getChannel();
        try{
            hiveSearchRequestOrBuilder hiveSearchRequestOrBuilder = hiveSearchRequest.newBuilder();
            ((hiveSearchRequest.Builder) hiveSearchRequestOrBuilder).setSql("select * from mfg_assembly_detail");
            hiveSearchGreeterGrpc.hiveSearchGreeterStub stub = hiveSearchGreeterGrpc.newStub(channel);
//        final CountDownLatch latch = new CountDownLatch(1);
            StreamObserver<hiveSearchResponse> hiveSearchResponseStreamObserver = new StreamObserver<hiveSearchResponse>() {
                @Override
                public void onNext(hiveSearchResponse hiveSearchResponse) {
                    System.out.println(hiveSearchResponse.getDataJson());
                }

                @Override
                public void onError(Throwable throwable) {
                    System.out.println("error：~~~~~~~~~~~~~~~~~~" + throwable.getMessage());
//                latch.countDown();
                    if(!channel.isShutdown()){
                        channel.shutdown();
                    }
                }

                @Override
                public void onCompleted() {
                    System.out.println("~~~~~~~~~~~~~~~~~~onCompleted" );
//                latch.countDown();
                    if(!channel.isShutdown()){
                        channel.shutdown();
                    }
                }
            };
            stub.hiveSearch(((hiveSearchRequest.Builder) hiveSearchRequestOrBuilder).build(), hiveSearchResponseStreamObserver);
            // 超時逻辑
//        if (!Uninterruptibles.awaitUninterruptibly(latch, 2, TimeUnit.SECONDS)) {
//            //TODO
//        }
            System.out.println("~~~~~~~~~~~~~~~~~~調用完成" );
        }catch (Exception e){
            if(!channel.isShutdown()){
                channel.shutdown();
            }
        }

    }
}
