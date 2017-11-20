package com.zenika.zencontact.resource;

import com.google.appengine.api.blobstore.BlobKey;
import com.zenika.zencontact.domain.blob.PhotoService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by ssinigag on 20/11/17.
 */
@WebServlet(name = "PhotoResource", value = "api/v0/photo/*")
public class PhotoResource extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo(); // /{id}
        String[] pathParts = pathInfo.split("/");
        if (pathParts.length == 0) {
            resp.setStatus(404);
            return;
        }
        Long id = Long.valueOf(pathParts[1]);
        PhotoService.getInstance().updatePhoto(id, req);
        resp.setContentType("/text/plain");
        resp.getWriter().println("ok");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo(); // /{id}
        String[] pathParts = pathInfo.split("/");
        if (pathParts.length == 0) {
            resp.setStatus(404);
            return;
        }
        Long id = Long.valueOf(pathParts[1]);
        String blobKey = pathParts[2];

        PhotoService.getInstance().serve(new BlobKey(blobKey), resp);
    }
}
