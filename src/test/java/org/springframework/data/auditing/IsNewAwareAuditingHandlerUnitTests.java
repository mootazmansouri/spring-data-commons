/*
 * Copyright 2008-2014 the original author or authors.
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
package org.springframework.data.auditing;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.data.mapping.context.MappingContext;
import org.springframework.data.mapping.context.SampleMappingContext;
import org.springframework.data.support.IsNewStrategy;
import org.springframework.data.support.IsNewStrategyFactory;

/**
 * Unit test for {@code AuditingHandler}.
 * 
 * @author Oliver Gierke
 * @since 1.5
 */
@RunWith(MockitoJUnitRunner.class)
public class IsNewAwareAuditingHandlerUnitTests extends AuditingHandlerUnitTests {

	@Mock IsNewStrategyFactory factory;
	@Mock IsNewStrategy strategy;

	@Before
	public void init() {
		when(factory.getIsNewStrategy(Mockito.any(Class.class))).thenReturn(strategy);
	}

	@Override
	@SuppressWarnings("deprecation")
	protected IsNewAwareAuditingHandler getHandler() {
		return new IsNewAwareAuditingHandler(factory);
	}

	@Test
	public void delegatesToMarkCreatedForNewEntity() {

		when(strategy.isNew(Mockito.any(Object.class))).thenReturn(true);
		AuditedUser user = new AuditedUser();
		getHandler().markAudited(user);

		assertThat(user.createdDate, is(notNullValue()));
		assertThat(user.modifiedDate, is(notNullValue()));
	}

	@Test
	public void delegatesToMarkModifiedForNonNewEntity() {

		when(strategy.isNew(Mockito.any(Object.class))).thenReturn(false);
		AuditedUser user = new AuditedUser();
		getHandler().markAudited(user);

		assertThat(user.createdDate, is(nullValue()));
		assertThat(user.modifiedDate, is(notNullValue()));
	}

	/**
	 * @see DATACMNS-365
	 */
	@Test(expected = IllegalArgumentException.class)
	public void rejectsNullMappingContext() {
		new IsNewAwareAuditingHandler(
				(MappingContext<? extends PersistentEntity<?, ?>, ? extends PersistentProperty<?>>) null);
	}

	/**
	 * @see DATACMNS-365
	 */
	@Test
	public void setsUpHandlerWithMappingContext() {
		new IsNewAwareAuditingHandler(new SampleMappingContext());
	}
}
