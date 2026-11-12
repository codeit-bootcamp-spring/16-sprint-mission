package com.sprint.mission.discodeit.controll;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/api/binary-contents")
@RequiredArgsConstructor
public class BinaryContentController {

    private final BinaryContentService binaryContentService;

    @RequestMapping(value = "/{id}download", method = RequestMethod.GET)
    public ResponseEntity<byte[]> download(@PathVariable("id") UUID id) {
        BinaryContent file = binaryContentService.findById(id);

        ContentDisposition disposition = ContentDisposition.attachment()
                .filename(file.getFilename(), StandardCharsets.UTF_8)
                .build();

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, disposition.toString())
                .body(file.getBytes());
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<BinaryContent> findById(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(binaryContentService.findById(id));
    }

    @RequestMapping(method = RequestMethod.GET, params = "ids")
    public ResponseEntity<List<BinaryContent>> findAllByIds(
            @RequestParam("ids") List<UUID> ids
    ) {
        return ResponseEntity.ok(binaryContentService.findAllByIds(ids));
    }
}