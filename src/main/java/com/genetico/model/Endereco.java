package com.genetico.model;

import java.util.Objects;

public record Endereco(Long id, Double latitude, Double longitude, String descricao) {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Endereco endereco)) return false;
        return Objects.equals(id, endereco.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
