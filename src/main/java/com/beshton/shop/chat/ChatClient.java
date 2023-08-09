package com.beshton.shop.chat;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class ChatClient {

    private static final Logger logger = Logger.getLogger(ChatClient.class.getName());

    private final ManagedChannel channel;
    private final ChatGrpc.ChatBlockingStub blockingStub;
    private final ChatGrpc.ChatStub asyncStub;

    public ChatClient(String host, int port) {
        this(ManagedChannelBuilder.forAddress(host, port).usePlaintext());
    }

    public ChatClient(ManagedChannelBuilder<?> channelBuilder) {
        channel = channelBuilder.build();
        blockingStub = ChatGrpc.newBlockingStub(channel);
        asyncStub = ChatGrpc.newStub(channel);
    }
//    ClientResponseObserver<HelloLogRequest, HelloReply> clientResponseObserver =
//            new ClientResponseObserver<HelloRequest, HelloReply>() {
//
//                ClientCallStreamObserver<HelloRequest> requestStream;
//
//                @Override
//                public void beforeStart(final ClientCallStreamObserver<HelloRequest> requestStream) {
//                    this.requestStream = requestStream;
//                    // Set up manual flow control for the response stream. It feels backwards to configure the response
//                    // stream's flow control using the request stream's observer, but this is the way it is.
////            requestStream.disableAutoRequestWithInitial(1);
//
//                    // Set up a back-pressure-aware producer for the request stream. The onReadyHandler will be invoked
//                    // when the consuming side has enough buffer space to receive more messages.
//                    //
//                    // Messages are serialized into a transport-specific transmit buffer. Depending on the size of this buffer,
//                    // MANY messages may be buffered, however, they haven't yet been sent to the server. The server must call
//                    // request() to pull a buffered message from the client.
//                    //
//                    // Note: the onReadyHandler's invocation is serialized on the same thread pool as the incoming
//                    // StreamObserver's onNext(), onError(), and onComplete() handlers. Blocking the onReadyHandler will prevent
//                    // additional messages from being processed by the incoming StreamObserver. The onReadyHandler must return
//                    // in a timely manner or else message processing throughput will suffer.
//                    requestStream.setOnReadyHandler(new Runnable() {
//                        // An iterator is used so we can pause and resume iteration of the request data.
//                        Iterator<String> iterator = names().iterator();
//
//                        @Override
//                        public void run() {
//                            // Start generating values from where we left off on a non-gRPC thread.
//                            while (requestStream.isReady()) {
//                                if (iterator.hasNext()) {
//                                    // Send more messages if there are more messages to send.
//                                    String name = iterator.next();
//                                    logger.info("--> " + name);
//                                    HelloRequest request = HelloRequest.newBuilder().setName(name).build();
//                                    requestStream.onNext(request);
//                                } else {
//                                    // Signal completion if there is nothing left to send.
//                                    requestStream.onCompleted();
//                                }
//                            }
//                        }
//                    });
//                }
//
//                @Override
//                public void onNext(HelloReply value) {
//                    logger.info("<-- " + value.getMessage());
//                    // Signal the sender to send one message.
//                    requestStream.request(1);
//                }
//
//                @Override
//                public void onError(Throwable t) {
//                    t.printStackTrace();
//                    done.countDown();
//                }
//
//                @Override
//                public void onCompleted() {
//                    logger.info("All Done");
//                    done.countDown();
//                }
//            };


    public void logout() throws InterruptedException {
//        LogoutRequest lor = new LogoutRequest.newBuilder().build();
//        if (blockingStub != null)
//            blockingStub.logout(lor);
//
//        if (asyncStub != null)
//            asyncStub.logout(lor);


    }
    public void login(String user, String password) throws InterruptedException {
//        LoginRequest lir = new LoginRequest.Builder.setName(user)
//                .setPassword(password).build();
//        LoginResponse bloginresponse = null;
//        if (blockingStub != null)
//            bloginresponse = blockingStub.login(lir, null);
//        if (asyncStub != null)
//            bloginresponse = asyncStub.login(lir, null);
    }

    public static void main(String[] args) throws InterruptedException {
        ChatClient chatClient = null;
        try{
            chatClient = new ChatClient("0.0.0.0", 6262);
            chatClient.login("dave", "super-secret");
//            StreamResponse chatResponse = chatClient.stream();
//            ayncStub.Stream(clientResponseObserver);


        }catch (Exception e){
            System.out.println("exception:" + e);
        }finally {
            chatClient.logout();

        }
    }


}