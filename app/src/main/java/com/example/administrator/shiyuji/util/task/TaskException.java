package com.example.administrator.shiyuji.util.task;

import android.content.res.Resources;
import android.text.TextUtils;

import com.example.administrator.shiyuji.R;
import com.example.administrator.shiyuji.util.common.GlobalContext;

/**
 * Created by Administrator on 2019/6/28.
 */


public class TaskException extends Exception {
    private static final long serialVersionUID = -6262214243381380676L;
    private String code;
    private String msg;
    private static IExceptionDeclare exceptionDeclare;

    public TaskException(String code) {
        this.msg = "";
        this.code = code;
    }

    public TaskException(String code, String msg) {
        this(code);
        this.msg = msg;
    }

    public String getCode() {
        return this.code;
    }

    public String getMessage() {
        if(!TextUtils.isEmpty(this.msg)) {
            return this.msg + "";
        } else {
            if(!TextUtils.isEmpty(this.code) && exceptionDeclare != null) {
                String res = exceptionDeclare.checkCode(this.code);
                if(!TextUtils.isEmpty(res)) {
                    return res + "";
                }
            }

            try {
                if(GlobalContext.getInstance() != null) {
                    Resources res1 = GlobalContext.getInstance().getResources();
                    TaskException.TaskError error = TaskException.TaskError.valueOf(this.code);
                    if(error != TaskException.TaskError.noneNetwork && error != TaskException.TaskError.failIOError) {
                        if(error != TaskException.TaskError.socketTimeout && error != TaskException.TaskError.timeout) {
                            if(error == TaskException.TaskError.resultIllegal) {
                                this.msg = res1.getString(R.string.comm_error_result_illegal);
                            }
                        } else {
                            this.msg = res1.getString(R.string.comm_error_timeout);
                        }
                    } else {
                        this.msg = res1.getString(R.string.comm_error_none_network);
                    }

                    if(!TextUtils.isEmpty(this.msg)) {
                        return this.msg + "";
                    }
                }
            } catch (Exception var3) {
                ;
            }

            return super.getMessage() + "";
        }
    }

    public static void config(IExceptionDeclare declare) {
        exceptionDeclare = declare;
    }

    public static void checkResponse(String response) throws TaskException {
        if(exceptionDeclare != null) {
            exceptionDeclare.checkResponse(response);
        }

    }

    public static enum TaskError {
        failIOError,
        noneNetwork,
        timeout,
        socketTimeout,
        resultIllegal;

        private TaskError() {
        }
    }
}
