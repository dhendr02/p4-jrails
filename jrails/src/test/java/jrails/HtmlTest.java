package jrails;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.isEmptyString;
import static org.junit.Assert.*;

public class HtmlTest {

    private Html html;

    @Before
    public void setUp() throws Exception {
        html = new Html();
    }

    @Test
    public void empty() {
        assertThat(View.empty().toString(), isEmptyString());
    }

    @Test
    public void simpleTest() {
        Html html = new Html();
        html = html.p(html.t("Simple Test Case"));
        System.out.println(html.toString());
        assert(html.toString().equals("<p>Simple Test Case</p>"));
    }
}
