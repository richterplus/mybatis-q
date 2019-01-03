package com.github.mybatisq;

/**
 * 数据库处理器工厂
 * @author richterplus
 */
public class DatabaseProcessorFactory {

    private static final String MYSQL = "mysql";

    private static final String POSTGRESQL = "postgresql";

    public static DatabaseProcessor createMapperFromDatasource(Datasource datasource) {
        if (datasource.getDriverClassName().contains(MYSQL)) {
            return new MysqlDatabaseProcessor(datasource);
        } else if (datasource.getDriverClassName().contains(POSTGRESQL)) {
            return new PostgresqlDatabaseProcessor(datasource);
        } else {
            return new MysqlDatabaseProcessor(datasource);
        }
    }
}
