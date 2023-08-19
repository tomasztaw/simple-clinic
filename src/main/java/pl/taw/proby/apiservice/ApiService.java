package pl.taw.proby.apiservice;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class ApiService {
    private final String BASE_URL = "https://jsonplaceholder.typicode.com";
    private final RestTemplate restTemplate;

    public ApiService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Post getPostById(int postId) {
        String url = BASE_URL + "/posts/" + postId;
        return restTemplate.getForObject(url, Post.class);
    }

    public List<Post> getPosts() {
        String url = BASE_URL + "/posts";
        return restTemplate.exchange(url, HttpMethod.GET, null,
                new ParameterizedTypeReference<List<Post>>() {}
        ).getBody();
    }

    public Comment getCommentById(int commentId) {
        String url = BASE_URL.concat("/comments/") + commentId;
        return restTemplate.getForObject(url, Comment.class);
    }

    public List<Comment> getComments() {
        String url = BASE_URL.concat("/comments");
        return restTemplate.exchange(url, HttpMethod.GET, null,
                new ParameterizedTypeReference<List<Comment>>() {
                }).getBody();
    }
}
