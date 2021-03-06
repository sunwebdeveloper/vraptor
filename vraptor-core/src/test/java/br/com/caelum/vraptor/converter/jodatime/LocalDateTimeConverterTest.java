package br.com.caelum.vraptor.converter.jodatime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import java.util.ResourceBundle;

import javax.servlet.FilterChain;
import javax.servlet.ServletContext;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.joda.time.LocalDateTime;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.converter.ConversionError;
import br.com.caelum.vraptor.core.JstlLocalization;
import br.com.caelum.vraptor.core.RequestInfo;
import br.com.caelum.vraptor.http.MutableRequest;

/**
 * Tests to {@link LocalDateTimeConverter}.
 */
public class LocalDateTimeConverterTest {
	
	private Mockery mockery;
	private MutableRequest request;
	private ServletContext context;
	private ResourceBundle bundle;
	private LocalDateTimeConverter converter;
	private JstlLocalization jstlLocalization;

	@Before
	public void setup() {
		this.mockery = new Mockery();

		this.request = mockery.mock(MutableRequest.class);
		this.context = mockery.mock(ServletContext.class);
		this.bundle = ResourceBundle.getBundle("messages");
		FilterChain chain = mockery.mock(FilterChain.class);

		final RequestInfo webRequest = new RequestInfo(context, chain, request, null);
        this.jstlLocalization = new JstlLocalization(webRequest);

		this.converter = new LocalDateTimeConverter(jstlLocalization);
	}

	@Test
	public void shouldBeAbleToConvert() {
		mockery.checking(new Expectations() {
			{
				exactly(1).of(request).getAttribute("javax.servlet.jsp.jstl.fmt.locale.request");
				will(returnValue("pt_br"));
			}
		});

		assertThat(converter.convert("05/06/2010 3:38", LocalDateTime.class, bundle),
				is(equalTo(new LocalDateTime(2010, 6, 5, 3, 38))));
		mockery.assertIsSatisfied();
	}
	
	@Test
	public void shouldBeAbleToConvertEmpty() {
		assertThat(converter.convert("", LocalDateTime.class, bundle), is(nullValue()));
		mockery.assertIsSatisfied();
	}

	@Test
	public void shouldBeAbleToConvertNull() {
		assertThat(converter.convert(null, LocalDateTime.class, bundle), is(nullValue()));
		mockery.assertIsSatisfied();
	}

	@Test
	public void shouldThrowExceptionWhenUnableToParse() {
		mockery.checking(new Expectations() {
			{
				exactly(2).of(request).getAttribute("javax.servlet.jsp.jstl.fmt.locale.request");
				will(returnValue("pt_br"));
			}
		});
		try {
			converter.convert("a,10/06/2008/a/b/c", LocalDateTime.class, bundle);
		} catch (ConversionError e) {
			assertThat(e.getMessage(), is(equalTo("a,10/06/2008/a/b/c is not a valid datetime.")));
		}
	}
}