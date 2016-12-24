package com.plalebluedotstardust.controllers;


import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.springframework.stereotype.Component;


import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;
import java.util.HashMap;
import java.util.Objects;

/**
 * Created by imtiaz on 12/23/2016.
 */

@Component
@Path("/files")
public class FileController {

    @GET
    @Path("/get")
    @Produces("application/json")
    public Response get() {

        HashMap<String,Object> apiResponse = new HashMap<>();

        apiResponse.put("message","Hello from jersey file service");

        return Response.ok(apiResponse).build();
    }

    @POST
    @Path("/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadFile(
            @FormDataParam("file") InputStream uploadedInputStream,
            @FormDataParam("file") FormDataContentDisposition fileDetail) {

        String uploadedFileLocation = "d://tmp/" + fileDetail.getFileName();

        // save it
        saveFile(uploadedInputStream, uploadedFileLocation);

        String output = "File uploaded to : " + uploadedFileLocation;

        return Response.status(200).entity(output).build();

    }

    private void saveFile(InputStream uploadedInputStream,
                         String serverLocation) {

        try {
            OutputStream outpuStream = new FileOutputStream(new File(serverLocation));
            int read = 0;
            byte[] bytes = new byte[1024];

            outpuStream = new FileOutputStream(new File(serverLocation));
            while ((read = uploadedInputStream.read(bytes)) != -1) {
                outpuStream.write(bytes, 0, read);
            }
            outpuStream.flush();
            outpuStream.close();
        } catch (IOException e) {

            e.printStackTrace();
        }

    }

}
