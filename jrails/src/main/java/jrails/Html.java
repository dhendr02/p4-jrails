package jrails;

public class Html {

    private final StringBuilder content = new StringBuilder();

    public String toString() {
        return content.toString();
    }

    public Html seq(Html h) {
        content.append(h.toString());
        return this;
    }

    public Html br() {
        content.append("<br/>");
        return this;
    }

    public Html t(Object o) {
        Html newHtml = new Html();
        newHtml.content.append(o.toString());
        return newHtml;
    }

    public Html p(Html child) {
        content.append("<p>");
        content.append(child.toString());
        content.append("</p>");
        return this;
    }

    public Html div(Html child) {
        content.append("<div>");
        content.append(child.toString());
        content.append("</div>");
        return this;
    }

    public Html strong(Html child) {
        content.append("<strong>");
        content.append(child.toString());
        content.append("</strong>");
        return this;
    }

    public Html h1(Html child) {
        content.append("<h1>");
        content.append(child.toString());
        content.append("</h1>");
        return this;
    }

    public Html tr(Html child) {
        content.append("<tr>");
        content.append(child.toString());
        content.append("</tr>");
        return this;
    }

    public Html th(Html child) {
        content.append("<th>");
        content.append(child.toString());
        content.append("</th>");
        return this;
    }

    public Html td(Html child) {
        content.append("<td>");
        content.append(child.toString());
        content.append("</td>");
        return this;
    }

    public Html table(Html child) {
        content.append("<table>");
        content.append(child.toString());
        content.append("</table>");
        return this;
    }

    public Html thead(Html child) {
        content.append("<thead>");
        content.append(child.toString());
        content.append("</thead>");
        return this;
    }

    public Html tbody(Html child) {
        content.append("<tbody>");
        content.append(child.toString());
        content.append("</tbody>");
        return this;
    }

    public Html textarea(String name, Html child) {
        content.append("<textarea name=\"").append(name).append("\">");
        content.append(child.toString());
        content.append("</textarea>");
        return this;
    }

    public Html link_to(String text, String url) {
        content.append("<a href=\"").append(url).append("\">");
        content.append(text);
        content.append("</a>");
        return this;
    }

    public Html form(String action, Html child) {
        content.append("<form action=\"").append(action).append("\" accept-charset=\"UTF-8\" method=\"post\">");
        content.append(child.toString());
        content.append("</form>");
        return this;
    }

    public Html submit(String value) {
        content.append("<input type=\"submit\" value=\"").append(value).append("\"/>");
        return this;
    }
}