/*******************************************************************************
 * Copyright (c) 2012 Original authors and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Original authors and others - initial API and implementation
 ******************************************************************************/
package org.eclipse.nebula.widgets.nattable.config;


import org.eclipse.nebula.widgets.nattable.config.DefaultComparator;
import org.junit.Assert;
import org.junit.Test;

public class DefaultComparatorTest {

	private DefaultComparator defaultComparator = DefaultComparator.getInstance();

	@Test
	public void testCompareNonNullComparables() {
		Assert.assertEquals("abc".compareTo("abc"), defaultComparator.compare("abc", "abc"));
		Assert.assertEquals("abc".compareTo("def"), defaultComparator.compare("abc", "def"));
		Assert.assertEquals("def".compareTo("abc"), defaultComparator.compare("def", "abc"));
	}

	@Test
	public void testCompareNullAB() {
		Assert.assertEquals(0, defaultComparator.compare(null, null));
	}

	@Test
	public void testCompareNullA() {
		Assert.assertEquals(-1, defaultComparator.compare(null, "abc"));
	}

	@Test
	public void testCompareNullB() {
		Assert.assertEquals(1, defaultComparator.compare("abc", null));
	}

	@Test
	public void testCompareNonComparables() {
		Assert.assertEquals(0, defaultComparator.compare(new Object(), new Object()));
	}
}