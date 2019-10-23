package com.jarrod.iocapplication;

import android.app.Activity;
import android.view.View;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class InjectManager {
	public static void inject(Activity activity) {
		//布局的注入
		injectLayout(activity);

		//控件的注入
		injectViews(activity);

		//事件的注入
		injectEvents(activity);
	}

	private static void injectLayout(Activity activity) {
		Class<? extends Activity> clazz = activity.getClass();
		ContentView contentView = clazz.getAnnotation(ContentView.class);
		if (contentView != null) {
			int layoutId = contentView.value();
			try {
				Method method = clazz.getMethod("setContentView", int.class);
				method.invoke(activity, layoutId);
			} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}

	private static void injectViews(Activity activity) {
		Class<? extends Activity> clazz = activity.getClass();
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			BindView bindView = field.getAnnotation(BindView.class);
			if (bindView == null) continue;
			try {
				Method findViewById = clazz.getMethod("findViewById", int.class);
				Object view = findViewById.invoke(activity, bindView.value());
				field.setAccessible(true);
				field.set(activity, view);
			} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}

	private static void injectEvents(Activity activity) {
		Class<? extends Activity> clazz = activity.getClass();
		//获取该activity中所有方法
		Method[] methods = clazz.getDeclaredMethods();
		//遍历方法
		for (Method method : methods) {
			//获取方法所有的注解
			Annotation[] annotations = method.getAnnotations();
			for (Annotation annotation : annotations) {
				//获取注解的类型
				Class<? extends Annotation> annotationType = annotation.annotationType();
				if (annotationType != null) {
					//获取注解的EventBase注解
					EventBase eventBase = annotationType.getAnnotation(EventBase.class);
					if (eventBase != null) {
						//获取EventBase注解的 监听setter方法 监听类型 回调方法
						String listenerSetter = eventBase.listenerSetter();
						Class listenerType = eventBase.listenerType();
						String callbackListener = eventBase.callbackListener();
						try {
							OnClick click = (OnClick) annotation;
							int[] values = click.value();
							//获取方法注解的value方法 拿到控件id数组
//							Method valueMethod = annotationType.getDeclaredMethod("value");
//							int[] values = (int[]) valueMethod.invoke(annotation);

							InjectEventProxyHandler handler = new InjectEventProxyHandler(activity);
							handler.add(callbackListener, method);
							//通过动态代理，
							Object listener = Proxy.newProxyInstance(listenerType.getClassLoader(), new Class[]{listenerType}, handler);

							assert values != null;
							for (int value : values) {
								View view = activity.findViewById(value);
								Method setXXX = View.class.getMethod(listenerSetter, listenerType);
								setXXX.invoke(view, listener);
							}
						} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}
}
