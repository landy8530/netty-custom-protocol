/*
 * Copyright 2013-2018 Lilinfeng.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.lyx.netty;

/**
 * @author Lilinfeng
 * @date 2014年3月15日
 * @version 1.0
 */
public enum MessageType {
	/**
	 * 业务请求消息
	 */
	SERVICE_REQ((byte) 0),
	/**
	 * 业务响应（应答）消息
	 */
	SERVICE_RESP((byte) 1), 
	/**
	 * 业务ONE WAY消息（既是请求消息又是响应消息）
	 */
	ONE_WAY((byte) 2),
	/**
	 * 握手请求消息
	 */
	LOGIN_REQ((byte) 3), 
	/**
	 * 握手响应（应答）消息
	 */
	LOGIN_RESP((byte) 4),
	/**
	 * 心跳请求消息
	 */
	HEARTBEAT_REQ((byte) 5), 
	/**
	 * 心跳响应（应答）消息
	 */
	HEARTBEAT_RESP((byte) 6);

	private byte value;

	private MessageType(byte value) {
		this.value = value;
	}

	public byte value() {
		return this.value;
	}
}
