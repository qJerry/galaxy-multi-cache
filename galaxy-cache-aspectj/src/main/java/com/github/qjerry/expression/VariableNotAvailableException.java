package com.github.qjerry.expression;

import org.springframework.expression.EvaluationException;

/**
 * <p>Name:Galaxy-Multi-Cache</p>
 * <p>Desc: </p>
 *
 * @author Jerry
 * @version 1.0
 * @since 2021/5/25
 */
public class VariableNotAvailableException extends EvaluationException {

	private final String name;

	public VariableNotAvailableException(String name) {
		super("Variable not available");
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
