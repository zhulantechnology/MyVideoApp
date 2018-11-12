package com.jeffrey.okhttp.listener;

/**
 * @author jeffrey
 * @function 为了实现json数据的“字节码对象”到实体对象的转换封装
 * 为什么要有这个DisposeDataHandle ,因为需要将监听器Listener和实体类通过一个封装
 * 的参数传过去，其实可以用两个参数分别传递，是否就不需要这个类了？
 */
public class DisposeDataHandle
{
	public DisposeDataListener mListener = null;
	public Class<?> mClass = null;
	public String mSource = null;

	public DisposeDataHandle(DisposeDataListener listener)
	{
		this.mListener = listener;
	}

	public DisposeDataHandle(DisposeDataListener listener, Class<?> clazz)
	{
		this.mListener = listener;
		this.mClass = clazz;
	}

	public DisposeDataHandle(DisposeDataListener listener, String source)
	{
		this.mListener = listener;
		this.mSource = source;
	}
}