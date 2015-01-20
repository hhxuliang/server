package com.way.chat.server;

import com.way.chat.common.util.Constants;

public class ServerWebSer {
	public void start() {
		System.out.println("服务器已经启动！");
		new Server(Constants.SERVER_PORT).start();
		new Server(Constants.SERVER_PORT_IOS).start();
	}
}
