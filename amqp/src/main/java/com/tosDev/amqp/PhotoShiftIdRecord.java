package com.tosDev.amqp;

import java.io.Serializable;

public record PhotoShiftIdRecord(String photoId,
                                 Integer shiftId,
                                 boolean isFirstPhoto,
                                 String token){}
