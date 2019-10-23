package com.jarrod.iocapplication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

@ContentView(R.layout.activity_main)
public class MainActivity extends AppCompatActivity {

	@BindView(R.id.tv_sub)
	private TextView tvSubject;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		InjectManager.inject(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		SayHello sayHello = (SayHello) Proxy.newProxyInstance(SayHello.class.getClassLoader(),
				new Class[]{SayHello.class},
				new InvocationHandler() {
					@Override
					public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
						Toast.makeText(getBaseContext(), method.getName(), Toast.LENGTH_LONG).show();
						return null;
					}
				});
		sayHello.say();
	}

	@OnClick(R.id.tv_sub)
	public void onTvSubClick(View view) {
		Toast.makeText(this, ((TextView) view).getText(), Toast.LENGTH_SHORT).show();
	}

	@OnLongClick(R.id.tv_sub)
	public void onTvSubLongClick(View view) {
		Toast.makeText(this, "Hello Long Click", Toast.LENGTH_LONG).show();
	}
}
