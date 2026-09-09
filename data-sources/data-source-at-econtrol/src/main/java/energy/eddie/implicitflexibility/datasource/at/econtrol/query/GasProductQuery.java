package energy.eddie.implicitflexibility.datasource.at.econtrol.query;

import energy.eddie.datasource.at.econtrol.RateGasProductsRequest;

public class GasProductQuery extends RateGasProductsRequest implements ProductQuery {

    private long brandId;
    private boolean smartMeter = false;

    @Override
    public long getBrandId() {
        return brandId;
    }

    public void setBrandId(long brandId) {
        this.brandId = brandId;
    }

    @Override
    public boolean getSmartMeter() {
        return smartMeter;
    }

    public void setSmartMeter(boolean smartMeter) {
        this.smartMeter = smartMeter;
    }
}
