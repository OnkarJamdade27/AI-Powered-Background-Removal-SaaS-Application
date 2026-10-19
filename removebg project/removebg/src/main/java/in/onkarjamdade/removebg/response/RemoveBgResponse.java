package in.onkarjamdade.removebg.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RemoveBgResponse {

    private boolean success;
    private HttpStatus statuscode;
    private Object data;
}
