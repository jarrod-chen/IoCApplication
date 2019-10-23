package com.jarrod.iocapplication;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface EventBase {

	//监听方法
	String listenerSetter();

	//监听的类型
	Class listenerType();


	//回调方法
	String callbackListener();
}
