package org.lyx.netty.custom.client;

import org.lyx.netty.MessageType;
import org.lyx.netty.ResultType;
import org.lyx.netty.custom.struct.Header;
import org.lyx.netty.custom.struct.NettyMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;

/**
 * @author landyChris
 * @date 2017年8月31日
 * @version 1.0
 */
public class LoginAuthReqHandler extends ChannelInboundHandlerAdapter {
	private static final Logger LOGGER = LoggerFactory.getLogger(LoginAuthReqHandler.class);

	/**
	 * Calls {@link ChannelHandlerContext#fireChannelActive()} to forward to the
	 * next {@link ChannelHandler} in the {@link ChannelPipeline}.
	 * 
	 * Sub-classes may override this method to change behavior.
	 */
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		LOGGER.info("通道激活，握手请求验证...");
		ctx.writeAndFlush(buildLoginReq());
	}

	/**
	 * Calls {@link ChannelHandlerContext#fireChannelRead(Object)} to forward to
	 * the next {@link ChannelHandler} in the {@link ChannelPipeline}.
	 * 
	 * Sub-classes may override this method to change behavior.
	 */
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		NettyMessage message = (NettyMessage) msg;

		// 如果是握手应答消息，需要判断是否认证成功
		if (message.getHeader() != null && message.getHeader().getType() == MessageType.LOGIN_RESP.value()) {
			byte loginResult = (Byte) message.getBody();
			if (loginResult != ResultType.SUCCESS.value()) {
				// 握手失败，关闭连接
				ctx.close();
			} else {
				System.out.println("Login is ok : " + message);
				ctx.fireChannelRead(msg);
			}
		} else
			ctx.fireChannelRead(msg);
	}
	/**
	 * 客户端与服务端建立了连接之后，由客户端发送握手请求消息，握手请求消息的定义如下：
	 * 1.消息头的type为3
	 * 2.可选附件个数为0
	 * 3.消息体为空
	 * 4.握手消息的长度为22个字节
	 * @return
	 */
	private NettyMessage buildLoginReq() {
		NettyMessage message = new NettyMessage();
		Header header = new Header();
		header.setType(MessageType.LOGIN_REQ.value());
		message.setHeader(header);
		return message;
	}

	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		ctx.fireExceptionCaught(cause);
	}
}
