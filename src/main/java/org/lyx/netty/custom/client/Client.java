package org.lyx.netty.custom.client;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.lyx.netty.NettyConstant;
import org.lyx.netty.custom.codec.NettyMessageDecoder;
import org.lyx.netty.custom.codec.NettyMessageEncoder;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;

public class Client {

	public static void main(String[] args) throws Exception {
		new Client().connect(NettyConstant.PORT, NettyConstant.REMOTEIP);
	}

	private ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
	//创建工作线程组
	EventLoopGroup group = new NioEventLoopGroup();

	public void connect(int port, String host) throws Exception {
		// 配置客户端NIO线程组
		try {
			Bootstrap b = new Bootstrap();
			b.group(group).channel(NioSocketChannel.class).option(ChannelOption.TCP_NODELAY, true)
					.handler(new ChannelInitializer<SocketChannel>() {
						@Override
						public void initChannel(SocketChannel ch) throws Exception {
							ch.pipeline().addLast(new NettyMessageDecoder(1024 * 1024, 4, 4));
							ch.pipeline().addLast(new NettyMessageEncoder());
							ch.pipeline().addLast("readTimeoutHandler", new ReadTimeoutHandler(50));
							ch.pipeline().addLast("LoginAuthHandler", new LoginAuthReqHandler());
							ch.pipeline().addLast("HeartBeatHandler", new HeartBeatReqHandler());
						}
					});
			// 发起异步连接操作
			ChannelFuture future = b.connect(new InetSocketAddress(host, port),
					new InetSocketAddress(NettyConstant.LOCALIP, NettyConstant.LOCAL_PORT)).sync();
			
			//手动发测试数据，验证是否会产生TCP粘包/拆包情况
//			Channel c = future.channel();
//			
//			for (int i = 0; i < 500; i++) {
//				NettyMessage message = new NettyMessage();
//				Header header = new Header();
//				header.setSessionID(1001L);
//				header.setPriority((byte) 1);
//				header.setType((byte) 0);
//				message.setHeader(header);
//				message.setBody("我是请求数据" + i);
//				c.writeAndFlush(message);
//			}
			
			future.channel().closeFuture().sync();
		} finally {
			// 所有资源释放完成之后，清空资源，再次发起重连操作
			executor.execute(new Runnable() {
				@Override
				public void run() {
					try {
						TimeUnit.SECONDS.sleep(1);
						try {
							connect(NettyConstant.PORT, NettyConstant.REMOTEIP);// 发起重连操作
						} catch (Exception e) {
							e.printStackTrace();
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			});
		}
	}

}
