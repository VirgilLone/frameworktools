package com.haoyunhu.tools.utils.mapping;

/**
 * Created by weijun.hu on 2016/5/24.
 */
public abstract class MapperMapping<S, D> {

    private S source;
    private D destination;

    public S getSource() {
        return source;
    }

    public void setSource(S source) {
        this.source = source;
    }

    public D getDestination() {
        return destination;
    }

    public void setDestination(D destination) {
        this.destination = destination;
    }

    //适配方法
    public abstract void configure();

}
