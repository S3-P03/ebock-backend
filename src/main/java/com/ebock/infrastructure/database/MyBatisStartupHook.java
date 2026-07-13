package com.ebock.infrastructure.database;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.apache.ibatis.session.SqlSessionFactory;

@ApplicationScoped
public class MyBatisStartupHook {

    @Inject
    SqlSessionFactory sqlSessionFactory;

    void onStart(@Observes StartupEvent ev) {
        sqlSessionFactory.getConfiguration().addInterceptor(new SchemaRoutingInterceptor());
    }
}
