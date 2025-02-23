package com.challenge.api.model.dto;

import java.util.List;

public record APIErrorResponse (List<String> errors) {
}
