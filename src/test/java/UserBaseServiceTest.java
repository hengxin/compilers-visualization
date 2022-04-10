import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import hmb.protobuf.User;


public class UserBaseServiceTest {
    public static void main(String[] args) throws IOException, URISyntaxException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            URI uri = new URI("http", null, "127.0.0.1", 9988, "/getUserInfo", "", null);
            HttpPost post = new HttpPost(uri);
            User.ExampleUserInfoRequest.Builder builder = User.ExampleUserInfoRequest.newBuilder();
            builder.setUserId(10086);
            post.setEntity(new ByteArrayEntity(builder.build().toByteArray()));
            post.setHeader("Content-Type", "application/x-protobuf");
            HttpResponse response = httpClient.execute(post);
            int code = response.getStatusLine().getStatusCode();
            System.out.println("\n\n\n\n");
            if (code == 200) {
                InputStream content = response.getEntity().getContent();
                User.ExampleUserInfoResponse result = User.ExampleUserInfoResponse.parseFrom(content);
                System.out.println("\n\n\nresult\n:" + result.toString());
            } else {
                System.err.println(code);
            }
        }
    }
}