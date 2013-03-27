/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.felix.ipojo.runtime.core.test.dependencies;

import org.apache.felix.ipojo.ComponentInstance;
import org.apache.felix.ipojo.architecture.Architecture;
import org.apache.felix.ipojo.architecture.InstanceDescription;
import org.apache.felix.ipojo.runtime.core.test.services.CheckService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.ServiceReference;

import java.util.Properties;

import static org.junit.Assert.*;

public class TestSimpleDependencies extends Common {

    ComponentInstance instance1, instance2, instance3, instance4, instance5, instance6, instance7, instance8;
    ComponentInstance fooProvider;

    @Before
    public void setUp() {
        try {
            Properties prov = new Properties();
            prov.put("instance.name", "FooProvider");
            fooProvider = ipojoHelper.getFactory("FooProviderType-1").createComponentInstance(prov);
            fooProvider.stop();

            Properties i1 = new Properties();
            i1.put("instance.name", "Simple");
            instance1 = ipojoHelper.getFactory("SimpleCheckServiceProvider").createComponentInstance(i1);

            Properties i2 = new Properties();
            i2.put("instance.name", "Void");
            instance2 = ipojoHelper.getFactory("VoidCheckServiceProvider").createComponentInstance(i2);

            Properties i3 = new Properties();
            i3.put("instance.name", "Object");
            instance3 = ipojoHelper.getFactory("ObjectCheckServiceProvider").createComponentInstance(i3);

            Properties i4 = new Properties();
            i4.put("instance.name", "Ref");
            instance4 = ipojoHelper.getFactory("RefCheckServiceProvider").createComponentInstance(i4);

            Properties i5 = new Properties();
            i5.put("instance.name", "Both");
            instance5 = ipojoHelper.getFactory("BothCheckServiceProvider").createComponentInstance(i5);

            Properties i6 = new Properties();
            i6.put("instance.name", "Double");
            instance6 = ipojoHelper.getFactory("DoubleCheckServiceProvider").createComponentInstance(i6);

            Properties i7 = new Properties();
            i7.put("instance.name", "Map");
            instance7 = ipojoHelper.getFactory("MapCheckServiceProvider").createComponentInstance(i7);

            Properties i8 = new Properties();
            i8.put("instance.name", "Dictionary");
            instance8 = ipojoHelper.getFactory("DictCheckServiceProvider").createComponentInstance(i8);
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }

    }

    @After
    public void tearDown() {
        instance1.dispose();
        instance2.dispose();
        instance3.dispose();
        instance4.dispose();
        instance5.dispose();
        instance6.dispose();
        instance7.dispose();
        instance8.dispose();
        fooProvider.dispose();
        instance1 = null;
        instance2 = null;
        instance3 = null;
        instance4 = null;
        instance5 = null;
        instance6 = null;
        instance7 = null;
        instance8 = null;
        fooProvider = null;
    }

    @Test public void testSimple() {
        ServiceReference arch_ref = ipojoHelper.getServiceReferenceByName(Architecture.class.getName(), instance1.getInstanceName());
        assertNotNull("Check architecture availability", arch_ref);
        InstanceDescription id = ((Architecture) osgiHelper.getServiceObject(arch_ref)).getInstanceDescription();
        assertTrue("Check instance invalidity - 1", id.getState() == ComponentInstance.INVALID);

        fooProvider.start();

        id = ((Architecture) osgiHelper.getServiceObject(arch_ref)).getInstanceDescription();
        assertTrue("Check instance validity - 1", id.getState() == ComponentInstance.VALID);

        ServiceReference cs_ref = ipojoHelper.getServiceReferenceByName(CheckService.class.getName(), instance1.getInstanceName());
        assertNotNull("Check CheckService availability", cs_ref);
        CheckService cs = (CheckService) osgiHelper.getServiceObject(cs_ref);
        assertTrue("check CheckService invocation", cs.check());

        fooProvider.stop();

        id = ((Architecture) osgiHelper.getServiceObject(arch_ref)).getInstanceDescription();
        assertTrue("Check instance invalidity - 2", id.getState() == ComponentInstance.INVALID);

        fooProvider.start();

        id = ((Architecture) osgiHelper.getServiceObject(arch_ref)).getInstanceDescription();
        assertTrue("Check instance validity - 2", id.getState() == ComponentInstance.VALID);

        cs_ref = ipojoHelper.getServiceReferenceByName(CheckService.class.getName(), instance1.getInstanceName());
        assertNotNull("Check CheckService availability", cs_ref);
        cs = (CheckService) osgiHelper.getServiceObject(cs_ref);
        assertTrue("check CheckService invocation", cs.check());

        fooProvider.stop();

        id = ((Architecture) osgiHelper.getServiceObject(arch_ref)).getInstanceDescription();
        assertTrue("Check instance invalidity - 2", id.getState() == ComponentInstance.INVALID);

        id = null;
        cs = null;
        getContext().ungetService(arch_ref);
        getContext().ungetService(cs_ref);
    }

