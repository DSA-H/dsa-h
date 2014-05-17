package sepm.dsa.service.test;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import sepm.dsa.service.ProductService;

/**
 * Created by Chris on 17.05.2014.
 */
public class ProductServiceTest {

    @Autowired
    private ProductService ps;
    //
    @Test
    @DatabaseSetup("productData.xml")
    public void testXML()
    {
        System.out.println(ps.getAll().toString());
    }
}
