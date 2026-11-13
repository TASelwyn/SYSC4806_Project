package tech.selwyn.sysc4806project.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record UpdateCartRequestDto(
        @NotNull Long bookId,
        @PositiveOrZero int quantity
) {}
