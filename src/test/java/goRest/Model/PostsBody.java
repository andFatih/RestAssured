package goRest.Model;

import java.util.List;

public class PostsBody {
    private Meta meta;
    private List<Post> data;

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    public List<Post> getData() {
        return data;
    }

    public void setData(List<Post> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "PostsBody{" +
                "meta=" + meta +
                ", data=" + data +
                '}';
    }
}
