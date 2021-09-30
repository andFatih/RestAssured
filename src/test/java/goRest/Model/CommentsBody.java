package goRest.Model;

import java.util.List;

public class CommentsBody {
    private Meta meta;
    private List<Comment> data;

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    public List<Comment> getData() {
        return data;
    }

    public void setData(List<Comment> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "CommentsBody{" +
                "meta=" + meta +
                ", data=" + data +
                '}';
    }
}
