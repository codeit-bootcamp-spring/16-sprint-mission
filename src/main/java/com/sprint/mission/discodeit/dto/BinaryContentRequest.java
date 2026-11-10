package com.sprint.mission.discodeit.dto;



public record BinaryContentRequest(String fileName, String contentType, byte[] bytes) {

    public BinaryContentRequest{
        bytes = (bytes==null)?new byte[0]:bytes.clone();
    }

    @Override
    public byte[] bytes() {
        return this.bytes.clone();
    }

}
