/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache license, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the license for the specific language governing permissions and
 * limitations under the license.
 */
package org.apache.logging.log4j.core.appender.mom.activemq;

import org.apache.logging.log4j.server.mom.activemq.ActiveMqBrokerServiceRule;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * A JUnit {@link TestRule} that builds a JmsTestConfig by getting a JMS broker
 * URL string from either a ActiveMqBrokerServiceRule or the one it was given in
 * the constructor.
 */
class JmsClientTestConfigRule implements TestRule {

	final ActiveMqBrokerServiceRule activeMqBrokerServiceRule;
	final String brokerUrlStr;
	private JmsClientTestConfig jmsClientTestConfig;
	final String jmsInitialContextFactoryClassName;
	final char[] password;
	final String userName;

	public JmsClientTestConfigRule(final ActiveMqBrokerServiceRule activeMqBrokerServiceRule,
			final String jmsInitialContextFactoryClassName, final String userName, final char[] password) {
		this.activeMqBrokerServiceRule = activeMqBrokerServiceRule;
		this.jmsInitialContextFactoryClassName = jmsInitialContextFactoryClassName;
		this.brokerUrlStr = null;
		this.userName = userName;
		this.password = password;
	}

	public JmsClientTestConfigRule(final String jmsInitialContextFactoryClassName, final String brokerUrlStr, final String userName,
			final char[] password) {
		this.activeMqBrokerServiceRule = null;
		this.jmsInitialContextFactoryClassName = jmsInitialContextFactoryClassName;
		this.brokerUrlStr = brokerUrlStr;
		this.userName = userName;
		this.password = password;
	}

	@Override
	public Statement apply(final Statement base, final Description description) {
		return new Statement() {

			@Override
			public void evaluate() throws Throwable {
				jmsClientTestConfig = new JmsClientTestConfig(jmsInitialContextFactoryClassName, getBrokerUrlString(),
						userName, password);
				try {
					base.evaluate();
				} finally {
					// no tear down.
				}
			}

			private String getBrokerUrlString() {
				return brokerUrlStr != null ? brokerUrlStr : activeMqBrokerServiceRule.getBrokerUrlString();
			}
		};
	}

	ActiveMqBrokerServiceRule getActiveMqBrokerServiceRule() {
		return activeMqBrokerServiceRule;
	}

	String getBrokerUrlStr() {
		return brokerUrlStr;
	}

	JmsClientTestConfig getJmsClientTestConfig() {
		return jmsClientTestConfig;
	}

	String getJmsInitialContextFactoryClassName() {
		return jmsInitialContextFactoryClassName;
	}

	char[] getPassword() {
		return password;
	}

	String getUserName() {
		return userName;
	}

	void setJmsClientTestConfig(final JmsClientTestConfig jmsClientTestConfig) {
		this.jmsClientTestConfig = jmsClientTestConfig;
	}

}