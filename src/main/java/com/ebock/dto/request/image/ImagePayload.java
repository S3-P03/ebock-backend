package com.ebock.dto.request.image;

import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.multipart.FileUpload;

public class ImagePayload {
    @RestForm("file")
    public FileUpload file;
}
