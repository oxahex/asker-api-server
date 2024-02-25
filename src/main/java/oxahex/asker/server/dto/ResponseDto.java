package oxahex.asker.server.dto;

public record ResponseDto<T>(String message, T data) {

}
