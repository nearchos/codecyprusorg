package org.codecyprus.th.api;

import org.codecyprus.th.db.UrlShortenerFactory;
import org.codecyprus.th.model.UrlShortener;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;

public class ForwardingServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/plain; charset=utf-8");
        response.setHeader("Access-Control-Allow-Origin", "*");

        final String uri = request.getRequestURI();
        final String key = uri.substring(uri.lastIndexOf('/') + 1);

        final UrlShortener urlShortener = UrlShortenerFactory.getUrlShortenerByKey(key);
        if(urlShortener == null) {
            final PrintWriter printWriter = response.getWriter();
            printWriter.println("Could not find a mapping for key: " + key);
        } else {
            response.sendRedirect(URLDecoder.decode(urlShortener.getTarget(), "UTF-8"));
        }
    }
}