package ru.itmo.se.is.dto;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmbeddedObjectDto<ID, T> {
    private ID id;

    @Valid
    private T value;

    public boolean isReference() {
        return id != null && value == null;
    }

    public boolean isNew() {
        return id == null && value != null;
    }

    public boolean isEmpty() {
        return id == null && value == null;
    }
}
