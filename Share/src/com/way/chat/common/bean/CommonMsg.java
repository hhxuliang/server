package com.way.chat.common.bean;

import java.io.Serializable;

public class CommonMsg implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String arg1;
	private String arg2;
	private String arg3;

	public String getarg1() {
		return arg1;
	}

	public void setarg1(String s) {
		arg1 = s;
	}

	public String getarg2() {
		return arg2;
	}

	public void setarg2(String s) {
		arg2 = s;
	}

	public String getarg3() {
		return arg3;
	}

	public void setarg3(String s) {
		arg3 = s;
	}

	@Override
	public String toString() {
		String js = null;
		js = "{\"arg1\":\"" + arg1 + "\",\"arg2\":\"" + arg2 + "\",\"arg3\":\""
				+ arg3 + "\"}";
		return js;
	}
}