    @Test public void testVoid() {
        ServiceReference arch_ref = ipojoHelper.getServiceReferenceByName(Architecture.class.getName(), instance2.getInstanceName());
        assertNotNull("Check architecture availability", arch_ref);
        InstanceDescription id = ((Architecture) osgiHelper.getServiceObject(arch_ref)).getInstanceDescription();
        assertTrue("Check instance invalidity - 1", id.getState() == ComponentInstance.INVALID);

        fooProvider.start();

        id = ((Architecture) osgiHelper.getServiceObject(arch_ref)).getInstanceDescription();
        assertTrue("Check instance validity", id.getState() == ComponentInstance.VALID);

        ServiceReference cs_ref = ipojoHelper.getServiceReferenceByName(CheckService.class.getName(), instance2.getInstanceName());
        assertNotNull("Check CheckService availability", cs_ref);
        Object o = osgiHelper.getServiceObject(cs_ref);
        CheckService cs = (CheckService) o;
        Properties props = cs.getProps();
        //Check properties
        assertTrue("check CheckService invocation -1", ((Boolean) props.get("result")).booleanValue());
        assertEquals("check void bind invocation -1 (" + ((Integer) props.get("voidB")).intValue() + ")", ((Integer) props.get("voidB")).intValue(), 1);
        assertEquals("check void unbind callback invocation -1", ((Integer) props.get("voidU")).intValue(), 0);
        assertEquals("check object bind callback invocation -1", ((Integer) props.get("objectB")).intValue(), 0);
        assertEquals("check object unbind callback invocation -1", ((Integer) props.get("objectU")).intValue(), 0);
        assertEquals("check ref bind callback invocation -1", ((Integer) props.get("refB")).intValue(), 0);
        assertEquals("check ref unbind callback invocation -1", ((Integer) props.get("refU")).intValue(), 0);
        assertEquals("check both bind callback invocation -1", ((Integer) props.get("bothB")).intValue(), 0);
        assertEquals("check both unbind callback invocation -1", ((Integer) props.get("bothU")).intValue(), 0);

        fooProvider.stop();

        id = ((Architecture) osgiHelper.getServiceObject(arch_ref)).getInstanceDescription();
        assertTrue("Check instance invalidity - 2", id.getState() == ComponentInstance.INVALID);

        id = null;
        cs = null;
        getContext().ungetService(arch_ref);
        getContext().ungetService(cs_ref);
    }

    @Test public void testObject() {
        ServiceReference arch_ref = ipojoHelper.getServiceReferenceByName(Architecture.class.getName(), instance3.getInstanceName());
        assertNotNull("Check architecture availability", arch_ref);
        InstanceDescription id = ((Architecture) osgiHelper.getServiceObject(arch_ref)).getInstanceDescription();
        assertTrue("Check instance invalidity - 1", id.getState() == ComponentInstance.INVALID);

        fooProvider.start();

        id = ((Architecture) osgiHelper.getServiceObject(arch_ref)).getInstanceDescription();
        assertTrue("Check instance validity", id.getState() == ComponentInstance.VALID);

        ServiceReference cs_ref = ipojoHelper.getServiceReferenceByName(CheckService.class.getName(), instance3.getInstanceName());
        assertNotNull("Check CheckService availability", cs_ref);
        CheckService cs = (CheckService) osgiHelper.getServiceObject(cs_ref);
        Properties props = cs.getProps();
        //Check properties
        assertTrue("check CheckService invocation -1", ((Boolean) props.get("result")).booleanValue());
        assertEquals("check void bind invocation -1", ((Integer) props.get("voidB")).intValue(), 0);
        assertEquals("check void unbind callback invocation -1", ((Integer) props.get("voidU")).intValue(), 0);
        assertEquals("check object bind callback invocation -1", ((Integer) props.get("objectB")).intValue(), 1);
        assertEquals("check object unbind callback invocation -1", ((Integer) props.get("objectU")).intValue(), 0);
        assertEquals("check ref bind callback invocation -1", ((Integer) props.get("refB")).intValue(), 0);
        assertEquals("check ref unbind callback invocation -1", ((Integer) props.get("refU")).intValue(), 0);
        assertEquals("check both bind callback invocation -1", ((Integer) props.get("bothB")).intValue(), 0);
        assertEquals("check both unbind callback invocation -1", ((Integer) props.get("bothU")).intValue(), 0);

        fooProvider.stop();

        id = ((Architecture) osgiHelper.getServiceObject(arch_ref)).getInstanceDescription();
        assertTrue("Check instance invalidity - 2", id.getState() == ComponentInstance.INVALID);

        id = null;
        cs = null;
        getContext().ungetService(arch_ref);
        getContext().ungetService(cs_ref);
    }

