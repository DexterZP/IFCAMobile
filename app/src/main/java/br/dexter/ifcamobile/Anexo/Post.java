package br.dexter.ifcamobile.Anexo;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Post
{
    private String name, path, url, data;
    private Long size;

    public Post() { }

    Post(String name, String path, String url, String data, Long size)
    {
        this.name = name;
        this.path = path;
        this.url = url;
        this.data = data;
        this.size = size;
    }

    @Exclude
    Map<String, Object> toMap()
    {
        HashMap<String, Object> result = new HashMap<>();
        result.put("Nome", name);
        result.put("Path", path);
        result.put("Download", url);
        result.put("Data", data);
        result.put("Tamanho", size);

        return result;
    }
}
