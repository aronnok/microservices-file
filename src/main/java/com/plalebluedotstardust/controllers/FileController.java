package com.plalebluedotstardust.controllers;


import org.glassfish.jersey.media.multipart.*;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;
import org.glassfish.jersey.media.multipart.file.StreamDataBodyPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


import javax.ws.rs.*;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Objects;

/**
 * Created by imtiaz on 12/23/2016.
 */

@Component
@Path("/files")
public class FileController {

    private static final Logger logger = LoggerFactory.getLogger(FileController.class);

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

    @POST
    @Path("/uploadandupdate")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadFileAndUpdate(
            @FormDataParam("file") InputStream uploadedInputStream,
            @FormDataParam("file") FormDataContentDisposition fileDetail) {

        String uploadedFileLocation = "d://tmp/" + fileDetail.getFileName();

        // save it
        saveFile(uploadedInputStream, uploadedFileLocation);

        String output = "File uploaded to MCSSCI : " + uploadedFileLocation;

        return Response.status(200).entity(output).build();

    }

    @POST
    @Path("/aggregator/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response aggregatorUploadFile(
            @FormDataParam("file") InputStream uploadedInputStream,
            @FormDataParam("file") FormDataContentDisposition fileDetail) throws UnsupportedEncodingException {

        String url = "http://localhost:8080/files/upload";
        Client client = ClientBuilder.newBuilder().register(MultiPartFeature.class).build();
        WebTarget target = client.target(url);

        logger.info("Attaching multipart form data....");
        StreamDataBodyPart streamDataBodyPart
                = new StreamDataBodyPart("file", uploadedInputStream,URLEncoder.encode(fileDetail.getFileName(),
                StandardCharsets.UTF_8.toString()));

        MultiPart multipart
                = new FormDataMultiPart().bodyPart(streamDataBodyPart);

       /* FileDataBodyPart fileDataBodyPart = new FileDataBodyPart("file",
                fileToUpload,
                MediaType.APPLICATION_OCTET_STREAM_TYPE);
        fileDataBodyPart.setContentDisposition(
                FormDataContentDisposition.name("file")
                        .fileName(fileToUpload.getName()).build());*/

        Response serverResponse = target.request(MediaType.MULTIPART_FORM_DATA).post(Entity.entity(multipart, multipart.getMediaType()));
        return Response.status(200).entity( serverResponse.readEntity(String.class) ).build();

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
