package pl.taw.business.apiservice;

import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api-ph")
@AllArgsConstructor
public class ApiRestController {

    private final ApiService apiService;

    @GetMapping(value = "/posts/{postId}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public Post getPostById(@PathVariable int postId) {
        return apiService.getPostById(postId);
    }

    @GetMapping(value = "/posts", produces = {MediaType.APPLICATION_JSON_VALUE})
    public List<Post> getPosts() {
        return apiService.getPosts();
    }

    @GetMapping(value = "/comments/{commentId}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public Comment getCommentById(@PathVariable int commentId) {
        return apiService.getCommentById(commentId);
    }

    @GetMapping(value = "/comments", produces = {MediaType.APPLICATION_JSON_VALUE})
    public List<Comment> getComments() {
        return apiService.getComments();
    }
}
