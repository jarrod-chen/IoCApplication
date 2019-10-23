package com.jarrod.iocapplication;

import android.util.ArrayMap;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

class InjectEventProxyHandler implements InvocationHandler {
	//防连点时间间隔
	private static final int QUICK_EVENT_TIME_SPAN = 500;

	// 目标对象
	private Object targetObject;

	private long lastClickTime;

	private ArrayMap<String, Method> map = new ArrayMap<>();

	InjectEventProxyHandler(Object targetObject) {
		this.targetObject = targetObject;
	}

	@Override
	//关联的这个实现类的方法被调用时将被执行
	/*InvocationHandler接口的方法，proxy表示代理，method表示原对象被调用的方法，args表示方法的参数*/
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		if (targetObject != null) {
			String name = method.getName();
			//阻塞事件 1S n次点击
			long timeSpan = System.currentTimeMillis() - lastClickTime;
			if (timeSpan < QUICK_EVENT_TIME_SPAN) {
				return null;
			}
			lastClickTime = System.currentTimeMillis();

			method = map.get(name);
			assert method != null;
			if (method.getGenericParameterTypes().length == 0) {
				return method.invoke(targetObject);
			} else {
				return method.invoke(targetObject, args);
			}
		}
		return null;
	}

	public void add(String callback, Method method) {
		map.put(callback, method);
	}
}
