package sepm.dsa.dbunit.dataset;

import com.github.springtestdbunit.dataset.AbstractDataSetLoader;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.springframework.core.io.Resource;

import java.io.InputStream;

/**
 * A {@link com.github.springtestdbunit.dataset.DataSetLoader data set loader} that can be used to load {@link org.dbunit.dataset.xml.FlatXmlDataSet xml datasets}
 *
 * This one is a plain copy of {@link com.github.springtestdbunit.dataset.FlatXmlDataSetLoader} but also enables columnSensing.
 *
 */
public class FlatXmlDataSetLoader extends AbstractDataSetLoader {
	@Override
	protected IDataSet createDataSet(Resource resource) throws Exception {
		FlatXmlDataSetBuilder builder = new FlatXmlDataSetBuilder();
		builder.setColumnSensing(true);
		InputStream inputStream = resource.getInputStream();
		try {
			return builder.build(inputStream);
		} finally {
			inputStream.close();
		}
	}
}
