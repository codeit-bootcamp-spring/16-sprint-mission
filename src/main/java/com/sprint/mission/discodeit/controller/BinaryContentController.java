package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.BinaryContentResponse;
import com.sprint.mission.discodeit.service.BinaryContentService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;

import java.nio.charset.StandardCharsets;

import java.util.List;
import java.util.UUID;

import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/binary-contents")
public class BinaryContentController {

    private final BinaryContentService binaryContentService;

    @RequestMapping(
            value = "/{id}",
            method = RequestMethod.GET
    )
    public ResponseEntity<byte[]> downloadOne(
            @PathVariable("id") UUID id
    ) {
        BinaryContentResponse content =
                binaryContentService.findById(id);

        byte[] bytes =
                binaryContentService.readBytes(id);

        MediaType mediaType;

        try {
            mediaType = MediaType.parseMediaType(
                    content.contentType()
            );
        } catch (InvalidMediaTypeException exception) {
            mediaType = MediaType.APPLICATION_OCTET_STREAM;
        }

        ContentDisposition disposition =
                ContentDisposition.attachment()
                        .filename(
                                safeFileName(content.fileName()),
                                StandardCharsets.UTF_8
                        )
                        .build();

        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(mediaType)
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        disposition.toString()
                )
                .contentLength(bytes.length)
                .body(bytes);
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<byte[]> downloadMany(
            @RequestParam("ids") List<UUID> ids
    ) {
        if (ids == null || ids.isEmpty()) {
            throw new IllegalArgumentException(
                    "다운로드할 파일 ID가 없습니다."
            );
        }

        byte[] zipBytes;

        try (
                ByteArrayOutputStream output =
                        new ByteArrayOutputStream();

                ZipOutputStream zip =
                        new ZipOutputStream(output)
        ) {
            for (UUID id : ids) {
                BinaryContentResponse content =
                        binaryContentService.findById(id);

                byte[] bytes =
                        binaryContentService.readBytes(id);

                String entryName =
                        id + "_" + safeFileName(content.fileName());

                ZipEntry entry = new ZipEntry(entryName);

                zip.putNextEntry(entry);
                zip.write(bytes);
                zip.closeEntry();
            }

            zip.finish();

            zipBytes = output.toByteArray();

        } catch (IOException exception) {
            throw new UncheckedIOException(
                    "ZIP 파일 생성에 실패했습니다.",
                    exception
            );
        }

        ContentDisposition disposition =
                ContentDisposition.attachment()
                        .filename("discodeit-files.zip")
                        .build();

        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(
                        MediaType.parseMediaType("application/zip")
                )
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        disposition.toString()
                )
                .contentLength(zipBytes.length)
                .body(zipBytes);
    }

    private String safeFileName(String fileName) {
        if (fileName == null || fileName.isBlank()) {
            return "file";
        }

        String normalized = fileName
                .replace('\\', '/');

        int slashIndex = normalized.lastIndexOf('/');

        if (slashIndex >= 0) {
            normalized = normalized.substring(slashIndex + 1);
        }

        normalized = normalized.replaceAll(
                "[\\p{Cntrl}]",
                "_"
        );

        if (normalized.isBlank()
                || normalized.equals(".")
                || normalized.equals("..")) {
            return "file";
        }

        return normalized;
    }
}