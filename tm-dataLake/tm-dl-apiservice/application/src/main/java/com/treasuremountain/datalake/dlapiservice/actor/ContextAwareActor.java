package com.treasuremountain.datalake.dlapiservice.actor;

import akka.actor.AbstractActor;

/**
 * Description:
 * <p>
 * Created by ref.tian on 2018/6/7.
 * Company: Maxnerva
 * Project: MaxIoT
 */
public abstract class ContextAwareActor extends AbstractActor {

    public static final int ENTITY_PACK_LIMIT = 1024;

    protected final ActorSystemContext systemContext;

    public ContextAwareActor(ActorSystemContext systemContext) {
        super();
        this.systemContext = systemContext;
    }
}
