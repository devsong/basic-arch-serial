package io.github.devsong.serial.entity.common;

import lombok.Data;

@Data
public class Result {
    private long id;
    private Status status;

    public Result() {

    }

    public Result(long id, Status status) {
        this.id = id;
        this.status = status;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Result{");
        sb.append("id=").append(id);
        sb.append(", status=").append(status);
        sb.append('}');
        return sb.toString();
    }
}
