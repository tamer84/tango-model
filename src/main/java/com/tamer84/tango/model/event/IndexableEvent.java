package com.tamer84.tango.model.event;


import com.tamer84.tango.model.Market;
import com.tamer84.tango.model.ProductType;

import java.util.UUID;

public class IndexableEvent extends TangoEvent {

    protected IndexableEvent() {}

    public IndexableEvent(final UUID productId,
                          final String sagaId,
                          final String source,
                          final ProductType productType,
                          final Market market,
                          final long timestamp) {

        super(productId, sagaId, source, productType, market, timestamp);
    }
}
