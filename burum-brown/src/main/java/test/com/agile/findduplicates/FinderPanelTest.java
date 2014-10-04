package test.com.agile.findduplicates; 

import junit.framework.Test; 
import junit.framework.TestSuite; 
import junit.framework.TestCase; 

/** 
* FinderPanel Tester. 
* 
* @author <Authors name> 
* @since <pre>10/04/2014</pre> 
* @version 1.0 
*/ 
public class FinderPanelTest extends TestCase { 
public FinderPanelTest(String name) { 
super(name); 
} 

public void setUp() throws Exception { 
super.setUp(); 
} 

public void tearDown() throws Exception { 
super.tearDown(); 
} 

/** 
* 
* Method: main(String[] args) 
* 
*/
@org.junit.Test
public void testMain() throws Exception { 
//TODO: Test goes here...
    assert true;

} 



public static Test suite() { 
return new TestSuite(FinderPanelTest.class); 
} 
} 
