package com.wuzl.im.common.message.boby;

import java.io.Serializable;

public class ConfirmReadReponse implements Serializable {

    private static final long               serialVersionUID = 1L;
    private static final ConfirmReadReponse reponse          = new ConfirmReadReponse();

    private ConfirmReadReponse(){

    }

    public static ConfirmReadReponse getConfirmReadReponse() {
        return reponse;
    }
}
