package com.itzstonlex.fastblock.api.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Vector2D {

    private double x, z;

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Vector2D) {

            Vector2D other = ((Vector2D) obj);
            return other.x == x && other.z == z;
        }

        return false;
    }
}