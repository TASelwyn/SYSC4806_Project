package tech.selwyn.sysc4806project.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.UUID;

public record UpdateCartRequestDto(
        @NotNull UUID bookId,
        @PositiveOrZero int quantity
) {}
