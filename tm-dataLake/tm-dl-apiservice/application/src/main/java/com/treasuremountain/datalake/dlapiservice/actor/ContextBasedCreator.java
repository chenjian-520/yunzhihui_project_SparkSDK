package com.treasuremountain.datalake.dlapiservice.actor;

import akka.japi.Creator;

/**
 * Description:
 * <p>
 * Created by ref.tian on 2018/6/7.
 * Company: Maxnerva
 * Project: MaxIoT
 */
public abstract class ContextBasedCreator<T> implements Creator<T> {

    private static final long serialVersionUID = 1L;

    protected final ActorSystemContext context;

    public ContextBasedCreator(ActorSystemContext context) {
        super();
        this.context = context;
    }
}