    @Test public void testRef() {
        ServiceReference arch_ref = ipojoHelper.getServiceReferenceByName(Architecture.class.getName(), instance4.getInstanceName());
        assertNotNull("Check architecture availability", arch_ref);
        InstanceDescription id = ((Architecture) osgiHelper.getServiceObject(arch_ref)).getInstanceDescription();
        assertTrue("Check instance invalidity - 1", id.getState() == ComponentInstance.INVALID);

        fooProvider.start();

        id = ((Architecture) osgiHelper.getServiceObject(arch_ref)).getInstanceDescription();
        assertTrue("Check instance validity", id.getState() == ComponentInstance.VALID);

        ServiceReference cs_ref = ipojoHelper.getServiceReferenceByName(CheckService.class.getName(), instance4.getInstanceName());
        assertNotNull("Check CheckService availability", cs_ref);
        CheckService cs = (CheckService) osgiHelper.getServiceObject(cs_ref);
        Properties props = cs.getProps();
        //Check properties
        assertTrue("check CheckService invocation -1", ((Boolean) props.get("result")).booleanValue());
        assertEquals("check void bind invocation -1", ((Integer) props.get("voidB")).intValue(), 0);
        assertEquals("check void unbind callback invocation -1", ((Integer) props.get("voidU")).intValue(), 0);
        assertEquals("check object bind callback invocation -1", ((Integer) props.get("objectB")).intValue(), 0);
        assertEquals("check object unbind callback invocation -1", ((Integer) props.get("objectU")).intValue(), 0);
        assertEquals("check ref bind callback invocation -1", ((Integer) props.get("refB")).intValue(), 1);
        assertEquals("check ref unbind callback invocation -1", ((Integer) props.get("refU")).intValue(), 0);
        assertEquals("check both bind callback invocation -1", ((Integer) props.get("bothB")).intValue(), 0);
        assertEquals("check both unbind callback invocation -1", ((Integer) props.get("bothU")).intValue(), 0);

        fooProvider.stop();

        id = ((Architecture) osgiHelper.getServiceObject(arch_ref)).getInstanceDescription();
        assertTrue("Check instance invalidity - 2", id.getState() == ComponentInstance.INVALID);

        id = null;
        cs = null;
        getContext().ungetService(arch_ref);
        getContext().ungetService(cs_ref);
    }

    @Test public void testBoth() {
        ServiceReference arch_ref = ipojoHelper.getServiceReferenceByName(Architecture.class.getName(), instance5.getInstanceName());
        assertNotNull("Check architecture availability", arch_ref);
        InstanceDescription id = ((Architecture) osgiHelper.getServiceObject(arch_ref)).getInstanceDescription();
        assertTrue("Check instance invalidity - 1", id.getState() == ComponentInstance.INVALID);

        fooProvider.start();

        id = ((Architecture) osgiHelper.getServiceObject(arch_ref)).getInstanceDescription();
        assertTrue("Check instance validity", id.getState() == ComponentInstance.VALID);

        ServiceReference cs_ref = ipojoHelper.getServiceReferenceByName(CheckService.class.getName(), instance5.getInstanceName());
        assertNotNull("Check CheckService availability", cs_ref);
        CheckService cs = (CheckService) osgiHelper.getServiceObject(cs_ref);
        Properties props = cs.getProps();
        //Check properties
        assertTrue("check CheckService invocation -1", ((Boolean) props.get("result")).booleanValue());
        assertEquals("check void bind invocation -1", ((Integer) props.get("voidB")).intValue(), 0);
        assertEquals("check void unbind callback invocation -1", ((Integer) props.get("voidU")).intValue(), 0);
        assertEquals("check object bind callback invocation -1", ((Integer) props.get("objectB")).intValue(), 0);
        assertEquals("check object unbind callback invocation -1", ((Integer) props.get("objectU")).intValue(), 0);
        assertEquals("check ref bind callback invocation -1", ((Integer) props.get("refB")).intValue(), 0);
        assertEquals("check ref unbind callback invocation -1", ((Integer) props.get("refU")).intValue(), 0);
        assertEquals("check both bind callback invocation -1", ((Integer) props.get("bothB")).intValue(), 1);
        assertEquals("check both unbind callback invocation -1", ((Integer) props.get("bothU")).intValue(), 0);

        fooProvider.stop();

        id = ((Architecture) osgiHelper.getServiceObject(arch_ref)).getInstanceDescription();
        assertTrue("Check instance invalidity - 2", id.getState() == ComponentInstance.INVALID);

        id = null;
        cs = null;
        getContext().ungetService(arch_ref);
        getContext().ungetService(cs_ref);
    }

