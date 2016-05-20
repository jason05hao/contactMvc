package com.contactMvc.dao;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.j8ql.DB;
import org.j8ql.DBBuilder;
import org.j8ql.Runner;
import org.j8ql.query.*;
import org.postgresql.ds.PGSimpleDataSource;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.*;
import java.util.stream.Stream;

import static org.j8ql.query.Query.*;

/**
 * <p>The da (Data Access) pattern allows to have a single point of access to lower level storage/ORM library such as J8QL, Hibernate, and JOOQ.
 * (think of it as the HibernateUtil, although it should focus on the way the application wants to access the data. It does not have to expose
 * all of what the lower level storage library has to offer)</p>
 *
 * <p>This da is J8QL backed, but it could obviously be back by any store library/storage such as Hibernate, MongoDb, or iBatis.</p>
 *
 * <p>While it is tempting to have the Daos or other objects directly using the Storage libraries (i.e. Hibernate, J8QL, and JOOQ) adding this single point of access
 * can been quite beneficial. Here are some of the benefits of such a pattern:</p>
 *
 * <ul>
 *     <li>Normalize the data access across entities based on what the application really needs.</li>
 *     <li>Trivial way to add "interceptor" logic across all entities (e.g., TimeStamps).</li>
 *     <li>Simple and flexible way to performance monitoring for future reporting.</li>
 *     <li>Being Guice managed, it can also add another level of security at the data access layer (in addition to the @WebRest and DAOs levels)</li>
 *     <li>Ease significantly storage library upgrade or switch and even storage migration.</li>
 * </ul>
 *
 */
@Singleton
public class DaoHelper {

    final DB db;
    final HikariDataSource ds;

    @Inject
    public DaoHelper(@Named("db.server") String server,
                     @Named("db.port") String portStr,
                     @Named("db.name") String name,
                     @Named("db.user") String user,
                     @Named("db.pwd") String pwd,
                     @Named("db.pool.maxsize") String maxPoolSizeStr) {

        //System.out.println("DaoHelper ..... create pool START");
        String poolName = "default";

        int port = Integer.parseInt(portStr);
        int poolMaxSize = Integer.parseInt(maxPoolSizeStr);

        // create the HikariCP datasource
        HikariConfig config = new HikariConfig();
        PGSimpleDataSource pg = new PGSimpleDataSource();
        pg.setPortNumber(port);
        pg.setServerName(server);
        pg.setDatabaseName(name);
        pg.setUser(user);
        pg.setPassword(pwd);
        config.setDataSource(pg);
        config.setMaximumPoolSize(poolMaxSize);
        config.setRegisterMbeans(true);
        config.setPoolName(poolName);
        ds = new HikariDataSource(config);

        // (to know IdleConnections, ActiveConnections, TotalConnections, ThreadsAwaitingConnection).
        // Also, can set : connectionTimeout, idleTimeout, maxLifetime, minimumIdle, maximumPoolSize

        db = new DBBuilder().build(ds);
        //System.out.println("DaoHelper ..... create pool END");
    }


    // --------- DML Query --------- //
    public <T> T execute(InsertQuery<T> query){
        try (Runner runner = db.openRunner()) {
            return runner.exec(query);
        }
    }


    public <T> T execute(UpdateQuery<T> query){
        try (Runner runner = db.openRunner()) {
            return runner.exec(query);
        }
    }


    public <T> T execute(DeleteQuery<T> query){
        try (Runner runner = db.openRunner()) {
            return runner.exec(query);
        }
    }
    // --------- /DML Query --------- //


    // --------- SelectQuery --------- //
    public <T> Optional<T> first(SelectQuery<T> query){
        try (Runner runner = db.openRunner()) {
            return runner.first(query);
        }
    }

    public long count(SelectQuery query){
        try (Runner runner = db.openRunner()) {
            return runner.count(query);
        }
    }

    public <T> Stream<T> stream(SelectQuery<T> query){
        try (Runner runner = db.openRunner()) {
            return runner.stream(query);
        }
    }
    public <T> List<T> list(SelectQuery<T> query){
        try (Runner runner = db.openRunner()) {
            return runner.list(query);
        }
    }
    // --------- /SelectQuery --------- //

    // --------- DB Proxy --------- //
    public List<String> getValidColumns(BaseQuery baseQuery, Collection<String> columnNames) {
        return db.getValidColumns(baseQuery, columnNames);
    }
    // --------- /DB Proxy --------- //

    public int executeUpdate(String sql,Object... values) {
        try (Runner runner = db.openRunner()){
            return runner.executeUpdate(sql,values);
        }
    }

    /**
     * WARNING: Call this only in application shutdown
     */
    public void closeDataSource(){
        ds.shutdown();
    }
}