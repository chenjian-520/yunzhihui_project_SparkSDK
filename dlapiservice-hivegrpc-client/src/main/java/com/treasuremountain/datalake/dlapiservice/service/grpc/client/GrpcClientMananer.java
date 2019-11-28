package com.treasuremountain.datalake.dlapiservice.service.grpc.client;


import io.grpc.ManagedChannel;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import io.grpc.netty.shaded.io.netty.handler.codec.http2.Http2SecurityUtil;
import io.grpc.netty.shaded.io.netty.handler.ssl.*;
import io.grpc.netty.shaded.io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.net.ssl.SSLException;
import java.util.concurrent.TimeUnit;

/**
 * Description:
 * <p>
 * Created by ref.tian on 2019/5/13.
 * Company: Foxconn
 * Project: TreasureMountain
 */
@Component
public class GrpcClientMananer {

    @Value("${grpc.client.host}")
    private String host;
    @Value("${grpc.client.port}")
    private Integer port;

    public ManagedChannel getChannel() {
        NettyChannelBuilder ncb = null;
        try {
            ncb = NettyChannelBuilder.forAddress(host, port).sslContext(SslContextBuilder.forClient()
                    .sslProvider(OpenSsl.isAlpnSupported() ? SslProvider.OPENSSL : SslProvider.JDK)
                    .ciphers(Http2SecurityUtil.CIPHERS, SupportedCipherSuiteFilter.INSTANCE)
                    .trustManager(InsecureTrustManagerFactory.INSTANCE)
                    .applicationProtocolConfig(
                            new ApplicationProtocolConfig(
                                    ApplicationProtocolConfig.Protocol.ALPN,
                                    ApplicationProtocolConfig.SelectorFailureBehavior.NO_ADVERTISE,
                                    ApplicationProtocolConfig.SelectedListenerFailureBehavior.ACCEPT,
                                    ApplicationProtocolNames.HTTP_2,
                                    ApplicationProtocolNames.HTTP_1_1))
                    .build())
                    .keepAliveWithoutCalls(false);
        } catch (SSLException e) {
            e.printStackTrace();
            System.out.println("建立grpc连接发现异常！" + e.getMessage());
        }
        return ncb.build();
    }
}