    @Test public void testDouble() {
        ServiceReference arch_ref = ipojoHelper.getServiceReferenceByName(Architecture.class.getName(), instance6.getInstanceName());
        assertNotNull("Check architecture availability", arch_ref);
        InstanceDescription id = ((Architecture) osgiHelper.getServiceObject(arch_ref)).getInstanceDescription();
        assertTrue("Check instance invalidity - 1", id.getState() == ComponentInstance.INVALID);

        fooProvider.start();

        id = ((Architecture) osgiHelper.getServiceObject(arch_ref)).getInstanceDescription();
        assertTrue("Check instance validity", id.getState() == ComponentInstance.VALID);

        ServiceReference cs_ref = ipojoHelper.getServiceReferenceByName(CheckService.class.getName(), instance6.getInstanceName());
        assertNotNull("Check CheckService availability", cs_ref);
        CheckService cs = (CheckService) osgiHelper.getServiceObject(cs_ref);
        assertNotNull("Check cs", cs);
        cs.check();
        Properties props = cs.getProps();
        //Check properties
        assertTrue("check CheckService invocation -1", ((Boolean) props.get("result")).booleanValue());
        assertEquals("check void bind invocation -1", ((Integer) props.get("voidB")).intValue(), 0);
        assertEquals("check void unbind callback invocation -1", ((Integer) props.get("voidU")).intValue(), 0);
        assertEquals("check object bind callback invocation -1", ((Integer) props.get("objectB")).intValue(), 1);
        assertEquals("check object unbind callback invocation -1", ((Integer) props.get("objectU")).intValue(), 0);
        assertEquals("check ref bind callback invocation -1", ((Integer) props.get("refB")).intValue(), 0);
        assertEquals("check ref unbind callback invocation -1", ((Integer) props.get("refU")).intValue(), 0);
        assertEquals("check both bind callback invocation -1", ((Integer) props.get("bothB")).intValue(), 0);
        assertEquals("check both unbind callback invocation -1", ((Integer) props.get("bothU")).intValue(), 0);

        fooProvider.stop();

        id = ((Architecture) osgiHelper.getServiceObject(arch_ref)).getInstanceDescription();
        assertTrue("Check instance invalidity - 2", id.getState() == ComponentInstance.INVALID);

        id = null;
        cs = null;
        getContext().ungetService(arch_ref);
        getContext().ungetService(cs_ref);
    }

