package com.tamer84.tango.model.event;

import com.tamer84.tango.model.Market;
import com.tamer84.tango.model.ProductType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.UUID;

public abstract class TangoEvent {

    /**
     * Uniquely identifies a Product (aggregateId)
     */
    private final UUID productId;

    /**
     * Uniquely identifies a saga
     */
    private final String sagaId;

    /**
     * The source of the event (usually an application)
     */
    private final String source;

    /**
     * Product Types
     */
    private final ProductType productType;

    /**
     * Market associated with the event (countryIsoCode)
     */
    private final Market market;

    /**
     * Time in epoch Millis seconds when this event was created
     */
    private final long timestamp;

    /**
     * The events full name including package
     */
    private final String eventName;

    protected TangoEvent() {
        this.productId = null;
        this.sagaId = null;
        this.source = null;
        this.productType = null;
        this.market = null;
        this.timestamp = 0;
        this.eventName = this.getClass().getName();
    }

    public TangoEvent(final UUID productId,
                      final String sagaId,
                      final String source,
                      final ProductType productType,
                      final Market market,
                      final long timestamp) {

        this.productId = productId;
        this.sagaId = sagaId;
        this.source = source;
        this.productType = productType;
        this.market = market;
        this.timestamp = timestamp;
        this.eventName = this.getClass().getName();
    }

    /**
     * AUXILIARY METHOD to be used for the AWS Event detailType field.
     *
     * @return the event simple name
     */
    public final String detailType() {
        return this.getClass().getSimpleName();
    }

    /**
     * AUXILIARY METHOD to provide the market as a String
     *
     * @return the market as a String
     */
    public final String market() {
        if (this.market == null) {
            throw new IllegalStateException("market is null for event: " + this);
        }
        return this.market.name();
    }

    /**
     * AUXILIARY METHOD to provide the productType as a String
     *
     * @return the productType as a String
     */
    public final String productType() {
        if (this.productType == null) {
            throw new IllegalStateException("productType is null for event: " + this);
        }
        return this.productType.name();
    }

    /**
     * AUXILIARY METHOD to provide the productId as a String
     *
     * @return the productId as a String
     */
    public final String productId() {
        if (this.productId == null) {
            throw new IllegalStateException("productId is null for event: " + this);
        }
        return this.productId.toString();
    }

    public String getEventName() {
        return this.eventName;
    }

    public final UUID getProductId() {
        return this.productId;
    }

    public ProductType getProductType() {
        return this.productType;
    }

    public final String getSagaId() {
        return this.sagaId;
    }

    public final long getTimestamp() {
        return this.timestamp;
    }

    public final String getSource() {
        return this.source;
    }

    public final Market getMarket() {
        return this.market;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SIMPLE_STYLE);
    }

}
