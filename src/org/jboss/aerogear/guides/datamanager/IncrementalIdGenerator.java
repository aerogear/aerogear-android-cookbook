package org.jboss.aerogear.guides.datamanager;

import org.jboss.aerogear.android.datamanager.IdGenerator;

import java.io.Serializable;

public class IncrementalIdGenerator implements IdGenerator {

    private static Long actualValue = 0L;

    @Override
    public Serializable generate() {
        return ++actualValue;
    }

}
