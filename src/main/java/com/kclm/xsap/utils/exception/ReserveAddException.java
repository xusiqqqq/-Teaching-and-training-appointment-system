package com.kclm.xsap.utils.exception;


import com.kclm.xsap.utils.R;

public class ReserveAddException extends RRException{

    private R r;

    public ReserveAddException(String msg) {
        super(msg);
    }

    public ReserveAddException(String msg, int code) {
        super(msg, code);
    }

    public ReserveAddException(String msg, R r) {
        super(msg);
        this.r = r;
    }

    public ReserveAddException(R r){
        super("预约失败");
        this.r=r;
    }

    public ReserveAddException(String msg, int code, R r) {
        super(msg, code);
        this.r = r;
    }

    public R getR() {
        return r;
    }
    public void setR(R r) {
        this.r = r;
    }







}
