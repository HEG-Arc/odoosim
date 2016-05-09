package ch.hearc.ig.tb.odoosim.database;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import javax.sql.PooledConnection;
import oracle.jdbc.pool.OracleConnectionPoolDataSource;

/**
 *
 * @author tomant
 */
public class OracleDataSource {
    
    private static DataSource ds = null;
    private static PooledConnection pool = null;
    
    public static Connection getConnection(String server, String db, String user, String pass){
        
       Connection connection = null;
        try{
            if(ds == null) {
                
                ds = new OracleConnectionPoolDataSource();
                //  Paramétres globaux de la connexion
                ((OracleConnectionPoolDataSource)ds).setDriverType("thin");
                ((OracleConnectionPoolDataSource)ds).setServerName(server);
                ((OracleConnectionPoolDataSource)ds).setPortNumber(1521);
                ((OracleConnectionPoolDataSource)ds).setDatabaseName(db);
                ((OracleConnectionPoolDataSource)ds).setUser(user);
                ((OracleConnectionPoolDataSource)ds).setPassword(pass);
            }
            
            //  Création du Pool de connexion
            pool = ((OracleConnectionPoolDataSource)ds).getPooledConnection();
            
            //  Récupération d'une connexion distincte
            connection = pool.getConnection();
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            System.out.println("#E : " +e.getMessage());
        } finally {
            return connection;
        }
    }
    public static void closeConnection(Connection connection){
       
        try{
            if(connection != null){
                connection.close();
            }
        }catch(SQLException e){
            System.out.println("#E : " +e.getMessage());
        }
    
    }
    
}
