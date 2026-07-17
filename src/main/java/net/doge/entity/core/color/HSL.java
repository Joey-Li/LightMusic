package net.doge.entity.core.color;

import lombok.AllArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@ToString
public class HSL {
    // 0 - 360
    public float h;
    // 0 - 100
    public float s;
    // 0 - 100
    public float l;
}