    @Test public void testMap() {
        ServiceReference arch_ref = ipojoHelper.getServiceReferenceByName(Architecture.class.getName(), instance7.getInstanceName());
        assertNotNull("Check architecture availability", arch_ref);
        InstanceDescription id = ((Architecture) osgiHelper.getServiceObject(arch_ref)).getInstanceDescription();
        assertTrue("Check instance invalidity - 1", id.getState() == ComponentInstance.INVALID);

        fooProvider.start();

        id = ((Architecture) osgiHelper.getServiceObject(arch_ref)).getInstanceDescription();
        assertTrue("Check instance validity", id.getState() == ComponentInstance.VALID);

        ServiceReference cs_ref = ipojoHelper.getServiceReferenceByName(CheckService.class.getName(), instance7.getInstanceName());
        assertNotNull("Check CheckService availability", cs_ref);
        CheckService cs = (CheckService) osgiHelper.getServiceObject(cs_ref);
        Properties props = cs.getProps();
        //Check properties
        assertTrue("check CheckService invocation -1", ((Boolean) props.get("result")).booleanValue());
        assertEquals("check void bind invocation -1", ((Integer) props.get("voidB")).intValue(), 0);
        assertEquals("check void unbind callback invocation -1", ((Integer) props.get("voidU")).intValue(), 0);
        assertEquals("check object bind callback invocation -1", ((Integer) props.get("objectB")).intValue(), 0);
        assertEquals("check object unbind callback invocation -1", ((Integer) props.get("objectU")).intValue(), 0);
        assertEquals("check ref bind callback invocation -1", ((Integer) props.get("refB")).intValue(), 0);
        assertEquals("check ref unbind callback invocation -1", ((Integer) props.get("refU")).intValue(), 0);
        assertEquals("check both bind callback invocation -1", ((Integer) props.get("bothB")).intValue(), 0);
        assertEquals("check both unbind callback invocation -1", ((Integer) props.get("bothU")).intValue(), 0);
        assertEquals("check map bind callback invocation -1", ((Integer) props.get("mapB")).intValue(), 1);
        assertEquals("check map unbind callback invocation -1", ((Integer) props.get("mapU")).intValue(), 0);
        assertEquals("check dict bind callback invocation -1", ((Integer) props.get("dictB")).intValue(), 0);
        assertEquals("check dict unbind callback invocation -1", ((Integer) props.get("dictU")).intValue(), 0);

        fooProvider.stop();

        id = ((Architecture) osgiHelper.getServiceObject(arch_ref)).getInstanceDescription();
        assertTrue("Check instance invalidity - 2", id.getState() == ComponentInstance.INVALID);

        id = null;
        cs = null;
        getContext().ungetService(arch_ref);
        getContext().ungetService(cs_ref);
    }

    @Test public void testDict() {
        ServiceReference arch_ref = ipojoHelper.getServiceReferenceByName(Architecture.class.getName(), instance8.getInstanceName());
        assertNotNull("Check architecture availability", arch_ref);
        InstanceDescription id = ((Architecture) osgiHelper.getServiceObject(arch_ref)).getInstanceDescription();
        assertTrue("Check instance invalidity - 1", id.getState() == ComponentInstance.INVALID);

        fooProvider.start();

        id = ((Architecture) osgiHelper.getServiceObject(arch_ref)).getInstanceDescription();
        assertTrue("Check instance validity", id.getState() == ComponentInstance.VALID);

        ServiceReference cs_ref = ipojoHelper.getServiceReferenceByName(CheckService.class.getName(), instance8.getInstanceName());
        assertNotNull("Check CheckService availability", cs_ref);
        CheckService cs = (CheckService) osgiHelper.getServiceObject(cs_ref);
        Properties props = cs.getProps();
        //Check properties
        assertTrue("check CheckService invocation -1", ((Boolean) props.get("result")).booleanValue());
        assertEquals("check void bind invocation -1", ((Integer) props.get("voidB")).intValue(), 0);
        assertEquals("check void unbind callback invocation -1", ((Integer) props.get("voidU")).intValue(), 0);
        assertEquals("check object bind callback invocation -1", ((Integer) props.get("objectB")).intValue(), 0);
        assertEquals("check object unbind callback invocation -1", ((Integer) props.get("objectU")).intValue(), 0);
        assertEquals("check ref bind callback invocation -1", ((Integer) props.get("refB")).intValue(), 0);
        assertEquals("check ref unbind callback invocation -1", ((Integer) props.get("refU")).intValue(), 0);
        assertEquals("check both bind callback invocation -1", ((Integer) props.get("bothB")).intValue(), 0);
        assertEquals("check both unbind callback invocation -1", ((Integer) props.get("bothU")).intValue(), 0);
        assertEquals("check map bind callback invocation -1", ((Integer) props.get("mapB")).intValue(), 0);
        assertEquals("check map unbind callback invocation -1", ((Integer) props.get("mapU")).intValue(), 0);
        assertEquals("check dict bind callback invocation -1", ((Integer) props.get("dictB")).intValue(), 1);
        assertEquals("check dict unbind callback invocation -1", ((Integer) props.get("dictU")).intValue(), 0);

        fooProvider.stop();

        id = ((Architecture) osgiHelper.getServiceObject(arch_ref)).getInstanceDescription();
        assertTrue("Check instance invalidity - 2", id.getState() == ComponentInstance.INVALID);

        id = null;
        cs = null;
        getContext().ungetService(arch_ref);
        getContext().ungetService(cs_ref);
    }

}