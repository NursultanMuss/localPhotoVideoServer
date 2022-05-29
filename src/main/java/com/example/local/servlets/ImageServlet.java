package com.example.local.servlets;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

@WebServlet(name = "ImageServlet",
            urlPatterns = "/home")
public class ImageServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        ServletContext cntx= req.getServletContext();
        // Get the absolute path of the image
        String filename = cntx.getRealPath("/home/nurs/Pictures/Screenshot from 2022-04-07 17-38-36.png");
        // retrieve mimeType dynamically
        String mime = cntx.getMimeType(filename);
        if (mime == null) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }

        resp.setContentType(mime);
        File file = new File(filename);
        resp.setContentLength((int)file.length());

        FileInputStream in = new FileInputStream(file);
        OutputStream out = resp.getOutputStream();

        // Copy the contents of the file to the output stream
        byte[] buf = new byte[1024];
        int count = 0;
        while ((count = in.read(buf)) >= 0) {
            out.write(buf, 0, count);
        }
        RequestDispatcher dispatcher = req.getRequestDispatcher(
                "../../WEB-INF/home.jsp");
        dispatcher.forward(req, resp);
        out.close();
        in.close();
//        resp.getOutputStream().print("dsfsd");
    }
}
