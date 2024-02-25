package oxahex.asker.server.dto;

import lombok.Getter;

@Getter
public record ResponseDto<T>(String message, T data) {

}
