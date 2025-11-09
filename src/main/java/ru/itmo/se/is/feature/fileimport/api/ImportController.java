package ru.itmo.se.is.feature.fileimport.api;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import ru.itmo.se.is.feature.fileimport.api.dto.FileUploadRequestDto;
import ru.itmo.se.is.feature.fileimport.api.dto.ImportOperationResponseDto;
import ru.itmo.se.is.feature.fileimport.application.ImportService;
import ru.itmo.se.is.platform.web.util.MultipartImportFileUtil;

@Path("/imports")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.MULTIPART_FORM_DATA)
public class ImportController {

    @Inject
    private MultipartImportFileUtil multipartImportFileUtil;
    @Inject
    private ImportService importService;

    @POST
    public Response importMovies(MultipartFormDataInput input) {
        FileUploadRequestDto requestDto = multipartImportFileUtil.from(input);
        ImportOperationResponseDto responseDto = importService.importMovies(requestDto);
        return Response.ok(responseDto).build();
    }
}
