package org.lyx.netty.custom.codec;

import java.io.IOException;

import org.jboss.marshalling.Unmarshaller;

import io.netty.buffer.ByteBuf;

/**
 * 根据编码规则去解码
 * @author landyChris
 *
 */
public class MarshallingDecoder {
	
	private Unmarshaller unmarshaller;

	public MarshallingDecoder() throws IOException{
		this.unmarshaller = MarshallingCodeCFactory.buildUnMarshalling();
	}
	
	public Object decode(ByteBuf in) throws Exception {
		try {
			//1 首先读取4个长度(实际body内容长度)
			int bodySize = in.readInt();
			//2 获取实际body的缓冲内容
			int readIndex = in.readerIndex();
			ByteBuf buf = in.slice(readIndex, bodySize);
			//3 转换
			ChannelBufferByteInput input = new ChannelBufferByteInput(buf);
			//4 读取操作:
			this.unmarshaller.start(input);
			Object ret = this.unmarshaller.readObject();
			this.unmarshaller.finish();
			//5 读取完毕以后, 更新当前读取起始位置: 
			//因为使用slice方法，原buf的位置还在readIndex上，故需要将位置重新设置一下 
			in.readerIndex(in.readerIndex() + bodySize);
			
			return ret;
			
		} finally {
			this.unmarshaller.close();
		}
	}

}
