package com.tosDev.web.dto.tg;

import lombok.Getter;

@Getter
public class TgPhotoDto {
    public boolean ok;
    public Result result;

    @Getter
    public static class Result {
        public String file_id;
        public String file_unique_id;
        public int file_size;
        public String file_path;
    }
}
