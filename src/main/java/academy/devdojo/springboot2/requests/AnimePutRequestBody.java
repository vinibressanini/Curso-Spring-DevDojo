package academy.devdojo.springboot2.requests;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
@Builder
public class AnimePutRequestBody {
    private Long id;
    @NotEmpty(message = "This attribute can´t be empty")
    private String name;
}
