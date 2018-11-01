/*
 * Copyright 2002-2018 the original author or authors.
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

package org.springframework.web.reactive;

import org.springframework.lang.Nullable;
import org.springframework.ui.Model;
import org.springframework.validation.support.BindingAwareConcurrentModel;
import org.springframework.web.bind.support.WebBindingInitializer;
import org.springframework.web.bind.support.WebExchangeDataBinder;
import org.springframework.web.server.ServerErrorException;
import org.springframework.web.server.ServerWebExchange;

/**
 * 帮助将请求数据绑定到对象上并提供对具有控制器特定属性的共享模型的访问的上下文。
 * 提供为特定目标创建WebExchangeDataBinder的方法，命令对象将数据绑定和验证应用，或者没有目标对象用于从请求值进行简单类型转换。用于请求的默认模型的容器。
 *
 * @author Rossen Stoyanchev
 * @since 5.0
 */
public class BindingContext {

	@Nullable
	private final WebBindingInitializer initializer;

	private final Model model = new BindingAwareConcurrentModel();


	/**
	 * Create a new {@code BindingContext}.
	 */
	public BindingContext() {
		this(null);
	}

	/**
	 * Create a new {@code BindingContext} with the given initializer.
	 * @param initializer the binding initializer to apply (may be {@code null})
	 */
	public BindingContext(@Nullable WebBindingInitializer initializer) {
		this.initializer = initializer;
	}


	/**
	 * Return the default model.
	 */
	public Model getModel() {
		return this.model;
	}


	/**
	 * Create a {@link WebExchangeDataBinder} to apply data binding and
	 * validation with on the target, command object.
	 * @param exchange the current exchange
	 * @param target the object to create a data binder for
	 * @param name the name of the target object
	 * @return the created data binder
	 * @throws ServerErrorException if {@code @InitBinder} method invocation fails
	 */
	public WebExchangeDataBinder createDataBinder(ServerWebExchange exchange, @Nullable Object target, String name) {
		WebExchangeDataBinder dataBinder = new WebExchangeDataBinder(target, name);
		if (this.initializer != null) {
			this.initializer.initBinder(dataBinder);
		}
		return initDataBinder(dataBinder, exchange);
	}

	/**
	 * Initialize the data binder instance for the given exchange.
	 * @throws ServerErrorException if {@code @InitBinder} method invocation fails
	 */
	protected WebExchangeDataBinder initDataBinder(WebExchangeDataBinder binder, ServerWebExchange exchange) {
		return binder;
	}

	/**
	 * Create a {@link WebExchangeDataBinder} without a target object for type
	 * conversion of request values to simple types.
	 * @param exchange the current exchange
	 * @param name the name of the target object
	 * @return the created data binder
	 * @throws ServerErrorException if {@code @InitBinder} method invocation fails
	 */
	public WebExchangeDataBinder createDataBinder(ServerWebExchange exchange, String name) {
		return createDataBinder(exchange, null, name);
	}

}
