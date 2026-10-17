package com.ejafi.placement;

import org.jspecify.annotations.Nullable;

record TorchTrail(@Nullable TorchMark lastTorch) {
    static final TorchTrail EMPTY = new TorchTrail(null);
}
